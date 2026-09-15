package bf.depeche226.service;

import bf.depeche226.dto.*;
import bf.depeche226.entity.UtilisateurInscrit;
import bf.depeche226.repository.UtilisateurInscritRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtilisateurInscritService {

    private final UtilisateurInscritRepository utilisateurInscritRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UtilisateurResponse inscrire(InscriptionRequest request) {

        if (utilisateurInscritRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        UtilisateurInscrit utilisateur = new UtilisateurInscrit();
        utilisateur.setPseudonyme(request.getPseudonyme());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setAge(request.getAge());
        utilisateur.setCentresInteret(request.getCentresInteret());
        utilisateur.setAbonneNewsletter(request.isAbonneNewsletter());

        UtilisateurInscrit saved = utilisateurInscritRepository.save(utilisateur);

        return toResponse(saved);
    }

    @Transactional
    public UtilisateurResponse modifierProfil(String email, ModifierProfilRequest request) {

        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        if (request.getPseudonyme() != null) {
            utilisateur.setPseudonyme(request.getPseudonyme());
        }
        if (request.getAge() != null) {
            utilisateur.setAge(request.getAge());
        }
        if (request.getCentresInteret() != null) {
            utilisateur.setCentresInteret(request.getCentresInteret());
        }
        if (request.getAbonneNewsletter() != null) {
            utilisateur.setAbonneNewsletter(request.getAbonneNewsletter());
        }

        UtilisateurInscrit saved = utilisateurInscritRepository.save(utilisateur);

        return toResponse(saved);
    }

    @Transactional
    public void changerMotDePasse(String email, ChangerMotDePasseRequest request) {

        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(request.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurInscritRepository.save(utilisateur);
    }

    // Petit utilitaire pour éviter de répéter la construction du DTO dans chaque méthode
    private UtilisateurResponse toResponse(UtilisateurInscrit u) {
        return new UtilisateurResponse(
            u.getId(),
            u.getPseudonyme(),
            u.getEmail(),
            u.getDateInscription(),
            u.getAge(),
            u.getCentresInteret(),
            u.isAbonneNewsletter()
        );
    }
}