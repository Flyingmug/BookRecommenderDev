package bookrecommenderdev.client.controller.layouts;

import bookrecommenderdev.routing.LayoutController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class DefaultLayout implements LayoutController {

  @FXML private BorderPane root;
  @FXML private StackPane content;

  public DefaultLayout() {
//    try {
//      FXMLLoader loader = new FXMLLoader(
//          DefaultLayout.class.getResource("/layouts/default_layout.fxml")
//      );
//
//      DefaultLayout controller = new DefaultLayout();
//      loader.setController(controller);
//      loader.load();
//
//      return controller;
//
//    } catch (IOException e) {
//      throw new IllegalStateException(
//          "Failed to load default layout FXML", e
//      );
//    }
  }

  @Override
  public Parent getRoot() {
    return root;
  }

  @Override
  public Parent getContent() { return content; }

  @Override
  public void setContent(Parent contentNode) {
    content.getChildren().setAll(contentNode);
  }
}
