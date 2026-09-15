package bf.depeche226.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "administrateur")
@Getter
@Setter
@NoArgsConstructor
public class Administrateur extends UtilisateurInscrit {
    // creerCompte(), supprimerCompte(), modifierCompte(), validerArticle()
    // -> toute cette logique ira dans AdministrateurService
}