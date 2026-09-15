package com.depeche226.domain.entity;

import com.depeche226.domain.enums.ArticleStatus;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "articles", indexes = {
    @Index(name = "idx_articles_status", columnList = "statut"),
    @Index(name = "idx_articles_rubrique", columnList = "rubrique_id"),
    @Index(name = "idx_articles_publication_date", columnList = "date_publication")
})
// Je represente un article et son cycle de vie editorial.
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ArticleStatus statut;

    @Column(length = 500)
    private String videoUrl;

    @Column(length = 32)
    private String youtubeVideoId;

    @Column(name = "date_publication")
    private Instant datePublication;

    @Column(length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redacteur_id", nullable = false)
    private User redacteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubrique_id", nullable = false)
    private Rubrique rubrique;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Article() {
        this.statut = ArticleStatus.BROUILLON;
    }

    public Article(String titre, String contenu, Rubrique rubrique, User redacteur) {
        this();
        this.titre = titre;
        this.contenu = contenu;
        this.rubrique = rubrique;
        this.redacteur = redacteur;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public ArticleStatus getStatut() { return statut; }
    public void setStatut(ArticleStatus statut) { this.statut = statut; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public String getYoutubeVideoId() { return youtubeVideoId; }
    public void setYoutubeVideoId(String youtubeVideoId) { this.youtubeVideoId = youtubeVideoId; }
    public Instant getDatePublication() { return datePublication; }
    public void setDatePublication(Instant datePublication) { this.datePublication = datePublication; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public User getRedacteur() { return redacteur; }
    public void setRedacteur(User redacteur) { this.redacteur = redacteur; }
    public Rubrique getRubrique() { return rubrique; }
    public void setRubrique(Rubrique rubrique) { this.rubrique = rubrique; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
