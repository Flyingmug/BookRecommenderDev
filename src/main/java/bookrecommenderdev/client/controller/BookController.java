package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Routable;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.server.dto.PaginaLibro;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;

public class BookController implements Routable {

  @FXML
  public VBox bookPage;
  @FXML
  public Label bookPageTitolo;
  @FXML
  public Label bookPageAutori;
  @FXML
  public Label bookPageAnnoPubblicazione;
  @FXML
  public Label bookPageEditore;
  @FXML
  public Label bookPageCategorie;
  @FXML
  public VBox scoresSection;
  @FXML
  public VBox scoresContainer;
  @FXML
  public VBox reviewsLinkContainer;

  AppContext context;
  int idLibro;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    this.context = context;
    loadPublicBookPage(Integer.parseInt(params.get("query")));
  }

  @FXML
  public void initialize() {

  }

  private void resetBookPage() {
    scoresContainer.getChildren().clear();
  }


  /**
   * Ottiene i dati relativi a un libro e li inserisce nei relativi campi
   * della pagina.
   * @param idLibro id del libro selezionato
   */
  protected void loadPublicBookPage(int idLibro) {
    System.out.println("BOOKPAGE id libro: " + idLibro); // DEBUG
    this.idLibro = idLibro;

    resetBookPage();
    bookPage.setVisible(true);

    try {

      PaginaLibro pagina = context.server().getPaginaLibro(idLibro);

      if (pagina != null) {

        if (pagina.getLibro() != null) {
          Libro l = pagina.getLibro();

          bookPageTitolo.setText(l.getTitolo());
          bookPageAutori.setText(l.getAutori());
          bookPageAnnoPubblicazione.setText(Integer.toString(l.getAnnoPubblicazione()));
          bookPageCategorie.setText(l.getCategorie());
          bookPageEditore.setText(l.getEditore());
        }

        if (pagina.getValutazioniAggregate() != null) {
          double[] scores = pagina.getValutazioniAggregate();
          showScores(scores);
        } else {
          scoresSection.getChildren().clear();
          scoresSection.getChildren().add(
              LabelCustomizer.createLabel(
                  "Nessuna valutazione presente",
                  Size.LG,
                  Color.BLACK
              )
          );
        }

      }

    } catch (RemoteException e) {
      e.printStackTrace();

      // todo Add error display
    }
  }


  public void onReviews() {
    Router.go("/book/:query/reviews" + idLibro);
  }

  /**
   * Valuta e carica i punteggi con relative medie.
   * @param valutazioni array contenente valutazioni del libro
   */
  private void showScores(double[] valutazioni) {

    double totalScore = Arrays.stream(valutazioni)
        .filter(v -> v > 0)
        .average()
        .orElse(0.0);
    scoresContainer.getChildren().add(buildStars(totalScore, 24));

    for (double score: valutazioni) {
      scoresContainer.getChildren().add(buildStars(score, 20));
    }
  }
  /**
   * Genera un contenitore di icone (stelle) rappresentanti il valore dato come parametro
   * @param score valore rappresentato
   * @param size dimensione delle icone
   * @return contenitore di icone
   */
  private HBox buildStars(double score, int size) {
    HBox stars = new HBox(2);

    int fullStars = (int) score;                 // whole number part
    boolean hasHalf = (score - fullStars) >= 0.5; // half star if remainder is greater than 0.5

    for (int i = 1; i <= 5; i++) {
      FontIcon star;

      if (i <= fullStars) {
        star = new FontIcon("mdi2s-star"); // full
        star.setIconColor(Paint.valueOf("gold"));
      } else if (i == fullStars + 1 && hasHalf) {
        star = new FontIcon("mdi2s-star-half-full"); // half
        star.setIconColor(Paint.valueOf("gold"));
      } else {
        star = new FontIcon("mdi2s-star-outline"); // empty
        star.setIconColor(Paint.valueOf("gray"));
      }

      star.setIconSize(size);
      stars.getChildren().add(star);
    }

    return stars;
  }

}
