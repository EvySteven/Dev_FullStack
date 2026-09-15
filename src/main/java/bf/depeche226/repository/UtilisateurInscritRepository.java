package bf.depeche226.repository;

import bf.depeche226.entity.UtilisateurInscrit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurInscritRepository extends JpaRepository<UtilisateurInscrit, Long> {
    Optional<UtilisateurInscrit> findByEmail(String email);
    Optional<UtilisateurInscrit> findByPseudonyme(String pseudonyme);
    boolean existsByEmail(String email);
}