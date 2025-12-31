package bookrecommenderdev.client.factory;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

public class StarIconFactory {

  private final static int STAR_SIZE = 25;
  private static double clipPadding() { return Math.ceil(STAR_SIZE * 0.05); }

  /**
   * Genera un contenitore di icone (stelle) rappresentanti il valore dato come parametro
   * I valori decimali di resto superiore a 0.5 avranno un icona di mezza stella come ultima.
   * @param score valore rappresentato
   * @return contenitore di icone
   */
  public static HBox buildStars(double score) {
    HBox stars = new HBox(2);

    int fullStars = (int) score;                  // punteggio troncato
    boolean hasHalf = (score - fullStars) >= 0.5; // mezza icona se il resto del punteggio è >= a 0.5

    for (int i = 1; i <= 5; i++) {
      stars.getChildren().add(buildStarSlot(i, fullStars, hasHalf));
    }

    return stars;
  }
  /** Metodo helper per la costruzione delle icone */
  private static StackPane buildStarSlot(int index, int fullStars, boolean hasHalf) {
    StackPane slot = new StackPane();

    FontIcon empty = new FontIcon("mdi2s-star");
    empty.getStyleClass().add("star-empty");
    empty.setIconSize(STAR_SIZE);

    slot.getChildren().add(empty);

    if (index <= fullStars) {
      FontIcon full = new FontIcon("mdi2s-star");
      full.getStyleClass().add("star");
      full.setIconSize(STAR_SIZE);
      slot.getChildren().add(full);

    } else if (index == fullStars + 1 && hasHalf) {
      FontIcon half = new FontIcon("mdi2s-star");
      half.getStyleClass().add("star");
      half.setIconSize(STAR_SIZE);

      StackPane wrapper = new StackPane(half);
      wrapper.setPrefSize(STAR_SIZE, STAR_SIZE);
      wrapper.setAlignment(Pos.CENTER_LEFT);

      wrapper.setClip(new Rectangle(STAR_SIZE / 2.0, STAR_SIZE + clipPadding()));

      slot.getChildren().add(wrapper);

    }

    return slot;
  }

}
