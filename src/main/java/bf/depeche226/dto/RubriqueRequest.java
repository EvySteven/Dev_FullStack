package bf.depeche226.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RubriqueRequest {
    @NotBlank(message = "Le nom de la rubrique est obligatoire")
    private String nom;
}