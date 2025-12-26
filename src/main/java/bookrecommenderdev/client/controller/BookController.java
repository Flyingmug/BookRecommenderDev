package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.ReviewsSectionController;
import bookrecommenderdev.client.controller.components.UserReviewSectionController;
import bookrecommenderdev.client.factory.StarIconFactory;
import bookrecommenderdev.model.CampoValutazione;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.context.CurrentBookContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.server.dto.PaginaLibro;
import bookrecommenderdev.utils.LabelCustomizer;
import bookrecommenderdev.utils.Size;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;

public class BookController implements Routable {

  @FXML private ScrollPane bookPage; // fixme removable if unused
  @FXML private Label bookTitolo;
  @FXML private Label bookAutori;
  @FXML private Label bookAnnoPubblicazione;
  @FXML private Label bookEditore;
  @FXML private Label bookCategorie;
  @FXML private VBox scoresSection;
  @FXML private TilePane scoresContainer;
  @FXML private VBox reviewsLinkContainer; // fixme kept for testing purposes
  @FXML private VBox reviewsSection;
  @FXML private ReviewsSectionController reviewsSectionController;  // assegnazione automatica tramite fx:include

  @FXML private Parent userReviewSection;
  @FXML private UserReviewSectionController userReviewSectionController;

  AppContext context;
  int idLibro;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    this.context = context;
    loadBookPage(Integer.parseInt(params.get("query")));

    userReviewSectionController.setContext(context, idLibro);
  }

  @FXML
  public void initialize() {
    setHandleLayoutChange();
  }

  /**
   * Ottiene i dati relativi a un libro e li inserisce nei relativi campi
   * della pagina.
   * @param idLibro id del libro selezionato
   */
  protected void loadBookPage(int idLibro) {
    this.idLibro = idLibro;

    try {
      PaginaLibro pagina = context.server().getPaginaLibro(idLibro);

      if (pagina != null) {

        if (pagina.getLibro() != null) {
          Libro l = pagina.getLibro();
          CurrentBookContext.set(l);

          setTextValue(bookTitolo, l.getTitolo());
          setTextValue(bookAutori, l.getAutori());
          setTextValue(bookAnnoPubblicazione, Integer.toString(l.getAnnoPubblicazione()));
          setTextValue(bookEditore, l.getEditore());
          setTextValue(bookCategorie, l.getCategorie());
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
          hideReviews();
        }

      }

    } catch (RemoteException e) {
      e.printStackTrace();

      // todo Add error display
    }
  }

  private void setTextValue(Label label, String text) {
    label.setText(text == null || text.isBlank() ? "Sconosciuto" : text);
  }

  private void showReviews() {
    // caricamento delle review
    reviewsSectionController.initializeForBook(idLibro, context);
  }

  private void hideReviews() {
    reviewsSection.setVisible(false);
    reviewsSection.setManaged(false);
  }

  // fixme kept for testing purposes.
  // fixme what happens when an unexisting route is requested? -> error
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

  /** Reimposta al valore precedente il {@code vvalue} dello {@link ScrollPane} al cambiamento di layout. */
  private void setHandleLayoutChange() {

    if (userReviewSectionController != null) {
      userReviewSectionController.setOnLayoutChange(() -> {
        double v = bookPage.getVvalue();

        bookPage.applyCss();
        bookPage.layout();

        Platform.runLater(() -> bookPage.setVvalue(v));
      });
    }
  }
}
