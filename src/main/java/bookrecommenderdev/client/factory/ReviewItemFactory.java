package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.ReviewItemController;
import bookrecommenderdev.model.Valutazione;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ReviewItemFactory {

  public static Parent create(Valutazione v) {

    try {
        FXMLLoader loader = new FXMLLoader(
            ReviewItemFactory.class.getResource("/bookrecommenderdev/client/components/review-item.fxml")
        );
        Parent node = loader.load();
        ReviewItemController controller = loader.getController();
        controller.setReview(v);
        return node;
    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Unable to load content", Size.SM, Color.RED)
      );
      e.printStackTrace();
      return node;
    }

  }

}
