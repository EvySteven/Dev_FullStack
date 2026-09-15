package com.depeche226.controller;

import com.depeche226.domain.enums.ArticleStatus;
import com.depeche226.dto.PageResponse;
import com.depeche226.dto.article.ArticleDetailResponse;
import com.depeche226.dto.article.ArticleSummaryResponse;
import com.depeche226.dto.article.CreateArticleRequest;
import com.depeche226.dto.article.UpdateArticleRequest;
import com.depeche226.dto.comment.CommentResponse;
import com.depeche226.dto.comment.CreateCommentRequest;
import com.depeche226.dto.comment.ModerationDecisionRequest;
import com.depeche226.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// J'ai regroupe ici le coeur de l'edition: articles, commentaires, likes et moderation.
// Je ne charge aucune donnee au boot, donc tout part vraiment de zero.
@RestController
@RequestMapping("/api/v1")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // Je peux retourner une liste publique ou la filtrer par rubrique, statut et recherche.
    @Operation(summary = "Liste les articles")
    @GetMapping("/articles")
    public ResponseEntity<PageResponse<ArticleSummaryResponse>> listArticles(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Long rubriqueId,
        @RequestParam(required = false) ArticleStatus statut) {
        Sort.Direction dir = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        Page<ArticleSummaryResponse> result = articleService.searchArticles(search, rubriqueId, statut, pageable);
        return ResponseEntity.ok(PageResponse.of(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements()));
    }

    // Je garde ce detail comme point d'entree pour la lecture publique d'un article.
    @Operation(summary = "Détail d'un article visible public si publié")
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDetailResponse> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticle(id));
    }

    @Operation(summary = "Créer un article", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/articles")
    @PreAuthorize("hasAnyRole('REDACTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<ArticleDetailResponse> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        return ResponseEntity.ok(articleService.createArticle(request));
    }

    @Operation(summary = "Modifier un article", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/articles/{id}")
    public ResponseEntity<ArticleDetailResponse> updateArticle(@PathVariable Long id, @Valid @RequestBody UpdateArticleRequest request) {
        return ResponseEntity.ok(articleService.updateArticle(id, request));
    }

    @Operation(summary = "Supprimer un article", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.getArticleForEditor(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Soumettre un article pour validation", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/articles/{id}/submit")
    public ResponseEntity<ArticleDetailResponse> submitArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.submitArticle(id));
    }

    @Operation(summary = "Publier un article", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/articles/{id}/publish")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ArticleDetailResponse> publishArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.publishArticle(id));
    }

    @Operation(summary = "Rejeter un article", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/articles/{id}/reject")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ArticleDetailResponse> rejectArticle(@PathVariable Long id, @RequestBody String reason) {
        return ResponseEntity.ok(articleService.rejectArticle(id, reason));
    }

    @Operation(summary = "Liste les commentaires approuvés d'un article")
    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<PageResponse<CommentResponse>> listComments(@PathVariable Long articleId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> result = articleService.listComments(articleId, pageable);
        return ResponseEntity.ok(PageResponse.of(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements()));
    }

    @Operation(summary = "Créer un commentaire", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/articles/{articleId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long articleId, @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(articleService.addComment(articleId, request));
    }

    @Operation(summary = "Toggle like sur un article", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/articles/{articleId}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.toggleLike(articleId));
    }

    @Operation(summary = "Retirer un like", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/articles/{articleId}/like")
    public ResponseEntity<Boolean> removeLike(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.toggleLike(articleId));
    }

    @Operation(summary = "Nombre de likes d'un article")
    @GetMapping("/articles/{articleId}/likes/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.countLikes(articleId));
    }

    @Operation(summary = "Commente à modérer", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/moderation/comments")
    @PreAuthorize("hasAnyRole('MODERATEUR','ADMINISTRATEUR')")
    public ResponseEntity<PageResponse<CommentResponse>> pendingComments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> result = articleService.pendingComments(pageable);
        return ResponseEntity.ok(PageResponse.of(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements()));
    }

    @Operation(summary = "Approuver ou rejeter un commentaire", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/moderation/comments/{commentId}/approve")
    @PreAuthorize("hasAnyRole('MODERATEUR','ADMINISTRATEUR')")
    public ResponseEntity<CommentResponse> approveComment(@PathVariable Long commentId, @RequestBody ModerationDecisionRequest request) {
        return ResponseEntity.ok(articleService.moderateComment(commentId, request));
    }

    @Operation(summary = "Rejeter un commentaire", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/moderation/comments/{commentId}/reject")
    @PreAuthorize("hasAnyRole('MODERATEUR','ADMINISTRATEUR')")
    public ResponseEntity<CommentResponse> rejectComment(@PathVariable Long commentId, @RequestBody ModerationDecisionRequest request) {
        return ResponseEntity.ok(articleService.moderateComment(commentId, request));
    }
}
