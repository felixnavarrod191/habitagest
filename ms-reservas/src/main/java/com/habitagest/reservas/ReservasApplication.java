package com.habitagest.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReservasApplication {

    public static void main(String[] args) {
        ConfiguracionObligatoria.exigir(System.getenv(), "MONGODB_URI");
        SpringApplication.run(ReservasApplication.class, args);
    }
}
