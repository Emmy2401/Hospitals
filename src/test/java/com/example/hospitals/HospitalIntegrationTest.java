package com.example.hospitals;

import com.example.hospitals.Controller.HospitalController;
import com.example.hospitals.DTO.DistanceRequestDTO;
import com.example.hospitals.DTO.HospitalDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Interface.UserClient;
import com.example.hospitals.Service.HospitalService;
import com.example.hospitals.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = HospitalController.class)
@Import(TestSecurityConfig.class)
public class HospitalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private HospitalService hospitalService;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAddHospital_OK() throws Exception {
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setId(1L);
        hospitalDTO.setName("Hospital Pasteur");
        hospitalDTO.setLatitude(48.8081);
        hospitalDTO.setLongitude(2.1266);
        hospitalDTO.setNumberOfBeds(150);

        Hospital mockedHospital = new Hospital();
        mockedHospital.setId(1L);
        mockedHospital.setName("Hospital Pasteur");
        mockedHospital.setLatitude(48.8081);
        mockedHospital.setLongitude(2.1266);
        mockedHospital.setNumberOfBeds(150);

        when(hospitalService.addHospital(any())).thenReturn(mockedHospital);
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
        Hospital mockedHospital = new Hospital();
        mockedHospital.setId(1L);
        mockedHospital.setName("Hospital Saint Louis");
        mockedHospital.setLatitude(48.8566);
        mockedHospital.setLongitude(2.3522);
        mockedHospital.setNumberOfBeds(200);

        when(hospitalService.findByName("Hospital Saint Louis")).thenReturn(mockedHospital);

        mockMvc.perform(get("/hospitals/name/{name}", "Hospital Saint Louis")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(mockedHospital.getName()))
                .andExpect(jsonPath("$.numberOfBeds").value(mockedHospital.getNumberOfBeds()));
    }

    @Test
    public void testUpdateHospital_OK() throws Exception {
        HospitalDTO updatedHospitalDTO = new HospitalDTO();
        updatedHospitalDTO.setId(1L);
        updatedHospitalDTO.setName("Hôpital Alpha - Updated");
        updatedHospitalDTO.setLatitude(50.0);
        updatedHospitalDTO.setLongitude(3.0);
        updatedHospitalDTO.setNumberOfBeds(999);

        Hospital existingHospital = new Hospital();
        existingHospital.setId(1L);
        existingHospital.setName("Hôpital Alpha");
        existingHospital.setLatitude(48.8566);
        existingHospital.setLongitude(2.3522);
        existingHospital.setNumberOfBeds(100);

        when(hospitalService.updateHospital(eq(1L), any())).thenReturn(existingHospital);

        mockMvc.perform(put("/hospitals/1")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedHospitalDTO)))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetHospitalById_OK() throws Exception {
        Hospital mockedHospital = new Hospital();
        mockedHospital.setId(1L);
        mockedHospital.setName("Louis Pasteur");

        when(hospitalService.FindById(1L)).thenReturn(mockedHospital);

        mockMvc.perform(get("/hospitals/id/1")
                        .header("Authorization", "Etp8iTPEGpWt4Cjbiydwu3ucF1CBmJRCta6wMB2P2jpKOsy2v3Vvie4M5IzsCX9H")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Louis Pasteur"));
    }
    @Test
    public void testCalculateDistance_OK() throws Exception {
        DistanceRequestDTO distanceRequestDTO = new DistanceRequestDTO(48.8566, 2.3522, 48.8588, 2.2945);

        when(hospitalService.getDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(5.5);

        mockMvc.perform(post("/hospitals/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5.5));
    }

}