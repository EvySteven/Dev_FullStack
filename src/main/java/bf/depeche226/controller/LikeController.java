package bf.depeche226.controller;

import bf.depeche226.dto.LikeStatusResponse;
import bf.depeche226.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles/{articleId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // Liker un article (authentification requise)
    @PostMapping
    public ResponseEntity<LikeStatusResponse> liker(Authentication authentication, @PathVariable Long articleId) {
        return ResponseEntity.ok(likeService.liker(authentication.getName(), articleId));
    }

    // Retirer son like (authentification requise)
    @DeleteMapping
    public ResponseEntity<LikeStatusResponse> retirerLike(Authentication authentication, @PathVariable Long articleId) {
        return ResponseEntity.ok(likeService.retirerLike(authentication.getName(), articleId));
    }

    // Consulter si l'utilisateur courant a liké + le total (authentification requise,
    // pour ne montrer l'état "activé" du bouton qu'à l'utilisateur concerné)
    @GetMapping("/moi")
    public ResponseEntity<LikeStatusResponse> getStatut(Authentication authentication, @PathVariable Long articleId) {
        return ResponseEntity.ok(likeService.getStatut(authentication.getName(), articleId));
    }
}