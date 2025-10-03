package com.example.bookrecommenderdev.routing;

/**
 * Identifica gli attributi di una pagina logica.
 * @param fxml Nome del file .fxml corrispondente
 * @param group Gruppo di pagine di appartenenza
 */
public class Route {
  private final String fxml;
  private final RouteGroup group;

  // Constructor with explicit group
  public Route(String fxml, RouteGroup group) {
    this.fxml = fxml;
    this.group = group;
  }

  // Constructor with default group
  public Route(String fxml) {
    this(fxml, RouteGroup.DEFAULT);
  }

  public String fxml() { return fxml; }
  public RouteGroup group() { return group; }
}