package com.depeche226.repository;

import com.depeche226.domain.entity.Rubrique;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Je donne aux services l'acces aux rubriques sans melanger SQL et logique metier.
public interface RubriqueRepository extends JpaRepository<Rubrique, Long> {

    Optional<Rubrique> findByNomIgnoreCase(String nom);

    Optional<Rubrique> findBySlugIgnoreCase(String slug);
}
