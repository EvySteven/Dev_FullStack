package com.depeche226.dto.article;

import com.depeche226.domain.enums.ArticleStatus;
import java.time.Instant;

// Je renvoie ici le detail complet d'un article.
public record ArticleDetailResponse(
    Long id,
    String titre,
    String contenu,
    ArticleStatus statut,
    String videoUrl,
    String youtubeVideoId,
    Instant datePublication,
    Long rubriqueId,
    String rubriqueNom,
    String redacteurPseudo,
    Instant createdAt,
    Instant updatedAt,
    long likesCount
) {}
