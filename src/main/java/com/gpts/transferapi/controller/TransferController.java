package com.gpts.transferapi.controller;

import com.gpts.transferapi.dto.TransferRequest;
import com.gpts.transferapi.event.TransferEvent;
import com.gpts.transferapi.service.TransferProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.KafkaException;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@Slf4j
@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferProducerService producerService;
    @Operation(summary = "Создать перевод между пользователями")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Создано событие перевода"),
            @ApiResponse(responseCode = "400", description = "Невалидный запрос"),
            @ApiResponse(responseCode = "503", description = "Kafka недоступна")
    })
    @PostMapping

    public ResponseEntity<String> createTransfer(@Valid @RequestBody TransferRequest request) {
        UUID transactionId = request.getTransactionId() != null
                ? request.getTransactionId()
                : UUID.randomUUID();


        try {
            log.info("ID:[{}] ==> [HTTP POST=/api/transfer] Запрос на перевод: {}", transactionId, request);

            TransferEvent event = TransferEvent.builder()
                    .transactionId(transactionId)
                    .fromUserId(request.getFromUserId())
                    .toUserId(request.getToUserId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .createdAt(Instant.now())
                    .build();

            log.info("ID:[{}] 📤 Отправка события в Kafka: {}", transactionId, event);
            producerService.send(event);

            log.info("ID:[{}] <== [HTTP POST=/api/transfer] HTTP 201 - Создано событие перевода.", transactionId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Создано событие перевода. transactionId: " + transactionId);

        } catch (KafkaException e) {
            log.error("ID:[{}] ❌ Kafka недоступна", transactionId, e);
            return ResponseEntity.status(503).body("Kafka недоступна. Повторите позже. transactionId: " + transactionId);
        }
    }
}
