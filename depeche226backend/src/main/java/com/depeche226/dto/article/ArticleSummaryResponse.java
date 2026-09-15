package com.depeche226.dto.article;

import com.depeche226.domain.enums.ArticleStatus;
import java.time.Instant;

// Je renvoie une version legere pour les listes d'articles.
public record ArticleSummaryResponse(
    Long id,
    String titre,
    String contenu,
    ArticleStatus statut,
    String videoUrl,
    String youtubeVideoId,
    Instant datePublication,
    String rubriqueNom,
    String redacteurPseudo,
    Instant createdAt
) {}
