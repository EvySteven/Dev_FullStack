package com.depeche226.service;

import com.depeche226.domain.entity.NewsletterSubscription;
import com.depeche226.dto.newsletter.NewsletterResponse;
import com.depeche226.dto.newsletter.NewsletterSubscribeRequest;
import com.depeche226.repository.NewsletterSubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// Je gere les inscriptions newsletter sans obliger la personne a creer un compte.
public class NewsletterService {

    private final NewsletterSubscriptionRepository newsletterSubscriptionRepository;

    public NewsletterService(NewsletterSubscriptionRepository newsletterSubscriptionRepository) {
        this.newsletterSubscriptionRepository = newsletterSubscriptionRepository;
    }

    @Transactional
    public NewsletterResponse subscribe(NewsletterSubscribeRequest request) {
        String email = request.email().trim().toLowerCase();
        NewsletterSubscription subscription = newsletterSubscriptionRepository.findByEmailIgnoreCase(email)
            .orElseGet(() -> new NewsletterSubscription(request.pseudonyme(), email));
        subscription.setPseudonyme(request.pseudonyme());
        subscription.setEmail(email);
        subscription.setActive(true);
        subscription.setUnsubscribedAt(null);
        newsletterSubscriptionRepository.save(subscription);
        return new NewsletterResponse(true, email, "subscribed");
    }

    @Transactional
    public NewsletterResponse unsubscribe(String email) {
        NewsletterSubscription subscription = newsletterSubscriptionRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new EntityNotFoundException("Abonnement introuvable"));
        subscription.setActive(false);
        subscription.setUnsubscribedAt(java.time.Instant.now());
        newsletterSubscriptionRepository.save(subscription);
        return new NewsletterResponse(false, email, "unsubscribed");
    }

    public NewsletterResponse status(String email) {
        return newsletterSubscriptionRepository.findByEmailIgnoreCase(email)
            .map(subscription -> new NewsletterResponse(subscription.isActive(), subscription.getEmail(), subscription.isActive() ? "subscribed" : "unsubscribed"))
            .orElse(new NewsletterResponse(false, email, "not_found"));
    }
}
