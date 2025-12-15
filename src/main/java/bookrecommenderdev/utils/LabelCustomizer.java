package bookrecommenderdev.utils;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class LabelCustomizer {

  /**
   * Genera contenitori {@link Label} per mostrare dati dalla classe interessata.
   * @param msg messaggio testuale
   * @param s dimensione del testo
   * @param color colore del testo
   * @return testo dato in un contenitore formattato
   */
  public static Label createLabel(String msg, Size s, Color color) {
    Label l = new Label(msg);
    l.setFont(Font.font(s.getValue()));
    l.setStyle("-fx-text-fill:"+ color.toString().replace("0x", "#") + ";");
    return l;
  }

  /**
   * Genera contenitori {@link Label} per mostrare dati dalla classe interessata.
   * @param msg messaggio testuale
   * @param s dimensione del testo
   * @param color colore del testo
   * @param alignment allineamento orizzontale
   * @return testo dato in un contenitore formattato
   */
  public static Label createLabel(String msg, Size s, Color color, TextAlignment alignment) {
    // todo removal IF unused
    Label l = createLabel(msg, s, color);
    l.setTextAlignment(alignment);
    return l;
  }

}
