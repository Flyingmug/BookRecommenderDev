package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class BookResultItemController {

  @FXML private Button linkButton;
  @FXML private Label titleLabel;
  @FXML private Label authorLabel;
  @FXML private Label yearLabel;
  @FXML private Button actionButton;
  @FXML private FontIcon actionIcon;

  private Libro libro;
  private Consumer<Integer> onOpen = _ -> {};
  private Consumer<Integer> onAction = null;

  public void setBook(Libro l, Consumer<Integer> onClickOpen) {
    this.libro = l;
    this.onOpen = (onClickOpen == null) ? (_ -> {}) : onClickOpen;

    titleLabel.setText(l.getTitolo());

    authorLabel.setText(l.getAutori());

    yearLabel.setText(l.getAnnoPubblicazione() > 0 ?
        Integer.toString(l.getAnnoPubblicazione()) : "");

    linkButton.setOnAction(_ ->  this.onOpen.accept(l.getIdLibro()));

    hideActionButton();
  }


  public void enableActionButton(String text, String iconLiteral, Consumer<Integer> onAction) {
    this.onAction = onAction;

    actionButton.setText(text == null ? "" : text);
    if (actionIcon != null) {
      actionIcon.setIconLiteral(iconLiteral == null ? "" : iconLiteral);
      actionIcon.setVisible(iconLiteral != null && !iconLiteral.isBlank());
    }

    actionButton.setVisible(true);
    actionButton.setManaged(true);
    actionButton.setDisable(false);
  }

  public void disableActionButton() {
    actionButton.setDisable(true);
  }

  public void hideActionButton() {
    actionButton.setVisible(false);
    actionButton.setManaged(false);
    this.onAction = null;
  }

  @FXML
  private void onActionClicked() {
    if (libro == null || onAction == null) return;
    onAction.accept(libro.getIdLibro());
  }

}
