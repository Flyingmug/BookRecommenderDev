package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.Router;
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

  public void onSearchAction() {
    String input = searchbar.getText();
    Router.go("/search/" + input); // Input relayed as a path parameter
  }
}
