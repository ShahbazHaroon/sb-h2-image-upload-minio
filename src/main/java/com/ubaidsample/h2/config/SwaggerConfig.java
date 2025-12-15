/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot REST API Application using H2 database to upload image using MinIO")
                        .version(appVersion)
                        .description("API documentation for spring boot application using H2 database to upload image using MinIO")
                        .termsOfService("http://springdoc.org/terms/")
                        .contact(new Contact()
                                .name("Muhammad Ubaid Ur Raheem")
                                .url("https://github.com/ShahbazHaroon")
                                .email("shahbazhrn@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addServersItem(new Server().url("http://localhost:8080/sb-h2-image-upload-minio").description("Development server"))
                .addServersItem(new Server().url("https://api.example.com").description("Production server"));
    }
}