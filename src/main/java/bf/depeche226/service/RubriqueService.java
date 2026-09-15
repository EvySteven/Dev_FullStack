package bf.depeche226.service;

import bf.depeche226.dto.RubriqueRequest;
import bf.depeche226.dto.RubriqueResponse;
import bf.depeche226.entity.Rubrique;
import bf.depeche226.repository.RubriqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RubriqueService {

    private final RubriqueRepository rubriqueRepository;

    @Transactional
    public RubriqueResponse creer(RubriqueRequest request) {
        if (rubriqueRepository.findByNom(request.getNom()).isPresent()) {
            throw new IllegalArgumentException("Cette rubrique existe déjà");
        }
        Rubrique rubrique = new Rubrique();
        rubrique.setNom(request.getNom());
        Rubrique saved = rubriqueRepository.save(rubrique);
        return new RubriqueResponse(saved.getId(), saved.getNom());
    }

    public List<RubriqueResponse> lister() {
        return rubriqueRepository.findAll().stream()
                .map(r -> new RubriqueResponse(r.getId(), r.getNom()))
                .toList();
    }
}
