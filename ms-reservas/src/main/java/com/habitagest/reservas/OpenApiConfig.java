package com.habitagest.reservas;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("HabitaGest · ms-reservas")
                .version("1.0.0")
                .description("Zonas comunes, disponibilidad y reservas. Contrato: specs/001-habitagest/contracts/reservas.yaml"));
    }
}
