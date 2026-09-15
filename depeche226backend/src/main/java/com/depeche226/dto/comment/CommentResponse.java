package com.depeche226.dto.comment;

import com.depeche226.domain.enums.CommentStatus;
import java.time.Instant;

// Je renvoie un commentaire dans un format lisible par le client.
public record CommentResponse(
    Long id,
    String contenu,
    CommentStatus statut,
    String auteurPseudo,
    Instant dateCreation,
    String rejectionReason
) {}
