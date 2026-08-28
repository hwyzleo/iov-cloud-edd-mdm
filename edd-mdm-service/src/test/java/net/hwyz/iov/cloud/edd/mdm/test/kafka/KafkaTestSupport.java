package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicDefinitionProvider;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicDefinition;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicProvisioningStatus;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Kafka 集成测试支持工具
 *
 * @author hwyz_leo
 */
public final class KafkaTestSupport {

    private KafkaTestSupport() {
    }

    public static MdmKafkaContainer newKafkaContainer() {
        return new MdmKafkaContainer();
    }

    public static Set<String> declaredTopicNames(MdmKafkaTopicDefinitionProvider provider) {
        return provider.topicDefinitions().stream()
                .map(KafkaTopicDefinition::name)
                .collect(Collectors.toSet());
    }

    public static AdminClient newAdminClient(String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);
        return AdminClient.create(props);
    }

    public static Set<String> listNonInternalTopics(String bootstrapServers) throws Exception {
        try (AdminClient admin = newAdminClient(bootstrapServers)) {
            Set<String> all = admin.listTopics().names().get(30, TimeUnit.SECONDS);
            return all.stream().filter(n -> !n.startsWith("__")).collect(Collectors.toSet());
        }
    }

    public static int partitionsOf(String bootstrapServers, String topic) throws Exception {
        try (AdminClient admin = newAdminClient(bootstrapServers)) {
            DescribeTopicsResult result = admin.describeTopics(Collections.singleton(topic));
            TopicDescription desc = result.all().get(30, TimeUnit.SECONDS).get(topic);
            return desc.partitions().size();
        }
    }

    public static void awaitReady(KafkaTopicProvisioningStatus status, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (status.state() == KafkaTopicProvisioningStatus.State.READY) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("等待 provisioning READY 被中断", e);
            }
        }
        assertEquals(KafkaTopicProvisioningStatus.State.READY, status.state(),
                "等待 Kafka Topic Provisioning READY 超时，当前状态=" + status.state()
                        + "，缺失 topic=" + status.missingTopics()
                        + "，最近失败=" + status.lastFailure().orElse(null));
    }

    public static List<String> readRecords(String bootstrapServers, String topic, Duration timeout) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "mdm-test-" + System.nanoTime());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase());

        List<String> records = new ArrayList<>();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<TopicPartition> partitions = consumer.partitionsFor(topic).stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .collect(Collectors.toList());
            consumer.assign(partitions);
            consumer.seekToBeginning(partitions);
            long deadline = System.currentTimeMillis() + timeout.toMillis();
            while (System.currentTimeMillis() < deadline) {
                consumer.poll(Duration.ofMillis(500)).forEach(r -> records.add(r.value()));
                if (!records.isEmpty()) {
                    break;
                }
            }
        }
        return records;
    }

    public static void preCreateTopic(String bootstrapServers, String topic, int partitions, short replicationFactor)
            throws Exception {
        try (AdminClient admin = newAdminClient(bootstrapServers)) {
            org.apache.kafka.clients.admin.NewTopic newTopic =
                    new org.apache.kafka.clients.admin.NewTopic(topic, partitions, replicationFactor);
            admin.createTopics(Collections.singletonList(newTopic)).all().get(30, TimeUnit.SECONDS);
        }
    }

    public static void assertTopicSetEquals(String bootstrapServers, Set<String> expected) throws Exception {
        Set<String> actual = listNonInternalTopics(bootstrapServers);
        assertEquals(expected, actual, "Kafka 中实际 Topic 集合应与 MDM 声明一致");
    }

    public static Map<String, Object> adminProperties(String bootstrapServers) {
        return Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    }
}
