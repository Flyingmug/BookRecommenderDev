package com.example.bookrecommenderdev.routing;

import com.example.bookrecommenderdev.client.controller.NavbarController;
import com.example.bookrecommenderdev.model.Utente;
import com.example.bookrecommenderdev.server.ServerInterface;

public class AppContext {
  private final ServerInterface server;
  private final NavbarController navbar;
  private Utente currentUser;

  public AppContext(ServerInterface server, Utente currentUser, NavbarController navbar) {
    this.server = server;
    this.currentUser = currentUser;
    this.navbar = navbar;

  }

  public ServerInterface server() { return server; }
  public Utente user() { return currentUser; }
  public NavbarController navbar() { return navbar; }

  public void setUser(Utente u) { currentUser = u; }
}
