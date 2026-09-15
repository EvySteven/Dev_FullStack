package bf.depeche226.repository;

import bf.depeche226.entity.Redacteur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RedacteurRepository extends JpaRepository<Redacteur, Long> {
    // Spring Data JPA sait traverser l'héritage JOINED : email vit dans utilisateur_inscrit,
    // mais cette requête ne retourne que les lignes qui sont aussi présentes dans la table redacteur
    Optional<Redacteur> findByEmail(String email);
}