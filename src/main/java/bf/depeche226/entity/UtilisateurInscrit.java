package bf.depeche226.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Fusion de l'ancienne classe Utilisateur : cette entité est maintenant la racine
// de la hiérarchie (Redacteur, Administrateur, Moderateur en héritent).
// Il n'existe aucun compte sur la plateforme qui ne soit pas un UtilisateurInscrit.
@Entity
@Table(name = "utilisateur_inscrit")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class UtilisateurInscrit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pseudonyme;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private boolean supprime = false;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    private Integer age;

    @ElementCollection
    @CollectionTable(
        name = "centres_interet",
        joinColumns = @JoinColumn(name = "utilisateur_inscrit_id")
    )
    @Column(name = "centre_interet")
    private List<String> centresInteret = new ArrayList<>();

    @Column(name = "abonne_newsletter", nullable = false)
    private boolean abonneNewsletter = false;


    @PrePersist
    protected void onCreate() {
        this.dateInscription = LocalDate.now();
    }
}