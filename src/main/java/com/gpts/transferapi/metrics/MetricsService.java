package com.gpts.transferapi.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter successCounter;
    private final Counter errorCounter;

    public MetricsService(MeterRegistry registry) {
        this.successCounter = Counter.builder("transfer_requests_success_total")
                .description("Количество успешно обработанных переводов")
                .register(registry);

        this.errorCounter = Counter.builder("transfer_requests_error_total")
                .description("Количество ошибок при обработке переводов")
                .register(registry);
    }

    public void incrementSuccessCounter() {
        successCounter.increment();
    }

    public void incrementErrorCounter() {
        errorCounter.increment();
    }
}
