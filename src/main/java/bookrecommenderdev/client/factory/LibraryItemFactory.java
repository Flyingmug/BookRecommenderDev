package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.LibraryItemController;
import bookrecommenderdev.model.Libreria;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class LibraryItemFactory {

  public static Parent createLibraryItem(Libreria lib, int totalCount, Runnable onClick) {

    try {
      FXMLLoader loader = new FXMLLoader(
          ReviewItemFactory.class.getResource("/bookrecommenderdev/client/components/library-item.fxml")
      );
      Parent node = loader.load();
      LibraryItemController controller = loader.getController();
      controller.setLibrary(lib, totalCount, onClick);
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
