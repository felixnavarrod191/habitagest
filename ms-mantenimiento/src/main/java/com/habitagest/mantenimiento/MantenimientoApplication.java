package com.habitagest.mantenimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MantenimientoApplication {

    public static void main(String[] args) {
        ConfiguracionObligatoria.exigir(System.getenv(), "MONGODB_URI");
        SpringApplication.run(MantenimientoApplication.class, args);
    }
}
