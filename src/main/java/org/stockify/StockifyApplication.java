package org.stockify;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.stockify.config.AppEnvConfig;
import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication
@EnableScheduling
public class StockifyApplication {
    public static void main(String[] args) {
        AppEnvConfig.loadEnv();

        //ENV
        Dotenv dotenv = Dotenv.load();
        String mpKey = dotenv.get("TEST_ACCESS_TOKEN");

        // SDK de Mercado Pago
        MercadoPagoConfig.setAccessToken(mpKey);


        SpringApplication.run(StockifyApplication.class, args);
    }
}
