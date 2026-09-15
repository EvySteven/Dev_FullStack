package bf.depeche226.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UtilisateurResponse {
    private Long id;
    private String pseudonyme;
    private String email;
    private LocalDate dateInscription;
    private Integer age;
    private List<String> centresInteret;
    private boolean abonneNewsletter;
}