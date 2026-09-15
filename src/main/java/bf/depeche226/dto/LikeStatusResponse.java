package bf.depeche226.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeStatusResponse {
    private boolean like;        // true si l'utilisateur courant a liké cet article
    private long nombreLikes;    // total de likes sur l'article
}