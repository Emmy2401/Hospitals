package com.example.hospitals.DTO;

import com.example.hospitals.Entity.Specialty;

import java.util.List;

public class HospitalWithDistanceDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer numberOfBeds;
    private List<Specialty> specialties;
    private Double distance; // Distance calculée en kilomètres


    public HospitalWithDistanceDTO(Long id, String name, Double latitude, Double longitude, Integer numberOfBeds, List<Specialty> specialties, Double distance) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberOfBeds = numberOfBeds;
        this.specialties = specialties;
        this.distance = distance;
    }

    // Constructeur
    public HospitalWithDistanceDTO(String name, Integer numberOfBeds, List<Specialty> specialties, Double distance) {
        this.name = name;
        this.numberOfBeds = numberOfBeds;
        this.specialties = specialties; // Pas de conversion, on garde la liste brute
        this.distance = distance;
    }



    // Getters et setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getNumberOfBeds() { return numberOfBeds; }
    public void setNumberOfBeds(int numberOfBeds) { this.numberOfBeds = numberOfBeds; }
    public List<Specialty> getSpecialties() { return specialties; }
    public void setSpecialties(List<Specialty> specialties) { this.specialties = specialties; }
    public Double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
}
