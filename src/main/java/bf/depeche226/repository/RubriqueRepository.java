package bf.depeche226.repository;

import bf.depeche226.entity.Rubrique;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RubriqueRepository extends JpaRepository<Rubrique, Long> {
    Optional<Rubrique> findByNom(String nom);
}