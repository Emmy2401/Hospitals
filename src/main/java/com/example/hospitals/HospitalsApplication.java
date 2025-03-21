package com.example.hospitals;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.annotations.OpenAPI31;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPI31
@OpenAPIDefinition(
        info = @Info(
                title = "MedHeadPoc Hospital",
                version = "1.0.0",
                description = "API for POC"
        ),
        servers = {
                @Server(url = "http://localhost:8085", description = "Local server"),
                @Server(url = "https://api.hospitals.example.com", description = "Production server")
        }
)
public class HospitalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalsApplication.class, args);
    }

}
