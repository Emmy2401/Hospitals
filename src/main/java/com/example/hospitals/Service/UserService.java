package com.example.hospitals.Service;
import com.example.hospitals.Interface.UserClient;
import com.example.hospitals.Provider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
public class UserService {

    private final UserClient userClient;
    private final JwtTokenProvider jwtTokenProvider; // Service pour stocker le token

    public UserService(UserClient userClient, JwtTokenProvider jwtTokenProvider) {
        this.userClient = userClient;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authentifie l'hôpital auprès du service Users et stocke le token JWT.
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return Le token JWT ou null en cas d'échec
     */
    public String authenticate(String username, String password) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        try {
            ResponseEntity<Map<String, String>> response = userClient.getToken(credentials);

            if (response.getBody() != null && response.getBody().containsKey("token")) {
                String jwtToken = response.getBody().get("token");
                jwtTokenProvider.setToken(jwtToken);  // 🔥 Stocke le token
                return jwtToken;
            } else {
                System.err.println("Réponse invalide : le token est absent.");
            }
        } catch (Exception e) {
            System.err.println("Échec de l'authentification : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère le token JWT stocké.
     * @return Token JWT ou null si non authentifié
     */
    public String getToken() {
        return jwtTokenProvider.getToken();
    }
}