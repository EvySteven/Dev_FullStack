package bf.depeche226.controller;

import bf.depeche226.dto.CommentaireCreateRequest;
import bf.depeche226.dto.CommentaireResponse;
import bf.depeche226.service.CommentaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    // Écrire un commentaire sur un article (authentification requise, tout UtilisateurInscrit)
    @PostMapping("/api/articles/{articleId}/commentaires")
    public ResponseEntity<CommentaireResponse> ecrire(
            Authentication authentication,
            @PathVariable Long articleId,
            @Valid @RequestBody CommentaireCreateRequest request) {
        CommentaireResponse response = commentaireService.ecrireCommentaire(authentication.getName(), articleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lecture publique des commentaires approuvés d'un article
    @GetMapping("/api/articles/{articleId}/commentaires")
    public ResponseEntity<List<CommentaireResponse>> listerApprouves(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentaireService.listerApprouves(articleId));
    }

    // Réservé au Modérateur : file d'attente de modération
    @GetMapping("/api/moderation/commentaires")
    public ResponseEntity<List<CommentaireResponse>> listerEnAttente() {
        return ResponseEntity.ok(commentaireService.listerEnAttente());
    }

    // Réservé au Modérateur
    @PutMapping("/api/moderation/commentaires/{id}/approuver")
    public ResponseEntity<CommentaireResponse> approuver(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(commentaireService.approuver(authentication.getName(), id));
    }

    // Réservé au Modérateur
    @DeleteMapping("/api/moderation/commentaires/{id}")
    public ResponseEntity<CommentaireResponse> supprimer(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(commentaireService.supprimer(authentication.getName(), id));
    }
}