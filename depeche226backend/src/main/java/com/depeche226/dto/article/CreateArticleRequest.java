package com.depeche226.dto.article;

import com.depeche226.domain.enums.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Je transporte les champs necessaires a la creation d'un article.
public record CreateArticleRequest(
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre est trop long")
    String titre,

    @NotBlank(message = "Le contenu est obligatoire")
    String contenu,

    @NotNull(message = "La rubrique est obligatoire")
    Long rubriqueId,

    String videoUrl,

    ArticleStatus statut
) {}
