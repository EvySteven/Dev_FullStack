package com.depeche226.dto.user;

import com.depeche226.domain.enums.Role;
import java.time.Instant;
import java.util.Set;

// Je renvoie un profil sans exposer les informations sensibles du compte.
public record UserResponse(
    Long id,
    String pseudonyme,
    String email,
    String centreInteret,
    boolean newsletterSubscribed,
    boolean enabled,
    Set<Role> roles,
    Instant createdAt,
    Instant updatedAt
) {}
