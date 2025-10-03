package com.example.bookrecommenderdev.routing;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Router {
  private static StackPane rootContainer;
  private static Map<String, Route> routes;
  private static AppContext appContext;

  /**
   * Metodo di inizializzazione per il router con il riferimento.
   * La lista di percorsi è un hashmap contenente coppie del tipo ("/nome-percorso", r: Route);
   * Il nomi dei percorsi saranno utilizzati dal metodo {@link #go(String) go} durante la ricerca della
   * presenza del percorso richiesto.
   * @param container Stack interessato.
   * @param ctx Contesto dell'applicazione.
   * @param routeList Hashmap di percorsi.
   */
  public static void init(StackPane container, AppContext ctx, Map<String, Route> routeList) {
    rootContainer = container;
    appContext = ctx;
    routes = routeList;

    // error routes?

  }


  /**
   * Metodo incaricato di gestire la navigazione tra pagine logiche.
   * @param path Percorso della pagina interessata.
   */
  public static void go(String path) {
    for (Map.Entry<String, Route> entry: routes.entrySet()) {
      String routeName = entry.getKey();
      Route route = entry.getValue();
      String fxml = route.fxml();

      Map<String, String> params = matchRoute(routeName, path);
      if (params != null) {
        loadPage(fxml, params);

        switch (route.group()) {
          case DEFAULT -> System.out.println("TestA");
          case WITH_SEARCH -> System.out.println("TestB");
          case AUTH -> System.out.println("TestC");
        }
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
   * Metodo helper per caricare una pagina utilizzando il nome del file corrispondente
   * al percorso, come specificato nei percorsi durante l'inizializzazione.
   * @param fxml Nome del file .fxml.
   * @param params Parametri da passare alla pagina.
   */
  private static void loadPage(String fxml, Map<String, String> params) {
    try {
      FXMLLoader loader = new FXMLLoader(Router.class.getResource("/com/example/bookrecommenderdev/client/" + fxml)); // "/com/example/bookrecommenderdev/client" + fxml
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
