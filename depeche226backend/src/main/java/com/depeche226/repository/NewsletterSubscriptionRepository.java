package com.depeche226.repository;

import com.depeche226.domain.entity.NewsletterSubscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Je garde ici les recherches d'inscriptions newsletter par email.
public interface NewsletterSubscriptionRepository extends JpaRepository<NewsletterSubscription, Long> {

    Optional<NewsletterSubscription> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
