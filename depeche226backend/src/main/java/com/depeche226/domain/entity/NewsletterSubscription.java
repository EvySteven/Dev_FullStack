package com.depeche226.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "newsletter_subscriptions",
    uniqueConstraints = {@UniqueConstraint(name = "uk_newsletter_email", columnNames = "email")},
    indexes = {@Index(name = "idx_newsletter_active", columnList = "active")}
)
// Je garde ici l'email et le statut d'une inscription newsletter.
public class NewsletterSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80)
    private String pseudonyme;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant subscribedAt;

    @Column
    private Instant unsubscribedAt;

    public NewsletterSubscription() {
        this.active = true;
    }

    public NewsletterSubscription(String pseudonyme, String email) {
        this();
        this.pseudonyme = pseudonyme;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPseudonyme() { return pseudonyme; }
    public void setPseudonyme(String pseudonyme) { this.pseudonyme = pseudonyme; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getSubscribedAt() { return subscribedAt; }
    public void setSubscribedAt(Instant subscribedAt) { this.subscribedAt = subscribedAt; }
    public Instant getUnsubscribedAt() { return unsubscribedAt; }
    public void setUnsubscribedAt(Instant unsubscribedAt) { this.unsubscribedAt = unsubscribedAt; }
}
