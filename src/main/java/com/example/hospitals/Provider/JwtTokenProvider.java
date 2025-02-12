package com.example.hospitals.Provider;

import org.springframework.stereotype.Service;

@Service
public class JwtTokenProvider {

    private String jwtToken;  // Stocke le token JWT

    public String getToken() {
        return jwtToken;
    }

    public void setToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
