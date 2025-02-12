package com.example.hospitals.Repository;

import com.example.hospitals.Entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
