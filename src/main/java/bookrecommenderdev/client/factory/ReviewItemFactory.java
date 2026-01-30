package bookrecommenderdev.client.factory;

import bookrecommenderdev.client.controller.components.ReviewItemController;
import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.model.utils.LabelCustomizer;
import bookrecommenderdev.model.utils.Size;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Factory responsabile della creazione del componente grafico "Recensione".
 *
 * <p>Centralizza il caricamento dell’FXML e l’inizializzazione del relativo
 * controller ({@link ReviewItemController}), fornendo un punto unico e consistente
 * per costruire l’elemento.</p>
 */
public class ReviewItemFactory {

  /**
   * Crea un elemento grafico di una recensione.
   *
   * <p>All'elemento vengono associati i dati relativi alla recensione, comprendenti testo e punteggi</p>
   *
   * <p><b>Gestione errori:</b> se il caricamento FXML fallisce, viene restituito
   * un nodo di fallback con messaggio.</p>
   *
   * @param v valutazione da rappresentare
   * @return nodo radice del componente caricato, oppure un nodo di fallback in caso di errore
   */
  public static Parent create(Valutazione v) {

    try {
        FXMLLoader loader = new FXMLLoader(
            ReviewItemFactory.class.getResource("/bookrecommenderdev/client/components/review-item.fxml")
        );
        Parent node = loader.load();
        ReviewItemController controller = loader.getController();

        controller.setReview(v);
        return node;

    } catch (IOException e) {
      VBox node = new VBox();
      node.getChildren().add(
          LabelCustomizer.createLabel("Impossibile caricare la recensione", Size.SM, Color.RED)
      );
      return node;
    }

  }

}
