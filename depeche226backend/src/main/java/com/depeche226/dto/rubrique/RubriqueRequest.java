package com.depeche226.dto.rubrique;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Je recois le nom et les informations de creation d'une rubrique.
public record RubriqueRequest(
    @NotBlank(message = "Le nom de la rubrique est obligatoire")
    @Size(max = 120, message = "Le nom est trop long")
    String nom
) {}
