package com.example.hospitals;

import com.example.hospitals.DTO.HospitalWithDistanceDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Repository.HospitalRepository;
import com.example.hospitals.Service.HospitalService;
import com.example.hospitals.Config.ResourceNotFoundException;
import com.example.hospitals.Interface.DistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class HospitalUnitTest {
    @InjectMocks
    private HospitalService hospitalService;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private DistanceService distanceService;
    private Hospital existingHospital;
    private Hospital anotherHospital;

    public HospitalUnitTest() {
        MockitoAnnotations.openMocks(this);
    }
    @BeforeEach
    void setUp() {
        existingHospital = new Hospital();
        existingHospital.setId(1L);
        existingHospital.setName("Old Hospital");
        existingHospital.setLatitude(48.8566);
        existingHospital.setLongitude(2.3522);
        existingHospital.setNumberOfBeds(300);

        anotherHospital = new Hospital();
        anotherHospital.setId(2L);
        anotherHospital.setName("New Hospital"); // Nom déjà existant
    }
    @Test
    public void testAddHospital() {
        Hospital hospital = new Hospital();
        hospital.setName("Central Hospital");
        hospital.setLatitude(48.8566);
        hospital.setLongitude(2.3522);
        hospital.setNumberOfBeds(300);

        when(hospitalRepository.save(hospital)).thenReturn(hospital);

        Hospital result = hospitalService.addHospital(hospital);

        assertNotNull(result);
        assertEquals("Central Hospital", result.getName());
        verify(hospitalRepository, times(1)).save(hospital);
    }
    @Test
    public void testAddHospital_NameAlreadyExists() {
        Hospital hospital = new Hospital();
        hospital.setName("Central Hospital");
        hospital.setLatitude(48.8566);
        hospital.setLongitude(2.3522);
        hospital.setNumberOfBeds(300);
        // Simuler qu'un hôpital avec ce nom existe déjà
        when(hospitalRepository.existsByName("Central Hospital")).thenReturn(true);

        // Vérifier que l'ajout d'un hôpital avec le même nom lève une exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalService.addHospital(hospital);
        });

        assertEquals("A hospital with this name already exists.", exception.getMessage());

        // Vérifier que save() n'est jamais appelé puisque l'ajout est refusé
        verify(hospitalRepository, times(1)).existsByName("Central Hospital");
        verify(hospitalRepository, never()).save(any(Hospital.class));
    }
    @Test
    public void testUpdateHospital_Success_SameName() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));
        when(hospitalRepository.save(existingHospital)).thenAnswer(invocation -> invocation.getArgument(0));

        Hospital result = hospitalService.updateHospital(1L, existingHospital);

        assertNotNull(result);
        assertEquals("Old Hospital", result.getName()); // Pas de changement de nom
        verify(hospitalRepository, times(1)).findById(1L);
        verify(hospitalRepository, times(1)).save(existingHospital);
    }

    @Test
    public void testUpdateHospital_Success_NewUniqueName() {
        Hospital updatedDetails = new Hospital();
        updatedDetails.setName("Unique Hospital");
        updatedDetails.setLatitude(48.8600);
        updatedDetails.setLongitude(2.3500);
        updatedDetails.setNumberOfBeds(350);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));
        when(hospitalRepository.existsByName("Unique Hospital")).thenReturn(false);
        when(hospitalRepository.save(existingHospital)).thenAnswer(invocation -> invocation.getArgument(0));

        Hospital result = hospitalService.updateHospital(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Unique Hospital", result.getName());
        verify(hospitalRepository, times(1)).findById(1L);
        verify(hospitalRepository, times(1)).existsByName("Unique Hospital");
        verify(hospitalRepository, times(1)).save(existingHospital);
    }

    @Test
    public void testUpdateHospital_Fail_NameAlreadyExists() {
        Hospital updatedDetails = new Hospital();
        updatedDetails.setName("New Hospital"); // Nom déjà utilisé par un autre hôpital
        updatedDetails.setLatitude(48.8600);
        updatedDetails.setLongitude(2.3500);
        updatedDetails.setNumberOfBeds(350);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));
        when(hospitalRepository.existsByName("New Hospital")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalService.updateHospital(1L, updatedDetails);
        });

        assertEquals("A hospital with this name already exists.", exception.getMessage());

        verify(hospitalRepository, times(1)).findById(1L);
        verify(hospitalRepository, times(1)).existsByName("New Hospital");
        verify(hospitalRepository, never()).save(any(Hospital.class));
    }

    @Test
    public void testUpdateHospital_NotFound() {
        when(hospitalRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> hospitalService.updateHospital(999L, new Hospital()));

        assertEquals("Hospital with id 999 not found", exception.getMessage());
        verify(hospitalRepository, times(1)).findById(999L);
        verify(hospitalRepository, never()).save(any(Hospital.class));
    }

    @Test
    public void testFindByName_Exist() {
        Hospital hospital = new Hospital();
        hospital.setName("City Hospital");

        when(hospitalRepository.findByName("City Hospital")).thenReturn(Optional.of(hospital));

        Hospital result = hospitalService.findByName("City Hospital");

        assertNotNull(result);
        assertEquals("City Hospital", result.getName());
        verify(hospitalRepository, times(1)).findByName("City Hospital");
    }

    @Test
    public void testFindByName_NotFound() {
        when(hospitalRepository.findByName("Unknown Hospital")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> hospitalService.findByName("Unknown Hospital"));

        assertEquals("Hospital not found with name: Unknown Hospital", exception.getMessage());
        verify(hospitalRepository, times(1)).findByName("Unknown Hospital");
    }

    @Test
    public void testFindHospitalsWithDistance() {
        List<Hospital> hospitals = new ArrayList<>();
        Hospital hospital1 = new Hospital();
        hospital1.setName("Hospital A");
        hospital1.setLatitude(48.8566);
        hospital1.setLongitude(2.3522);
        hospital1.setNumberOfBeds(200);
        hospitals.add(hospital1);

        when(hospitalRepository.findByNumberOfBedsGreaterThanEqual(100)).thenReturn(hospitals);
        when(distanceService.calculateDistance(48.8566, 2.3522, 48.8566, 2.3522)).thenReturn(0.0);

        List<HospitalWithDistanceDTO> result = hospitalService.findHospitalsWithDistance(100, null, 48.8566, 2.3522);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hospital A", result.get(0).getName());
        assertEquals(0.0, result.get(0).getDistance());
        verify(hospitalRepository, times(1)).findByNumberOfBedsGreaterThanEqual(100);
        verify(distanceService, times(1)).calculateDistance(48.8566, 2.3522, 48.8566, 2.3522);
    }


    @Test
    public void testFindById_Exist() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        Hospital result = hospitalService.FindById(1L);
        assertNotNull(result);
        assertEquals(hospital.getId(), result.getId());
        verify(hospitalRepository, times(1)).findById(1L);
    }

    @Test public void testFindById_NotFound() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> hospitalService.FindById(1L));

        assertEquals("Hospital with id " + 1L +" not found", exception.getMessage());
        verify(hospitalRepository, times(1)).findById(1L);
    }
}
