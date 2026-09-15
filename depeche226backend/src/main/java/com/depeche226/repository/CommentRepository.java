package com.depeche226.repository;

import com.depeche226.domain.entity.Article;
import com.depeche226.domain.entity.Comment;
import com.depeche226.domain.entity.User;
import com.depeche226.domain.enums.CommentStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Je centralise ici l'acces aux commentaires et a leur statut de moderation.
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByArticleAndStatut(Article article, CommentStatus statut, Pageable pageable);

    List<Comment> findByArticleAndStatut(Article article, CommentStatus statut);

    Page<Comment> findByStatut(CommentStatus statut, Pageable pageable);

    List<Comment> findByAuteur(User auteur);
}
