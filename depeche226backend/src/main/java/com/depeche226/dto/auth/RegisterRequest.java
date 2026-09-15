package com.depeche226.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Je recois les informations de base pour creer un nouveau compte.
public record RegisterRequest(
    @NotBlank(message = "Le pseudonyme est obligatoire")
    @Size(min = 3, max = 80, message = "Le pseudonyme doit contenir entre 3 et 80 caractères")
    String pseudonyme,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email est invalide")
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    String password,

    String centreInteret
) {}
