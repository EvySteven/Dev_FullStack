package com.depeche226.dto.user;

import jakarta.validation.constraints.Size;

// Je transporte les champs que l'utilisateur peut modifier sur son profil.
public record UpdateProfileRequest(
    @Size(min = 3, max = 80, message = "Le pseudonyme doit contenir entre 3 et 80 caractères")
    String pseudonyme,

    @Size(max = 255, message = "Le centre d'intérêt est trop long")
    String centreInteret,

    Boolean newsletterSubscribed
) {}
