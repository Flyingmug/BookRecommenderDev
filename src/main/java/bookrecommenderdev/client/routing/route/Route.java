package bookrecommenderdev.client.routing.route;

import bookrecommenderdev.client.auth.AccessPolicy;
import bookrecommenderdev.client.routing.layout.LayoutType;
import bookrecommenderdev.client.routing.Router;

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
    AccessPolicy access
) {
}