package com.depeche226.dto.auth;

// Je renvoie le token et les infos minimales apres une connexion reussie.
public record AuthResponse(String token, String type, String email, String pseudonyme) {
    public static AuthResponse of(String token, String email, String pseudonyme) {
        return new AuthResponse(token, "Bearer", email, pseudonyme);
    }
}
