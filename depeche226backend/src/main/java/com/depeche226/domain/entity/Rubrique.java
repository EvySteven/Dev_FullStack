package com.depeche226.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "rubriques", indexes = {
    @Index(name = "idx_rubriques_nom", columnList = "nom")
})
// Je represente une rubrique qui sert a organiser les articles.
public class Rubrique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nom;

    @Column(length = 120)
    private String slug;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "rubrique")
    private List<Article> articles = new ArrayList<>();

    public Rubrique() {}

    public Rubrique(String nom, String slug) {
        this.nom = nom;
        this.slug = slug;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }
}
