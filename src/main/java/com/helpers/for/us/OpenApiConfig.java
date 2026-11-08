package com.you.and.me;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Felicita",
                        email = "pd.dimbulana@gmail.com"
                ),
                description = "Felicita API Documentation",
                title = "Felicita API",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "local",
                        url = "http://localhost:8080"
                )
        }
)
//@SecurityScheme(
//        name = "no auth",
//        description = "no auth",
//        type = SecuritySchemeType.DEFAULT
//)
public class OpenApiConfig {
}
