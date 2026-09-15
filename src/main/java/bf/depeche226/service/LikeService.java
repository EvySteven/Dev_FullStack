package bf.depeche226.service;

import bf.depeche226.dto.LikeStatusResponse;
import bf.depeche226.entity.Article;
import bf.depeche226.entity.Like;
import bf.depeche226.entity.UtilisateurInscrit;
import bf.depeche226.repository.ArticleRepository;
import bf.depeche226.repository.LikeRepository;
import bf.depeche226.repository.UtilisateurInscritRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;
    private final UtilisateurInscritRepository utilisateurInscritRepository;

    // Ajoute un like de l'utilisateur courant sur l'article
    @Transactional
    public LikeStatusResponse liker(String email, Long articleId) {

        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));

        // La contrainte d'unicité en base (utilisateur_id + article_id) empêche de toute façon
        // le doublon, mais on vérifie ici pour renvoyer un message clair plutôt qu'une erreur SQL
        if (likeRepository.existsByUtilisateurIdAndArticleId(utilisateur.getId(), articleId)) {
            throw new IllegalArgumentException("Vous avez déjà aimé cet article");
        }

        Like like = new Like();
        like.setUtilisateur(utilisateur);
        like.setArticle(article);
        likeRepository.save(like);

        return construireStatut(utilisateur.getId(), articleId);
    }

    // Retire le like de l'utilisateur courant sur l'article
    @Transactional
    public LikeStatusResponse retirerLike(String email, Long articleId) {

        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Like like = likeRepository.findByUtilisateurIdAndArticleId(utilisateur.getId(), articleId)
                .orElseThrow(() -> new IllegalArgumentException("Vous n'avez pas aimé cet article"));

        likeRepository.delete(like);

        return construireStatut(utilisateur.getId(), articleId);
    }

    // Renvoie l'état du like pour l'utilisateur courant + le total, utile pour afficher un bouton "j'aime" activé ou non
    public LikeStatusResponse getStatut(String email, Long articleId) {
        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        return construireStatut(utilisateur.getId(), articleId);
    }

    private LikeStatusResponse construireStatut(Long utilisateurId, Long articleId) {
        boolean aLike = likeRepository.existsByUtilisateurIdAndArticleId(utilisateurId, articleId);
        long total = likeRepository.countByArticleId(articleId);
        return new LikeStatusResponse(aLike, total);
    }
}