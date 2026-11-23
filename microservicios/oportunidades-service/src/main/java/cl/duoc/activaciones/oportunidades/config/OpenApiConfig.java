package cl.duoc.activaciones.oportunidades.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI oportunidadesOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API de Oportunidades")
                .version("1.0")
                .description("CRUD de oportunidades para la aplicacion de activaciones"));
    }
}
