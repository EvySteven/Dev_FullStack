package com.depeche226.dto.comment;

import jakarta.validation.constraints.NotBlank;

// Je transporte la decision et le motif saisi par le moderateur.
public record ModerationDecisionRequest(
    @NotBlank(message = "La décision est obligatoire")
    String decision,

    String rejectionReason
) {}
