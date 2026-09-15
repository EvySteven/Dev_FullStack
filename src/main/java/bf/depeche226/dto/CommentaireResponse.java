package bf.depeche226.dto;

import bf.depeche226.entity.StatutCommentaire;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentaireResponse {
    private Long id;
    private String contenu;
    private LocalDateTime date;
    private StatutCommentaire statut;
    private String auteurPseudonyme;
    private Long articleId;
    private String moderateurPseudonyme; // null tant que non modéré
}