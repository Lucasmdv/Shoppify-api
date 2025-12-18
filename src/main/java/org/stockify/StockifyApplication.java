package org.stockify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.stockify.config.AppEnvConfig;

@SpringBootApplication
@EnableScheduling
public class StockifyApplication {
    public static void main(String[] args) {
        AppEnvConfig.loadEnv();
        SpringApplication.run(StockifyApplication.class, args);
    }
}
