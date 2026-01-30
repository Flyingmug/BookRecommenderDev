package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.RecommendedItemController;
import bookrecommenderdev.model.dto.LibroConsigliato;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Factory responsabile della creazione del componente grafico "Consiglio di Lettura".
 *
 * <p>Centralizza il caricamento dell’FXML e l’inizializzazione del relativo
 * controller ({@link RecommendedItemController}), fornendo un punto unico e consistente
 * per costruire l’elemento.</p>
 */
public class RecommendedItemFactory {

  /**
   * Crea un elemento grafico di un consiglio di lettura.
   *
   * <p>All'elemento vengono associati:</p>
   * <ul>
   *  <li>i dati relativi al consiglio (informazioni sul libro e sul conteggio di raccomandazioni)</li>
   *  <li>l'handler di click</li>
   * </ul>
   *
   * <p><b>Gestione errori:</b> se il caricamento FXML fallisce, viene restituito
   * un nodo di fallback con messaggio.</p>
   *
   * @param cons libro consigliato
   * @param onClick callback eseguita quando l’utente seleziona l’elemento
   * @return nodo radice del componente caricato, oppure un nodo di fallback in caso di errore
   */
  public static Parent create(
      LibroConsigliato cons,
      Consumer<Integer> onClick
  ) {
    try {
      FXMLLoader loader = new FXMLLoader(
          BookResultItemFactory.class.getResource("/bookrecommenderdev/client/components/recommended-item.fxml")
      );
      Parent node = loader.load();
      RecommendedItemController controller = loader.getController();

      controller.setItem(cons, onClick);
      return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Impossibile caricare il consiglio", Size.SM, Color.RED)
      );
      return node;
    }
  }


}
