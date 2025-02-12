package com.example.hospitals.DTO;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotBlank(message = "Hospital name cannot be blank")
    private String name;

    @NotNull
    private double latitude;
    @NotNull
    private double longitude;
    @Min(0)
    private int numberOfBeds;
    private List<SpecialtyDTO> specialties = new ArrayList<>();

    public HospitalDTO(Long id, String name, double latitude, double longitude, int numberOfBeds) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberOfBeds = numberOfBeds;
    }
    public HospitalDTO() {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(int numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public List<SpecialtyDTO> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtyDTO> specialties) {
        this.specialties = specialties;
    }

    // Embriqué Specialty
    public static class SpecialtyDTO {

        @NotNull
        private Long id;

        @NotNull
        @NotEmpty
        private String name;

        private List<SubSpecialtyDTO> subSpecialties = new ArrayList<>();

        public SpecialtyDTO(Long id, String name) {
            this.id = id;
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

        public List<SubSpecialtyDTO> getSubSpecialties() {
            return subSpecialties;
        }

        public void setSubSpecialties(List<SubSpecialtyDTO> subSpecialties) {
            this.subSpecialties = subSpecialties;
        }
    }

    // Imbriqué  SubSpecialty
    public static class SubSpecialtyDTO {

        @NotNull
        private Long id;

        @NotNull
        @NotEmpty
        private String name;

        public SubSpecialtyDTO(Long id, String name) {
            this.id = id;
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
    }
}
