package bf.depeche226.controller;

import bf.depeche226.dto.InscriptionRequest;
import bf.depeche226.dto.LoginRequest;
import bf.depeche226.dto.LoginResponse;
import bf.depeche226.dto.UtilisateurResponse;
import bf.depeche226.service.AuthService;
import bf.depeche226.service.UtilisateurInscritService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Lombok génère automatiquement le constructeur avec ces deux champs
public class AuthController {

    private final UtilisateurInscritService utilisateurInscritService;
    private final AuthService authService;

    // Endpoint d'inscription
    @PostMapping("/inscription")
    public ResponseEntity<UtilisateurResponse> inscrire(@Valid @RequestBody InscriptionRequest request) {
        UtilisateurResponse response = utilisateurInscritService.inscrire(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint de connexion
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.connecter(request);
        return ResponseEntity.ok(response);
    }
}