package com.example.hospitals.Service;

import com.example.hospitals.Config.ResourceNotFoundException;
import com.example.hospitals.DTO.DistanceRequestDTO;
import com.example.hospitals.DTO.HospitalWithDistanceDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Interface.DistanceClient;
import com.example.hospitals.Repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public List<HospitalWithDistanceDTO> findHospitalsWithDistance(
            Integer minBeds,
            String specialtyName,
            double refLat,
            double refLng
    ) {
        // 1. Récupération des hôpitaux
        //    Si tu as besoin de filtrer sur minBeds, tu peux utiliser la méthode correspondante ;
        //    sinon, tu les récupères tous.
        List<Hospital> hospitals;
        if (minBeds != null) {
            hospitals = hospitalRepository.findByNumberOfBedsGreaterThanEqual(minBeds);
        } else {
            hospitals = hospitalRepository.findAll();
        }

        // 2. Filtrer en mémoire sur la spécialité, s’il y en a une
        if (specialtyName != null && !specialtyName.isEmpty()) {
            // Filtrage sur la liste des spécialités dans l’objet Hospital
            // Suppose que hospital.getSpecialties() est une List<String> ou List<Specialty>
            hospitals = hospitals.stream()
                    .filter(hospital -> hospital.getSpecialties().stream()
                            .anyMatch(spe -> spe.getName().equalsIgnoreCase(specialtyName))
                    )
                    .collect(Collectors.toList());
        }

        // 3. Calculer la distance et mapper les résultats en DTO
        return hospitals.stream()
                .map(hospital -> {
                    DistanceRequestDTO request = new DistanceRequestDTO(
                            refLat,
                            refLng,
                            hospital.getLatitude(),
                            hospital.getLongitude()
                    );

                    double distance = distanceClient.calculateDistance(request) / 1000;

                    return new HospitalWithDistanceDTO(
                            hospital.getName(),
                            hospital.getNumberOfBeds(),
                            hospital.getSpecialties(),
                            distance
                    );
                })
                // 4. (Optionnel) Trier par distance la liste
                .sorted(Comparator.comparingDouble(HospitalWithDistanceDTO::getDistance))
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

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

}
