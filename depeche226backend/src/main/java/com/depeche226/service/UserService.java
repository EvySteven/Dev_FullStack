package com.depeche226.service;

import com.depeche226.domain.entity.User;
import com.depeche226.domain.enums.Role;
import com.depeche226.dto.auth.AuthResponse;
import com.depeche226.dto.auth.RegisterRequest;
import com.depeche226.dto.user.ChangePasswordRequest;
import com.depeche226.dto.user.UpdateProfileRequest;
import com.depeche226.dto.user.UserResponse;
import com.depeche226.repository.UserRepository;
import com.depeche226.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// Je gere ici l'inscription, la connexion, le profil et les droits utilisateur.
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String pseudonyme = request.pseudonyme().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        if (userRepository.existsByPseudonymeIgnoreCase(pseudonyme)) {
            throw new IllegalArgumentException("Pseudonyme déjà utilisé");
        }

        User user = User.builder()
            .pseudonyme(pseudonyme)
            .email(email)
            .passwordHash(passwordEncoder.encode(request.password()))
            .centreInteret(request.centreInteret())
            .newsletterSubscribed(false)
            .enabled(true)
            .roles(Set.of(Role.LECTEUR))
            .build();

        return userRepository.save(user);
    }

    public AuthResponse login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, user.getEmail(), user.getPseudonyme());
    }

    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(UpdateProfileRequest request) {
        User user = getAuthenticatedUser();
        if (request.pseudonyme() != null && !request.pseudonyme().isBlank()) {
            user.setPseudonyme(request.pseudonyme().trim());
        }
        if (request.centreInteret() != null) {
            user.setCentreInteret(request.centreInteret());
        }
        if (request.newsletterSubscribed() != null) {
            user.setNewsletterSubscribed(request.newsletterSubscribed());
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Ancien mot de passe invalide");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAllWithRoles(pageable).map(this::toResponse);
    }

    public UserResponse getById(Long id) {
        return toResponse(userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable")));
    }

    @Transactional
    public UserResponse updateRoles(Long userId, Set<Role> roles) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        user.setRoles(roles);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        userRepository.delete(user);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        String email = authentication.getName();
        return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getPseudonyme(),
            user.getEmail(),
            user.getCentreInteret(),
            user.isNewsletterSubscribed(),
            user.isEnabled(),
            user.getRoles(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
