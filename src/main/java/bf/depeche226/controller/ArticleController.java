package bf.depeche226.controller;

import bf.depeche226.dto.ArticleCreateRequest;
import bf.depeche226.dto.ArticleResponse;
import bf.depeche226.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> creer(
            Authentication authentication,
            @Valid @RequestBody ArticleCreateRequest request) {
        ArticleResponse response = articleService.creerArticle(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/soumettre")
    public ResponseEntity<ArticleResponse> soumettre(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(articleService.soumettre(authentication.getName(), id));
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<ArticleResponse> valider(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(articleService.valider(authentication.getName(), id));
    }

    @PutMapping("/{id}/refuser")
    public ResponseEntity<ArticleResponse> refuser(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(articleService.refuser(authentication.getName(), id));
    }

    // Public : liste des articles publiés. Contenu complet si connecté, extrait sinon.
    @GetMapping
    public ResponseEntity<List<ArticleResponse>> listerPublies(Authentication authentication) {
        return ResponseEntity.ok(articleService.listerPublies(estAuthentifie(authentication)));
    }

    // Public : détail d'un article publié. Contenu complet si connecté, extrait sinon.
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticlePublie(id, estAuthentifie(authentication)));
    }

    @GetMapping("/moi")
    public ResponseEntity<List<ArticleResponse>> mesArticles(Authentication authentication) {
        return ResponseEntity.ok(articleService.mesArticles(authentication.getName()));
    }

    // Détermine si la requête porte un token JWT valide, ou si c'est un visiteur anonyme.
    // Spring Security peuple automatiquement un utilisateur "anonymousUser" par défaut
    // quand aucun token n'est fourni : on l'exclut explicitement.
    private boolean estAuthentifie(Authentication authentication) {
        return authentication != null && !"anonymousUser".equals(authentication.getPrincipal());
    }
}