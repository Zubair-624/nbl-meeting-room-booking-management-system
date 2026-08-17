package com.nblinternship.mrbms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MrbmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MrbmsApplication.class, args);
    }
}