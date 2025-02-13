package com.example.hospitals.Config;
import feign.codec.Encoder;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import feign.jackson.JacksonEncoder;
import feign.jackson.JacksonDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class FeignConfig {
//
//    @Bean
//    public Encoder feignEncoder() {
//        return new JacksonEncoder();
//    }
//
//    @Bean
//    public Decoder feignDecoder() {
//        return new OptionalDecoder(new JacksonDecoder());
//    }
//}