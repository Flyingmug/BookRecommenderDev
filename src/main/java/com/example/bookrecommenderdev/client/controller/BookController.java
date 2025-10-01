package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.client.AppContext;
import com.example.bookrecommenderdev.client.Routable;
import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.server.dto.PaginaLibro;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
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
  public VBox scoresContainer;

  AppContext context;

  @FXML
  public void initialize() {

  }

  private void resetBookPage() {
    scoresContainer.getChildren().clear();
  }


  /**
   * Mostra il contenitore della pagina di un libro, nel quale vengono caricati i dati del libro selezionato.
   * @param idLibro id del libro selezionato
   */
  @FXML
  protected void onPublicBookPage(int idLibro) {
    System.out.println("id: " + idLibro); // DEBUG

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
        }

      }

    } catch (RemoteException e) {
      e.printStackTrace();

      //
      //
      //
      // Add error display
      //
      //
      //
    }
  }

  /**
   * Crea la rappresentazione dei punteggi e della media di essi
   * @param valutazioni array contenente valutazioni
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
   * Restituisce un contenitore di icone di stelle rappresentanti il valore dato come parametro
   * @param score valore rappresentato
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

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    this.context = context;
  }
}
