package com.depeche226.dto.newsletter;

// Je renvoie le statut courant d'une adresse newsletter.
public record NewsletterResponse(Boolean subscribed, String email, String status) {}
