package de.uol.vpp.production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;


@SpringBootApplication(scanBasePackages = {"de.uol.vpp.production"})
@EnableScheduling
public class ProductionApplication {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));
        SpringApplication.run(ProductionApplication.class, args);
    }

}

