package com.depeche226.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "likes",
    uniqueConstraints = {@UniqueConstraint(name = "uk_likes_user_article", columnNames = {"user_id", "article_id"})},
    indexes = {
        @Index(name = "idx_likes_article", columnList = "article_id"),
        @Index(name = "idx_likes_user", columnList = "user_id")
    }
)
// Je stocke ici le lien entre un utilisateur et un article aime.
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    public Like() {}

    public Like(User user, Article article) {
        this.user = user;
        this.article = article;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instant getDateLike() { return dateLike; }
    public void setDateLike(Instant dateLike) { this.dateLike = dateLike; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }
}
