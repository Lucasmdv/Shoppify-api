package org.stockify.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.stockify.model.service.TransactionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionCleanupScheduler {

    private final TransactionService transactionService;

    @Value("${transaction.cleanup.timeout-minutes:1}") // Default to 15 minutes if not specified in application.properties
    private int timeoutMinutes;

    @Scheduled(cron = "0 * * * * *") // Runs every minute
    public void cleanupAbandonedTransactions() {
        log.debug("Running abandoned transaction cleanup task...");
        try {
            transactionService.cancelExpiredTransactions(timeoutMinutes);
        } catch (Exception e) {
            log.error("Error during transaction cleanup task", e);
        }
    }
}