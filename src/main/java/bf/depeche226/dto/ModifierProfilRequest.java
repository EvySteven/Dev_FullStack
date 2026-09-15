package bf.depeche226.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ModifierProfilRequest {

    // Champs modifiables du profil (pas l'email, qui sert d'identifiant unique)
    private String pseudonyme;

    @Min(value = 13, message = "Âge minimum requis : 13 ans")
    private Integer age;

    private List<String> centresInteret;

    private Boolean abonneNewsletter;
}
