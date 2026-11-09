package com.habitagest.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        ConfiguracionObligatoria.exigir(System.getenv(), "MONGODB_URI");
        SpringApplication.run(WebApplication.class, args);
    }
}
