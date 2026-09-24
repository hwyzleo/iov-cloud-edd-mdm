package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicInitializationProperties;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProperties;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * MDM Kafka Topic 启动预检与初始化（MDM-DSN-CR-041 §4）
 * <p>
 * 在应用开始接收业务流量前（ApplicationRunner）执行：
 * <ol>
 *   <li>读取并校验 MdmKafkaTopicProperties</li>
 *   <li>describeTopics 获取现有 Topic，与 19 个生产 Topic 求差生成 missingProducerTopics</li>
 *   <li>消费 Topic 仅生成 missingConsumerTopics，不加入创建集合</li>
 *   <li>生产 Topic 缺失且 create-missing-producer-topics=true 时，使用 NewTopic 批量幂等创建</li>
 *   <li>创建后再次 describeTopics 验证全部目标 Topic 可见</li>
 *   <li>根据 fail-fast 决定失败时终止启动或置健康为 DOWN</li>
 * </ol>
 * 幂等与并发：仅对差集调用 createTopics；并发实例同时初始化时将 TopicExistsException
 * 视为可重试幂等结果，以最终 describe 校验为准；已存在 Topic 不执行 alterConfigs /
 * createPartitions。结果写入 {@link MdmKafkaTopicReadiness} 供 Outbox Relay 门禁消费。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MdmKafkaTopicInitializer implements ApplicationRunner {

    private final MdmKafkaTopicProperties topicProperties;
    private final MdmKafkaTopicInitializationProperties initialization;
    private final Admin admin;
    private final MdmKafkaTopicReadiness readiness;
    private final MdmKafkaTopicMetrics metrics;

    @Override
    public void run(ApplicationArguments args) {
        if (!initialization.isEnabled()) {
            readiness.update(new MdmKafkaTopicReadiness.Snapshot(
                    MdmKafkaTopicReadiness.State.DISABLED, Set.of(), Set.of(),
                    topicProperties.producerTopics().size(), topicProperties.consumerTopics().size(),
                    false, Instant.now(), Optional.empty()));
            log.info("MDM Kafka Topic 初始化已禁用（topic-initialization.enabled=false），跳过预检，走兼容路径");
            return;
        }

        long started = System.nanoTime();
        List<String> producers = topicProperties.producerTopics();
        Set<String> consumers = topicProperties.consumerTopics();
        log.info("开始 MDM Kafka Topic 预检: producerTopics={}, consumerTopics={}, createMissing={}, failFast={}, partitions={}, replicas={}",
                producers, consumers, initialization.isCreateMissingProducerTopics(),
                initialization.isFailFast(), initialization.getPartitions(), initialization.getReplicas());
        logClusterId();

        try {
            Set<String> existing = existingTopics(new LinkedHashSet<>(producers));
            Set<String> missingProducers = new LinkedHashSet<>(producers);
            missingProducers.removeAll(existing);
            Set<String> missingConsumers = new LinkedHashSet<>(consumers);
            missingConsumers.removeAll(existing);

            metrics.recordCheck("producer", missingProducers.isEmpty() ? "ok" : "missing");
            metrics.recordCheck("consumer", missingConsumers.isEmpty() ? "ok" : "missing");
            for (String topic : missingProducers) {
                metrics.recordMissing(topic, "producer", true);
            }
            for (String topic : missingConsumers) {
                metrics.recordMissing(topic, "consumer", true);
            }

            log.info("MDM Kafka Topic 预检结果: expectedProducer={}, existing={}, missingProducer={}, missingConsumer={}",
                    producers.size(), existing.size(), missingProducers, missingConsumers);

            if (!missingProducers.isEmpty() && initialization.isCreateMissingProducerTopics()) {
                log.info("自动创建缺失生产 Topic: missing={}, partitions={}, replicas={}",
                        missingProducers, initialization.getPartitions(), initialization.getReplicas());
                CreateSummary summary = createMissing(missingProducers);
                metrics.recordCreate("created", summary.created());
                metrics.recordCreate("failed", summary.failed());
                metrics.recordCreate("skipped", 0);
                // 创建后再次 describe 验证
                Set<String> verified = existingTopics(new LinkedHashSet<>(producers));
                Set<String> stillMissing = new LinkedHashSet<>(producers);
                stillMissing.removeAll(verified);
                missingProducers.clear();
                missingProducers.addAll(stillMissing);
                log.info("MDM Kafka Topic 创建完成: created={}, failed={}, stillMissing={}",
                        summary.created(), summary.failed(), stillMissing);
            }

            if (missingProducers.isEmpty()) {
                readiness.update(new MdmKafkaTopicReadiness.Snapshot(
                        MdmKafkaTopicReadiness.State.READY, Set.of(), missingConsumers,
                        producers.size(), consumers.size(),
                        initialization.isCreateMissingProducerTopics(), Instant.now(), null));
                long elapsedMs = (System.nanoTime() - started) / 1_000_000;
                log.info("MDM Kafka Topic 预检完成，全部生产 Topic 就绪: expected={}, missingConsumer={}, 耗时={}ms",
                        producers.size(), missingConsumers, elapsedMs);
                return;
            }

            // 仍有缺失生产 Topic
            String message = String.format(
                    "MDM Kafka Topic 未就绪: missingProducerTopics=%s, createMissing=%s", missingProducers,
                    initialization.isCreateMissingProducerTopics());
            readiness.update(new MdmKafkaTopicReadiness.Snapshot(
                    MdmKafkaTopicReadiness.State.NOT_READY, missingProducers, missingConsumers,
                    producers.size(), consumers.size(),
                    initialization.isCreateMissingProducerTopics(), Instant.now(),
                    Optional.of(new IllegalStateException(message))));
            if (initialization.isFailFast()) {
                throw new MdmKafkaTopicNotReadyException(message);
            }
            log.warn("{}，fail-fast=false，健康状态置 DOWN，Outbox Relay 保持暂停", message);
        } catch (MdmKafkaTopicNotReadyException e) {
            throw e;
        } catch (Exception e) {
            String message = "MDM Kafka Topic 预检失败: " + e.getMessage();
            readiness.update(new MdmKafkaTopicReadiness.Snapshot(
                    MdmKafkaTopicReadiness.State.NOT_READY,
                    new LinkedHashSet<>(producers), consumers,
                    producers.size(), consumers.size(),
                    initialization.isCreateMissingProducerTopics(), Instant.now(), Optional.of(e)));
            if (initialization.isFailFast()) {
                throw new MdmKafkaTopicNotReadyException(message, e);
            }
            log.error("{}，fail-fast=false，健康状态置 DOWN，Outbox Relay 保持暂停", message, e);
        }
    }

    /**
     * 描述目标 Topic，返回其中已存在的名称集合；
     * 非"Topic 不存在"异常视为 describe 不可靠，向上传播。
     */
    private Set<String> existingTopics(Collection<String> target) throws Exception {
        Set<String> existing = new HashSet<>();
        if (target.isEmpty()) {
            return existing;
        }
        DescribeTopicsResult result = admin.describeTopics(target);
        for (Map.Entry<String, KafkaFuture<TopicDescription>> entry : result.values().entrySet()) {
            String name = entry.getKey();
            try {
                entry.getValue().get(initialization.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
                existing.add(name);
            } catch (ExecutionException ex) {
                if (!(unwrap(ex) instanceof UnknownTopicOrPartitionException)) {
                    throw ex;
                }
            }
        }
        return existing;
    }

    /**
     * 批量创建缺失生产 Topic；TopicExistsException 视为幂等成功。
     */
    private CreateSummary createMissing(Set<String> missing) throws Exception {
        List<NewTopic> newTopics = missing.stream()
                .map(name -> new NewTopic(name, initialization.getPartitions(), initialization.getReplicas()))
                .toList();
        CreateTopicsResult result = admin.createTopics(newTopics);
        int created = 0;
        int failed = 0;
        for (Map.Entry<String, KafkaFuture<Void>> entry : result.values().entrySet()) {
            String name = entry.getKey();
            try {
                entry.getValue().get(initialization.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
                created++;
            } catch (ExecutionException ex) {
                if (unwrap(ex) instanceof TopicExistsException) {
                    // 并发实例 / 已存在场景视为幂等成功
                    created++;
                } else {
                    failed++;
                    log.warn("创建 Kafka Topic 失败: topic={}, cause={}", name, unwrap(ex).getMessage());
                }
            }
        }
        return new CreateSummary(created, failed);
    }

    /**
     * 尝试记录 Kafka 集群标识（权限不足时仅告警，不影响预检）。
     */
    private void logClusterId() {
        try {
            String clusterId = admin.describeCluster().clusterId()
                    .get(initialization.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
            log.info("Kafka 集群标识: clusterId={}", clusterId);
        } catch (Exception e) {
            log.warn("无法获取 Kafka 集群标识（可能权限不足或集群不可达）: {}", e.getMessage());
        }
    }

    /**
     * 创建结果汇总。
     */
    private record CreateSummary(int created, int failed) {
    }

    private Throwable unwrap(Throwable ex) {
        Throwable t = ex;
        while ((t instanceof ExecutionException || t instanceof java.util.concurrent.CompletionException)
                && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
}
