package com.habitagest.pqr;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionComun {

    /** Reloj del servicio, en hora de Colombia; las pruebas lo reemplazan por uno fijo. */
    @Bean
    public Clock reloj() {
        return Clock.system(ZoneId.of("America/Bogota"));
    }
}
