package de.uol.vpp.masterdata.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "de.uol.vpp.masterdata.application",
        "de.uol.vpp.masterdata.infrastructure"
    })
public class MasterdataApplication {
    public static void main(String[] args) {
        SpringApplication.run(MasterdataApplication.class, args);
    }
}

