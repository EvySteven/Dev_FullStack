package bf.depeche226.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "redacteur")
@Getter
@Setter
@NoArgsConstructor
public class Redacteur extends UtilisateurInscrit {
    // rediger() et soumettreArticle() -> logique métier, pas des champs
    // le lien vers les Articles rédigés est porté par Article (voir plus loin)
}