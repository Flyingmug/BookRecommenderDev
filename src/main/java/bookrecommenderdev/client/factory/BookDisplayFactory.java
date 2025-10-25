package bookrecommenderdev.client.factory;

import bookrecommenderdev.model.Libro;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.function.Consumer;

public class BookDisplayFactory extends VBoxFactory {

  public VBox createVBox(Libro l, Consumer<Integer> onClick) {
    VBox row = new VBox(5);
    row.setStyle("-fx-padding: 10; -fx-border-color: #99b1e9; -fx-border-width: 0 0 1 0;");

    Label titolo = new Label(l.getTitolo());
    titolo.setMaxWidth(750);
    titolo.setEllipsisString("...");
    titolo.setFont(new Font("Arial", 14));
    titolo.setTextFill(Paint.valueOf("#1e81c5"));
    titolo.getStyleClass().add("result-book");
    Button titoloButton = new Button();
    titoloButton.setGraphic(titolo);
    titoloButton.getStyleClass().add("result-book-button");
    titoloButton.setOnAction(_ ->  onClick.accept(l.getIdLibro()));


    Label autori = new Label(l.getAutori());
    autori.setMaxWidth(750);
    autori.setEllipsisString("...");

    autori.setFont(new Font("Arial", 12));

    Label anno = new Label(l.getAnnoPubblicazione() > 0 ? Integer.toString(l.getAnnoPubblicazione()) : "");
    anno.setFont(new Font("Arial", 12));

    row.getChildren().addAll(titoloButton, autori, anno);

    return row;
  }

}
