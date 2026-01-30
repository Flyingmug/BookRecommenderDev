package bookrecommenderdev.client.routing.history;

import bookrecommenderdev.client.routing.animation.TransitionAnimation;

/**
 /**
 * Rappresenta una singola voce della cronologia di navigazione.
 *
 * <p>Un {@code RouteEntry} memorizza tutte le informazioni necessarie
 * per poter ripristinare una navigazione precedente o successiva.</p>
 *
 * <p>Ogni voce include:</p>
 * <ul>
 *   <li>il percorso della route</li>
 *   <li>la transizione utilizzata</li>
 *   <li>lo stato di navigazione associato</li>
 * </ul>
 *
 * <p>Questa classe viene utilizzata dal {@link HistoryManager}
 * per implementare la navigazione avanti e indietro.</p>
 *
 * @param path percorso della route
 * @param transition transizione utilizzata nella navigazione verso la route
 * @param state stato di navigazione associato; può essere {@code null}
 */
public record RouteEntry(String path, TransitionAnimation transition, Object state) {
}
