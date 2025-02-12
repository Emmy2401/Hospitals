package com.example.hospitals.Controller;

import com.example.hospitals.DTO.HospitalWithDistanceDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Service.HospitalService;
import com.example.hospitals.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hospitals")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String token = userService.authenticate(credentials.get("username"), credentials.get("password"));

        if (token != null) {
            return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
    }

    /**
     * Endpoint pour récupérer le token stocké.
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken() {
        String token = userService.getToken();
        if (token != null) {
            return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "No token available"));
    }


    @PostMapping
    public Hospital addHospital(@RequestHeader("Authorization") String token,@RequestBody Hospital hospital) {
        return hospitalService.addHospital(hospital);
    }

    @PutMapping(value = "/{id}")
    public Hospital updateHospital(@RequestHeader("Authorization") String token,@PathVariable Long id, @RequestBody Hospital hospitalDetails) {
        return hospitalService.updateHospital(id, hospitalDetails);
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<Hospital> getHospitalByName(@RequestHeader("Authorization") String token,@PathVariable String name) {
        Hospital hospital = hospitalService.findByName(name);
        return ResponseEntity.ok(hospital);
    }
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<Hospital> getHospitalById(@RequestHeader("Authorization") String token,@PathVariable Long id) {
        Hospital hospital = hospitalService.FindById(id);
        return ResponseEntity.ok(hospital);
    }

    @GetMapping("/searchCriteria")
    public List<HospitalWithDistanceDTO> getHospitalsWithDistance(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer minBeds,
            @RequestParam(required = false) String specialtyName,
            @RequestParam double refLat,
            @RequestParam double refLng) {
        return hospitalService.findHospitalsWithDistance(minBeds, specialtyName, refLat, refLng);
    }
}
