package com.example.hospitals.Config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@FeignClient(
        configuration = FeignSimpleEncoderConfig.class
)
public class FeignConfig {
    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return requestTemplate -> {
            // Récupérez le token depuis SecurityContextHolder
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() != null) {
                String token = authentication.getCredentials().toString();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
