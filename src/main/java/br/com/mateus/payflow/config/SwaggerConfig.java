package br.com.mateus.payflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customAPI() {
        return new OpenAPI()
                    .info(new Info()
                    .title("PayFlow API")
                    .version("1.0")
                    .description("Documentação da API PayFlow"));
    }
}
