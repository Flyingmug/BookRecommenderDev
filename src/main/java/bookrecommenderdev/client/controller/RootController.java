package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.Utente;
import bookrecommenderdev.routing.*;
import bookrecommenderdev.routing.layout.LayoutRegistry;
import bookrecommenderdev.routing.layout.LayoutType;
import bookrecommenderdev.routing.route.Route;
import bookrecommenderdev.server.ServerInterface;
import bookrecommenderdev.utils.FileManager;
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

import static bookrecommenderdev.Constants.LOCAL_CREDENTIALS;

public class RootController {

  @FXML public StackPane content;
  @FXML private VBox serverConnErrorWrapper;
  @FXML private Label serverErrorTitle;
  @FXML private Label serverErrorLabel;

  // utente corrente
  Utente currentUser;

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
    routes.add(new Route("/", "home-view.fxml"));
    routes.add(new Route("/search/:query", "searchResults-view.fxml", LayoutType.INTEGRATED));
    routes.add(new Route("/book/:query", "book-view.fxml", LayoutType.INTEGRATED));
    routes.add(new Route("/login", "login-view.fxml", LayoutType.EMPTY));
    routes.add(new Route("/registration", "registration-view.fxml", LayoutType.EMPTY));
    routes.add(new Route("/profile", "profile-view.fxml"));
    routes.add(new Route("/libraries", "libraries-view.fxml"));
    routes.add(new Route("/libraries/:query", "library-view.fxml"));

    initRegistry();
    if (bookRecommender != null) {
      context = new AppContext(bookRecommender, currentUser);
      Router.init(content, context, routes, layouts);

      // fixme TEST
      //Router.go("/");
      Router.go("/book/1");

      initVerifyLocalUserCredentials();
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
  private void initVerifyLocalUserCredentials() {
//    System.out.println("TMP message: local info verification"); // debug

    String str = FileManager.read(LOCAL_CREDENTIALS);
    if (str != null && !str.isEmpty()) {
      String[] split = str.split(",");
      String email = split[0];
      String password = split[1];
//      loginEmailloginEmail.setText(email);
//      loginPassword.setText(password);
//      onConfirmLogin();
    }

    // todo separate string manipulation from logic

//    try {
//      // todo implementation
//    } catch (Error e) {
//
//    }
  }

  private void notifyServerError(String originalMessage, String titleMessage) {
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    serverConnErrorWrapper.setManaged(true);
  }

}
