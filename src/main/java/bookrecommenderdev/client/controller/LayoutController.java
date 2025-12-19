package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.image.Image;

import java.net.URL;

// todo scrap class
public class LayoutController {

  @FXML private StackPane centerStackContainer;

  @FXML private HBox navbar;
  @FXML private NavbarController navbarController;
  @FXML private HBox navbarControls;

  @FXML
  public void initialize() {
    setCenterBackground();
  }

  /**
   * Carica lo sfondo della pagina centrale.
   */
  private void setCenterBackground() {
    // background
    URL imageUrl = getClass().getResource("/bookrecommenderdev/assets/library-background2.jpg");
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

  @FXML
  protected void onLibraryList() {

    Router.go("/libraries");
//    try {
//      List<Pair<Libreria, Integer>> librerie = bookRecommender.getListLibrerie(1);
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

}