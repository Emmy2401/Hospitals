package com.example.hospitals.Service;

import com.example.hospitals.Config.ResourceNotFoundException;
import com.example.hospitals.DTO.DistanceRequestDTO;
import com.example.hospitals.DTO.HospitalWithDistanceDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Interface.DistanceClient;
import com.example.hospitals.Repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    private final DistanceClient distanceClient;
    public HospitalService(DistanceClient distanceClient) {
        this.distanceClient = distanceClient;
    }

    public Hospital addHospital(Hospital hospital) {
        if (hospital == null) {
            throw new IllegalArgumentException("Hospital cannot be null");
        }

        // Vérifier si le nom existe déjà
        if (hospitalRepository.existsByName(hospital.getName())) {
            throw new IllegalArgumentException("A hospital with this name already exists.");
        }

        return hospitalRepository.save(hospital);
    }

    public Hospital updateHospital(Long id, Hospital hospitalDetails) {
        if (hospitalDetails == null) {
            throw new IllegalArgumentException("Hospital details cannot be null");
        }

        return hospitalRepository.findById(id)
                .map(existingHospital -> {
                    // Vérifie si le nom est changé et existe déjà pour un autre hôpital
                    if (!existingHospital.getName().equals(hospitalDetails.getName()) &&
                            hospitalRepository.existsByName(hospitalDetails.getName())) {
                        throw new IllegalArgumentException("A hospital with this name already exists.");
                    }

                    existingHospital.setName(hospitalDetails.getName());
                    existingHospital.setLatitude(hospitalDetails.getLatitude());
                    existingHospital.setLongitude(hospitalDetails.getLongitude());
                    existingHospital.setNumberOfBeds(hospitalDetails.getNumberOfBeds());

                    return hospitalRepository.save(existingHospital);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Hospital with id " + id + " not found"));
    }


    public Hospital findByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return hospitalRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with name: " + name));
    }

    public List<HospitalWithDistanceDTO> findHospitalsWithDistance(Integer minBeds, String specialtyName, double refLat, double refLng) {
        List<Hospital> hospitals;

        if (minBeds != null && specialtyName != null && !specialtyName.isEmpty()) {
            hospitals = hospitalRepository.findByNumberOfBedsAndSpecialtiesName(minBeds, specialtyName);
        } else if (minBeds != null) {
            hospitals = hospitalRepository.findByNumberOfBedsGreaterThanEqual(minBeds);
        } else if (specialtyName != null && !specialtyName.isEmpty()) {
            hospitals = hospitalRepository.findBySpecialtiesName(specialtyName);
        } else {
            hospitals = hospitalRepository.findAll();
        }

        return hospitals.stream()
                .map(hospital -> {
                    // Utilisation de DistanceRequestDTO
                    DistanceRequestDTO request = new DistanceRequestDTO(
                            refLat, refLng, hospital.getLatitude(), hospital.getLongitude()
                    );

                    //  Appel correct de Feign Client avec DTO
                    double distance = distanceClient.calculateDistance(request);

                    return new HospitalWithDistanceDTO(hospital.getName(), hospital.getNumberOfBeds(), specialtyName, distance);
                })
                .collect(Collectors.toList());
    }

    public Hospital FindById(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return hospitalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hospital with id " + id + " not found"));

    }
    public double getDistance(Double latitudeFrom, Double longitudeFrom, Double latitudeTo, Double longitudeTo) {
        DistanceRequestDTO request = new DistanceRequestDTO(latitudeFrom, longitudeFrom, latitudeTo, longitudeTo);
        return distanceClient.calculateDistance(request); //  Envoie le DTO au lieu d'une Map
    }
}
