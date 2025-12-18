package bookrecommenderdev.routing;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

import static bookrecommenderdev.routing.Animations.*;

public class Router {
  private static StackPane rootContainer;
  private static Map<String, Route> routes;
  private static AppContext appContext;

  private static RouteEntry loadedEntry = null;
  private static final HistoryManager<RouteEntry> history = new HistoryManager<>(5);
  private static final BooleanProperty navigationLocked = new SimpleBooleanProperty(false);
  private static final BooleanProperty canBack = new SimpleBooleanProperty(false);
  private static final BooleanProperty canForward = new SimpleBooleanProperty(false);

  public static ReadOnlyBooleanProperty canBack() { return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canBack); }
  public static ReadOnlyBooleanProperty canForward() { return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canForward); }

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

    // todo loading screen linked to server loadings?
  }


  /**
   * Consente la navigazione tra pagine.
   * @param path Percorso della pagina interessata
   */
  public static void go(String path) { resolve(path, TransitionAnimation.DEFAULT, true); }

  /**
   * Consente la navigazione tra pagine e la selezione del tipo di transizione da utilizzare.
   * @param path Percorso della pagina interessata
   * @param transition Tipologia di animazione da utilizzare
   */
  public static void go(String path, TransitionAnimation transition) {
    resolve(path, transition, true);
  }

  /**
   * Metodo incaricato di gestire la navigazione tra pagine logiche.
   * @param path Percorso della pagina interessata
   * @param transition Tipologia di animazione da utilizzare
   * @param pushHistory Salvataggio della visita alla pagina su memoria
   */
  private static void resolve(String path, TransitionAnimation transition, boolean pushHistory) {
    System.out.println("ROUTER resolve request received");
    if(navigationLocked.get()) {
      System.out.println("ROUTER Animation locked");
      return;
    }

    // verifica se il percorso è caricato
    if (loadedEntry != null && loadedEntry.path().equals(path)) {
      return;
    }

    for (Map.Entry<String, Route> entry: routes.entrySet()) {
      String routeName = entry.getKey();
      Route route = entry.getValue();
      String fxml = route.fxml();

      Map<String, String> params = matchRoute(routeName, path);

      if (params != null) {
        navigationLocked.set(true);

        loadPage(
            fxml,
            params,
            transition,
            () -> {
              RouteEntry newEntry = new RouteEntry(path, transition);

              if(pushHistory) history.visit(newEntry);
              loadedEntry = newEntry;

              navigationLocked.set(false);
            }
        );

//        switch (route.group()) {
//          case DEFAULT -> System.out.println("TestRouteGroupA");
//          case WITH_SEARCH -> System.out.println("TestRouteGroupB");
//          case AUTH -> System.out.println("TestRouteGroupC");
//        }
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
   * @param params Parametri di percorso da passare alla pagina
   */
  private static void loadPage(String fxml, Map<String, String> params, TransitionAnimation transition, Runnable onFinished) {
    try {
      FXMLLoader loader = new FXMLLoader(Router.class.getResource("/bookrecommenderdev/client/" + fxml)); // "/com/example/bookrecommenderdev/client" + fxml
      Parent newRoot = loader.load();
      Object controller = loader.getController();

      // Assegnazione dei parametri alle pagine che li richiedono
      if (controller instanceof Routable routable) {
        routable.onRoute(params, appContext);
      }

      // Gestione della transizione
      switch(transition) {
        case FADE_INTO ->
          fadeTransition(rootContainer, newRoot, Duration.millis(115), onFinished);
        case TOP_SLIDE ->
          slideTransition(rootContainer, newRoot, Direction.TOP, Duration.millis(150), onFinished);
        case RIGHT_SLIDE ->
            slideTransition(rootContainer, newRoot, Direction.RIGHT, Duration.millis(150), onFinished);
        case BOTTOM_SLIDE ->
            slideTransition(rootContainer, newRoot, Direction.BOTTOM, Duration.millis(150), onFinished);
        case LEFT_SLIDE ->
            slideTransition(rootContainer, newRoot, Direction.LEFT, Duration.millis(150), onFinished);
        default -> {
          rootContainer.getChildren().setAll(newRoot);
          onFinished.run();
        }
      }

      updateHistoryState();
    } catch (IOException e) {
      System.err.println("Loading Page Error: Error in loading page.");
      navigationLocked.set(false);
      e.printStackTrace();
    }
  }

  /** Torna alla pagina successiva con transizione a scorrimento verso sinistra. */
  public static void goForward() {
    if(navigationLocked.get()) return;

    history.forward().ifPresent(entry ->
        resolve(entry.path(), entry.transition(), false)
    );
  }

  /** Torna alla pagina precedente con transizione a scorrimento verso destra. */
  public static void goBack() {
    if(navigationLocked.get()) return;

    TransitionAnimation transition =
        history.getCurrent()
        .map(RouteEntry::transition)
        .orElse(TransitionAnimation.DEFAULT);

    history.back().ifPresent(entry ->
        resolve(entry.path(), reverseTransition(transition), false)
    );
  }

  /** Metodo helper per aggiornare i valori esposti. */
  private static void updateHistoryState() {
    canBack.set(history.canBack());
    canForward.set(history.canForward());
  }

}
