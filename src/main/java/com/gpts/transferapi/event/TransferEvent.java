package com.gpts.transferapi.event;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferEvent {
    private UUID transactionId;
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private String currency;
    private Instant createdAt;
}
