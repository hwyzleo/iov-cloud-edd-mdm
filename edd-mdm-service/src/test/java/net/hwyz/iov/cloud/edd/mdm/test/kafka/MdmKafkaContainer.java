package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * MDM Kafka 集成测试容器
 * <p>
 * 使用本地可用的 bitnami/kafka 镜像，以 KRaft 单节点模式启动，
 * 并禁用 broker 自动创建 Topic（auto.create.topics.enable=false），
 * 以验证 FW-KAFKA Topic Provisioning 的幂等创建行为。
 *
 * @author hwyz_leo
 */
public final class MdmKafkaContainer extends GenericContainer<MdmKafkaContainer> {

    public static final String IMAGE = "bitnami/kafka:3.5.2";
    private static final int KAFKA_PORT = 9092;
    private static final int CONTROLLER_PORT = 9093;
    private static final int HOST_PORT = 19092;

    public MdmKafkaContainer() {
        super(DockerImageName.parse(IMAGE));
        withExposedPorts(KAFKA_PORT);
        addFixedExposedPort(HOST_PORT, KAFKA_PORT);
        withEnv("KAFKA_ENABLE_KRAFT", "yes");
        withEnv("KAFKA_CFG_NODE_ID", "1");
        withEnv("KAFKA_CFG_PROCESS_ROLES", "broker,controller");
        withEnv("KAFKA_CFG_CONTROLLER_LISTENER_NAMES", "CONTROLLER");
        withEnv("KAFKA_CFG_LISTENERS", "PLAINTEXT://:" + KAFKA_PORT + ",CONTROLLER://:" + CONTROLLER_PORT);
        withEnv("KAFKA_CFG_ADVERTISED_LISTENERS", "PLAINTEXT://localhost:" + HOST_PORT);
        withEnv("KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP", "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT");
        withEnv("KAFKA_CFG_CONTROLLER_QUORUM_VOTERS", "1@localhost:" + CONTROLLER_PORT);
        withEnv("KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE", "false");
        withStartupTimeout(Duration.ofSeconds(120));
        waitingFor(Wait.forLogMessage(".*Kafka Server started.*", 1));
    }

    /**
     * 供测试 JVM 内客户端连接使用的 bootstrap servers
     */
    public String getBootstrapServers() {
        return "localhost:" + HOST_PORT;
    }
}
