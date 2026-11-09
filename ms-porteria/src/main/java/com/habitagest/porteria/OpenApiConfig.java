package com.habitagest.porteria;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("HabitaGest · ms-porteria")
                .version("1.0.0")
                .description("Visitas, ingresos, paquetes y novedades. Contrato: specs/001-habitagest/contracts/porteria.yaml"));
    }
}
