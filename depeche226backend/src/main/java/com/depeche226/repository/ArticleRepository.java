package com.depeche226.repository;

import com.depeche226.domain.entity.Article;
import com.depeche226.domain.entity.Rubrique;
import com.depeche226.domain.entity.User;
import com.depeche226.domain.enums.ArticleStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
// Je laisse Spring Data executer les recherches d'articles dont les services ont besoin.
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("select a from Article a join fetch a.rubrique join fetch a.redacteur r left join fetch r.roles where a.id = :id")
    Optional<Article> findByIdWithRelations(@Param("id") Long id);

    @Query("select a from Article a join fetch a.rubrique join fetch a.redacteur r left join fetch r.roles where a.statut = :statut")
    Page<Article> findAllByStatutWithRelations(@Param("statut") ArticleStatus statut, Pageable pageable);

    @Query("select a from Article a join fetch a.rubrique join fetch a.redacteur r left join fetch r.roles where (:search is null or lower(a.titre) like lower(concat('%', :search, '%')) or lower(a.contenu) like lower(concat('%', :search, '%'))) and (:rubrique is null or a.rubrique = :rubrique) and (:statut is null or a.statut = :statut)")
    Page<Article> searchArticles(@Param("search") String search,
                                @Param("rubrique") Rubrique rubrique,
                                @Param("statut") ArticleStatus statut,
                                Pageable pageable);

    Page<Article> findByRedacteur(User redacteur, Pageable pageable);

    Page<Article> findByRubrique(Rubrique rubrique, Pageable pageable);
}
