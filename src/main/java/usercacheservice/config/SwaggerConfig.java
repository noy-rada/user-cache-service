package usercacheservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Cache Service API")
                        .description("Global user management service with Redis write-through caching and PostgreSQL persistence")
                        .version("1.0.0")
                        .contact(new Contact().name("User Cache Service")));
    }
}
