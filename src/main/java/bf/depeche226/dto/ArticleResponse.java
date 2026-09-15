package bf.depeche226.dto;

import bf.depeche226.entity.StatutArticle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private String titre;
    private String contenu;
    private boolean contenuComplet; // false si le contenu a été tronqué (visiteur non connecté)
    private StatutArticle statut;
    private String videoUrl;
    private String videoEmbedUrl;
    private String imageUrl;
    private LocalDate datePublication;
    private String redacteurPseudonyme;
    private String administrateurPseudonyme;
    private String rubriqueNom;
    private long nombreLikes;
    private String lienPartage; // URL publique prête à copier/coller
}