package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.BookResultItemController;
import bookrecommenderdev.client.controller.components.ReviewItemController;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.function.Consumer;

public class BookResultItemFactory {

  /**
   * todo doc
   * @param l
   * @param onClick
   * @return
   */
  public static Parent createBookResultItem(Libro l, Consumer<Integer> onClick) {
    return createBookResultItem(l, onClick, null, null, null);
  }

  /**
   * todo doc
   * @param l
   * @param onOpen
   * @param actionText
   * @param onAction
   * @return
   */
  public static Parent createBookResultItem(
      Libro l,
      Consumer<Integer> onOpen,
      String actionText,
      String actionIconLiteral,
      Consumer<Integer> onAction
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/book-result-item.fxml")
      );
      Parent node = loader.load();
      BookResultItemController controller = loader.getController();

      controller.setBook(l, onOpen);

      if (onAction != null) {
        controller.enableActionButton(actionText, actionIconLiteral, onAction);
      }

      return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Unable to load content", Size.SM, Color.RED)
      );
      return node;
    }
  }

}
