package bookrecommenderdev.routing.route;

import bookrecommenderdev.routing.layout.LayoutType;
import bookrecommenderdev.routing.Router;

/**
 * Oggetto rappresentante un percorso utilizzabile dal {@link Router Router}.
 * @param pathPattern Stringa del percorso
 * @param fxml Nome del file <i>.fxml</i> corrispondente
 * @param layout Tipo di layout utilizzato
 */
public record Route(
    String pathPattern,
    String fxml,
    LayoutType layout,
    boolean requiresAuth
) {
}