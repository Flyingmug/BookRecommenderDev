package bookrecommenderdev.client.routing;

import bookrecommenderdev.client.routing.animation.Direction;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.history.HistoryManager;
import bookrecommenderdev.client.routing.history.RouteEntry;
import bookrecommenderdev.client.routing.layout.LayoutController;
import bookrecommenderdev.client.routing.layout.LayoutHandle;
import bookrecommenderdev.client.routing.layout.LayoutRegistry;
import bookrecommenderdev.client.routing.layout.LayoutType;
import bookrecommenderdev.client.routing.route.Routable;
import bookrecommenderdev.client.routing.route.Route;
import bookrecommenderdev.client.routing.route.RouteMatch;
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

import static bookrecommenderdev.client.routing.animation.Animations.*;

/**
 * Gestisce la navigazione client-side dell'applicazione JavaFX, risolvendo un {@link Route} a partire da un percorso
 * (es. {@code /login} o {@code /book/42}), applicando eventuali controlli di accesso e orchestrando il cambio di layout,
 * il caricamento delle view e le animazioni di transizione.
 *
 * <h2>Concetti principali</h2>
 * <ul>
 *   <li><b>Routes</b>: l'elenco di {@link Route} registrati contiene i pattern dei percorsi e le informazioni necessarie
 *   a caricare una pagina {@link Routable}.</li>
 *   <li><b>Layouts</b>: un {@link LayoutType} definisce un contenitore (layout) in cui viene montata la pagina corrente.
 *   Il router cambia layout solo quando necessario.</li>
 *   <li><b>Storia</b>: tramite {@link HistoryManager} mantiene una cronologia di {@link RouteEntry} e aggiorna le
 *   proprietà osservabili {@link #canBack()} e {@link #canForward()}.</li>
 *   <li><b>Controllo dell' accesso</b>: usa {@link AuthContext} (tramite {@link AppContext}) per verificare se un percorso è
 *   visitabile dall'utente corrente.</li>
 * </ul>
 *
 * <h2>Threading</h2>
 * Questo router modifica i nodi contenuti nella JavaFX scene; le chiamate pubbliche dovrebbero avvenire sul
 * <i>JavaFX Application Thread</i>. Le animazioni possono bloccare temporaneamente la navigazione tramite un lock
 * interno (vedi {@code navigationLocked}) per evitare richieste concorrenti.
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
  private static final BooleanProperty canBack = new SimpleBooleanProperty(false);
  private static final BooleanProperty canForward = new SimpleBooleanProperty(false);
  private static boolean navigationLocked = false;
  private static boolean initialized = false;

  /**
   * Proprietà di sola lettura che indica se è possibile tornare indietro nella cronologia.
   *
   * @return proprietà booleana di sola lettura, aggiornata dal router dopo ogni navigazione
   */
  public static ReadOnlyBooleanProperty canBack() { return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canBack); }

  /**
   * Proprietà di sola lettura che indica se è possibile avanzare nella cronologia.
   *
   * @return proprietà booleana di sola lettura, aggiornata dal router dopo ogni navigazione
   */
  public static ReadOnlyBooleanProperty canForward() { return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canForward); }

  /**
   * Restituisce il contesto applicativo attualmente associato al router.
   *
   * <p>Il contesto viene impostato in {@link #init(StackPane, AppContext, List, LayoutRegistry)} e fornisce accesso
   * a servizi condivisi lato client (es. {@link AuthContext}).
   *
   * @return contesto applicativo del client; può essere {@code null} se {@link #init(StackPane, AppContext, List, LayoutRegistry)}
   *         non è stato ancora invocato
   */
  public static AppContext context() { return appContext; }


  /**
   * <p>Metodo di inizializzazione con le istanze richieste.
   *
   * <p> Deve essere chiamato esattamente una volta prima di poter usufruire dei servizi di routing.
   *
   * <p>Il nomi dei percorsi saranno utilizzati dal metodo {@link #resolve resolve()} durante la ricerca della
   * presenza del percorso richiesto.
   * @param container Root container JavaFX dove il layout verrà inserito
   * @param ctx Contesto dell'applicazione
   * @param routeList Lista di percorsi registrati
   * @param layoutRegistry Registry di layout disponibili
   * @throws IllegalStateException Se non è definito un percorso d'errore "/not-found" con un layout esistente
   */
  public static void init(StackPane container, AppContext ctx, List<Route> routeList, LayoutRegistry layoutRegistry)
    throws IllegalStateException {

    rootContainer = container;
    appContext = ctx;
    routes = routeList;
    layouts = layoutRegistry;

    routes.stream()
        .filter(r -> r.pathPattern().equals("/not-found"))
        .findFirst()
        .ifPresentOrElse(
            r -> {
              if (!layoutRegistry.contains(r.layout())) {
                throw new IllegalStateException(
                    "Layout " + r.layout() + " utilizzato da /not-found non è registrato"
                );
              }
            },
            () -> {
              throw new IllegalStateException("Percorso prerequisito mancante: /not-found");
            }
        );

    initialized = true;
  }


  /**
   * Naviga al percorso dato con transizione di default.
   * @param path Percorso della pagina interessata
   */
  public static void go(String path) {
    resolve(path, TransitionAnimation.DEFAULT, true, null);
  }

  /**
   * Naviga al percorso dato passando un parametro di navigazione.
   * @param path Percorso della pagina interessata
   * @param state Oggetto passato come parametro
   */
  public static void go(String path, Object state) {
    resolve(path, TransitionAnimation.DEFAULT, true, state);
  }

  /**
   * Naviga al percorso dato usando una transizione specificata e passando un parametro di navigazione.
   *
   * @param path Percorso della pagina interessata
   * @param transition Transizione animata da utilizzare
   * @param state Oggetto passato come parametro, può essere {@code null}.
   */
  public static void go(String path, TransitionAnimation transition, Object state) {
    resolve(path, transition, true, state);
  }

  /**
   * Naviga in avanti nella storia.
   *
   * <p>Se la navigazione è bloccata o se non esiste una pagina successiva, la chiamata è ignorata.</p>
  */
  public static void goForward() {
    if(navigationLocked) return;

    history.forward().ifPresent(entry ->
        resolve(entry.path(), entry.transition(), false, entry.state())
    );
  }

  /**
   * Naviga all' indietro nella storia.
   *
   * <p>Se la navigazione è bloccata o se non esiste una pagina precedente, la chiamata è ignorata.</p>
   */
  public static void goBack() {
    if(navigationLocked) return;

    TransitionAnimation transition =
        history.getCurrent()
        .map(RouteEntry::transition)
        .orElse(TransitionAnimation.DEFAULT);

    history.back().ifPresent(entry ->
        resolve(entry.path(), reverseTransition(transition), false, entry.state())
    );
  }


  /**
   * Risolve una richiesta ed esegue la navigazione.
   *
   * <p>Il metodo si occupa di eseguire route matching, controllo dell' accesso,
   * cambiamento del layout, caricamento delle pagine, gestire le animazioni di transizione
   * e aggiornare la storia.</p>
   *
   * @param path Percorso di navigazione richiesto
   * @param transition transizione da utilizzare
   * @param pushHistory Se memorizzare la visita al percorso
   * @param state parametro di navigazione opzionale
   */
  private static void resolve(String path, TransitionAnimation transition, boolean pushHistory, Object state) {
    if (navigationLocked || !initialized) return;
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
        .orElse(null);

    if (match == null) {
      go("/not-found", TransitionAnimation.FADE_INTO);
      return;
    }

    // Verifica delle policy di accesso al percorso
    if (!isAccessAllowed(match.route())) {
      handleAccessDenied(match.route());
      return;
    }

    navigationLocked = true;

    try {
      Parent page = loadPage(match.route().fxml(), match.params(), state);

      if (match.route().layout() != currentLayout) {
        switchLayout(match.route().layout(), page, transition, path, pushHistory, state);
      } else {
        switchPage(page, transition, path, pushHistory, state);
      }
    } catch (IOException | IllegalStateException ex) {
      navigationLocked = false;
      go("/not-found", TransitionAnimation.DEFAULT, "Impossibile caricare la pagina...");
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
   * Carica e inserisce un layout e di una pagina con transizione specificata.
   *
   * @param layout Tipo di layout
   * @param page Pagina contenuta
   * @param transition Transizione da utilizzare
   * @param path Percorso richiesto
   * @param pushHistory Se memorizzare la visita al percorso
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
   * Conclude la navigazione.
   * <p>Aggiorna lo stato interno.
   *
   * @param path Percorso richiesto
   * @param transition Transizione da utilizzare
   * @param pushHistory Se memorizzare la visita al percorso
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

    updateHistoryState(); // nota: non ridondante
    navigationLocked = false;
  }

  /**
   * Carica una pagina con relativo controller e view associato.
   *
   * @param fxml Percorso relativo
   * @param params Parametri da passare alla pagina
   * @return Nodo caricato dal file fxml
   * @throws IOException In caso di errore nel caricamento dai file
  */
  private static Parent loadPage(String fxml, Map<String, String> params, Object state)
      throws IOException, IllegalStateException {

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
   * Effettua una transizione visuale tra due nodi utilizzando la {@link TransitionAnimation transizione} specificata.
   * <p>Causa l'aggiornamento dello stato esposto.</p>
   *
   * @param container Nodo radice
   * @param content Nodo nuovo
   * @param transition Transizione da utilizzare
   * @param onFinished Callback
   */
  private static void transition(Pane container, Node content, TransitionAnimation transition, Runnable onFinished) {

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

  /**
   * Aggiorna lo stato esposto relativo alla storia di navigazione.
  */
  private static void updateHistoryState() {
    canBack.set(history.canBack());
    canForward.set(history.canForward());
  }

  /**
   * Determina se l'utente può accedere al percorso specificato.
   *
   * @param route Percorso
   * @return {@code true} se l'accesso è consentito, altrimenti {@code false}
   */
  private static boolean isAccessAllowed(Route route) {
    boolean authenticated = AuthContext.isAuthenticated();

    return switch (route.access()) {
      case PUBLIC -> true;
      case AUTH_ONLY -> authenticated;
      case GUEST_ONLY -> !authenticated;
    };
  }

  /**
   * Gestisce il comportamento del router a seguito di un tentativo di accesso non consentito.
   *
   * @param route Percorso negato
   */
  private static void handleAccessDenied(Route route) {
    switch (route.access()) {
      case PUBLIC -> {}
      case AUTH_ONLY -> go("/login", TransitionAnimation.FADE_INTO);
      case GUEST_ONLY -> go("/", TransitionAnimation.FADE_INTO);
    }
  }

}
