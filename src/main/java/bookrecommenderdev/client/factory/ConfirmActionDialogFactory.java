package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.controls.ConfirmActionDialogController;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ConfirmActionDialogFactory {

  public static Parent create(
      Runnable onConfirm,
      Runnable onCancel
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/controls/confirm-action-dialog.fxml")
      );
      Parent node = loader.load();
      ConfirmActionDialogController controller = loader.getController();

      controller.setOnConfirm(onConfirm);
      controller.setOnCancel(onCancel);

      return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Unable to load dialog", Size.SM, Color.RED)
      );
      return node;
    }
  }

}
