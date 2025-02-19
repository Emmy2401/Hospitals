package com.example.hospitals.Service;

import com.example.hospitals.Config.ResourceNotFoundException;
import com.example.hospitals.DTO.DistanceRequestDTO;
import com.example.hospitals.DTO.HospitalWithDistanceDTO;
import com.example.hospitals.Entity.Hospital;
import com.example.hospitals.Entity.Specialty;
import com.example.hospitals.Entity.SubSpecialty;
import com.example.hospitals.Interface.DistanceClient;
import com.example.hospitals.Repository.HospitalRepository;
import com.example.hospitals.Repository.SpecialtyRepository;
import com.example.hospitals.Repository.SubSpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private SubSpecialtyRepository subSpecialtyRepository; ;

    private final DistanceClient distanceClient;
    public HospitalService(DistanceClient distanceClient) {
        this.distanceClient = distanceClient;
    }

    public Hospital addHospital(Hospital hospital) {
        if (hospital == null) {
            throw new IllegalArgumentException("Hospital cannot be null");
        }

        // Vérifier si l'hôpital existe déjà
        if (hospitalRepository.existsByName(hospital.getName())) {
            throw new IllegalArgumentException("A hospital with this name already exists.");
        }

        // Vérifier et enregistrer les spécialités sans se prendre la tête
        List<Specialty> existingSpecialties = new ArrayList<>();

        for (Specialty specialty : hospital.getSpecialties()) {
            Specialty existingSpecialty = specialtyRepository.findByName(specialty.getName()).orElse(null);

            if (existingSpecialty == null) {
                // Forcer l'enregistrement si elle n'existe pas
                existingSpecialty = specialtyRepository.save(new Specialty(specialty.getName()));
            }

            // Ajouter directement les sous-spécialités sans trop chercher
            for (SubSpecialty sub : specialty.getSubSpecialties()) {
                if (!existingSpecialty.getSubSpecialties().contains(sub)) {
                    sub.setSpecialty(existingSpecialty);
                    existingSpecialty.getSubSpecialties().add(sub);
                }
            }

            // Sauvegarde un peu sale, mais qui fonctionne
            existingSpecialty = specialtyRepository.save(existingSpecialty);

            if (!existingSpecialties.contains(existingSpecialty)) {
                existingSpecialties.add(existingSpecialty);
            }
        }

        hospital.setSpecialties(existingSpecialties);

        return hospitalRepository.save(hospital);
    }

    public Hospital updateHospital(Long id, Hospital hospitalDetails) {
        if (hospitalDetails == null) {
            throw new IllegalArgumentException("Hospital details cannot be null");
        }

        return hospitalRepository.findById(id)
                .map(existingHospital -> {
                    // Vérifier si le nom est modifié et existe déjà pour un autre hôpital
                    if (!existingHospital.getName().equals(hospitalDetails.getName()) &&
                            hospitalRepository.existsByName(hospitalDetails.getName())) {
                        throw new IllegalArgumentException("A hospital with this name already exists.");
                    }

                    //  Mise à jour des informations générales
                    existingHospital.setName(hospitalDetails.getName());
                    existingHospital.setLatitude(hospitalDetails.getLatitude());
                    existingHospital.setLongitude(hospitalDetails.getLongitude());
                    existingHospital.setNumberOfBeds(hospitalDetails.getNumberOfBeds());

                    //  Gestion des spécialités
                    List<Specialty> updatedSpecialties = new ArrayList<>();

                    for (Specialty specialty : hospitalDetails.getSpecialties()) {
                        // Vérifier si la spécialité existe déjà
                        Specialty existingSpecialty = specialtyRepository.findByName(specialty.getName())
                                .orElseGet(() -> specialtyRepository.save(new Specialty(specialty.getName())));

                        //  Mise à jour des sous-spécialités sans vider la liste
                        Set<SubSpecialty> existingSubSpecialties = new HashSet<>(existingSpecialty.getSubSpecialties());
                        Set<SubSpecialty> updatedSubSpecialties = new HashSet<>();

                        for (SubSpecialty sub : specialty.getSubSpecialties()) {
                            SubSpecialty existingSubSpecialty = subSpecialtyRepository.findByName(sub.getName())
                                    .orElseGet(() -> subSpecialtyRepository.save(new SubSpecialty(sub.getName())));

                            existingSubSpecialty.setSpecialty(existingSpecialty);
                            updatedSubSpecialties.add(existingSubSpecialty);
                        }

                        // Comparer les sous-spécialités avant d'appliquer la mise à jour
                        if (!existingSubSpecialties.equals(updatedSubSpecialties)) {
                            existingSpecialty.getSubSpecialties().clear();
                            existingSpecialty.getSubSpecialties().addAll(updatedSubSpecialties);
                        }

                        updatedSpecialties.add(existingSpecialty);
                    }

                    //  Mise à jour propre des spécialités sans `orphanRemoval` involontaire
                    existingHospital.getSpecialties().clear();
                    existingHospital.getSpecialties().addAll(updatedSpecialties);

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
        List<Hospital> hospitals;

        if (minBeds != null && specialtyName != null && !specialtyName.isEmpty()) {
            hospitals = hospitalRepository.findByNumberOfBedsGreaterThanEqual(minBeds)
                    .stream()
                    .filter(hospital -> hospital.getSpecialties() != null && !hospital.getSpecialties().isEmpty()) // Exclure ceux sans spécialité
                    .filter(hospital -> hospital.getSpecialties().stream()
                            .anyMatch(specialty -> specialty.getName().equalsIgnoreCase(specialtyName)))
                    .collect(Collectors.toList());
        } else if (minBeds != null) {
            hospitals = hospitalRepository.findByNumberOfBedsGreaterThanEqual(minBeds)
                    .stream()
                    .filter(hospital -> hospital.getSpecialties() != null && !hospital.getSpecialties().isEmpty()) // Exclure ceux sans spécialité
                    .collect(Collectors.toList());
        } else if (specialtyName != null && !specialtyName.isEmpty()) {
            hospitals = hospitalRepository.findBySpecialties_NameIgnoreCase(specialtyName)
                    .stream()
                    .filter(hospital -> hospital.getSpecialties() != null && !hospital.getSpecialties().isEmpty()) // Exclure ceux sans spécialité
                    .collect(Collectors.toList());
        } else {
            hospitals = hospitalRepository.findAll()
                    .stream()
                    .filter(hospital -> hospital.getSpecialties() != null && !hospital.getSpecialties().isEmpty()) // Exclure ceux sans spécialité
                    .collect(Collectors.toList());
        }

        return hospitals.stream()
                .map(hospital -> {
                    DistanceRequestDTO request = new DistanceRequestDTO(
                            refLat,
                            refLng,
                            hospital.getLatitude(),
                            hospital.getLongitude()
                    );

                    double distance = distanceClient.calculateDistance(request) /1000;


                    return new HospitalWithDistanceDTO(
                            hospital.getName(),
                            hospital.getNumberOfBeds(),
                            hospital.getSpecialties(),
                            distance
                    );
                })
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
