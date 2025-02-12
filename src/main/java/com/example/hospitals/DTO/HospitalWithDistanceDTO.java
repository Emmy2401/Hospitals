package com.example.hospitals.DTO;

public class HospitalWithDistanceDTO {
    private String name;
    private Integer numberOfBeds;
    private String specialtyName;
    private Double distance; // Distance calculée en kilomètres

    // Constructeurs
    public HospitalWithDistanceDTO(String name, Integer numberOfBeds, String specialtyName, Double distance) {
        this.name = name;
        this.numberOfBeds = numberOfBeds;
        this.specialtyName = specialtyName;
        this.distance = distance;
    }

    // Getters et setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getNumberOfBeds() { return numberOfBeds; }
    public void setNumberOfBeds(int numberOfBeds) { this.numberOfBeds = numberOfBeds; }
    public String getSpecialtyName() { return specialtyName; }
    public void setSpecialtyName(String specialtyName) { this.specialtyName = specialtyName; }
    public Double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
}
