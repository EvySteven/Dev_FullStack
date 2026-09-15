package bf.depeche226.controller;

import bf.depeche226.dto.ChangerMotDePasseRequest;
import bf.depeche226.dto.ModifierProfilRequest;
import bf.depeche226.dto.UtilisateurResponse;
import bf.depeche226.service.UtilisateurInscritService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurInscritService utilisateurInscritService;

    // Modifie le profil de l'utilisateur actuellement connecté
    // L'email est récupéré depuis le token JWT (via le contexte de sécurité), jamais depuis le body
    @PutMapping("/profil")
    public ResponseEntity<UtilisateurResponse> modifierProfil(
            Authentication authentication,
            @Valid @RequestBody ModifierProfilRequest request) {

        String email = authentication.getName();
        UtilisateurResponse response = utilisateurInscritService.modifierProfil(email, request);
        return ResponseEntity.ok(response);
    }

    // Change le mot de passe de l'utilisateur actuellement connecté
    @PutMapping("/mot-de-passe")
    public ResponseEntity<Void> changerMotDePasse(
            Authentication authentication,
            @Valid @RequestBody ChangerMotDePasseRequest request) {

        String email = authentication.getName();
        utilisateurInscritService.changerMotDePasse(email, request);
        return ResponseEntity.noContent().build();
    }

    // Déconnexion : avec un JWT stateless, il n'y a rien à invalider côté serveur.
    // Le client doit simplement supprimer le token de son stockage local.
    // Cet endpoint existe pour la cohérence de l'API (et pourra gérer une liste noire de tokens plus tard).
    @PostMapping("/deconnexion")
    public ResponseEntity<Void> deconnexion() {
        return ResponseEntity.ok().build();
    }
}