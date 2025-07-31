package com.example.scope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScopeCompletenessApp {
    public static void main(String[] args) {
        SpringApplication.run(ScopeCompletenessApp.class, args);
    }
}
