package bookrecommenderdev.client.routing.route;

import bookrecommenderdev.client.routing.AppContext;

import java.util.Map;

/**
 * Interfaccia che identifica un controller di pagina navigabile.
 *
 * <p>Ogni pagina dell'applicazione che può essere caricata dal {@code Router}
 * può implementare questa interfaccia.</p>
 *
 */
public interface Routable {

  /**
   * Metodo invocato dal {@code Router} quando la pagina viene caricata.
   * <p>
   * Consente alle classi ereditanti di ottenere informazioni rilevanti dal sistema di routing.
   *
   * @param params  parametri estratti dal percorso
   * @param context contesto applicativo client condiviso
   * @param state   stato di navigazione opzionale (può contenere dati aggiuntivi)
   */
  void onRoute(Map<String, String> params, AppContext context, Object state);
}