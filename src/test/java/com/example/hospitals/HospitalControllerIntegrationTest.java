package com.example.hospitals;

import com.example.hospitals.DTO.DistanceRequestDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Repository.HospitalRepository;
import com.example.hospitals.Service.HospitalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testUser", roles = {"USER"})
public class HospitalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalService hospitalService;



    /**
     *  Tester la récupération d'un hôpital par son ID (GET /id/{id})
     */
    @Test
    void testGetHospitalById_Exists() throws Exception {
        // On crée un hospital via le repository (ou via un service).
        Hospital hospital = new Hospital();
        hospital.setName("TestHospital");
        hospital.setLatitude(10.0);
        hospital.setLongitude(20.0);
        hospital.setNumberOfBeds(50);

        // On sauvegarde en base
        hospital = hospitalRepository.save(hospital);

        // On lance la requête GET
        mockMvc.perform(get("/hospitals/id/" + hospital.getId())
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHVzZXIuZnIiLCJpYXQiOjE3NDAwNjI1NDUsImV4cCI6MTc0MDE0ODk0NX0.HDSGaPTqSOPtYGa577GME8TcegRUhT-jKwSY2Uf6UEE")  // ou tout autre token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(hospital.getId()))
                .andExpect(jsonPath("$.name").value("TestHospital"))
                .andExpect(jsonPath("$.latitude").value(10.0))
                .andExpect(jsonPath("$.longitude").value(20.0))
                .andExpect(jsonPath("$.numberOfBeds").value(50));
    }

    @Test
    void testGetHospitalById_NotFound() throws Exception {
        // ID qui n’existe pas
        long nonExistentId = 9999L;

        mockMvc.perform(get("/hospitals/id/" + nonExistentId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHVzZXIuZnIiLCJpYXQiOjE3NDAwNjI1NDUsImV4cCI6MTc0MDE0ODk0NX0.HDSGaPTqSOPtYGa577GME8TcegRUhT-jKwSY2Uf6UEE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        // À toi de décider le message/format d’erreur géré par ton exception
        // .andExpect(content().string("Some error message"));
    }
    

    /**
     *  Tester le POST /add (consumes = "text/plain")
     */
    @Test
    void testAddHospital() throws Exception {
        // On crée un JSON (sous forme de String brut) correspondant à un Hospital
        // (le controller consomme "text/plain", mais le contenu reste un JSON)
        String rawHospitalJson = """
            {
              "name": "NewHospital",
              "latitude": 55.0,
              "longitude": 44.0,
              "numberOfBeds": 99
            }
            """;

        // On envoie la requête
        mockMvc.perform(post("/hospitals/add")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHVzZXIuZnIiLCJpYXQiOjE3NDAwNjI1NDUsImV4cCI6MTc0MDE0ODk0NX0.HDSGaPTqSOPtYGa577GME8TcegRUhT-jKwSY2Uf6UEE")
                        .contentType(MediaType.TEXT_PLAIN)  // important car c’est ce que consomme le @PostMapping
                        .content(rawHospitalJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())  // l’ID doit être généré
                .andExpect(jsonPath("$.name").value("NewHospital"))
                .andExpect(jsonPath("$.latitude").value(55.0))
                .andExpect(jsonPath("$.longitude").value(44.0))
                .andExpect(jsonPath("$.numberOfBeds").value(99));

        // Vérif en base
        List<Hospital> hospitals = hospitalRepository.findAll();
        assertThat(hospitals).hasSize(1);
        assertThat(hospitals.get(0).getName()).isEqualTo("NewHospital");
    }

}

