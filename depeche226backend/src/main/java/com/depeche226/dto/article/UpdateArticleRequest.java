package com.depeche226.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Je transporte les champs modifiables d'un article existant.
public record UpdateArticleRequest(
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre est trop long")
    String titre,

    @NotBlank(message = "Le contenu est obligatoire")
    String contenu,

    Long rubriqueId,
    String videoUrl,
    String rejectionReason
) {}
