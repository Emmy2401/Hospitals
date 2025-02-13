package com.example.hospitals.Interface;
import com.example.hospitals.DTO.DistanceRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Map;

@FeignClient(name = "distance", url = "http://localhost:8081/api/distance")
public interface DistanceClient {
    //implémentation local dans un premier temps mais grâce à l'interface on pourra rempalcer cela plus tard
   // Double calculateDistance(double refLat, double refLng, double targetLat, double targetLng);
    @PostMapping("/distance")
    double calculateDistance(@RequestBody DistanceRequestDTO request);
}
