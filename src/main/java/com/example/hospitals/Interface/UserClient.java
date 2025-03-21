package com.example.hospitals.Interface;

import com.example.hospitals.Config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Client Feign pour le service d'authentification et de gestion des utilisateurs.
 * <p>
 * Ce client se connecte à l'API des utilisateurs hébergée à l'adresse
 * <code>http://localhost:8082/api/users</code> et utilise la configuration fournie par
 * {@link FeignClientInterceptor} pour gérer les aspects liés à l'interception des requêtes.
 * </p>
 * <p>
 * La méthode implémentée dans cette interface permet d'effectuer une authentification en envoyant
 * une requête POST vers l'endpoint <code>/login</code>. Elle prend en charge un objet JSON sous forme de
 * {@code Map<String, String>} contenant les identifiants (credentials) et renvoie une réponse
 * encapsulant le token d'authentification dans une {@link ResponseEntity}.
 * </p>
 * <p>
 * </p>
 *
 * @see FeignClientInterceptor
 */
@FeignClient(name = "users", url = "http://localhost:8082/api/users", configuration = FeignClientInterceptor.class)
public interface UserClient {

    @PostMapping("/login")
    ResponseEntity<Map<String, String>> getToken(@RequestBody Map<String, String> credentials);
}
