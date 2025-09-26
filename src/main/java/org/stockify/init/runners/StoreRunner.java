package org.stockify.init.runners;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.stockify.init.StoreSeeder;


@Component
@Order(1)
@RequiredArgsConstructor
public class StoreRunner implements CommandLineRunner {
    private final StoreSeeder storeSeeder;

    @Override
    public void run(String... args){
        storeSeeder.ensureDefault();
    }
}
