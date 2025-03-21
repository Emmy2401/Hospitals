package com.example.hospitals.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource,JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //.cors(cors -> cors.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Configuration CORS
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF si nécessaire
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/hospitals/login", "/hospitals/distance","/error","/hospitals/token","hospitals/id/{id}","/actuator/**","/v3/**","/api-docs.yaml","/error").permitAll() // Routes publiques
                        //.requestMatchers("/hospitals/getAll").hasRole("USER")
                        .anyRequest().authenticated() // Toutes les autres routes nécessitent une authentification
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
