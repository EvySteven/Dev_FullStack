package bf.depeche226.controller;

import bf.depeche226.dto.InscriptionRequest;
import bf.depeche226.dto.ModifierProfilRequest;
import bf.depeche226.dto.UtilisateurResponse;
import bf.depeche226.service.AdministrateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/administration/comptes")
@RequiredArgsConstructor
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    @PostMapping("/redacteurs")
    public ResponseEntity<UtilisateurResponse> creerRedacteur(@Valid @RequestBody InscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administrateurService.creerCompteRedacteur(request));
    }

    @PostMapping("/moderateurs")
    public ResponseEntity<UtilisateurResponse> creerModerateur(@Valid @RequestBody InscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administrateurService.creerCompteModerateur(request));
    }

    @PostMapping("/administrateurs")
    public ResponseEntity<UtilisateurResponse> creerAdministrateur(@Valid @RequestBody InscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administrateurService.creerCompteAdministrateur(request));
    }

    // Modifier n'importe quel compte par son id (Lecteur, Rédacteur, Modérateur, Administrateur)
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ModifierProfilRequest request) {
        return ResponseEntity.ok(administrateurService.modifierCompte(id, request));
    }

    // Supprimer (anonymiser) n'importe quel compte par son id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        administrateurService.supprimerCompte(id);
        return ResponseEntity.noContent().build();
    }
}