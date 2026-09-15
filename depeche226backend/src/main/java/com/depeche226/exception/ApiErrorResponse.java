package com.depeche226.exception;

import java.time.Instant;
import java.util.Map;

// Je garde une reponse d'erreur unique pour que le client sache toujours quoi lire.
public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> fieldErrors
) {}
