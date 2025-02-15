package com.example.hospitals.Interface;

import com.example.hospitals.Config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "users", url = "http://localhost:8082/api/users", configuration = FeignClientInterceptor.class)
public interface UserClient {

    @PostMapping("/login")
    ResponseEntity<Map<String, String>> getToken(@RequestBody Map<String, String> credentials);
}
