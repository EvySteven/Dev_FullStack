package bf.depeche226.config;

import bf.depeche226.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()

                // Comptes Rédacteur/Modérateur/Administrateur : réservé à l'Administrateur
                .requestMatchers("/api/administration/**").hasRole("ADMINISTRATEUR")

                // Rubriques : lecture publique, création réservée à l'Administrateur
                .requestMatchers(HttpMethod.GET, "/api/rubriques").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/rubriques").hasRole("ADMINISTRATEUR")

                // Commentaires : lecture publique, écriture pour tout utilisateur connecté,
                // modération réservée au Modérateur
                .requestMatchers(HttpMethod.GET, "/api/articles/*/commentaires").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/articles/*/commentaires").authenticated()
                .requestMatchers("/api/moderation/**").hasRole("MODERATEUR")

                // Articles : routes spécifiques d'abord, avant la règle GET générique plus bas
                .requestMatchers(HttpMethod.GET, "/api/articles/moi").hasRole("REDACTEUR")
                .requestMatchers(HttpMethod.POST, "/api/articles").hasRole("REDACTEUR")
                .requestMatchers(HttpMethod.PUT, "/api/articles/*/soumettre").hasRole("REDACTEUR")
                .requestMatchers(HttpMethod.PUT, "/api/articles/*/valider").hasRole("ADMINISTRATEUR")
                .requestMatchers(HttpMethod.PUT, "/api/articles/*/refuser").hasRole("ADMINISTRATEUR")
                .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()

                // Likes : authentification requise pour liker/retirer/consulter son propre statut
                .requestMatchers("/api/articles/*/likes/**").authenticated()
                // Documentation Swagger : accessible publiquement (interface de test uniquement, pas de données sensibles exposées)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}