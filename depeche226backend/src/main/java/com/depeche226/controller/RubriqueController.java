package com.depeche226.controller;

import com.depeche226.dto.rubrique.RubriqueRequest;
import com.depeche226.dto.rubrique.RubriqueResponse;
import com.depeche226.service.RubriqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Je gere ici les categories de l'edito.
// Elles me servent a classer les articles et a rendre les filtres plus clairs.
@RestController
@RequestMapping("/api/v1")
public class RubriqueController {

    private final RubriqueService rubriqueService;

    public RubriqueController(RubriqueService rubriqueService) {
        this.rubriqueService = rubriqueService;
    }

    @Operation(summary = "Liste les rubriques")
    @GetMapping("/categories")
    public ResponseEntity<List<RubriqueResponse>> listCategories() {
        return ResponseEntity.ok(rubriqueService.findAll());
    }

    @Operation(summary = "Création d'une rubrique", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<RubriqueResponse> createCategory(@Valid @RequestBody RubriqueRequest request) {
        return ResponseEntity.ok(rubriqueService.create(request));
    }

    @Operation(summary = "Modification d'une rubrique", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<RubriqueResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody RubriqueRequest request) {
        return ResponseEntity.ok(rubriqueService.update(id, request));
    }

    @Operation(summary = "Suppression d'une rubrique", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        rubriqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
