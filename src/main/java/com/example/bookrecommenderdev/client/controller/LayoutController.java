package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.client.AppContext;
import com.example.bookrecommenderdev.client.Router;
import com.example.bookrecommenderdev.model.Utente;
import com.example.bookrecommenderdev.server.ServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static com.example.bookrecommenderdev.utils.Tools.setRandomBackgroundColor;

public class LayoutController {


  @FXML
  private Label serverErrorTitle;
  @FXML
  private Label serverErrorLabel;
  @FXML
  private VBox serverConnErrorWrapper;

  @FXML
  private StackPane centerStackContainer;

  @FXML
  private HBox navbar;
  @FXML
  private HBox navbarControls;


  @FXML
  private Button librariesButton;
  @FXML
  private Button profileButton;
  @FXML
  private Label profilePicture;

  @FXML
  private StackPane searchbarWrapper;

  // utente corrente
  Utente currentUser;

  ServerInterface bookRecommender;
  AppContext context;


  @FXML
  public void initialize() {
    initPriorityLayout();
    initRegistry();
    initSetupLayout();

    context = new AppContext(bookRecommender);
    Router.init(centerStackContainer, context);
    //
    //
    // TEST
    // testBookSearchPage();
  }

  /**
   * Inizializza gli elementi grafici privati visualizzabili solo dopo certe azioni.
   */
  private void initSetupLayout() {

    // background
    URL imageUrl = getClass().getResource("/com/example/bookrecommenderdev/assets/library-background2.jpg");
    if (imageUrl != null) {
      Image image = new Image(imageUrl.toExternalForm());

      BackgroundImage backgroundImage = new BackgroundImage(
          image,
          BackgroundRepeat.NO_REPEAT,
          BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(100, 100, true, true, false, true)
      );

      centerStackContainer.setBackground(new Background(backgroundImage));
    }

  }
  private void initPriorityLayout() {
    Label serverErrorIcon = new Label();
    serverErrorIcon.setGraphic(new FontIcon("mdi2a-alert-outline"));
    serverErrorIcon.setFont(new Font(75));
    serverErrorTitle = new Label();
    serverErrorTitle.getStyleClass().addAll("error-label", "connection-error-title");
    serverErrorLabel = new Label();
    serverErrorLabel.getStyleClass().addAll("error-label", "connection-error-label");
    serverConnErrorWrapper = new VBox();
    serverConnErrorWrapper.setAlignment(Pos.CENTER);
    serverConnErrorWrapper.getChildren().addAll(serverErrorIcon, serverErrorTitle, serverErrorLabel);


    librariesButton = new Button("Librerie");
    librariesButton.setFont(new Font("Arial", 15));
    librariesButton.getStyleClass().addAll("libraries-button", "navbar-button");
    librariesButton.setOnAction(_ -> onLibraryList());

    profileButton = new Button();
    profileButton.setText("");
    profileButton.tooltipProperty().set(new Tooltip("Pagina profilo"));
    setRandomBackgroundColor(profileButton);
    profileButton.getStyleClass().add("profile-picture-button");
    profileButton.setOnAction(_ -> Router.go("/profile"));
    profilePicture = new Label();
    profilePicture.getStyleClass().add("profile-picture-text");
    profileButton.setGraphic(profilePicture);
  }
  /**
   * Inizializza l'oggetto remoto del server dal repository.
   */
  private void initRegistry() {
    try {
      Registry reg = LocateRegistry.getRegistry("localhost", 1099);
      bookRecommender = (ServerInterface) reg.lookup("serverBR");
      Router.go("/"); // apertura della pagina iniziale all'esecuzione

    } catch(RemoteException e) {
      notifyServerError(e.getMessage(), "Server connection failed!\n (Server might not be online or address is wrong)");
      // error display on main page
    } catch(NotBoundException e) {
      notifyServerError(e.getMessage(), "Server not found!\n");

      // error display on main page
    } finally {
      if (bookRecommender != null)
        System.out.println("TMP message: local info verification");
//        initVerifyLocalUserCredentials();
    }
  }


//  /**
//   * Nasconde tutti i contenitori rappresentanti le pagine dell'applicazione
//  */
//  private void hideAllPages() {
//    centerStackContainer.getChildren().forEach(child -> child.setVisible(false));
//  }


  @FXML
  protected void onLibraryList() {

    Router.go("/libraries");
//    try {
//
//      List<Pair<Libreria, Integer>> librerie = bookRecommender.getListLibrerie(1);
//
//
//
//    } catch (RemoteException e) {
//
//    }
  }


//  private void topSearchbar() {
//    if (!navbar.getChildren().contains(searchbarWrapper)) {
//      homePage.getChildren().clear();
//      searchbarWrapper.getStyleClass().remove("searchbar-center");
//      searchbarWrapper.getStyleClass().add("searchbar-navbar");
//      navbar.getChildren().addFirst(searchbarWrapper);
//    }
//
//  }


  @FXML
  protected void onSearchCriteriaDisplay() {
  }
  @FXML
  protected void onCriteriaSelection() {
  }
  @FXML
  protected void onCreateUser() {
  }
  @FXML
  protected void onDeleteUser() {
  }
  @FXML
  protected void onAction() {
  }

  private void notifyServerError(String originalMessage, String titleMessage) {
    navbar.getChildren().clear();
    centerStackContainer.getChildren().clear();
    serverErrorTitle.setText(titleMessage);
    serverErrorLabel.setText(originalMessage);
    centerStackContainer.getChildren().add(serverConnErrorWrapper);
  }

  public void onRegister(ActionEvent actionEvent) {
  }

  public void onHomepage(ActionEvent actionEvent) {
  }

  public void onLogin(ActionEvent actionEvent) {
  }

  public void onSearchAction(MouseEvent mouseEvent) {

  }
}