package com.depeche226.dto.rubrique;

import java.time.Instant;

// Je renvoie une rubrique deja creee avec son identifiant et son slug.
public record RubriqueResponse(Long id, String nom, String slug, Instant createdAt) {}
