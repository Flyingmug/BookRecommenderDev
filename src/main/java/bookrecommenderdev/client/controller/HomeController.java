package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;

public class HomeController {

  @FXML private StackPane welcomePane;
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

  @FXML
  private void initialize() {
    setTitleBackground();
  }

  private void setTitleBackground() {
    // background
    URL imageUrl = getClass().getResource("/bookrecommenderdev/assets/book-recommender-title.png");
    if (imageUrl != null) {
      Image originalImage = new Image(imageUrl.toExternalForm());

      ImageView tempView = new ImageView(originalImage);
      tempView.setOpacity(0.6);

      SnapshotParameters params = new SnapshotParameters();
      params.setFill(Color.TRANSPARENT);
      Image transparentImage = tempView.snapshot(params, null);

      BackgroundImage backgroundImage = new BackgroundImage(
          transparentImage,
          BackgroundRepeat.NO_REPEAT,
          BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(1077, 123,
              false, false, true, false
          )
      );

      if (welcomePane != null) {
        welcomePane.setBackground(new Background(backgroundImage));
      }
    }
  }
}
