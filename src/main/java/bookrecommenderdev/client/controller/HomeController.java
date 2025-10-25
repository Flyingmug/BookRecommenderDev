package bookrecommenderdev.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeController {
  @FXML
  private Label welcomeText;
  @FXML
  private StackPane centerStackContainer;
  @FXML
  private VBox homePage;
  @FXML
  private TextField searchbar;
  @FXML
  private StackPane homeSearchbarWrapper;
  @FXML
  private Button searchButton;
  @FXML
  private Region mainPageSpacer;

  /**
   * Ripristina gli elementi della pagina iniziale nelle loro posizioni originali.
   */
  private void resetHomepage() {

//    if (!homePage.getChildren().contains(searchbarWrapper)) {
//      searchbarWrapper.getStyleClass().remove("searchbar-navbar");
//      searchbarWrapper.getStyleClass().add("searchbar-center");
//      searchbar.setText("");
//      homePage.getChildren().addAll(welcomeText, searchbarWrapper, mainPageSpacer);
//    }

    searchbar.setText("");

  }

  public void onSearchAction() {

  }
}
