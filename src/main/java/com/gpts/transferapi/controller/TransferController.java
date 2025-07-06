package com.gpts.transferapi.controller;

import com.gpts.transferapi.dto.TransferRequest;
import com.gpts.transferapi.event.TransferEvent;
import com.gpts.transferapi.service.TransferProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferProducerService producerService;

    @PostMapping
    public ResponseEntity<String> createTransfer(@Valid @RequestBody TransferRequest request) {
        log.info("==>[HTTP POST=/api/transfer]Получен запрос на перевод: {}", request);

        TransferEvent event = TransferEvent.builder()
                .fromUserId(request.getFromUserId())
                .toUserId(request.getToUserId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .createdAt(Instant.now())
                .build();

        log.info("==>kafka-topic[transfer-requests] Отправка события в Kafka: {}", event);
        producerService.send(event);
        log.info("<==[HTTP POST=/api/transfer][202]Возвращаем HTTP 202: принято в обработку");
        return ResponseEntity.accepted().body("принято в обработку"); // HTTP 202
    }
}
