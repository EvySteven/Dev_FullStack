package bf.depeche226.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class InscriptionRequest {

    @NotBlank(message = "Le pseudonyme est obligatoire")
    @Size(min = 3, max = 30)
    private String pseudonyme;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @Min(value = 13, message = "Âge minimum requis : 13 ans")
    private Integer age;

    private List<String> centresInteret;

    private boolean abonneNewsletter = false;
}