package bookrecommenderdev.client.factory;

import bookrecommenderdev.model.Libro;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.function.Consumer;

public class BookDisplayFactory extends VBoxFactory {

  public VBox createVBox(Libro l, Consumer<Integer> onClick) {
    VBox row = new VBox(5);
    row.getStyleClass().add("result-book");

    Label titolo = LabelCustomizer.createLabel(l.getTitolo(), Size.SM, Color.valueOf("#1e81c5"));
    titolo.getStyleClass().add("result-book-label");
    titolo.setMaxWidth(750);
    titolo.setEllipsisString("...");

    Button titoloButton = new Button();
    titoloButton.setGraphic(titolo);
    titoloButton.getStyleClass().add("result-book-button");
    titoloButton.setOnAction(_ ->  onClick.accept(l.getIdLibro()));

    Label autori = LabelCustomizer.createLabel(l.getAutori(), Size.XS);
    autori.setMaxWidth(750);
    autori.setEllipsisString("...");

    Label anno = LabelCustomizer.createLabel(
        l.getAnnoPubblicazione() > 0 ? Integer.toString(l.getAnnoPubblicazione()) : "",
        Size.XS);

    row.getChildren().addAll(titoloButton, autori, anno);
    return row;
  }

}
