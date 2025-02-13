package com.example.hospitals.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Permet toutes les origines (à ajuster pour la production)
        config.addAllowedHeader("*"); // Permet tous les en-têtes
        config.addAllowedMethod("*"); // Permet toutes les méthodes (GET, POST, PUT, DELETE, OPTIONS)
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}