package com.habitagest.finanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanzasApplication {

    public static void main(String[] args) {
        ConfiguracionObligatoria.exigir(System.getenv(), "MONGODB_URI");
        SpringApplication.run(FinanzasApplication.class, args);
    }
}
