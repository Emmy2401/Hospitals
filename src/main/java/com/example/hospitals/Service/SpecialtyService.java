package com.example.hospitals.Service;

import com.example.hospitals.Entity.Specialty;
import com.example.hospitals.Entity.SubSpecialty;
import com.example.hospitals.Repository.SpecialtyRepository;
import com.example.hospitals.Repository.SubSpecialtyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpecialtyService {
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    SubSpecialtyRepository subSpecialtyRepository;

    public Specialty addSpecialty(Specialty specialty, List<String> subSpecialtyNames) {
        if (specialty == null) {
            throw new IllegalArgumentException("Specialty cannot be null");
        }

        // Vérifier si la spécialité existe déjà
        Optional<Specialty> existingSpecialty = specialtyRepository.findByName(specialty.getName());
        if (existingSpecialty.isPresent()) {
            throw new IllegalArgumentException("A specialty with this name already exists.");
        }

        // Gérer les sous-spécialités
        List<SubSpecialty> subSpecialties = subSpecialtyNames.stream()
                .map(name -> {
                    SubSpecialty sub = subSpecialtyRepository.findByName(name)
                            .orElseGet(() -> subSpecialtyRepository.save(new SubSpecialty(name)));
                    sub.setSpecialty(specialty); // Associer la sous-spécialité à la spécialité
                    return sub;
                })
                .collect(Collectors.toList());

        specialty.setSubSpecialties(subSpecialties);
        return specialtyRepository.save(specialty);
    }
    public Specialty updateSpecialty(Long specialtyId, List<String> newSubSpecialtyNames) {
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        // Supprimer les anciennes sous-spécialités qui ne sont plus présentes
        specialty.getSubSpecialties().removeIf(sub -> !newSubSpecialtyNames.contains(sub.getName()));

        // Ajouter les nouvelles sous-spécialités
        List<SubSpecialty> updatedSubSpecialties = newSubSpecialtyNames.stream()
                .map(name -> {
                    SubSpecialty sub = subSpecialtyRepository.findByName(name)
                            .orElseGet(() -> subSpecialtyRepository.save(new SubSpecialty(name)));
                    sub.setSpecialty(specialty);
                    return sub;
                })
                .collect(Collectors.toList());

        specialty.setSubSpecialties(updatedSubSpecialties);
        return specialtyRepository.save(specialty);
    }
}
