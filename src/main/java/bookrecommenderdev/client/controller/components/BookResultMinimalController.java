package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.base.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class BookResultMinimalController {

  @FXML private Label title;
  @FXML private Button actionButton;
  @FXML private FontIcon actionIcon;

  private Libro libro;
  private Consumer<Integer> onAction = null;

  public void setBook(Libro l) {
    this.title.setText(l.getTitolo());
    this.libro = l;
  }

  public void enableActionButton(String iconLiteral, Consumer<Integer> onAction) {
    this.onAction = onAction;

    if (actionIcon != null) {
      actionIcon.setIconLiteral(iconLiteral == null ? "mdi2p-plus-box-outline" : iconLiteral);
      actionIcon.setVisible(iconLiteral != null && !iconLiteral.isBlank());
    }

    actionButton.setVisible(true);
    actionButton.setManaged(true);
    actionButton.setDisable(false);
  }

  @FXML
  private void onActionClicked() {
    if (libro == null || onAction == null) return;
    onAction.accept(libro.getIdLibro());
  }
}
