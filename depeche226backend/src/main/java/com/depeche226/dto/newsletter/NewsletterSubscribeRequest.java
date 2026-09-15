package com.depeche226.dto.newsletter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Je recois seulement l'email pour gerer une inscription newsletter.
public record NewsletterSubscribeRequest(
    String pseudonyme,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email est invalide")
    String email
) {}
