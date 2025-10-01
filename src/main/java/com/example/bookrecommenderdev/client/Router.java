package com.example.bookrecommenderdev.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Router {
  private static StackPane rootContainer;
  private static final Map<String, String> routes = new HashMap<>();
  private static AppContext appContext;

  /**
   * Metodo di inizializzazione per il router con il riferimento
   * @param container Stack interessato
   */
  public static void init(StackPane container, AppContext ctx) {
    rootContainer = container;
    appContext = ctx;

    // Registrazione delle pagine
    routes.put("/", "home-view.fxml");
    routes.put("/search/:query", "searchResults-view.fxml");
    routes.put("/book/:query", "book-view.fxml");
    routes.put("/login", "login-view.fxml");
    routes.put("/registration", "registration-view.fxml");
    routes.put("/profile", "profile-view.fxml");
    routes.put("/libraries", "libraries-view.fxml");
    routes.put("/libraries/:query", "library-view.fxml");
    // error routes?

  }


  /**
   * Metodo incaricato di gestire la navigazione tra pagine logiche.
   * @param path Percorso della pagina interessata
   */
  public static void go(String path) {
    for (Map.Entry<String, String> entry: routes.entrySet()) {
      String route = entry.getKey();
      String fxml = entry.getValue();

      Map<String, String> params = matchRoute(route, path);
      if (params != null) {
        loadPage(fxml, params);
        return;
      }
    }
    System.err.println("No route found for " + path);
    throw new RuntimeException();
  }

  /**
   * Metodo helper per la gestione delle pagine. odadasdasdasdaseffdc
   * @param route nome del percorso
   * @param path percorso richiesto
   */
  private static Map<String, String> matchRoute(String route, String path) {
    String[] routeParts = route.split("/");
    String[] pathParts = path.split("/");

    if (routeParts.length != pathParts.length) return null;

    Map<String, String> params = new HashMap<>();
    for (int i = 0; i < routeParts.length; i++) {
      if (routeParts[i].startsWith(":")) {
        params.put(routeParts[i].substring(1), pathParts[i]);
      } else if (!routeParts[i].equals(pathParts[i])) {
        return null;
      }
    }

    return params;
  }

  /**
   * Metodo helper per caricare una pagina.
   *
   * @param fxml   Nome del file .fxml
   * @param params parametri da passare
   */
  private static void loadPage(String fxml, Map<String, String> params) {
    try {
      FXMLLoader loader = new FXMLLoader(Router.class.getResource(fxml)); // "/com/example/bookrecommenderdev/client" + fxml
      Parent root = loader.load();
      Object controller = loader.getController();

      // Assegnazione dei parametri alle pagine che li richiedono
      if (controller instanceof Routable routable) {
        routable.onRoute(params, appContext);
      }

      rootContainer.getChildren().setAll(root);

    } catch (IOException e) {
      System.err.println("Error in loading page.");
      e.printStackTrace();
    }
  }
}
