package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.ReviewsSectionController;
import bookrecommenderdev.client.factory.StarIconFactory;
import bookrecommenderdev.model.CampoValutazione;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.server.dto.PaginaLibro;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;

public class BookController implements Routable {

  @FXML public ScrollPane bookPage; // fixme removable if unused
  @FXML public Label bookTitolo;
  @FXML public Label bookAutori;
  @FXML public Label bookAnnoPubblicazione;
  @FXML public Label bookEditore;
  @FXML public Label bookCategorie;
  @FXML public VBox scoresSection;
  @FXML public TilePane scoresContainer;
  @FXML public VBox reviewsLinkContainer; // fixme kept for testing purposes
  @FXML public VBox reviewsSection;
  @FXML private ReviewsSectionController reviewsSectionController;  // assegnazione automatica tramite fx:include


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

  /**
   * Ottiene i dati relativi a un libro e li inserisce nei relativi campi
   * della pagina.
   * @param idLibro id del libro selezionato
   */
  protected void loadPublicBookPage(int idLibro) {
    System.out.println("BOOKPAGE id libro: " + idLibro); // DEBUG
    this.idLibro = idLibro;

    try {
      PaginaLibro pagina = context.server().getPaginaLibro(idLibro);

      if (pagina != null) {

        if (pagina.getLibro() != null) {
          Libro l = pagina.getLibro();

          bookTitolo.setText(l.getTitolo());
          bookAutori.setText(l.getAutori());
          bookAnnoPubblicazione.setText(Integer.toString(l.getAnnoPubblicazione()));
          bookCategorie.setText(l.getCategorie());
          bookEditore.setText(l.getEditore());
        }

        if (pagina.getValutazioniAggregate() != null && scoresPresent(pagina.getValutazioniAggregate())) {
          double[] scores = pagina.getValutazioniAggregate();

          showReviews();  // mostra la sezione delle recensioni
          showScores(scores); // mostra le medie delle valutazioni
        } else {
          reviewsLinkContainer.setVisible(false);
          scoresContainer.getChildren().add(
              LabelCustomizer.createLabel(
                  "Nessuna valutazione presente",
                  Size.LG,
                  Color.BLACK
              )
          );
          setReviewsVisible(false);
        }

      }

    } catch (RemoteException e) {
      e.printStackTrace();

      // todo Add error display
    }
  }

  private void showReviews() {
    // caricamento delle review
    reviewsSectionController.initializeForBook(idLibro, context);
  }

  private void setReviewsVisible(boolean b) {
    reviewsSection.setVisible(b);
    reviewsSection.setManaged(b);
  }

  // fixme kept for testing purposes.
  public void onReviews() {
    Router.go("/book/"+idLibro+"/reviews");
  }


  /**
   * Verifica che il numero dei campi di valutazione nel vettore dato rispetti la dimensione dei campi definiti in {@link CampoValutazione}.
   */
  private boolean scoresPresent(double[] scores) {
    return scores != null && scores.length == CampoValutazione.values().length - 1;
  }
  /**
   * Valuta la media dei punteggi e li inserisce nella grafica.
   * @param valutazioni Vettore contenente valutazioni del libro
   */
  private void showScores(double[] valutazioni) {

    double totalScore = Arrays.stream(valutazioni)
        .filter(v -> v > 0)
        .average()
        .orElse(0.0);

    scoresContainer.getChildren().add(
        buildScoreItem(CampoValutazione.GENERALE.label(), totalScore)
    );

    CampoValutazione[] campi = CampoValutazione.values();
    for (int i = 1; i < campi.length; i++) {
      scoresContainer.getChildren().add(
          buildScoreItem(campi[i].label(), valutazioni[i - 1])
      );
    }
  }

  /**
   * Organizza le informazioni date e rappresenta il valore {@code score} tramite icone.
   *
   * @param name  Nome del campo
   * @param score Punteggio
   * @return {@link VBox} contenente le informazioni organizzate
   */
  private VBox buildScoreItem(String name, double score) {
    HBox header = new HBox(
        LabelCustomizer.createLabel(name.toUpperCase(), Size.SM, FontWeight.MEDIUM, Color.BLACK),
        LabelCustomizer.createLabel(String.format("%.1f", score), Size.MD, Color.BLACK)
    );
    header.setSpacing(3);
    header.setPadding(new Insets(0, 0, 0, 5));
    header.setAlignment(Pos.BASELINE_LEFT);
    header.setCache(false);

    return new VBox(
        header,
        StarIconFactory.buildStars(score)
    );
  }

}
