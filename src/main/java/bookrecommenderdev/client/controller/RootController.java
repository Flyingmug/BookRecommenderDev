package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ConnectionErrorController;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.InvalidCredentialsException;
import bookrecommenderdev.client.auth.AccessPolicy;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.auth.AuthStorage;
import bookrecommenderdev.client.routing.*;
import bookrecommenderdev.client.routing.layout.LayoutRegistry;
import bookrecommenderdev.client.routing.layout.LayoutType;
import bookrecommenderdev.client.routing.route.Route;
import bookrecommenderdev.model.ServerInterface;
import bookrecommenderdev.model.dto.UtenteSessione;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;

/**
 * Controller JavaFX “root” dell’applicazione client.
 * <p>
 * Si occupa di
 * <ul>
 *   <li>Inizializzare la connessione RMI;</li>
 *   <li>Configurare Router (rotte + layout);</li>
 *   <li>Tentare il ripristino automatico della sessione.</li>
 * </ul>
 */
public class RootController {

  @FXML private StackPane content;
  @FXML private ConnectionErrorController connErrorController;

  ServerInterface bookRecommender;
  AppContext context;

  /**
   * Configura l’azione di retry in caso di errore e avvia l’inizializzazione dell’app.
   */
  @FXML
  public void initialize() {
    connErrorController.setRetryAction(this::init);

    init();
  }

  /**
   * Inizializza l’applicazione: layout, rotte, connessione RMI e Router.
   * <p>
   * Se la connessione al server fallisce, mostra un errore e interrompe l’avvio.
   */
  private void init() {
    LayoutRegistry layouts = buildLayouts();
    List<Route> routes = buildRoutes();

    initRegistry();

    if (bookRecommender == null) return;

    connErrorController.hideError();

    context = new AppContext(bookRecommender);

    try {
      Router.init(content, context, routes, layouts);

      attemptAutoLogin();

      Router.go("/");

    } catch (IllegalStateException e) {
      notifySystemError("Errore nel Router", e.getMessage());
    }
  }

  /**
   * Inizializza lo stub RMI recuperando l’oggetto remoto dal registry.
   * <p>
   * In caso di fallimento mostra la schermata di errore di sistema {@link ConnectionErrorController}.
   */
  private void initRegistry() {
    bookRecommender = null;
    try {
      Registry reg = LocateRegistry.getRegistry("localhost", 1099);
      bookRecommender = (ServerInterface) reg.lookup("serverBR");

    } catch(RemoteException e) {
      notifySystemError("Connessione al server fallita!", "Il server potrebbe non essere attivo...");

    } catch(NotBoundException e) {
      notifySystemError("Server non trovato!\n", "");
    }
  }

  /**
   * Registra i layout disponibili per le viste (default, integrato, vuoto).
   *
   * @return registro layout configurato
   */
  private LayoutRegistry buildLayouts() {
    return new LayoutRegistry()
        .register(LayoutType.DEFAULT, "default-layout.fxml")
        .register(LayoutType.INTEGRATED, "integrated-layout.fxml")
        .register(LayoutType.EMPTY, "empty-layout.fxml");
  }

  /**
   * Definisce la tabella di routing dell’applicazione client.
   * <p>
   * Ogni rotta include un percorso nominale, vista FXML, tipo di layout e policy di accesso.
   *
   * @return lista delle rotte registrate
   */
  private List<Route> buildRoutes() {
    List<Route> routes = new LinkedList<>();
    routes.add(new Route("/", "home-view.fxml", LayoutType.DEFAULT, AccessPolicy.PUBLIC));
    routes.add(new Route("/search", "search-view.fxml", LayoutType.INTEGRATED, AccessPolicy.PUBLIC));

    routes.add(new Route("/book/:id", "book-view.fxml", LayoutType.INTEGRATED, AccessPolicy.PUBLIC));
    routes.add(new Route("/book/:id/review", "review-form-view.fxml", LayoutType.DEFAULT, AccessPolicy.AUTH_ONLY));
    routes.add(new Route("/book/:id/recommendations/add", "recommendation-selector-view.fxml", LayoutType.DEFAULT, AccessPolicy.AUTH_ONLY));

    routes.add(new Route("/login", "login-view.fxml", LayoutType.EMPTY, AccessPolicy.GUEST_ONLY));
    routes.add(new Route("/registration", "registration-view.fxml", LayoutType.EMPTY, AccessPolicy.GUEST_ONLY));

    routes.add(new Route("/libraries", "libraries-view.fxml", LayoutType.DEFAULT, AccessPolicy.AUTH_ONLY));
    routes.add(new Route("/libraries/search", "search-libraries-view.fxml", LayoutType.DEFAULT, AccessPolicy.AUTH_ONLY));
    routes.add(new Route("/libraries/create", "library-creator-view.fxml", LayoutType.DEFAULT, AccessPolicy.AUTH_ONLY));
    routes.add(new Route("/libraries/:id", "library-page-view.fxml", LayoutType.DEFAULT, AccessPolicy.AUTH_ONLY));

    routes.add(new Route("/not-found", "errors/not-found-view.fxml", LayoutType.DEFAULT, AccessPolicy.PUBLIC));
    return routes;
  }

  /**
   * Tenta il ripristino automatico della sessione verificando la presenza di un token salvato localmente.
   * <p>
   * Se il token non è valido viene eliminato; se server/DB non sono raggiungibili
   * l’auto-login viene semplicemente ignorato.
   */
  private void attemptAutoLogin() {
    if (context == null) return;

    AuthStorage.load().ifPresent(token -> {
      try {
        UtenteSessione sessione = context.server().resumeSessione(token);
        AuthContext.login(sessione);

      } catch (InvalidCredentialsException e) {
        AuthStorage.clear();

      } catch (DataAccessException | RemoteException e) {
        // ignora -> server o db non raggiungibili
      }
    });
  }

  /**
   * Mostra un errore “di sistema” nella schermata root (es. problemi di connessione/avvio).
   *
   * @param titleMessage titolo del messaggio
   * @param subtitleMessage sottotitolo/dettaglio
   */
  private void notifySystemError(String titleMessage, String subtitleMessage) {
    connErrorController.showError(titleMessage, subtitleMessage);
  }

}
