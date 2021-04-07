package de.uol.vpp.load;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = {"de.uol.vpp.load"})
@EnableScheduling
public class LoadApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadApplication.class, args);
    }

}

