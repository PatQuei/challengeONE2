package com.alura.forumhub.infra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentação da API
 * Define metadados da API como título, versão, descrição, servidor, etc.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Fórum Hub API",
                version = "v1.0.0",
                description = "API REST para gerenciar um fórum de discussões. " +
                        "Suporta criação de tópicos, respostas, autenticação com JWT e autorização baseada em roles.",
                contact = @Contact(
                        name = "Fórum Hub",
                        url = "https://github.com/PatQuei/challengeONE2",
                        email = "support@forumhub.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Servidor de Desenvolvimento"
                ),
                @Server(
                        url = "https://api.forumhub.com",
                        description = "Servidor de Produção"
                )
        }
)
@SecurityScheme(
        name = "Bearer Token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT para autenticação segura. Obtenha o token fazendo login em /auth/login",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfiguration {
    // Configuração é feita via anotações
}
