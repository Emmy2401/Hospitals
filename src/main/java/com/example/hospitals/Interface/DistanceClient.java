package com.example.hospitals.Interface;
//import com.example.hospitals.Config.FeignConfig;
import com.example.hospitals.DTO.DistanceRequestDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Client Feign pour le service de calcul de distance.
 * <p>
 * Cette interface permet de communiquer avec le service de distance accessible à l'URL
 * <code>http://localhost:8081/api/distance</code>. Elle offre une abstraction à OSRM
 * </p>
 * <p>
 * La méthode {@link #calculateDistance(DistanceRequestDTO)} envoie une requête POST contenant un objet JSON
 * conforme à {@code DistanceRequestDTO}. La réponse renvoyée est un {@link Double} représentant la distance
 * calculée entre deux points géographiques.
 * </p>
 *
 * @see DistanceRequestDTO
 */
@FeignClient(name = "distance", url = "http://localhost:8081/api/distance")
public interface DistanceClient {

    @RequestMapping(value = "",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
    Double calculateDistance(@RequestBody DistanceRequestDTO request);

}
