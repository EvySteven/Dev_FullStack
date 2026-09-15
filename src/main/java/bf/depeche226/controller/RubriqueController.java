package bf.depeche226.controller;

import bf.depeche226.dto.RubriqueRequest;
import bf.depeche226.dto.RubriqueResponse;
import bf.depeche226.service.RubriqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rubriques")
@RequiredArgsConstructor
public class RubriqueController {

    private final RubriqueService rubriqueService;

    // Création réservée à l'Administrateur (voir SecurityConfig)
    @PostMapping
    public ResponseEntity<RubriqueResponse> creer(@Valid @RequestBody RubriqueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rubriqueService.creer(request));
    }

    // Lecture publique : tout le monde doit pouvoir voir les rubriques disponibles
    @GetMapping
    public ResponseEntity<List<RubriqueResponse>> lister() {
        return ResponseEntity.ok(rubriqueService.lister());
    }
}