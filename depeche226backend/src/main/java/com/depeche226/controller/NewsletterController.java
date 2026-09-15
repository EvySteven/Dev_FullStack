package com.depeche226.controller;

import com.depeche226.dto.newsletter.NewsletterResponse;
import com.depeche226.dto.newsletter.NewsletterSubscribeRequest;
import com.depeche226.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Je garde ici les trois actions newsletter: inscription, desinscription et statut.
// Je l'ai decouplee des comptes pour que le code reste lisible.
@RestController
@RequestMapping("/api/v1/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @Operation(summary = "Inscription à la newsletter")
    @PostMapping("/subscribe")
    public ResponseEntity<NewsletterResponse> subscribe(@Valid @RequestBody NewsletterSubscribeRequest request) {
        return ResponseEntity.ok(newsletterService.subscribe(request));
    }

    @Operation(summary = "Désinscription à la newsletter")
    @PostMapping("/unsubscribe")
    public ResponseEntity<NewsletterResponse> unsubscribe(@RequestBody NewsletterSubscribeRequest request) {
        return ResponseEntity.ok(newsletterService.unsubscribe(request.email()));
    }

    @Operation(summary = "Statut de la newsletter")
    @GetMapping("/status")
    public ResponseEntity<NewsletterResponse> status(@RequestParam String email) {
        return ResponseEntity.ok(newsletterService.status(email));
    }
}
