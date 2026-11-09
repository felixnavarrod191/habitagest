package com.habitagest.copropiedad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CopropiedadApplication {

    public static void main(String[] args) {
        ConfiguracionObligatoria.exigir(System.getenv(), "MONGODB_URI");
        SpringApplication.run(CopropiedadApplication.class, args);
    }
}
