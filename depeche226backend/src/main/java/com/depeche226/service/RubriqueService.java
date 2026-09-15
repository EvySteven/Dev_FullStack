package com.depeche226.service;

import com.depeche226.domain.entity.Rubrique;
import com.depeche226.dto.rubrique.RubriqueRequest;
import com.depeche226.dto.rubrique.RubriqueResponse;
import com.depeche226.repository.RubriqueRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// Je gere la creation, la modification et la suppression des rubriques.
public class RubriqueService {

    private final RubriqueRepository rubriqueRepository;

    public RubriqueService(RubriqueRepository rubriqueRepository) {
        this.rubriqueRepository = rubriqueRepository;
    }

    public List<RubriqueResponse> findAll() {
        return rubriqueRepository.findAll(Sort.by("nom")).stream()
            .map(this::toResponse)
            .toList();
    }

    public RubriqueResponse getById(Long id) {
        return toResponse(rubriqueRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rubrique introuvable")));
    }

    @Transactional
    public RubriqueResponse create(RubriqueRequest request) {
        if (rubriqueRepository.findByNomIgnoreCase(request.nom()).isPresent()) {
            throw new IllegalArgumentException("Rubrique déjà existante");
        }
        String slug = request.nom().trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        Rubrique rubrique = new Rubrique(request.nom().trim(), slug);
        return toResponse(rubriqueRepository.save(rubrique));
    }

    @Transactional
    public RubriqueResponse update(Long id, RubriqueRequest request) {
        Rubrique rubrique = rubriqueRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rubrique introuvable"));
        rubrique.setNom(request.nom().trim());
        rubrique.setSlug(request.nom().trim().toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        return toResponse(rubriqueRepository.save(rubrique));
    }

    @Transactional
    public void delete(Long id) {
        Rubrique rubrique = rubriqueRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rubrique introuvable"));
        rubriqueRepository.delete(rubrique);
    }

    private RubriqueResponse toResponse(Rubrique rubrique) {
        return new RubriqueResponse(rubrique.getId(), rubrique.getNom(), rubrique.getSlug(), rubrique.getCreatedAt());
    }
}
