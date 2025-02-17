package com.example.hospitals.Repository;

import com.example.hospitals.Entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByName(String name);
    Optional<Hospital> findById(Long id);
    List<Hospital> findByNumberOfBedsAndSpecialtiesName(Integer minBeds, String specialtyName);
    List<Hospital> findByNumberOfBedsGreaterThanEqual(Integer minBeds);
    List<Hospital> findBySpecialtiesName(String specialtyName);
    boolean existsByName(String name);
    List<Hospital> findByNumberOfBedsGreaterThanEqualAndSpecialtiesName(Integer minBeds, String specialtyName);
}
