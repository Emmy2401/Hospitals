package com.example.hospitals.Config;

import com.example.hospitals.Interface.DistanceService;
import com.example.hospitals.Service.LocalDistanceServiceTemporaire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistanceServiceConfig {

    @Bean
    public DistanceService distanceService() {
        return new LocalDistanceServiceTemporaire(); // Utilisation de l'implémentation locale temporaire
    }
}
