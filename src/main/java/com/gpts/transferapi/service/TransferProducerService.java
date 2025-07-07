package com.gpts.transferapi.service;

import com.gpts.transferapi.event.TransferEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferProducerService {

    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.transfer-requests}")
    private String topic;

    public void send(TransferEvent event) {
        String transactionId = event.getTransactionId().toString();

        try {
            SendResult<String, TransferEvent> result = kafkaTemplate.send(topic, event).get(); // <== блокирующий вызов
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("ID:[{}] ✅ Успешно отправлено в Kafka: topic={}, partition={}, offset={}",
                    transactionId, metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // важно: корректно сбросить флаг
            log.error("ID:[{}] ⛔ Поток был прерван при отправке в Kafka", transactionId, e);
            throw new KafkaException("Поток был прерван при отправке события в Kafka", e);
        } catch (ExecutionException e) {
            log.error("ID:[{}] ❌ Ошибка при отправке в Kafka", transactionId, e.getCause());
            throw new KafkaException("Ошибка при отправке события в Kafka", e.getCause());
        }
    }
}
