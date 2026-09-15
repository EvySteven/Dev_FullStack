package bf.depeche226.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "commentaire")
@Getter
@Setter
@NoArgsConstructor
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String contenu;

    @Column(nullable = false)
    private LocalDateTime date;

    // Article "1" *-- "0..*" Commentaire : contient
    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // UtilisateurInscrit "1" --> "0..*" Commentaire : ecrit
    @ManyToOne(optional = false)
    @JoinColumn(name = "auteur_id", nullable = false)
    private UtilisateurInscrit auteur;

    // Moderateur "1" --> "0..*" Commentaire : modere
    @ManyToOne
    @JoinColumn(name = "moderateur_id")
    private Moderateur moderateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommentaire statut = StatutCommentaire.EN_ATTENTE;
    // EN_ATTENTE, APPROUVE, SUPPRIME

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }
}