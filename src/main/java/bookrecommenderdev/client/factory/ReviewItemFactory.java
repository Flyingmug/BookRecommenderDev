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
import java.util.function.Consumer;

public class ReviewItemFactory {


  public static Parent createReviewNode(Valutazione v) {

    try {
      FXMLLoader loader = new FXMLLoader(
          ReviewItemFactory.class.getResource("/bookrecommenderdev/client/components/review-item.fxml")
      );
      Parent node = loader.load();
      ReviewItemController controller = loader.getController();
      controller.setReview(v);
      return node;
    } catch (IOException e) {
      // temp error item
      // todo needs customization, but it might be good done this way
      // todo consider using a separate class for this kind of items
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Unable to retrieve content", Size.SM, Color.RED)
      );
      return node;
    }

  }

}
