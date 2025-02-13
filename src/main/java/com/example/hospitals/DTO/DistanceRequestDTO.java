package com.example.hospitals.DTO;

import jakarta.validation.constraints.NotNull;

public class DistanceRequestDTO {
    @NotNull(message = "LatitudeFrom est obligatoire")
    private Double latitudeFrom;
    @NotNull(message = "longitudeFrom est obligatoire")
    private Double longitudeFrom;
    @NotNull(message = "LatitudeTo est obligatoire")
    private Double latitudeTo;
    @NotNull(message = "LongitudeTo est obligatoire")
    private Double longitudeTo;

    public DistanceRequestDTO(double latitudeFrom, double longitudeFrom, double latitudeTo, double longitudeTo) {
    }

    public DistanceRequestDTO() {
    }

    // Getters et Setters
    public Double getLatitudeFrom() {
        return latitudeFrom;
    }

    public void setLatitudeFrom(Double latitudeFrom) {
        this.latitudeFrom = latitudeFrom;
    }

    public Double getLongitudeFrom() {
        return longitudeFrom;
    }

    public void setLongitudeFrom(Double longitudeFrom) {
        this.longitudeFrom = longitudeFrom;
    }

    public Double getLatitudeTo() {
        return latitudeTo;
    }

    public void setLatitudeTo(Double latitudeTo) {
        this.latitudeTo = latitudeTo;
    }

    public Double getLongitudeTo() {
        return longitudeTo;
    }

    public void setLongitudeTo(Double longitudeTo) {
        this.longitudeTo = longitudeTo;
    }
}
