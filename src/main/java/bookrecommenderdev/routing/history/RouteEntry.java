package bookrecommenderdev.routing.history;

import bookrecommenderdev.routing.animation.TransitionAnimation;

/**
 * Oggetto rappresentante la visita ad un percorso con la relativa transizione utilizzata per raggiungerlo.
 * @param path Percorso
 * @param transition Transizione utilizzata
 */
public record RouteEntry(String path, TransitionAnimation transition, Object state) {
}
