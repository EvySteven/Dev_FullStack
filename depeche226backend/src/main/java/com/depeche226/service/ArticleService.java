package com.depeche226.service;

import com.depeche226.domain.entity.Article;
import com.depeche226.domain.entity.Comment;
import com.depeche226.domain.entity.Like;
import com.depeche226.domain.entity.Rubrique;
import com.depeche226.domain.entity.User;
import com.depeche226.domain.enums.ArticleStatus;
import com.depeche226.domain.enums.CommentStatus;
import com.depeche226.dto.article.ArticleDetailResponse;
import com.depeche226.dto.article.ArticleSummaryResponse;
import com.depeche226.dto.article.CreateArticleRequest;
import com.depeche226.dto.article.UpdateArticleRequest;
import com.depeche226.dto.comment.CommentResponse;
import com.depeche226.dto.comment.CreateCommentRequest;
import com.depeche226.dto.comment.ModerationDecisionRequest;
import com.depeche226.repository.ArticleRepository;
import com.depeche226.repository.CommentRepository;
import com.depeche226.repository.LikeRepository;
import com.depeche226.repository.RubriqueRepository;
import com.depeche226.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// Je porte ici les regles metier des articles, commentaires, likes et moderation.
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final RubriqueRepository rubriqueRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public ArticleService(ArticleRepository articleRepository,
                          RubriqueRepository rubriqueRepository,
                          CommentRepository commentRepository,
                          LikeRepository likeRepository,
                          UserRepository userRepository,
                          UserService userService) {
        this.articleRepository = articleRepository;
        this.rubriqueRepository = rubriqueRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Page<ArticleSummaryResponse> searchArticles(String search, Long rubriqueId, ArticleStatus statut, Pageable pageable) {
        Rubrique rubrique = rubriqueId == null ? null : rubriqueRepository.findById(rubriqueId)
            .orElseThrow(() -> new EntityNotFoundException("Rubrique introuvable"));
        return articleRepository.searchArticles(search == null || search.isBlank() ? null : search, rubrique, statut, pageable)
            .map(this::toSummaryResponse);
    }

    public ArticleDetailResponse getArticle(Long id) {
        Article article = articleRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        if (article.getStatut() != ArticleStatus.PUBLIE) {
            throw new IllegalStateException("Article non public");
        }
        return toDetailResponse(article);
    }

    public ArticleDetailResponse getArticleForEditor(Long id) {
        Article article = articleRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        return toDetailResponse(article);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('REDACTEUR', 'ADMINISTRATEUR')")
    public ArticleDetailResponse createArticle(CreateArticleRequest request) {
        User currentUser = userService.getAuthenticatedUser();
        Rubrique rubrique = rubriqueRepository.findById(request.rubriqueId())
            .orElseThrow(() -> new EntityNotFoundException("Rubrique introuvable"));
        Article article = new Article(request.titre(), request.contenu(), rubrique, currentUser);
        article.setStatut(request.statut() == null ? ArticleStatus.BROUILLON : request.statut());
        if (request.videoUrl() != null && !request.videoUrl().isBlank()) {
            article.setVideoUrl(request.videoUrl());
            article.setYoutubeVideoId(extractYoutubeVideoId(request.videoUrl()));
        }
        Article saved = articleRepository.save(article);
        return toDetailResponse(saved);
    }

    @Transactional
    public ArticleDetailResponse updateArticle(Long id, UpdateArticleRequest request) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        User current = userService.getAuthenticatedUser();
        if (!current.getId().equals(article.getRedacteur().getId()) && !current.getRoles().contains(com.depeche226.domain.enums.Role.ADMINISTRATEUR)) {
            throw new IllegalStateException("Vous ne pouvez pas modifier cet article");
        }
        article.setTitre(request.titre());
        article.setContenu(request.contenu());
        if (request.rubriqueId() != null) {
            Rubrique rubrique = rubriqueRepository.findById(request.rubriqueId())
                .orElseThrow(() -> new EntityNotFoundException("Rubrique introuvable"));
            article.setRubrique(rubrique);
        }
        if (request.videoUrl() != null) {
            article.setVideoUrl(request.videoUrl());
            article.setYoutubeVideoId(extractYoutubeVideoId(request.videoUrl()));
        }
        return toDetailResponse(articleRepository.save(article));
    }

    @Transactional
    public ArticleDetailResponse submitArticle(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        User current = userService.getAuthenticatedUser();
        if (!current.getId().equals(article.getRedacteur().getId()) && !current.getRoles().contains(com.depeche226.domain.enums.Role.ADMINISTRATEUR)) {
            throw new IllegalStateException("Vous ne pouvez pas soumettre cet article");
        }
        article.setStatut(ArticleStatus.SOUMIS);
        return toDetailResponse(articleRepository.save(article));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ArticleDetailResponse publishArticle(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        if (article.getStatut() != ArticleStatus.SOUMIS) {
            throw new IllegalStateException("L'article doit être soumis avant publication");
        }
        article.setStatut(ArticleStatus.PUBLIE);
        article.setDatePublication(Instant.now());
        article.setRejectionReason(null);
        return toDetailResponse(articleRepository.save(article));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ArticleDetailResponse rejectArticle(Long id, String reason) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        article.setStatut(ArticleStatus.REJETE);
        article.setRejectionReason(reason);
        return toDetailResponse(articleRepository.save(article));
    }

    @Transactional
    public CommentResponse addComment(Long articleId, CreateCommentRequest request) {
        User current = userService.getAuthenticatedUser();
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        Comment comment = new Comment(request.contenu().trim(), current, article);
        comment.setStatut(CommentStatus.EN_ATTENTE);
        Comment saved = commentRepository.save(comment);
        return toCommentResponse(saved);
    }

    public Page<CommentResponse> listComments(Long articleId, Pageable pageable) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        return commentRepository.findByArticleAndStatut(article, CommentStatus.APPROUVE, pageable)
            .map(this::toCommentResponse);
    }

    @Transactional
    public CommentResponse moderateComment(Long commentId, ModerationDecisionRequest request) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Commentaire introuvable"));
        if (request.decision().equalsIgnoreCase("APPROUVE")) {
            comment.setStatut(CommentStatus.APPROUVE);
            comment.setRejectionReason(null);
        } else {
            comment.setStatut(CommentStatus.REJETE);
            comment.setRejectionReason(request.rejectionReason());
        }
        comment.setModeratedAt(Instant.now());
        return toCommentResponse(commentRepository.save(comment));
    }

    public Page<CommentResponse> pendingComments(Pageable pageable) {
        return commentRepository.findByStatut(CommentStatus.EN_ATTENTE, pageable).map(this::toCommentResponse);
    }

    @Transactional
    public boolean toggleLike(Long articleId) {
        User current = userService.getAuthenticatedUser();
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        Optional<Like> existing = likeRepository.findByUserAndArticle(current, article);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        }
        likeRepository.save(new Like(current, article));
        return true;
    }

    public long countLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));
        return likeRepository.countByArticle(article);
    }

    public String extractYoutubeVideoId(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String normalized = url.trim();
        if (normalized.contains("youtu.be/")) {
            return normalized.substring(normalized.indexOf("youtu.be/") + 9).split("[?&]")[0];
        }
        if (normalized.contains("watch?v=")) {
            return normalized.substring(normalized.indexOf("watch?v=") + 8).split("[?&]")[0];
        }
        if (normalized.contains("embed/")) {
            return normalized.substring(normalized.indexOf("embed/") + 6).split("[?&]")[0];
        }
        return normalized.matches("^[A-Za-z0-9_-]{11}$") ? normalized : null;
    }

    public ArticleDetailResponse toDetailResponse(Article article) {
        return new ArticleDetailResponse(
            article.getId(),
            article.getTitre(),
            article.getContenu(),
            article.getStatut(),
            article.getVideoUrl(),
            article.getYoutubeVideoId(),
            article.getDatePublication(),
            article.getRubrique().getId(),
            article.getRubrique().getNom(),
            article.getRedacteur().getPseudonyme(),
            article.getCreatedAt(),
            article.getUpdatedAt(),
            likeRepository.countByArticle(article)
        );
    }

    public ArticleSummaryResponse toSummaryResponse(Article article) {
        return new ArticleSummaryResponse(
            article.getId(),
            article.getTitre(),
            article.getContenu(),
            article.getStatut(),
            article.getVideoUrl(),
            article.getYoutubeVideoId(),
            article.getDatePublication(),
            article.getRubrique().getNom(),
            article.getRedacteur().getPseudonyme(),
            article.getCreatedAt()
        );
    }

    public CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContenu(),
            comment.getStatut(),
            comment.getAuteur().getPseudonyme(),
            comment.getDateCreation(),
            comment.getRejectionReason()
        );
    }
}
