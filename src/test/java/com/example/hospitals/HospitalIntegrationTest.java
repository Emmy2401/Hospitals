package com.example.hospitals;

import com.example.hospitals.DTO.HospitalDTO;
import com.example.hospitals.DTO.HospitalWithDistanceDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Repository.HospitalRepository;
import com.example.hospitals.Service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HospitalIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HospitalRepository hospitalRepository;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // Nettoyer la base de données avant chaque test
        hospitalRepository.deleteAll();
    }
    @Test
    public void testAddHospital_OK() throws Exception {
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setName("Hospital Pasteur");
        hospitalDTO.setLatitude(48.8081);
        hospitalDTO.setLongitude(2.1266);
        hospitalDTO.setNumberOfBeds(150);

        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(hospitalDTO.getName()))
                .andExpect(jsonPath("$.latitude").value(hospitalDTO.getLatitude()))
                .andExpect(jsonPath("$.longitude").value(hospitalDTO.getLongitude()))
                .andExpect(jsonPath("$.numberOfBeds").value(hospitalDTO.getNumberOfBeds()));
    }

    @Test
    public void testGetHospitalByName_OK() throws Exception {
        // Ajouter un hôpital
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setName("Hospital Saint Louis");
        hospitalDTO.setLatitude(48.8566);
        hospitalDTO.setLongitude(2.3522);
        hospitalDTO.setNumberOfBeds(200);

        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO)))
                .andExpect(status().isOk());

        // Rechercher l'hôpital par nom
        mockMvc.perform(get("/hospitals/name/{name}", hospitalDTO.getName())
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(hospitalDTO.getName()))
                .andExpect(jsonPath("$.numberOfBeds").value(hospitalDTO.getNumberOfBeds()));
    }

    @Test
    public void testGetHospitalByName_NotFound() throws Exception {
        mockMvc.perform(get("/hospitals/name/{name}",
                        "Unknown Hospital")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Hospital not found with name: Unknown Hospital")));
    }
    @Test
    public void testUpdateHospital_NotFound() throws Exception {
        HospitalDTO updatedHospitalDTO = new HospitalDTO();
        updatedHospitalDTO.setName("Non-existent Hospital");
        updatedHospitalDTO.setLatitude(48.8588);
        updatedHospitalDTO.setLongitude(2.2945);
        updatedHospitalDTO.setNumberOfBeds(250);

        mockMvc.perform(put("/hospitals/9999")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())// ID inexistant
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedHospitalDTO)))
                .andExpect(status().isNotFound()) // Vérification que le statut est 404
                .andExpect(content().string(containsString("Hospital with id 9999 not found")));
    }
    @Test
    public void testUpdateHospital_OK() throws Exception {
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setName("Hôpital Alpha");
        hospitalDTO.setLatitude(48.8566);
        hospitalDTO.setLongitude(2.3522);
        hospitalDTO.setNumberOfBeds(100);

        String createResponse = mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HospitalDTO createdHospital = objectMapper.readValue(createResponse, HospitalDTO.class);
        Long createdId = createdHospital.getId();

        HospitalDTO updatedHospitalDTO = new HospitalDTO();
        updatedHospitalDTO.setName("Hôpital Alpha - Updated");
        updatedHospitalDTO.setLatitude(50.0);
        updatedHospitalDTO.setLongitude(3.0);
        updatedHospitalDTO.setNumberOfBeds(999);


        mockMvc.perform(put("/hospitals/" + createdId)
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedHospitalDTO)))
                .andExpect(status().isOk());


        mockMvc.perform(get("/hospitals/name/" + updatedHospitalDTO.getName())
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hôpital Alpha - Updated"))
                .andExpect(jsonPath("$.latitude").value(50.0))
                .andExpect(jsonPath("$.longitude").value(3.0))
                .andExpect(jsonPath("$.numberOfBeds").value(999));
    }
    @Test
    public void testGetHospitalsWithDistance_OK() throws Exception {
        // Ajouter plusieurs hôpitaux avec des caractéristiques différentes
        HospitalDTO hospitalDTO1 = new HospitalDTO();
        hospitalDTO1.setName("Hopital A"); // Ne devrait pas apparaître (50 lits)
        hospitalDTO1.setLatitude(48.85);
        hospitalDTO1.setLongitude(2.35);
        hospitalDTO1.setNumberOfBeds(50);

        HospitalDTO hospitalDTO2 = new HospitalDTO();
        hospitalDTO2.setName("Hopital B"); // Devrait apparaître (150 lits)
        hospitalDTO2.setLatitude(48.90);
        hospitalDTO2.setLongitude(2.38);
        hospitalDTO2.setNumberOfBeds(150);

        HospitalDTO hospitalDTO3 = new HospitalDTO();
        hospitalDTO3.setName("Hopital C"); // Devrait apparaître (200 lits)
        hospitalDTO3.setLatitude(48.70);
        hospitalDTO3.setLongitude(2.20);
        hospitalDTO3.setNumberOfBeds(200);

        // Ajouter les hôpitaux via l'API POST
        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO3)))
                .andExpect(status().isOk());

        // Vérifier que les hôpitaux sont bien ajoutés dans la base
        List<Hospital> allHospitals = hospitalRepository.findAll();
        assertEquals(3, allHospitals.size(), "Il devrait y avoir 3 hôpitaux dans la base.");

        // Debug: Afficher les hôpitaux en base
        System.out.println("Hôpitaux en base :");
        allHospitals.forEach(h -> System.out.println("Nom : " + h.getName() + ", Lits : " + h.getNumberOfBeds()));

        // Appeler l'API pour récupérer les hôpitaux filtrés (minBeds = 100)
        String response = mockMvc.perform(get("/hospitals/searchCriteria")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .param("refLat", "48.85")
                        .param("refLng", "2.35")
                        .param("minBeds", "100") // Filtrer pour au moins 100 lits
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Désérialiser la réponse JSON en une liste d'objets HospitalWithDistanceDTO
        List<HospitalWithDistanceDTO> results = objectMapper.readValue(
                response, new TypeReference<List<HospitalWithDistanceDTO>>() {});

        // Debug: Afficher les résultats
        System.out.println("Nombre d'hôpitaux trouvés : " + results.size());
        results.forEach(h -> System.out.println("Hôpital : " + h.getName() + ", Lits : " + h.getNumberOfBeds()));

        // Vérifier que la liste contient les hôpitaux attendus
        assertEquals(2, results.size(), "Il devrait y avoir 2 hôpitaux avec au moins 100 lits.");

        // Vérifier que les noms des hôpitaux attendus sont présents dans la liste
        boolean containsHospitalB = results.stream().anyMatch(h -> h.getName().equals("Hopital B"));
        boolean containsHospitalC = results.stream().anyMatch(h -> h.getName().equals("Hopital C"));

        assertTrue(containsHospitalB, "La liste doit contenir 'Hopital B'");
        assertTrue(containsHospitalC, "La liste doit contenir 'Hopital C'");

        // Vérifier que les distances sont calculées (les valeurs exactes peuvent varier)
        results.forEach(hospital -> assertTrue(hospital.getDistance() >= 0,
                "La distance pour " + hospital.getName() + " doit être calculée et >= 0."));
    }

    @Test
    public void testGetHospitalBy_Id_OK() throws Exception {
        // Ajouter un hôpital
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setName("Louis Pasteur");
        hospitalDTO.setLatitude(48.8566);
        hospitalDTO.setLongitude(2.3522);
        hospitalDTO.setNumberOfBeds(200);
        String createResponse  =  mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        HospitalDTO createdHospital = objectMapper.readValue(createResponse, HospitalDTO.class);
        Long createdId = createdHospital.getId();
        // Rechercher l'hôpital par id
        mockMvc.perform(get("/hospitals/id/{id}",createdId)
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(hospitalDTO.getName()))
                .andExpect(jsonPath("$.numberOfBeds").value(hospitalDTO.getNumberOfBeds()));

    }

    @Test
    public void testGetHospitalById_NotFound() throws Exception {
        mockMvc.perform(get("/hospitals/id/{id}", 98L)
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Hospital with id " + 98L + " not found")));
    }
    @Test
    public void testAddHospital_NameAlreadyExists() throws Exception {
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setName("Duplicate Hospital");
        hospitalDTO.setLatitude(48.8081);
        hospitalDTO.setLongitude(2.1266);
        hospitalDTO.setNumberOfBeds(150);

        // Ajouter le premier hôpital
        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO)))
                .andExpect(status().isOk());

        // Tenter d'ajouter un hôpital avec le même nom
        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("A hospital with this name already exists.")));
    }

    @Test
    public void testUpdateHospital_NameAlreadyExists() throws Exception {
        // Ajouter deux hôpitaux distincts
        HospitalDTO hospital1 = new HospitalDTO();
        hospital1.setName("Hospital A");
        hospital1.setLatitude(48.85);
        hospital1.setLongitude(2.35);
        hospital1.setNumberOfBeds(200);

        HospitalDTO hospital2 = new HospitalDTO();
        hospital2.setName("Hospital B");
        hospital2.setLatitude(48.86);
        hospital2.setLongitude(2.36);
        hospital2.setNumberOfBeds(250);

        String response1 = mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospital1)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        HospitalDTO createdHospital1 = objectMapper.readValue(response1, HospitalDTO.class);

        mockMvc.perform(post("/hospitals")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospital2)))
                .andExpect(status().isOk());

        // Essayer de mettre à jour hospital1 avec le nom de hospital2
        HospitalDTO updatedHospitalDTO = new HospitalDTO();
        updatedHospitalDTO.setName("Hospital B"); // Conflit de nom
        updatedHospitalDTO.setLatitude(48.88);
        updatedHospitalDTO.setLongitude(2.38);
        updatedHospitalDTO.setNumberOfBeds(300);

        mockMvc.perform(put("/hospitals/" + createdHospital1.getId())
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedHospitalDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("A hospital with this name already exists.")));
    }
    
}
