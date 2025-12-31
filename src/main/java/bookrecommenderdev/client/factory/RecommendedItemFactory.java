package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.RecommendedItemController;
import bookrecommenderdev.model.dto.LibroConsigliato;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.function.Consumer;

public class RecommendedItemFactory {

  public static Parent create(
      LibroConsigliato cons,
      Consumer<Integer> onClick
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/recommended-item.fxml")
      );
      Parent node = loader.load();
      RecommendedItemController controller = loader.getController();

      controller.setItem(cons, onClick);

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
