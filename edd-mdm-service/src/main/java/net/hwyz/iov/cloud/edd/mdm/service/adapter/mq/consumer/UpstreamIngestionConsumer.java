package net.hwyz.iov.cloud.edd.mdm.service.adapter.mq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpstreamIngestionConsumer {

    @KafkaListener(topics = "upstream.placeholder.product.brand", groupId = "mdm-ingestion", autoStartup = "false")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("收到上游消息: topic={}, partition={}, offset={}, key={}",
                record.topic(), record.partition(), record.offset(), record.key());

        try {
            String[] parts = record.topic().split("\\.");
            if (parts.length != 4) {
                log.error("Topic格式非法: {}", record.topic());
                return;
            }
            String sourceSystem = parts[1];
            String entityType = parts[3].toUpperCase();

            log.info("上游消息解析: sourceSystem={}, entityType={}, value={}",
                    sourceSystem, entityType, record.value());

            // TODO: 完善JSON解析并调用 ingestionAppService.ingest()
            // 实际 topic 列表通过 Nacos 配置动态注入，此处为占位

        } catch (Exception e) {
            log.error("处理上游消息失败: topic={}, offset={}", record.topic(), record.offset(), e);
        }
    }
}
