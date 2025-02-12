package com.example.hospitals.Service;
import com.example.hospitals.Interface.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserClient userClient;
    private String jwtToken; // Stocke le token JWT

    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    /**
     * Authentifie l'hôpital auprès de Users et stocke le token JWT.
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
                jwtToken = response.getBody().get("token");  // 🔥 Stocke le token JWT
                return jwtToken;
            }
        } catch (Exception e) {
            System.err.println("Échec de l'authentification : " + e.getMessage());
        }
        return null;  // 🔥 Retourne null si login échoue
    }

    /**
     * Récupère le token JWT stocké.
     * @return Token JWT ou null si non authentifié
     */
    public String getToken() {
        return jwtToken;
    }
}
