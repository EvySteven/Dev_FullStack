package com.depeche226.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Je recois le contenu d'un nouveau commentaire.
public record CreateCommentRequest(
    @NotBlank(message = "Le commentaire est obligatoire")
    @Size(max = 1000, message = "Le commentaire est trop long")
    String contenu
) {}
