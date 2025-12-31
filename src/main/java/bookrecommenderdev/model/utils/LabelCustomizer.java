package bookrecommenderdev.model.utils;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LabelCustomizer {

  /**
   * Genera contenitori {@link Label} per mostrare dati dalla classe interessata.
   * @param msg Messaggio testuale
   * @param s Dimensione del testo
   * @param weight Spessore del testo
   * @param color Colore del testo
   * @return testo formattato in un elemento {@link Label}
   */
  public static Label createLabel(String msg, Size s, FontWeight weight, Color color) {
    Label l = new Label(msg);
    l.setFont(Font.font("Montserrat", weight, s.getValue()));
    l.setStyle(
        "-fx-text-fill:" + color.toString().replace("0x", "#") + ";");
    return l;
  }

  /**
   * Genera contenitori {@link Label} per mostrare dati dalla classe interessata.
   * @param msg messaggio testuale
   * @param s dimensione del testo
   * @param color colore del testo
   * @return testo dato in un contenitore formattato
   */
  public static Label createLabel(String msg, Size s, Color color) {
    return createLabel(msg, s, FontWeight.NORMAL, color);
  }

  /**
   * Genera contenitori {@link Label} per mostrare dati dalla classe interessata.
   * @param msg messaggio testuale
   * @param s dimensione del testo
   * @return testo dato in un contenitore formattato
   */
  public static Label createLabel(String msg, Size s) {
    Label l = new Label(msg);
    l.setFont(Font.font("Montserrat", s.getValue()));
    return l;
  }

}
