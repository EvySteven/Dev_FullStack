package bf.depeche226.service;

import bf.depeche226.dto.LoginRequest;
import bf.depeche226.dto.LoginResponse;
import bf.depeche226.entity.Administrateur;
import bf.depeche226.entity.Moderateur;
import bf.depeche226.entity.Redacteur;
import bf.depeche226.entity.UtilisateurInscrit;
import bf.depeche226.repository.UtilisateurInscritRepository;
import bf.depeche226.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurInscritRepository utilisateurInscritRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse connecter(LoginRequest request) {

        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        if (utilisateur.isSupprime()) {
            throw new IllegalArgumentException("Ce compte a été supprimé");
        }

        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect");
        }

        String role = determinerRole(utilisateur);
        String token = jwtService.genererToken(utilisateur.getEmail(), role);

        return new LoginResponse(token, utilisateur.getPseudonyme(), utilisateur.getEmail(), role);
    }

    private String determinerRole(UtilisateurInscrit utilisateur) {
        if (utilisateur instanceof Administrateur) {
            return "ADMINISTRATEUR";
        } else if (utilisateur instanceof Redacteur) {
            return "REDACTEUR";
        } else if (utilisateur instanceof Moderateur) {
            return "MODERATEUR";
        } else {
            return "LECTEUR";
        }
    }
}