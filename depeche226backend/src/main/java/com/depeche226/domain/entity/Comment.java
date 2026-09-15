package com.depeche226.domain.entity;

import com.depeche226.domain.enums.CommentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_comments_status", columnList = "statut"),
    @Index(name = "idx_comments_article", columnList = "article_id"),
    @Index(name = "idx_comments_user", columnList = "user_id")
})
// Je represente un commentaire qui peut ensuite passer par la moderation.
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CommentStatus statut;

    @Column(length = 500)
    private String rejectionReason;

    @Column
    private Instant moderatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User auteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    public Comment() {
        this.statut = CommentStatus.EN_ATTENTE;
    }

    public Comment(String contenu, User auteur, Article article) {
        this();
        this.contenu = contenu;
        this.auteur = auteur;
        this.article = article;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public Instant getDateCreation() { return dateCreation; }
    public void setDateCreation(Instant dateCreation) { this.dateCreation = dateCreation; }
    public CommentStatus getStatut() { return statut; }
    public void setStatut(CommentStatus statut) { this.statut = statut; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public Instant getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(Instant moderatedAt) { this.moderatedAt = moderatedAt; }
    public User getAuteur() { return auteur; }
    public void setAuteur(User auteur) { this.auteur = auteur; }
    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }
}
