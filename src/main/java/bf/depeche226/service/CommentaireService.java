package bf.depeche226.service;

import bf.depeche226.dto.CommentaireCreateRequest;
import bf.depeche226.dto.CommentaireResponse;
import bf.depeche226.entity.*;
import bf.depeche226.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final ArticleRepository articleRepository;
    private final UtilisateurInscritRepository utilisateurInscritRepository;
    private final ModerateurRepository moderateurRepository;

    // Un UtilisateurInscrit écrit un commentaire sur un article publié
    @Transactional
    public CommentaireResponse ecrireCommentaire(String emailAuteur, Long articleId, CommentaireCreateRequest request) {

        UtilisateurInscrit auteur = utilisateurInscritRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));

        // On ne peut commenter que sur un article déjà publié
        if (article.getStatut() != StatutArticle.PUBLIE) {
            throw new IllegalArgumentException("Impossible de commenter un article non publié");
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(request.getContenu());
        commentaire.setArticle(article);
        commentaire.setAuteur(auteur);
        commentaire.setStatut(StatutCommentaire.EN_ATTENTE);

        Commentaire saved = commentaireRepository.save(commentaire);
        return toResponse(saved);
    }

    // Liste publique : uniquement les commentaires approuvés d'un article
    public List<CommentaireResponse> listerApprouves(Long articleId) {
        return commentaireRepository.findByArticleIdAndStatut(articleId, StatutCommentaire.APPROUVE)
                .stream().map(this::toResponse).toList();
    }

    // Le Modérateur consulte tous les commentaires en attente, tous articles confondus
    public List<CommentaireResponse> listerEnAttente() {
        return commentaireRepository.findByStatut(StatutCommentaire.EN_ATTENTE)
                .stream().map(this::toResponse).toList();
    }

    // Le Modérateur approuve un commentaire en attente
    @Transactional
    public CommentaireResponse approuver(String emailModerateur, Long commentaireId) {

        Moderateur moderateur = moderateurRepository.findByEmail(emailModerateur)
                .orElseThrow(() -> new IllegalArgumentException("Modérateur introuvable"));

        Commentaire commentaire = commentaireRepository.findById(commentaireId)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));

        if (commentaire.getStatut() != StatutCommentaire.EN_ATTENTE) {
            throw new IllegalArgumentException("Seul un commentaire en attente peut être approuvé");
        }

        commentaire.setStatut(StatutCommentaire.APPROUVE);
        commentaire.setModerateur(moderateur);

        return toResponse(commentaireRepository.save(commentaire));
    }

    // Le Modérateur supprime un commentaire (approuvé ou en attente) — suppression logique
    @Transactional
    public CommentaireResponse supprimer(String emailModerateur, Long commentaireId) {

        Moderateur moderateur = moderateurRepository.findByEmail(emailModerateur)
                .orElseThrow(() -> new IllegalArgumentException("Modérateur introuvable"));

        Commentaire commentaire = commentaireRepository.findById(commentaireId)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable"));

        commentaire.setStatut(StatutCommentaire.SUPPRIME);
        commentaire.setModerateur(moderateur);

        return toResponse(commentaireRepository.save(commentaire));
    }

    private CommentaireResponse toResponse(Commentaire c) {
        return new CommentaireResponse(
            c.getId(),
            c.getContenu(),
            c.getDate(),
            c.getStatut(),
            c.getAuteur().getPseudonyme(),
            c.getArticle().getId(),
            c.getModerateur() != null ? c.getModerateur().getPseudonyme() : null
        );
    }
}