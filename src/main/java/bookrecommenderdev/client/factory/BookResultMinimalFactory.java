package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.BookResultMinimalController;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.function.Consumer;

public class BookResultMinimalFactory {

  public static Parent create(
      Libro l,
      String actionIconLiteral,
      Consumer<Integer> onAction
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/book-result-minimal.fxml")
      );
      Parent node = loader.load();
      BookResultMinimalController controller = loader.getController();

      controller.setBook(l);

      if (onAction != null) {
        controller.enableActionButton(actionIconLiteral, onAction);
      }

      return node;

    } catch (IOException e) {
      e.printStackTrace();
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Unable to load content", Size.SM, Color.RED)
      );
      return node;
    }
  }

}
