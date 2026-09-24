package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicInitializationProperties;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProperties;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

/**
 * MdmKafkaTopicInitializer 单元测试（MDM-DSN-CR-041 §9.2）
 * <p>
 * 验证：空集群创建 19 个生产 Topic；部分已存在只创建差集；TopicExists 幂等；
 * 自动创建关闭时输出完整缺失清单；fail-fast 终止启动；Kafka 不可达处理；
 * 消费 Topic 仅检查不创建；已存在 Topic 参数不被修改。
 *
 * @author hwyz_leo
 */
@DisplayName("MdmKafkaTopicInitializer 测试")
class MdmKafkaTopicInitializerTest {

    private Admin admin;
    private MdmKafkaTopicProperties topicProperties;
    private MdmKafkaTopicInitializationProperties initialization;
    private MdmKafkaTopicReadiness readiness;
    private MdmKafkaTopicInitializer initializer;

    /**
     * 模拟 Kafka 集群现有 Topic 状态：createTopics 成功后加入此集合。
     */
    private final Set<String> existingState = new HashSet<>();

    @BeforeEach
    void setUp() throws Exception {
        admin = mock(Admin.class);
        topicProperties = new MdmKafkaTopicProperties();
        initialization = new MdmKafkaTopicInitializationProperties();
        readiness = new MdmKafkaTopicReadiness();
        @SuppressWarnings("unchecked")
        ObjectProvider<io.micrometer.core.instrument.MeterRegistry> registryProvider = mock(ObjectProvider.class);
        MdmKafkaTopicMetrics metrics = new MdmKafkaTopicMetrics(registryProvider);
        initializer = new MdmKafkaTopicInitializer(topicProperties, initialization, admin, readiness, metrics);

        DescribeClusterResult cluster = mock(DescribeClusterResult.class);
        when(cluster.clusterId()).thenReturn(KafkaFuture.completedFuture("test-cluster"));
        when(admin.describeCluster()).thenReturn(cluster);

        when(admin.describeTopics(anyCollection())).thenAnswer(inv -> describeResult(inv.getArgument(0)));
        when(admin.createTopics(anyCollection())).thenAnswer(inv -> {
            Collection<NewTopic> newTopics = inv.getArgument(0);
            CreateTopicsResult result = mock(CreateTopicsResult.class);
            Map<String, KafkaFuture<Void>> values = new HashMap<>();
            for (NewTopic nt : newTopics) {
                existingState.add(nt.name());
                values.put(nt.name(), KafkaFuture.completedFuture(null));
            }
            when(result.values()).thenReturn(values);
            return result;
        });
    }

    private DescribeTopicsResult describeResult(Collection<String> names) {
        DescribeTopicsResult result = mock(DescribeTopicsResult.class);
        Map<String, KafkaFuture<TopicDescription>> values = new HashMap<>();
        for (String name : names) {
            values.put(name, existingState.contains(name)
                    ? KafkaFuture.completedFuture(null)
                    : failedKafkaFuture(new UnknownTopicOrPartitionException(name)));
        }
        when(result.values()).thenReturn(values);
        return result;
    }

    private Set<String> capturedCreatedTopicNames() {
        ArgumentCaptor<Collection<NewTopic>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(admin).createTopics(captor.capture());
        return captor.getValue().stream().map(NewTopic::name).collect(Collectors.toSet());
    }

    @Nested
    @DisplayName("正常初始化")
    class InitializationTests {

        @Test
        @DisplayName("空集群创建全部 19 个生产 Topic，状态 READY")
        void emptyCluster_createsAllProducerTopics() {
            initializer.run(null);

            assertEquals(MdmKafkaTopicReadiness.State.READY, readiness.state());
            assertEquals(19, capturedCreatedTopicNames().size());
            assertEquals(Set.copyOf(topicProperties.producerTopics()), capturedCreatedTopicNames());
            assertEquals(Set.copyOf(topicProperties.producerTopics()), Set.copyOf(existingState));
        }

        @Test
        @DisplayName("新建 Topic 使用配置的分区数与副本数")
        void createdTopics_useConfiguredPartitionsAndReplicas() {
            initialization.setPartitions(5);
            initialization.setReplicas((short) 2);

            initializer.run(null);

            ArgumentCaptor<Collection<NewTopic>> captor = ArgumentCaptor.forClass(Collection.class);
            verify(admin).createTopics(captor.capture());
            for (NewTopic nt : captor.getValue()) {
                assertEquals(5, nt.numPartitions());
                assertEquals((short) 2, nt.replicationFactor());
            }
        }

        @Test
        @DisplayName("部分 Topic 已存在时只创建差集")
        void partialExisting_createsOnlyMissing() {
            existingState.add("mdm.brand");
            existingState.add("mdm.vehicle-node");
            existingState.add("mdm.part");

            initializer.run(null);

            assertEquals(MdmKafkaTopicReadiness.State.READY, readiness.state());
            Set<String> created = capturedCreatedTopicNames();
            assertEquals(16, created.size());
            assertFalse(created.contains("mdm.brand"));
            assertFalse(created.contains("mdm.vehicle-node"));
            assertFalse(created.contains("mdm.part"));
            assertTrue(created.contains("mdm.supplier"));
        }

        @Test
        @DisplayName("TopicExistsException 视为幂等成功，最终校验通过")
        void topicExists_isIdempotent() throws Exception {
            when(admin.createTopics(anyCollection())).thenAnswer(inv -> {
                Collection<NewTopic> newTopics = inv.getArgument(0);
                CreateTopicsResult result = mock(CreateTopicsResult.class);
                Map<String, KafkaFuture<Void>> values = new HashMap<>();
                for (NewTopic nt : newTopics) {
                    // 模拟并发实例抢先创建：存在性已满足，但返回 TopicExists
                    existingState.add(nt.name());
                    values.put(nt.name(), failedKafkaFuture(new TopicExistsException(nt.name())));
                }
                when(result.values()).thenReturn(values);
                return result;
            });

            initializer.run(null);

            assertEquals(MdmKafkaTopicReadiness.State.READY, readiness.state());
            assertTrue(readiness.snapshot().missingProducerTopics().isEmpty());
        }

        @Test
        @DisplayName("已存在 Topic 不执行 alterConfigs / createPartitions")
        void existingTopics_notAltered() {
            existingState.add("mdm.brand");

            initializer.run(null);

            verify(admin, never()).alterConfigs(any());
            verify(admin, never()).createPartitions(any());
        }
    }

    @Nested
    @DisplayName("自动创建关闭")
    class CreateDisabledTests {

        @BeforeEach
        void disableCreate() {
            initialization.setCreateMissingProducerTopics(false);
        }

        @Test
        @DisplayName("缺失 Topic 时状态 NOT_READY 且输出完整缺失清单，不创建")
        void missingTopics_notReadyWithFullList() {
            initialization.setFailFast(false);

            initializer.run(null);

            assertEquals(MdmKafkaTopicReadiness.State.NOT_READY, readiness.state());
            assertEquals(Set.copyOf(topicProperties.producerTopics()), readiness.snapshot().missingProducerTopics());
            verify(admin, never()).createTopics(anyCollection());
        }

        @Test
        @DisplayName("fail-fast 开启时抛 MdmKafkaTopicNotReadyException 终止启动")
        void failFast_throwsToBlockStartup() {
            initialization.setFailFast(true);

            assertThrows(MdmKafkaTopicNotReadyException.class, () -> initializer.run(null));
            assertEquals(MdmKafkaTopicReadiness.State.NOT_READY, readiness.state());
            assertEquals(19, readiness.snapshot().missingProducerTopics().size());
        }
    }

    @Nested
    @DisplayName("异常场景")
    class FailureTests {

        @Test
        @DisplayName("Kafka 不可达且 fail-fast 开启时终止启动")
        void brokerUnreachable_failFast_throws() throws Exception {
            when(admin.describeTopics(anyCollection()))
                    .thenThrow(new org.apache.kafka.common.errors.TimeoutException("broker unreachable"));
            initialization.setFailFast(true);

            assertThrows(MdmKafkaTopicNotReadyException.class, () -> initializer.run(null));
            assertEquals(MdmKafkaTopicReadiness.State.NOT_READY, readiness.state());
        }

        @Test
        @DisplayName("Kafka 不可达且 fail-fast 关闭时状态 NOT_READY，不抛出")
        void brokerUnreachable_failFastOff_notReady() throws Exception {
            when(admin.describeTopics(anyCollection()))
                    .thenThrow(new org.apache.kafka.common.errors.TimeoutException("broker unreachable"));
            initialization.setFailFast(false);

            initializer.run(null);

            assertEquals(MdmKafkaTopicReadiness.State.NOT_READY, readiness.state());
            assertFalse(readiness.snapshot().missingProducerTopics().isEmpty());
        }

        @Test
        @DisplayName("创建部分失败时以最终 describe 为准，仍缺失则 NOT_READY")
        void createPartialFailure_stillMissing_notReady() throws Exception {
            when(admin.createTopics(anyCollection())).thenAnswer(inv -> {
                Collection<NewTopic> newTopics = inv.getArgument(0);
                CreateTopicsResult result = mock(CreateTopicsResult.class);
                Map<String, KafkaFuture<Void>> values = new HashMap<>();
                for (NewTopic nt : newTopics) {
                    if (nt.name().equals("mdm.supplier")) {
                        values.put(nt.name(), failedKafkaFuture(
                                new org.apache.kafka.common.errors.TopicAuthorizationException("no create permission")));
                    } else {
                        existingState.add(nt.name());
                        values.put(nt.name(), KafkaFuture.completedFuture(null));
                    }
                }
                when(result.values()).thenReturn(values);
                return result;
            });
            initialization.setFailFast(false);

            initializer.run(null);

            assertEquals(MdmKafkaTopicReadiness.State.NOT_READY, readiness.state());
            assertEquals(Set.of("mdm.supplier"), readiness.snapshot().missingProducerTopics());
        }
    }

    @Nested
    @DisplayName("禁用与消费 Topic")
    class DisabledAndConsumerTests {

        @Test
        @DisplayName("初始化禁用时状态 DISABLED，不访问 Admin")
        void disabled_skipsPreCheck() {
            initialization.setEnabled(false);

            initializer.run(null);

            assertEquals(MdmKafkaTopicReadiness.State.DISABLED, readiness.state());
            verify(admin, never()).describeTopics(anyCollection());
            verify(admin, never()).createTopics(anyCollection());
        }

        @Test
        @DisplayName("消费 Topic 仅检查不创建（当前消费清单为空）")
        void consumerTopics_neverCreated() {
            assertTrue(topicProperties.consumerTopics().isEmpty(), "当前无消费 Topic");
            initializer.run(null);

            Set<String> created = capturedCreatedTopicNames();
            assertTrue(created.stream().noneMatch(name -> topicProperties.consumerTopics().contains(name)),
                    "消费 Topic 不应被创建");
            assertEquals(19, created.size());
        }
    }

    /**
     * 构造一个以指定异常失败的 KafkaFuture（当前 kafka-clients 版本无 failedFuture 静态方法）。
     */
    @SuppressWarnings("unchecked")
    private static <T> KafkaFuture<T> failedKafkaFuture(Throwable ex) {
        return new KafkaFuture<T>() {
            @Override
            public CompletionStage<T> toCompletionStage() {
                return java.util.concurrent.CompletableFuture.failedFuture(ex);
            }

            @Override
            public <R> KafkaFuture<R> thenApply(BaseFunction<T, R> fn) {
                return (KafkaFuture<R>) failedKafkaFuture(ex);
            }

            @Override
            public <R> KafkaFuture<R> thenApply(Function<T, R> fn) {
                return (KafkaFuture<R>) failedKafkaFuture(ex);
            }

            @Override
            public KafkaFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
                return failedKafkaFuture(ex);
            }

            @Override
            protected boolean complete(T value) {
                return false;
            }

            @Override
            protected boolean completeExceptionally(Throwable exception) {
                return false;
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                throw new ExecutionException(ex);
            }

            @Override
            public T get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                throw new ExecutionException(ex);
            }

            @Override
            public T getNow(T valueIfAbsent) throws InterruptedException, ExecutionException {
                throw new ExecutionException(ex);
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isCompletedExceptionally() {
                return true;
            }

            @Override
            public boolean isDone() {
                return true;
            }
        };
    }
}
