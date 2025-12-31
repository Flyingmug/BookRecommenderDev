package bookrecommenderdev.client.routing;

import bookrecommenderdev.model.ServerInterface;

/**
 * todo sServe descrizione
 */
public class AppContext {
  private final ServerInterface server;

  public AppContext(ServerInterface server) {
    this.server = server;
  }

  public ServerInterface server() { return server; }
}
