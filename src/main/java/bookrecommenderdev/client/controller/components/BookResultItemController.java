package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class BookResultItemController {

  @FXML private Button linkButton;
  @FXML private Label titleLabel;
  @FXML private Label authorLabel;
  @FXML private Label yearLabel;

  public void setBook(Libro l, Consumer<Integer> onClick) {

    titleLabel.setText(l.getTitolo());
    titleLabel.setEllipsisString("...");

    linkButton.setOnAction(_ ->  onClick.accept(l.getIdLibro()));

    authorLabel.setText(l.getAutori());
    authorLabel.setEllipsisString("...");

    yearLabel.setText(l.getAnnoPubblicazione() > 0 ?
        Integer.toString(l.getAnnoPubblicazione()) : "");

  }

}
