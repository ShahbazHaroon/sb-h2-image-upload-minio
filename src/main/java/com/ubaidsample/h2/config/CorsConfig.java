/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        // Allow CORS on all paths
                        .addMapping("/**")

                        // Use externalized configuration for flexibility
                        .allowedOrigins(allowedOrigins)
                        //.allowedOriginPatterns("http://*.example.com", "https://*.example.com")

                        // Allowed HTTP methods
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")

                        // Allow all headers
                        //.allowedHeaders("*")
                        // Restrict headers explicitly for security
                        .allowedHeaders("Authorization", "Content-Type", "Accept", "X-Requested-With")

                        // Expose headers to frontend if needed
                        //.exposedHeaders("Authorization", "Content-Type")

                        // Enable credentials (cookies, authorization headers, etc.)
                        .allowCredentials(true)

                        // Cache pre-flight response for 1 hour
                        .maxAge(3600);
            }
        };
    }
}
