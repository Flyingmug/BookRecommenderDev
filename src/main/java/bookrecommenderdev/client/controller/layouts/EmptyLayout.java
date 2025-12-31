package bookrecommenderdev.client.controller.layouts;

import bookrecommenderdev.client.routing.layout.LayoutController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;

public class EmptyLayout implements LayoutController {

  @FXML
  private StackPane content;

  @FXML
  void initialize() {
    setCenterBackground();
  }

  @Override
  public Pane getContent() {
    return content;
  }

  @Override
  public void setContent(Parent contentNode) {
    content.getChildren().setAll(contentNode);
  }

  /**
   * Carica lo sfondo della pagina centrale.
   */
  private void setCenterBackground() {
    // background
    URL imageUrl = getClass().getResource("/bookrecommenderdev/assets/library-background-clear.jpg");
    if (imageUrl != null) {
      Image image = new Image(imageUrl.toExternalForm());

      BackgroundImage backgroundImage = new BackgroundImage(
          image,
          BackgroundRepeat.NO_REPEAT,
          BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(100, 100, true, true, false, true)
      );

      if (content != null) {
        content.setBackground(new Background(backgroundImage));
      }
    }


  }

}
