package bookrecommenderdev.client.routing.route;

import java.util.Map;


/**
 * Rappresenta il risultato di una risoluzione di route.
 *
 * <p>Un {@code RouteMatch} viene prodotto dal router quando un percorso
 * richiesto corrisponde a una {@link Route} registrata.</p>
 *
 * <p>Contiene:</p>
 * <ul>
 *   <li>la route risolta</li>
 *   <li>i parametri estratti dal percorso</li>
 * </ul>
 *
 * @param route route che ha generato il match
 * @param params mappa dei parametri estratti dal percorso
 */
public record RouteMatch(Route route, Map<String, String> params) {}
