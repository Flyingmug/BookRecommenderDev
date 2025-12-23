package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.auth.AuthContext;
import bookrecommenderdev.model.auth.AuthStatus;
import bookrecommenderdev.model.auth.AuthStorage;
import bookrecommenderdev.routing.*;
import bookrecommenderdev.routing.layout.LayoutRegistry;
import bookrecommenderdev.routing.layout.LayoutType;
import bookrecommenderdev.routing.route.Route;
import bookrecommenderdev.server.ServerInterface;
import bookrecommenderdev.server.dto.AuthResult;
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
    List<Route> routes = new LinkedList<>();
    LayoutRegistry layouts = new LayoutRegistry()
        .register(LayoutType.DEFAULT, "default-layout.fxml")
        .register(LayoutType.INTEGRATED, "integrated-layout.fxml")
        .register(LayoutType.EMPTY, "empty-layout.fxml");



    // Registrazione delle pagine
//    routes.put("/loading", new Route("loading-view.fxml"));
    routes.add(new Route("/", "home-view.fxml", LayoutType.DEFAULT, false));
    routes.add(new Route("/search/:query", "searchResults-view.fxml", LayoutType.INTEGRATED, false));
    routes.add(new Route("/book/:query", "book-view.fxml", LayoutType.INTEGRATED, false));
    routes.add(new Route("/login", "login-view.fxml", LayoutType.EMPTY, false));
    routes.add(new Route("/registration", "registration-view.fxml", LayoutType.EMPTY, false));
    routes.add(new Route("/profile", "profile-view.fxml", LayoutType.DEFAULT, true));
    routes.add(new Route("/libraries", "libraries-view.fxml", LayoutType.DEFAULT, true));
    routes.add(new Route("/libraries/:query", "library-view.fxml", LayoutType.DEFAULT, true));

    initRegistry();
    if (bookRecommender != null) {
      context = new AppContext(bookRecommender);
      Router.init(content, context, routes, layouts);

      // fixme TEST
      //Router.go("/");
      Router.go("/book/1");

      attemptAutoLogin();
    }

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

  /**
   * DDD
   */
  private void attemptAutoLogin() {

    AuthStorage.load().ifPresent(credentials -> {
      try {

        AuthResult result = context.server().login(
            credentials.email(), credentials.password()
        );

        if (result.authStatus() == AuthStatus.SUCCESS) {
          System.out.println("Auto Login successful");  // DEBUG
          AuthContext.login(result.user());
        }

      } catch(RemoteException e) {
        e.printStackTrace();  // fixme perhaps leave nothing? does it influence anything relevant?
      }
    });
  }

  private void notifyServerError(String originalMessage, String titleMessage) {
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    serverConnErrorWrapper.setManaged(true);
  }

}
