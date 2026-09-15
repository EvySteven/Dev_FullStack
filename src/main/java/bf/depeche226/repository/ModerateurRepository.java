package bf.depeche226.repository;

import bf.depeche226.entity.Moderateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ModerateurRepository extends JpaRepository<Moderateur, Long> {
    Optional<Moderateur> findByEmail(String email);
}