package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.ReviewsSectionController;
import bookrecommenderdev.client.controller.components.UserReviewSectionController;
import bookrecommenderdev.client.factory.StarIconFactory;
import bookrecommenderdev.model.CampoValutazione;
import bookrecommenderdev.model.DataAccessException;
import bookrecommenderdev.model.NotFoundException;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.routing.auth.AuthContext;
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

  @FXML private ScrollPane bookPage;
  @FXML private VBox content;
  @FXML private Label titolo;
  @FXML private Label autori;
  @FXML private Label annoPubblicazione;
  @FXML private Label editore;
  @FXML private Label categorie;
  @FXML private TilePane scoresContainer;
  @FXML private VBox myReviewSection;

  @FXML private VBox reviewsSection;
  @FXML private ReviewsSectionController reviewsSectionController;  // assegnazione automatica tramite fx:include

  @FXML private Parent userReviewSection;
  @FXML private UserReviewSectionController userReviewSectionController;

  @FXML private ErrorBannerController errorBannerController;


  AppContext context;
  int idLibro;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;

    idLibro = (Integer.parseInt(params.get("id")));

    userReviewSectionController.setContext(context, idLibro);

    loadBookPage(idLibro);
  }

  @FXML
  public void initialize() {

    setHandleLayoutChange();

    myReviewSection.visibleProperty().bind(
        AuthContext.userProperty().isNotNull()
    );
    myReviewSection.managedProperty().bind(
        AuthContext.userProperty().isNotNull()
    );
  }

  /**
   * Ottiene i dati relativi a un libro e li inserisce nei relativi campi
   * della pagina.
   * @param idLibro id del libro selezionato
   */
  protected void loadBookPage(int idLibro) {
    try {
      PaginaLibro pagina = context.server().getPaginaLibro(idLibro);

      setContentVisible(true);

      Libro l = pagina.getLibro();

      setTextValue(titolo, l.getTitolo());
      setTextValue(autori, l.getAutori());
      setTextValue(annoPubblicazione, Integer.toString(l.getAnnoPubblicazione()));
      setTextValue(editore, l.getEditore());
      setTextValue(categorie, l.getCategorie());

      double[] scores = pagina.getValutazioniAggregate();
      if (scoresPresent(pagina.getValutazioniAggregate())) {
        showReviews();  // mostra la sezione delle recensioni
        showScores(scores); // mostra le medie delle valutazioni
      } else {

        scoresContainer.getChildren().add(
            LabelCustomizer.createLabel(
                "Nessuna valutazione presente",
                Size.LG,
                Color.BLACK
            )
        );
        hideReviews();
      }

    } catch (NotFoundException e) {
      Platform.runLater(() -> Router.go("/not-found", TransitionAnimation.LEFT_SLIDE));

    } catch (DataAccessException e) {
      setContentVisible(false);
      content.setVisible(false);
      content.setManaged(false);

      errorBannerController.show(
          "Servizio dati non disponibile (errore database). Riprova tra poco.",
          () -> loadBookPage(idLibro),
          () -> Router.go("/")
      );

    } catch (RemoteException e) {
      content.setVisible(false);
      content.setManaged(false);

      errorBannerController.show(
          "Server non raggiungibile. Verifica la connessione e riprova.",
          () -> loadBookPage(idLibro),
          () -> Router.go("/")
      );
    }
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

  private void setContentVisible(boolean b) {
    content.setVisible(b);
    content.setManaged(b);
  }

}
