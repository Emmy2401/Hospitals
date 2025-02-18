package com.example.hospitals.Repository;

import com.example.hospitals.Entity.SubSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubSpecialtyRepository extends JpaRepository<SubSpecialty, Long> {
    Optional<SubSpecialty> findByName(String name);
}

