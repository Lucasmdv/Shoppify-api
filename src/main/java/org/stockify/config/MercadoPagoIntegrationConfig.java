package org.stockify.config;

import com.mercadopago.MercadoPagoConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MercadoPagoIntegrationConfig implements InitializingBean {
    private static final String DEFAULT_FRONTEND_BASE = "http://localhost:4200";

    @Value("${TEST_ACCESS_TOKEN}")
    private String accessToken;

    @Getter
    @Value("${MERCADOPAGO_WEBHOOK_SECRET}")
    private String webhookSecret;

    @Getter
    @Value("${mp.frontend-base.url}")
    private String frontendBase;

    @Getter
    @Value("${mp.notification.url}")
    private String notificationUrl;

    @Override
    public void afterPropertiesSet() {
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("Mercado Pago Access Token is not set. Payment features may not work.");
            return;
        }
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Mercado Pago SDK initialized successfully.");
    }

    public String getSuccessUrl() {
        return buildPurchaseUrl(null);
    }

    public String getPendingUrl() {
        return buildPurchaseUrl(null);
    }

    public String getFailureUrl() {
        return buildPurchaseUrl(null);
    }

    public String getSuccessUrl(Long saleId) {
        return buildPurchaseUrl(saleId);
    }

    public String getPendingUrl(Long saleId) {
        return buildPurchaseUrl(saleId);
    }

    public String getFailureUrl(Long saleId) {
        return buildPurchaseUrl(saleId);
    }

    private String buildPurchaseUrl(Long saleId) {
        String base = normalizeFrontendBase();
        if (saleId == null) {
            return base + "/purchases";
        }
        return base + "/purchase/" + saleId;
    }

    private String normalizeFrontendBase() {
        String base = frontendBase;
        if (base == null || base.isBlank()) {
            log.warn("mp.frontend-base.url is blank; using default {}", DEFAULT_FRONTEND_BASE);
            base = DEFAULT_FRONTEND_BASE;
        }
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }
}
