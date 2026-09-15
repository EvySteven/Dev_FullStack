package bf.depeche226.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "moderateur")
@Getter
@Setter
@NoArgsConstructor
public class Moderateur extends UtilisateurInscrit {
    // approuverCommentaire(), supprimerCommentaire() -> ModerateurService
}