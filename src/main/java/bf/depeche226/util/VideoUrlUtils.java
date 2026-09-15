package bf.depeche226.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Utilitaire pour extraire l'identifiant d'une vidéo YouTube depuis différents formats d'URL
// et construire une URL d'intégration (embed) utilisable dans une <iframe>.
public class VideoUrlUtils {

    // Couvre les formats courants :
    // https://www.youtube.com/watch?v=XXXXXXXXXXX
    // https://youtu.be/XXXXXXXXXXX
    // https://www.youtube.com/embed/XXXXXXXXXXX
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
        "(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)([a-zA-Z0-9_-]{11})"
    );

    // Empêche l'instanciation : classe utilitaire, méthodes statiques uniquement
    private VideoUrlUtils() {
    }

    // Extrait l'id de la vidéo depuis l'URL fournie, ou null si l'URL ne correspond à aucun format connu
    public static String extraireIdVideo(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    // Construit l'URL d'intégration prête pour une <iframe>, ou null si l'URL d'origine n'est pas reconnue
    public static String construireUrlEmbed(String url) {
        String id = extraireIdVideo(url);
        return id != null ? "https://www.youtube.com/embed/" + id : null;
    }
}