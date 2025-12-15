package bookrecommenderdev.routing;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

import static bookrecommenderdev.routing.Animations.fadeTransition;
import static bookrecommenderdev.routing.Animations.slideTransition;

public class Router {
  private static StackPane rootContainer;
  private static Map<String, Route> routes;
  private static AppContext appContext;

  private static final HistoryManager<Node> history = new HistoryManager<>(3);
  private static Route currentRoute;

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

    // todo error routes?
  }


  /**
   * Metodo incaricato di gestire la navigazione tra pagine logiche.
   * @param path Percorso della pagina interessata
   */
  public static void go(String path) {
    go(path, TransitionAnimation.DEFAULT);
  }

  /**
   * Metodo incaricato di gestire la navigazione tra pagine logiche.
   * @param path Percorso della pagina interessata
   * @param transition Tipologia di animazione da utilizzare
   */
  public static void go(String path, TransitionAnimation transition) {
    for (Map.Entry<String, Route> entry: routes.entrySet()) {
      String routeName = entry.getKey();
      Route route = entry.getValue();
      String fxml = route.fxml();

      Map<String, String> params = matchRoute(routeName, path);
      if (params != null) {
        loadPage(fxml, params, transition);

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
   * Metodo helper per la gestione delle pagine e degli eventuali parametri di percorso.
   * todo chiarezza
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
   * @param fxml Nome del file .fxml
   * @param params Parametri da passare alla pagina
   */
  private static void loadPage(String fxml, Map<String, String> params, TransitionAnimation transition) {
    try {
      FXMLLoader loader = new FXMLLoader(Router.class.getResource("/bookrecommenderdev/client/" + fxml)); // "/com/example/bookrecommenderdev/client" + fxml
      Parent newRoot = loader.load();
      Object controller = loader.getController();



      //
      // Work in progress.
      //
      history.visit(newRoot);




      // Assegnazione dei parametri alle pagine che li richiedono
      if (controller instanceof Routable routable) {
        routable.onRoute(params, appContext);
      }

      // Gestione della transizione
      switch(transition) {
        case FADE_INTO:
          fadeTransition(rootContainer, newRoot, Duration.millis(115));
          break;
        case SIDE_STACK:
          slideTransition(rootContainer, newRoot, Direction.LEFT, Duration.millis(250));
          break;
        default:
          rootContainer.getChildren().setAll(newRoot);

      }

    } catch (IOException e) {
      System.err.println("Loading Page Error: Error in loading page.");
      e.printStackTrace();
    }
  }

  public static void goForward() {
    Optional<Node> forward = history.forward();
    if (forward.isEmpty()) {
      return; // do nothing?
    }
    Node node = forward.get();
    slideTransition(rootContainer, node, Direction.LEFT, Duration.millis(500));
  }

  public static void goBack() {
    Optional<Node> back = history.back();
    if (back.isEmpty()) return; // fixme do nothing?

    Node node = back.get();
    slideTransition(rootContainer, node, Direction.RIGHT, Duration.millis(500));
  }

  private static void stackPage(String fxml, Map<String, String> params) {
    try {
      FXMLLoader loader = new FXMLLoader(Router.class.getResource("/bookrecommenderdev/client/" + fxml)); // "/com/example/bookrecommenderdev/client" + fxml
      Parent page = loader.load();
      Object controller = loader.getController();

      // Assegnazione dei parametri alle pagine che li richiedono
      if (controller instanceof Routable routable) {
        routable.onRoute(params, appContext);
      }

      rootContainer.getChildren().add(page);

    } catch (IOException e) {
      System.err.println("Page Stacking Error: Error in loading page.");
      e.printStackTrace();
    }
  }

}
