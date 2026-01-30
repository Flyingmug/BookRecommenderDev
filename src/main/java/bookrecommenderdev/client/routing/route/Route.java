package bookrecommenderdev.client.routing.route;

import bookrecommenderdev.client.auth.AccessPolicy;
import bookrecommenderdev.client.routing.layout.LayoutType;

/**
 * Rappresenta una definizione di route dell'applicazione.

 * <p>Una route associa un percorso logico (path pattern) a:</p>
 * <ul>
 *   <li>una view FXML</li>
 *   <li>un layout di destinazione</li>
 *   <li>policy di accesso</li>
 * </ul>
 *
 * <p>Le route vengono registrate all'avvio dell'applicazione e
 * utilizzate dal router per risolvere le richieste di navigazione.</p>
 *
 * <p>Un path pattern avente {@code /:nome_parametro} identifica un parametro di percorso</p>
 *
 * @param pathPattern pattern del percorso (es. {@code /login}, {@code /book/:id})
 * @param fxml percorso classpath del file FXML associato alla pagina
 * @param layout layout su cui la pagina deve essere montata
 * @param access regola di accesso alla route
 */
public record Route(
    String pathPattern,
    String fxml,
    LayoutType layout,
    AccessPolicy access
) {
}