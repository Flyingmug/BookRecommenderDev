package bookrecommenderdev.client.routing;

import bookrecommenderdev.model.ServerInterface;

/**
 * Contesto applicativo condiviso lato client.
 *
 * <p>Questa classe funge da contenitore per le dipendenze e i servizi
 * globali utilizzati dall'applicazione client, rendendoli accessibili
 * ai vari componenti dell'interfaccia grafica (router, controller, servizi).</p>
 *
 * <h2>Responsabilità</h2>
 * <ul>
 *   <li>Centralizzare l'accesso ai servizi remoti</li>
 *   <li>Evitare dipendenze statiche dirette verso il server</li>
 *   <li>Facilitare testabilità e manutenibilità del client</li>
 * </ul>
 */
public class AppContext {
  private final ServerInterface server;

  /**
   * Crea un nuovo contesto applicativo.
   *
   * @param server implementazione remota dell'interfaccia server
   */
  public AppContext(ServerInterface server) {
    this.server = server;
  }

  /**
   * Restituisce l'interfaccia remota del server associata al contesto.
   *
   * <p>Il valore restituito può essere utilizzato dai controller
   * e dai servizi client per invocare operazioni remote.</p>
   *
   * @return Interfaccia remota del server
   */
  public ServerInterface server() { return server; }
}
