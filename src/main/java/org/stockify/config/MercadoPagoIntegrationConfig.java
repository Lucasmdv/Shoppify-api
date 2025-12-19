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
        return frontendBase + "/purchases";
    }

    public String getPendingUrl() {
        return frontendBase + "/purchases";
    }

    public String getFailureUrl() {
        return frontendBase + "/purchases";
    }
}
