package com.example.hospitals.Interface;
//import com.example.hospitals.Config.FeignConfig;
import com.example.hospitals.DTO.DistanceRequestDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "distance", url = "http://localhost:8081/api/distance")
public interface DistanceClient {
    //implémentation local dans un premier temps mais grâce à l'interface on pourra rempalcer cela plus tard
   // Double calculateDistance(double refLat, double refLng, double targetLat, double targetLng);
    //@PostMapping("/distance")
    @RequestMapping(value = "",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
    Double calculateDistance(@RequestBody DistanceRequestDTO request);

}
