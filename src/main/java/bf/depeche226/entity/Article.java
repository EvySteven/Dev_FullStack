package bf.depeche226.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Lob
    @Column(nullable = false)
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutArticle statut = StatutArticle.BROUILLON;
    // ex: BROUILLON, SOUMIS, PUBLIE, REFUSE

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    // Redacteur "1" --> "0..*" Article : redige
    @ManyToOne(optional = false)
    @JoinColumn(name = "redacteur_id", nullable = false)
    private Redacteur redacteur;

    // Administrateur "1" --> "0..*" Article : valide
    @ManyToOne
    @JoinColumn(name = "administrateur_id")
    private Administrateur administrateur;

    // Rubrique "1" --> "0..*" Article : classe
    @ManyToOne(optional = false)
    @JoinColumn(name = "rubrique_id", nullable = false)
    private Rubrique rubrique;

    // Article "1" *-- "0..*" Commentaire : contient (composition)
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires = new ArrayList<>();

    // Article "1" --> "0..*" Like : recoit
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();
}