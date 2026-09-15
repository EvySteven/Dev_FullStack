package com.depeche226.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Je recois ici les informations necessaires pour ouvrir une session.
public record LoginRequest(
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email est invalide")
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    String password
) {}
