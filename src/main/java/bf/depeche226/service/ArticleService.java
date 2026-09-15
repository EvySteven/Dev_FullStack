package bf.depeche226.service;

import bf.depeche226.dto.ArticleCreateRequest;
import bf.depeche226.dto.ArticleResponse;
import bf.depeche226.entity.*;
import bf.depeche226.repository.*;
import bf.depeche226.util.VideoUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final RedacteurRepository redacteurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final RubriqueRepository rubriqueRepository;
    private final LikeRepository likeRepository;
     @org.springframework.beans.factory.annotation.Value("${app.frontend-url}")
    private String frontendUrl;

    // Proportion du contenu visible pour un visiteur non connecté
    private static final double PROPORTION_EXTRAIT = 0.20;

    @Transactional
    public ArticleResponse creerArticle(String emailRedacteur, ArticleCreateRequest request) {

        Redacteur redacteur = redacteurRepository.findByEmail(emailRedacteur)
                .orElseThrow(() -> new IllegalArgumentException("Rédacteur introuvable"));

        Rubrique rubrique = rubriqueRepository.findById(request.getRubriqueId())
                .orElseThrow(() -> new IllegalArgumentException("Rubrique introuvable"));

        if (request.getVideoUrl() != null && !request.getVideoUrl().isBlank()
                && VideoUrlUtils.extraireIdVideo(request.getVideoUrl()) == null) {
            throw new IllegalArgumentException("L'URL vidéo fournie n'est pas une URL YouTube valide");
        }

        Article article = new Article();
        article.setTitre(request.getTitre());
        article.setContenu(request.getContenu());
        article.setVideoUrl(request.getVideoUrl());
        article.setImageUrl(request.getImageUrl());
        article.setRubrique(rubrique);
        article.setRedacteur(redacteur);
        article.setStatut(StatutArticle.BROUILLON);

        Article saved = articleRepository.save(article);
        // Le rédacteur voit toujours son propre contenu en entier, même juste après création
        return toResponse(saved, true);
    }

    @Transactional
    public ArticleResponse soumettre(String emailRedacteur, Long articleId) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));

        if (!article.getRedacteur().getEmail().equals(emailRedacteur)) {
            throw new IllegalArgumentException("Vous ne pouvez soumettre que vos propres articles");
        }

        if (article.getStatut() != StatutArticle.BROUILLON) {
            throw new IllegalArgumentException("Seul un article en brouillon peut être soumis");
        }

        article.setStatut(StatutArticle.SOUMIS);
        return toResponse(articleRepository.save(article), true);
    }

    @Transactional
    public ArticleResponse valider(String emailAdmin, Long articleId) {

        Administrateur admin = administrateurRepository.findByEmail(emailAdmin)
                .orElseThrow(() -> new IllegalArgumentException("Administrateur introuvable"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));

        if (article.getStatut() != StatutArticle.SOUMIS) {
            throw new IllegalArgumentException("Seul un article soumis peut être validé");
        }

        article.setStatut(StatutArticle.PUBLIE);
        article.setAdministrateur(admin);
        article.setDatePublication(LocalDate.now());

        return toResponse(articleRepository.save(article), true);
    }

    @Transactional
    public ArticleResponse refuser(String emailAdmin, Long articleId) {

        Administrateur admin = administrateurRepository.findByEmail(emailAdmin)
                .orElseThrow(() -> new IllegalArgumentException("Administrateur introuvable"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));

        if (article.getStatut() != StatutArticle.SOUMIS) {
            throw new IllegalArgumentException("Seul un article soumis peut être refusé");
        }

        article.setStatut(StatutArticle.REFUSE);
        article.setAdministrateur(admin);

        return toResponse(articleRepository.save(article), true);
    }

    // Liste publique : le paramètre authentifie détermine si le contenu complet est renvoyé
    public List<ArticleResponse> listerPublies(boolean authentifie) {
        return articleRepository.findByStatutOrderByDatePublicationDesc(StatutArticle.PUBLIE)
                .stream().map(article -> toResponse(article, authentifie)).toList();
    }

    public ArticleResponse getArticlePublie(Long id, boolean authentifie) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));

        if (article.getStatut() != StatutArticle.PUBLIE) {
            throw new IllegalArgumentException("Cet article n'est pas encore publié");
        }
        return toResponse(article, authentifie);
    }

    // Le Rédacteur voit toujours ses propres articles en entier, quel que soit leur statut
    public List<ArticleResponse> mesArticles(String emailRedacteur) {
        Redacteur redacteur = redacteurRepository.findByEmail(emailRedacteur)
                .orElseThrow(() -> new IllegalArgumentException("Rédacteur introuvable"));

        return articleRepository.findByRedacteurId(redacteur.getId())
                .stream().map(article -> toResponse(article, true)).toList();
    }

    // Tronque le contenu aux 20% premiers caractères pour un visiteur non connecté
    private String tronquer(String contenu) {
        int longueurExtrait = (int) (contenu.length() * PROPORTION_EXTRAIT);
        if (longueurExtrait >= contenu.length()) {
            return contenu;
        }
        return contenu.substring(0, longueurExtrait).trim() + "...";
    }

        private ArticleResponse toResponse(Article article, boolean authentifie) {
        String contenu = authentifie ? article.getContenu() : tronquer(article.getContenu());

        return new ArticleResponse(
            article.getId(),
            article.getTitre(),
            contenu,
            authentifie,
            article.getStatut(),
            article.getVideoUrl(),
            VideoUrlUtils.construireUrlEmbed(article.getVideoUrl()),
            article.getImageUrl(),
            article.getDatePublication(),
            article.getRedacteur().getPseudonyme(),
            article.getAdministrateur() != null ? article.getAdministrateur().getPseudonyme() : null,
            article.getRubrique().getNom(),
            likeRepository.countByArticleId(article.getId()),
            frontendUrl + "/articles/" + article.getId()
        );
    }
}