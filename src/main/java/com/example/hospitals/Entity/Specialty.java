package com.example.hospitals.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    @ManyToMany(mappedBy = "specialties")
    private List<Hospital> hospitals = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubSpecialty> subSpecialties = new ArrayList<>();

    // Constructors
    public Specialty() {}

    public Specialty(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubSpecialty> getSubSpecialties() {
        return subSpecialties;
    }

    public void setSubSpecialties(List<SubSpecialty> subSpecialties) {
        this.subSpecialties = subSpecialties;
    }
}
