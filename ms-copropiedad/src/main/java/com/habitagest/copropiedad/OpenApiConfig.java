package com.habitagest.copropiedad;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("HabitaGest · ms-copropiedad")
                .version("1.0.0")
                .description("Torres, apartamentos con coeficiente, propietarios y residentes. Contrato: specs/001-habitagest/contracts/copropiedad.yaml"));
    }
}
