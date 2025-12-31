package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.dto.LibroConsigliato;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class RecommendedItemController {

  @FXML private Button linkButton;
  @FXML private Label recommendCountLabel;
  @FXML private Label titleLabel;
  @FXML private Label authorLabel;
  @FXML private Label yearLabel;

  public void setItem(LibroConsigliato cons, Consumer<Integer> onClickOpen) {

    Libro l = cons.libro();
    int count = cons.countConsigliato();

    titleLabel.setText(l.getTitolo());

    authorLabel.setText(l.getAutori());

    yearLabel.setText(l.getAnnoPubblicazione() > 0 ?
        Integer.toString(l.getAnnoPubblicazione()) : "");

    recommendCountLabel.setText(Integer.toString(count));

    linkButton.setOnMouseClicked(onClickOpen == null ?
        _ -> {} :
        _ -> onClickOpen.accept(l.getIdLibro()));

  }

}
