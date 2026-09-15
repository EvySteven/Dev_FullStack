package com.depeche226.controller;

import com.depeche226.domain.enums.Role;
import com.depeche226.dto.PageResponse;
import com.depeche226.dto.user.ChangePasswordRequest;
import com.depeche226.dto.user.UpdateProfileRequest;
import com.depeche226.dto.user.UserResponse;
import com.depeche226.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// J'utilise ce controller pour le profil utilisateur et la gestion admin des comptes.
// Un utilisateur gere ses infos, tandis que l'admin peut gerer les roles et les comptes.
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Je retrouve le profil courant a partir du token JWT.
    @Operation(summary = "Retourne le profil courant", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @Operation(summary = "Met à jour le profil courant", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/users/me")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUser(request));
    }

    @Operation(summary = "Change le mot de passe du profil courant", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/users/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Supprime un compte", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Liste les utilisateurs", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction dir = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        Page<UserResponse> result = userService.findAll(pageable);
        return ResponseEntity.ok(PageResponse.of(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements()));
    }

    @Operation(summary = "Met à jour les rôles d'un utilisateur", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/users/{id}/roles")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UserResponse> updateRoles(@PathVariable Long id, @RequestBody Set<Role> roles) {
        return ResponseEntity.ok(userService.updateRoles(id, roles));
    }
}
