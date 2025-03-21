package com.example.hospitals.Controller;

import com.example.hospitals.DTO.DistanceRequestDTO;
import com.example.hospitals.DTO.HospitalWithDistanceDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Service.HospitalService;
import com.example.hospitals.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/hospitals")
@Tag(name = "HOSPITALCONTROLLER", description = "Controller API HOSPITAL")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private UserService userService;

    /* Endpoint pour authentifier l'hôpital et récupérer le token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        String token = userService.authenticate(username, password);
        if (token != null) {
            return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
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


    @PostMapping(value = "/add", consumes = "text/plain", produces = "application/json")
    @Operation(summary = "add", description = "ajout  hopital")
    public Hospital addHospital(@RequestHeader("Authorization") String token, @RequestBody String rawText) throws IOException {
        // Instanciation directe de ObjectMapper dans la méthode
        ObjectMapper objectMapper = new ObjectMapper();
        Hospital hospital = objectMapper.readValue(rawText, Hospital.class);

        // Sauvegarde dans le service
        return hospitalService.addHospital(hospital);
    }

    @PostMapping(value = "/detail/{id}")
    @Operation(summary = "detail", description = "detail pour fiche hopital")
    public Hospital detailHospital(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Hospital hospitalDetails) {
        return hospitalService.detailHospital(id, hospitalDetails);
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
    @Operation(summary = "searchCiretera", description = "recherche par critère")
    public List<HospitalWithDistanceDTO> getHospitalsWithDistance(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer minBeds,
            @RequestParam(required = false) String specialtyName,
            @RequestParam double refLat,
            @RequestParam double refLng) {
        return hospitalService.findHospitalsWithDistance(minBeds, specialtyName, refLat, refLng);
    }
    @PostMapping("/distance")
    @Operation(summary = "distance", description = "appel calcul distance")//  Passe en POST
    public ResponseEntity<Double> calculateDistance(@RequestBody DistanceRequestDTO request) {
        double distance = hospitalService.getDistance(
                request.getLatitudeFrom(),
                request.getLongitudeFrom(),
                request.getLatitudeTo(),
                request.getLongitudeTo()
        );
        return ResponseEntity.ok(distance);
    }

    @GetMapping("/getAll")
    @Operation(summary = "getAll", description = "récupère la liste de tous les hopitaux")
    public ResponseEntity<List<Hospital>> getAllHospitals( @RequestHeader("Authorization") String token) {
        List<Hospital> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(hospitals);
    }
}
