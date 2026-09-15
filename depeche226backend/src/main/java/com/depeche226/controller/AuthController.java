package com.depeche226.controller;

import com.depeche226.dto.auth.AuthResponse;
import com.depeche226.dto.auth.LoginRequest;
import com.depeche226.dto.auth.RegisterRequest;
import com.depeche226.dto.user.UserResponse;
import com.depeche226.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// J'ai garde ce controller assez simple: register, login, et me.
// Je prefere eviter les users fake ou les donnees par defaut au demarrage.
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Inscription d'un utilisateur")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.toResponse(userService.register(request)));
    }

    // Ici, je recois le mail et le password, puis je retourne un JWT si tout est ok.
    @Operation(summary = "Connexion utilisateur")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request.email(), request.password()));
    }

    // Je recupere le profil correspondant au token JWT envoye par le client.
    @Operation(summary = "Profil courant", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}
