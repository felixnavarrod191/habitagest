package com.habitagest.pqr;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("HabitaGest · ms-pqr")
                .version("1.0.0")
                .description("PQR con su flujo de estados y comunicados. Contrato: specs/001-habitagest/contracts/pqr.yaml"));
    }
}
