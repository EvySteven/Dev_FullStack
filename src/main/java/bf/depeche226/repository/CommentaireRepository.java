package bf.depeche226.repository;

import bf.depeche226.entity.Commentaire;
import bf.depeche226.entity.StatutCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByArticleId(Long articleId);
    List<Commentaire> findByStatut(StatutCommentaire statut);
    List<Commentaire> findByArticleIdAndStatut(Long articleId, StatutCommentaire statut);
}