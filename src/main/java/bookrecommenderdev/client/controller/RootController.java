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
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;

public class RootController {

  @FXML private StackPane content;
  @FXML private ConnectionErrorController connErrorController;

  ServerInterface bookRecommender;
  AppContext context;

  @FXML
  public void initialize() {
    connErrorController.setRetryAction(this::init);

    init();
  }

  private void init() {
    LayoutRegistry layouts = buildLayouts();
    List<Route> routes = buildRoutes();

    initRegistry();

    if (bookRecommender == null) return;

    connErrorController.hideError();

    context = new AppContext(bookRecommender);
    Router.init(content, context, routes, layouts);

    attemptAutoLogin();

    Router.go("/");
  }

  /**
   * Inizializza l'oggetto remoto RMI server dal repository.
   */
  private void initRegistry() {
    bookRecommender = null;
    try {
      Registry reg = LocateRegistry.getRegistry("localhost", 1099);
      bookRecommender = (ServerInterface) reg.lookup("serverBR");

    } catch(RemoteException e) {
      notifyServerError("Connessione al server fallita!", "Il server potrebbe non essere attivo...");

    } catch(NotBoundException e) {
      notifyServerError("Server non trovato!\n", "");
    }
  }

  private LayoutRegistry buildLayouts() {
    return new LayoutRegistry()
        .register(LayoutType.DEFAULT, "default-layout.fxml")
        .register(LayoutType.INTEGRATED, "integrated-layout.fxml")
        .register(LayoutType.EMPTY, "empty-layout.fxml");
  }

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
   * todo doc
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
        // ignora server o db non raggiungibili
      }
    });
  }

  private void notifyServerError(String titleMessage, String subtitleMessage) {
    connErrorController.showError(titleMessage, subtitleMessage);
  }

}
