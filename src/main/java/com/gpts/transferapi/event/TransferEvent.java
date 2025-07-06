package com.gpts.transferapi.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransferEvent {
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private String currency;
    private Instant createdAt;
}
