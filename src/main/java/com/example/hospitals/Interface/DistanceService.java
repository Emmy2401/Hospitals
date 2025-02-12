package com.example.hospitals.Interface;

public interface DistanceService {
    //implémentation local dans un premier temps mais grâce à l'interface on pourra rempalcer cela plus tard
    Double calculateDistance(double refLat, double refLng, double targetLat, double targetLng);
}
