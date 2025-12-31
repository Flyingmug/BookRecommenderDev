package bookrecommenderdev.routing;

import bookrecommenderdev.routing.animation.Direction;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.history.HistoryManager;
import bookrecommenderdev.routing.history.RouteEntry;
import bookrecommenderdev.routing.layout.LayoutController;
import bookrecommenderdev.routing.layout.LayoutHandle;
import bookrecommenderdev.routing.layout.LayoutRegistry;
import bookrecommenderdev.routing.layout.LayoutType;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.routing.route.Route;
import bookrecommenderdev.routing.route.RouteMatch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

import static bookrecommenderdev.routing.animation.Animations.*;

/**
 * todo Serve una descrizione approfondita del funzionamento.
 * todo descrivere anche il contesto
 */
public class Router {
  private static StackPane rootContainer;
  private static List<Route> routes;
  private static LayoutRegistry layouts;
  private static AppContext appContext;

  private static LayoutType currentLayout;
  private static LayoutHandle currentLayoutHandle;
  private static RouteEntry loadedEntry;
  private static final HistoryManager<RouteEntry> history = new HistoryManager<>(10);
  private static final BooleanProperty navigationLocked = new SimpleBooleanProperty(false);
  private static final BooleanProperty canBack = new SimpleBooleanProperty(false);
  private static final BooleanProperty canForward = new SimpleBooleanProperty(false);

  public static ReadOnlyBooleanProperty canBack() { return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canBack); }
  public static ReadOnlyBooleanProperty canForward() { return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canForward); }
  public static AppContext context() { return appContext; }

  /**
   * <p>Metodo di inizializzazione per il router con il riferimento.
   * La lista di percorsi è un hashmap contenente coppie del tipo ("/nome-percorso", r: Route);
   *
   * <p>Il nomi dei percorsi saranno utilizzati dal metodo {@link #resolve resolve()} durante la ricerca della
   * presenza del percorso richiesto.
   * @param container Stack interessato.
   * @param ctx Contesto dell'applicazione.
   * @param routeList Lista di percorsi.
   * @param layoutRegistry Registry di layout.
   */
  public static void init(StackPane container, AppContext ctx, List<Route> routeList, LayoutRegistry layoutRegistry) {
    rootContainer = container;
    appContext = ctx;
    routes = routeList;
    layouts = layoutRegistry;
    // todo error routes?

    // todo loading screen linked to server loadings?
  }


  /**
   * Consente la navigazione tra pagine.
   * @param path Percorso della pagina interessata
   */
  public static void go(String path) {
    resolve(path, TransitionAnimation.DEFAULT, true, null);
  }

  /**
   * Consente la navigazione tra pagine con il passaggio di un parametro di navigazione.
   * @param path Percorso della pagina interessata
   * @param state Oggetto passato come parametro
   */
  public static void go(String path, Object state) {
    resolve(path, TransitionAnimation.DEFAULT, true, state);
  }

  /**
   * Consente la navigazione tra pagine, passaggio di parametro e la selezione del tipo di transizione da utilizzare.
   * @param path Percorso della pagina interessata
   * @param transition Tipologia di animazione da utilizzare
   * @param state Oggetto passato come parametro.
   */
  public static void go(String path, TransitionAnimation transition, Object state) {
    resolve(path, transition, true, state);
  }

  /** Torna alla pagina successiva con transizione a scorrimento verso sinistra. */
  public static void goForward() {
    if(navigationLocked.get()) return;

    history.forward().ifPresent(entry ->
        resolve(entry.path(), entry.transition(), false, entry.state())
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
        resolve(entry.path(), reverseTransition(transition), false, entry.state())
    );
  }



  /**
   * todo add guarding note
   * <p>Risolve le richieste di navigazione ai percorsi.
   * <p>Dal percorso viene ricercato un {@link Route} corrispondente nel registry locale,
   * e vengono estratti eventuali parametri di percorso.
   * <p>Nota: una richiesta ad un percorso
   * che definisce lo stesso layout eseguirà un aggiornamento solo del contenuto del layout.
   * In caso di layout diverso, sia layout che il contenuto saranno aggiornati.
   * @param path Percorso della pagina
   * @param transition Animazione di transizione da utilizzare
   * @param pushHistory Memorizzazione della visita al percorso
   */
  private static void resolve(String path, TransitionAnimation transition, boolean pushHistory, Object state) {

    if (navigationLocked.get()) return;
    if (loadedEntry != null
        && loadedEntry.path().equals(path)
        && Objects.equals(loadedEntry.state(), state)) {
      return;
    }

    RouteMatch match = routes.stream()
        .map(r -> matchRoute(r.pathPattern(), path)
            .map(params -> new RouteMatch(r, params)))
        .flatMap(Optional::stream)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No route for " + path));


    // Route guarding -> verifica delle policy di accesso
    if (!isAccessAllowed(match.route())) {
      handleAccessDenied(match.route());
      return;
    }

    navigationLocked.set(true);

    try {
      Parent page = loadPage(match.route().fxml(), match.params(), state);

      if (match.route().layout() != currentLayout) {
        switchLayout(match.route().layout(), page, transition, path, pushHistory, state);
      } else {
        switchPage(page, transition, path, pushHistory, state);
      }
    } catch (IOException ex) {
      navigationLocked.set(false);
      throw new RuntimeException(ex);
      // todo pagina d'errore nel percorso richiesto
    }
  }

  /**
   * <p>Esegue un match tra il percorso dato e i route definiti.
   * <p>Se viene trovato un match valido, i parametri di percorso sono estratti e restituiti come {@link Map}.
   * <p>Nota: i parametri di percorso sono definiti tramite la sintassi {@code /:nome_parametro}
   * @param pattern Nome del percorso
   * @param path Percorso richiesto
   * @return Map contenente eventuali parametri di percorso
   */
  private static Optional<Map<String, String>> matchRoute(String pattern, String path) {
    String[] routeParts = pattern.split("/");
    String[] pathParts = path.split("/");

    if (routeParts.length != pathParts.length) return Optional.empty();

    Map<String, String> params = new HashMap<>();

    for (int i = 0; i < routeParts.length; i++) {
      String r = routeParts[i];
      String p = pathParts[i];

      if (r.startsWith(":")) {
        params.put(r.substring(1), p);
      } else if (!r.equals(p)) {
        return Optional.empty();
      }
    }
    return Optional.of(params);
  }

  /**
   * Carica un layout e una pagina riferiti nella grafica. Consente la scelta di una transizione animata.
   * Gestisce il caricamento di un layout e di una pagina con transizione specificata.
   * @param layout Layout contenitore
   * @param page Pagina contenuta
   * @param transition Transizione da utilizzare
   * @param path Percorso richiesto
   * @param pushHistory Memorizzazione della visita al percorso
   * @throws IOException Errore nel caricamento degli elementi grafici
   */
  private static void switchLayout(
      LayoutType layout,
      Parent page,
      TransitionAnimation transition,
      String path,
      boolean pushHistory,
      Object state
  ) throws IOException {

    FXMLLoader loader =
        new FXMLLoader(Router.class.getResource(
            "/bookrecommenderdev/client/layouts/" +
                layouts.fxml(layout)
        ));

    Parent layoutRoot = loader.load();
    LayoutController controller = loader.getController();

    controller.setContent(page);

    transition(
        rootContainer,
        layoutRoot,
        transition,
        () -> finalizeNavigation(path, transition, pushHistory, layout, controller, state)
    );
  }

  /**
   * Carica una pagina nel layout corrente. Consente la scelta di una transizione animata.
   * @param page Pagina
   * @param transition Transizione da utilizzare
   * @param path Percorso
   * @param pushHistory Memorizzazione della visita al percorso
   */
  private static void switchPage(
      Parent page,
      TransitionAnimation transition,
      String path,
      boolean pushHistory,
      Object state
  ) {
    transition(
        currentLayoutHandle.controller().getContent(),
        page,
        transition,
        () -> finalizeNavigation(path, transition, pushHistory, currentLayout, currentLayoutHandle.controller(), state)
    );
  }

  /**
   * Svolge le operazioni conclusive del caricamento di una pagina.
   * <p>Se {@code pushHistory} è {@code true}, la visita alla pagina viene memorizzata con la relativa transizione utilizzata.
   * <p>Aggiorna i campi referenti layout e pagina correntemente caricati.
   * @param path Percorso
   * @param transition Transizione da utilizzare
   * @param pushHistory Memorizzazione della visita al percorso
   * @param layout Layout da utilizzare
   * @param controller Controller del layout
   */
  private static void finalizeNavigation(
      String path,
      TransitionAnimation transition,
      boolean pushHistory,
      LayoutType layout,
      LayoutController controller,
      Object state
  ) {
    RouteEntry entry = new RouteEntry(path, transition, state);

    if (pushHistory) history.visit(entry);

    loadedEntry = entry;
    currentLayout = layout;
    currentLayoutHandle = new LayoutHandle(controller);

    updateHistoryState(); // note: not redundant.
    navigationLocked.set(false);
  }

  /**
   * Restituisce il nodo corrispondente a una pagina definita da path e parametri dati.
   * @param fxml Percorso corrispondente alla pagina
   * @param params Parametri della pagina
   * @return Nodo caricato
   * @throws IOException Errore nel reperimento del file
   */
  private static Parent loadPage(String fxml, Map<String, String> params, Object state)
      throws IOException {

    FXMLLoader loader =
        new FXMLLoader(Router.class.getResource(
            "/bookrecommenderdev/client/" + fxml
        ));

    Parent page = loader.load();

    Object controller = loader.getController();
    if (controller instanceof Routable r) {
      r.onRoute(params, appContext, state);
    }

    return page;
  }

  /**
   * Effettua i cambiamenti grafici utilizzando una {@link TransitionAnimation transizione} specificata.
   * Causa l'aggiornamento dello stato esposto.
   * @param container Nodo radice
   * @param content Noto figlio
   * @param transition Transizione da utilizzare
   * @param onFinished Operazioni finali
   */
  private static void transition(Pane container, Node content, TransitionAnimation transition, Runnable onFinished) {
    // Gestione della transizione
    switch(transition) {
      case FADE_INTO ->
          fadeTransition(container, content, Duration.millis(115), onFinished);
      case TOP_SLIDE ->
          slideTransition(container, content, Direction.TOP, Duration.millis(150), onFinished);
      case RIGHT_SLIDE ->
          slideTransition(container, content, Direction.RIGHT, Duration.millis(150), onFinished);
      case BOTTOM_SLIDE ->
          slideTransition(container, content, Direction.BOTTOM, Duration.millis(150), onFinished);
      case LEFT_SLIDE ->
          slideTransition(container, content, Direction.LEFT, Duration.millis(150), onFinished);
      default -> {
        container.getChildren().setAll(content);
        onFinished.run();
      }
    }

    updateHistoryState();
  }

  /** Metodo helper per aggiornare i valori di stato esposti. */
  private static void updateHistoryState() {
    canBack.set(history.canBack());
    canForward.set(history.canForward());
  }


  /**
   * todo doc
   * */
  private static boolean isAccessAllowed(Route route) {
    boolean authenticated = AuthContext.isAuthenticated();

    return switch (route.access()) {
      case PUBLIC -> true;
      case AUTH_ONLY -> authenticated;
      case GUEST_ONLY -> !authenticated;
    };
  }

  /**
   * todo doc
   * */
  private static void handleAccessDenied(Route route) {
    switch (route.access()) {
      case AUTH_ONLY -> go("/login", TransitionAnimation.FADE_INTO);
      case GUEST_ONLY -> go("/", TransitionAnimation.FADE_INTO);
      default -> throw new IllegalStateException();
    }
  }

}
