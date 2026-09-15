package bf.depeche226.repository;

import bf.depeche226.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUtilisateurIdAndArticleId(Long utilisateurId, Long articleId);
    long countByArticleId(Long articleId);
    boolean existsByUtilisateurIdAndArticleId(Long utilisateurId, Long articleId);
}