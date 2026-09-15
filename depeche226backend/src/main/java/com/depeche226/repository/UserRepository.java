package com.depeche226.repository;

import com.depeche226.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
// Je centralise ici les recherches de comptes par email et identifiant.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPseudonymeIgnoreCase(String pseudonyme);

    Optional<User> findByPseudonymeIgnoreCase(String pseudonyme);

    @Query("select u from User u left join fetch u.roles where u.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    @Query("select u from User u left join fetch u.roles")
    Page<User> findAllWithRoles(Pageable pageable);
}
