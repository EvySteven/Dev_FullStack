package bf.depeche226.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Clé secrète utilisée pour signer les tokens.
    // À terme, à externaliser dans application.properties (ne jamais coder en dur en production).
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
        "changeCetteCleSecreteEnProductionAuMoins32Caracteres".getBytes()
    );

    // Durée de validité du token : 24h ici
    private final long expirationMs = 1000 * 60 * 60 * 24;

    // Génère un token JWT à partir de l'email (subject) ET du rôle de l'utilisateur (claim personnalisé)
    public String genererToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    // Extrait l'email (subject) contenu dans le token
    public String extraireEmail(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    // Extrait le rôle contenu dans le token
    public String extraireRole(String token) {
        return extraireClaim(token, claims -> claims.get("role", String.class));
    }

    // Vérifie que le token est valide pour cet email et non expiré
    public boolean estValide(String token, String email) {
        final String emailDuToken = extraireEmail(token);
        return emailDuToken.equals(email) && !estExpire(token);
    }

    private boolean estExpire(String token) {
        return extraireClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraireClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}