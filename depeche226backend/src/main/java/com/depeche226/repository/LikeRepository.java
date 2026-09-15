package com.depeche226.repository;

import com.depeche226.domain.entity.Article;
import com.depeche226.domain.entity.Like;
import com.depeche226.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Je m'appuie sur ce repository pour ajouter, retirer et compter les likes.
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndArticle(User user, Article article);

    long countByArticle(Article article);

    boolean existsByUserAndArticle(User user, Article article);

    void deleteByUserAndArticle(User user, Article article);
}
