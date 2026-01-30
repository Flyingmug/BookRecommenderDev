package bookrecommenderdev.client.controller.layouts;

import bookrecommenderdev.client.controller.components.NavbarController;
import bookrecommenderdev.client.routing.layout.LayoutController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;

/**
 * Controller JavaFX per il layout che raggruppa:
 * <ul>
 *   <li>Barra di navigazione con barra di ricerca integrata;</li>
 *   <li>Nodo <i>contenuto</i>.</li>
 * </ul>
 */
public class IntegratedLayout implements LayoutController {

  @FXML private StackPane content;
  @FXML private NavbarController navbarController;

  @FXML
  void initialize() {
    setCenterBackground();
    if (navbarController != null) {
      navbarController.setSearchbarVisible(true);
    }
  }

  @Override
  public Pane getContent() { return content; }

  @Override
  public void setContent(Parent contentNode) {
    content.getChildren().setAll(contentNode);
  }

  /**
   * Carica lo sfondo della pagina centrale.
   */
  private void setCenterBackground() {
    // background
    URL imageUrl = getClass().getResource("/bookrecommenderdev/assets/library-background.jpg");
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
