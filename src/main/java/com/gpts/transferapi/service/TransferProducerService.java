package com.gpts.transferapi.service;

import com.gpts.transferapi.event.TransferEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferProducerService {

    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.transfer-requests}")
    private String topic;

    public void send(TransferEvent event) {
        CompletableFuture<SendResult<String, TransferEvent>> future =
                kafkaTemplate.send(topic, event);

        future.thenAccept(result -> {
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("Success Sent event to Kafka: topic={}, partition={}, offset={}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        }).exceptionally(ex -> {
            log.error("Failed to send event to Kafka", ex);
            return null;
        });
    }
}
