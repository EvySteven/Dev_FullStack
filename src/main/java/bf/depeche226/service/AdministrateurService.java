package bf.depeche226.service;

import bf.depeche226.dto.InscriptionRequest;
import bf.depeche226.dto.ModifierProfilRequest;
import bf.depeche226.dto.UtilisateurResponse;
import bf.depeche226.entity.*;
import bf.depeche226.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdministrateurService {

    private final UtilisateurInscritRepository utilisateurInscritRepository;
    private final RedacteurRepository redacteurRepository;
    private final ModerateurRepository moderateurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UtilisateurResponse creerCompteRedacteur(InscriptionRequest request) {
        verifierEmailDisponible(request.getEmail());
        Redacteur redacteur = new Redacteur();
        remplirChamps(redacteur, request);
        return toResponse(redacteurRepository.save(redacteur));
    }

    @Transactional
    public UtilisateurResponse creerCompteModerateur(InscriptionRequest request) {
        verifierEmailDisponible(request.getEmail());
        Moderateur moderateur = new Moderateur();
        remplirChamps(moderateur, request);
        return toResponse(moderateurRepository.save(moderateur));
    }

    @Transactional
    public UtilisateurResponse creerCompteAdministrateur(InscriptionRequest request) {
        verifierEmailDisponible(request.getEmail());
        Administrateur administrateur = new Administrateur();
        remplirChamps(administrateur, request);
        return toResponse(administrateurRepository.save(administrateur));
    }

    // Modifie le profil de N'IMPORTE QUEL compte (Lecteur, Rédacteur, Modérateur, Administrateur)
    // Se limite aux champs de profil : pas de changement d'email ni de rôle ici.
    @Transactional
    public UtilisateurResponse modifierCompte(Long id, ModifierProfilRequest request) {

        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable"));

        if (utilisateur.isSupprime()) {
            throw new IllegalArgumentException("Ce compte a été supprimé");
        }

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

        return toResponse(utilisateurInscritRepository.save(utilisateur));
    }

    // Suppression douce : le compte est anonymisé et invalidé, mais son contenu
    // (articles, commentaires, likes) reste intact et rattaché, désormais affiché
    // comme provenant d'un "Compte supprimé".
    @Transactional
    public void supprimerCompte(Long id) {

        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable"));

        if (utilisateur.isSupprime()) {
            throw new IllegalArgumentException("Ce compte est déjà supprimé");
        }

        utilisateur.setPseudonyme("Compte supprimé");
        // Email anonymisé mais unique (contrainte d'unicité toujours respectée),
        // et impossible à deviner pour éviter toute réutilisation frauduleuse.
        utilisateur.setEmail("supprime-" + UUID.randomUUID() + "@depeche226.local");
        // Mot de passe remplacé par une valeur aléatoire hashée : connexion définitivement impossible
        utilisateur.setMotDePasse(passwordEncoder.encode(UUID.randomUUID().toString()));
        utilisateur.setSupprime(true);

        utilisateurInscritRepository.save(utilisateur);
    }

    private void verifierEmailDisponible(String email) {
        if (utilisateurInscritRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }
    }

    private void remplirChamps(UtilisateurInscrit utilisateur, InscriptionRequest request) {
        utilisateur.setPseudonyme(request.getPseudonyme());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setAge(request.getAge());
        utilisateur.setCentresInteret(request.getCentresInteret());
        utilisateur.setAbonneNewsletter(request.isAbonneNewsletter());
    }

    private UtilisateurResponse toResponse(UtilisateurInscrit u) {
        return new UtilisateurResponse(
            u.getId(), u.getPseudonyme(), u.getEmail(), u.getDateInscription(),
            u.getAge(), u.getCentresInteret(), u.isAbonneNewsletter()
        );
    }
}