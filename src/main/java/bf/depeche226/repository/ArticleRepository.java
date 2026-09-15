package bf.depeche226.repository;

import bf.depeche226.entity.Article;
import bf.depeche226.entity.StatutArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByStatut(StatutArticle statut);
    List<Article> findByRubriqueId(Long rubriqueId);
    List<Article> findByRedacteurId(Long redacteurId);
    List<Article> findByStatutOrderByDatePublicationDesc(StatutArticle statut);
}