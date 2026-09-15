package bf.depeche226.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentaireCreateRequest {

    @NotBlank(message = "Le commentaire ne peut pas être vide")
    private String contenu;
}