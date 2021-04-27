package de.uol.vpp.action;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"de.uol.vpp.action"})
@EnableScheduling
public class ActionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActionApplication.class, args);
    }

}
