package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.InvalidCredentialsException;
import bookrecommenderdev.routing.auth.AccessPolicy;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.auth.AuthStorage;
import bookrecommenderdev.routing.*;
import bookrecommenderdev.routing.layout.LayoutRegistry;
import bookrecommenderdev.routing.layout.LayoutType;
import bookrecommenderdev.routing.route.Route;
import bookrecommenderdev.server.ServerInterface;
import bookrecommenderdev.server.dto.UtenteSessione;
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

  @FXML public StackPane content;
  @FXML private VBox serverConnErrorWrapper;
  @FXML private Label serverErrorTitle;
  @FXML private Label serverErrorLabel;

  ServerInterface bookRecommender;
  AppContext context;

  @FXML
  public void initialize() {
    LayoutRegistry layouts = buildLayouts();
    List<Route> routes = buildRoutes();

    initRegistry();

    if (bookRecommender == null) return;

    context = new AppContext(bookRecommender);
    Router.init(content, context, routes, layouts);

    attemptAutoLogin();

    Router.go("/"); // real start
  }

  /**
   * Inizializza l'oggetto remoto RMI server dal repository.
   */
  private void initRegistry() {
    try {
      Registry reg = LocateRegistry.getRegistry("localhost", 1099);
      bookRecommender = (ServerInterface) reg.lookup("serverBR");
    } catch(RemoteException e) {
      notifyServerError(e.getMessage(), "Server connection failed!\n (Server might not be online or address is wrong)");
    } catch(NotBoundException e) {
      notifyServerError(e.getMessage(), "Server not found!\n");
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
   * DDD
   */
  private void attemptAutoLogin() {
    AuthStorage.load().ifPresent(token -> {
      try {
        UtenteSessione session = context.server().resumeSessione(token);
        AuthContext.login(session);

      } catch (InvalidCredentialsException e) {
        AuthStorage.clear();

      } catch (DataAccessException | RemoteException e) {
        // ignora server o db non raggiungibili
      }
    });
  }

  private void notifyServerError(String originalMessage, String titleMessage) {
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    serverConnErrorWrapper.setManaged(true);
  }

}
