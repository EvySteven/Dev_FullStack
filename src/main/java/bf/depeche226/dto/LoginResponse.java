package bf.depeche226.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String pseudonyme;
    private String email;
    private String role;
}