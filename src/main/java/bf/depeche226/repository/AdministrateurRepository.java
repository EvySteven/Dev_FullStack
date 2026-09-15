package bf.depeche226.repository;

import bf.depeche226.entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
    Optional<Administrateur> findByEmail(String email);
}