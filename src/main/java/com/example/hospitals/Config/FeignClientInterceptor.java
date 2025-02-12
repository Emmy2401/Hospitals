package com.example.hospitals.Config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import com.example.hospitals.Provider.JwtTokenProvider;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    public FeignClientInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String jwtToken = jwtTokenProvider.getToken();
        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestTemplate.header("Authorization", "Bearer " + jwtToken);
        }
    }
}
