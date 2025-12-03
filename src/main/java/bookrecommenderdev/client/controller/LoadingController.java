package bookrecommenderdev.client.controller;

import bookrecommenderdev.model.BookParams;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class LoadingController {


  @FXML public StackPane animationGroup;

  @FXML
  public void initialize() {

    List<BookParams> books = List.of(
        new BookParams(100, 5, 10, 25, 15, 90, 300, 500),
        new BookParams(80, 10, 15, 20, 10, 110, 250, 420),
        new BookParams(120, 6, 12, 18, 15, 60, 320, 480)
    );

    int totalThickness = books.stream().mapToInt(BookParams::thickness).sum();
    int startY = - totalThickness / 2;
    int currentY = startY;

    for (BookParams p : books) {
      Group g = createBookPathGroup(
          p.thickness(),
          p.extendH(), p.extendW(),
          p.convexR(), p.concaveR(),
          p.startAngle(),
          p.width(), p.height()
      );

      g.setTranslateY(currentY + (double) p.thickness() /2);
      currentY += p.thickness();

      animationGroup.getChildren().add(g);
    }

  }

////    if (angle % 360 < 30) { // fixme 30 is a placeholder
////      maxProjection = getMaxVerticalProjection(width, height);
////    }

// TODO Using the wrong key value throws error, consider not using a map at all -> method split
// TODO Fix Error page layout
// TODO Make the loading screen appear before the error page

// fixme double coverWidth = Math.hypot(width, vals.get("vertical_projection_height")); // proiezione dell'altezza sul piano

  private Group createBookPathGroup(
      double bookThickness,
      double extendH, double extendW,
      double convexR, double concaveR,
      double startAngle, double bookWidth, double bookHeight) {

    // Sizes evaluation
    Map<String, Double> vals = getVerticalProjections(bookWidth, bookHeight, startAngle);
    double projWidth = vals.get("projected_width"); // proiezione della base sul piano
    double projHeight = vals.get("projected_height"); // proiezione dell'altezza sul piano
    double width = projWidth + projHeight;

    // Input clamp
    double extW = Math.min(extendW, width);
    double convR = Math.min(convexR, width);
    double concR = Math.min(concaveR, width);

    // "Cover" path definition
    LineTo line0 = new LineTo(width - convR, 0);
    QuadCurveTo q0 = new QuadCurveTo(width, 0, width, bookThickness / 2);
    QuadCurveTo q1 = new QuadCurveTo(width, bookThickness, width - convR, bookThickness);
    LineTo line1 = new LineTo(0, bookThickness);
    LineTo line2 = new LineTo(0, bookThickness - extendH);
    LineTo line3 = new LineTo(extW, bookThickness - extendH);
    QuadCurveTo q2 = new QuadCurveTo(extW + concR, bookThickness - extendH, extW + concR, bookThickness / 2);
    QuadCurveTo q3 = new QuadCurveTo(extW + concR, extendH, extW, extendH);
    LineTo line4 = new LineTo(0, extendH);

    Path path1 = new Path(
        new MoveTo(0, 0),
        line0, q0, q1, line1, line2, line3, q2, q3, line4,
        new ClosePath()
    );
    path1.setStrokeWidth(0);
    path1.setFill(Color.TURQUOISE);

    // keyframes
    List<Timeline> keyframes = new LinkedList<>();

    double t1 = 0; // intermediate keyframe time instant
    double duration = 1; // animation duration
    double vertAngle = getVerticalDiagonalAngle(bookWidth, bookHeight);

    if(startAngle > vertAngle) {
      double maxDiagonalProj = Math.hypot(bookWidth, bookHeight);
      List<KeyValue> keyValues = new ArrayList<>();

      // Cover Keyframe 1
      addXAnimation(keyValues, line0, maxDiagonalProj - convR);
      addCurveXAnimation(keyValues, q0, maxDiagonalProj, maxDiagonalProj);
      addCurveXAnimation(keyValues, q1, maxDiagonalProj, maxDiagonalProj - convR);

      t1 = getIntermediateTime(duration, bookWidth, bookHeight, startAngle, vertAngle);
      keyframes.add(new Timeline(
          new KeyFrame(Duration.seconds(t1), keyValues.toArray(KeyValue[]::new))
      ));
    }

    // Cover Keyframe 2
    List<KeyValue> keyValues = new ArrayList<>();

    addXAnimation(keyValues, line0, bookHeight);
    addCurveXAnimation(keyValues, q0, bookHeight, bookHeight);
    addCurveXAnimation(keyValues, q1, bookHeight, bookHeight);
    addXAnimation(keyValues, line3, 0);
    addCurveXAnimation(keyValues, q2, 0, 0);
    addCurveXAnimation(keyValues, q3, 0, 0);

    keyframes.add(new Timeline(
        new KeyFrame(Duration.seconds(duration - t1), keyValues.toArray(KeyValue[]::new))
    ));

    SequentialTransition seq =
        new SequentialTransition(keyframes.toArray(Timeline[]::new));
    seq.setCycleCount(-1);
    seq.setAutoReverse(true);
    seq.play();


    //
    //
    //
    //
    //
    // "Pages" path definition
    LineTo line5 = new LineTo(projWidth - convR, extendH);
    QuadCurveTo q4 = new QuadCurveTo(projWidth - extendH, extendH, projWidth - extendH, bookThickness / 2);
    QuadCurveTo q5 = new QuadCurveTo(projWidth - extendH, bookThickness - extendH, projWidth - convR, bookThickness - extendH);
    LineTo line6 = new LineTo(extW, bookThickness - extendH);
    QuadCurveTo q6 = new QuadCurveTo(extW + concR, bookThickness - extendH, extW + concR, bookThickness / 2);
    QuadCurveTo q7 = new QuadCurveTo(extW + concR, extendH, extW, extendH);

    Path path2 = new Path(
        new MoveTo(extW, extendH),
        line5, q4, q5, line6, q6, q7,
        new ClosePath()
    );
    path2.setStrokeWidth(0);
    path2.setFill(Color.IVORY);

// keyframes
    List<Timeline> keyframesPages = new LinkedList<>();

    double t12 = 0; // intermediate keyframe time instant
    double vertAngle2 = getVerticalDiagonalAngle(bookWidth, bookHeight);

    if(startAngle > vertAngle2) {
//      double maxDiagonalProj = Math.hypot(bookWidth, bookHeight); // fixme check if needed  at all
      Map<String, Double> valss = getVerticalProjections(bookWidth, bookHeight, vertAngle2);
      double hProjWidthKEYFRAME = valss.get("projected_width");
      List<KeyValue> keyValuesPages = new ArrayList<>();

      // Cover Keyframe 1
      addXAnimation(keyValuesPages, line5, extW + hProjWidthKEYFRAME);
      addCurveXAnimation(keyValuesPages, q4, extW + hProjWidthKEYFRAME + convR, extW + hProjWidthKEYFRAME + convR);
      addCurveXAnimation(keyValuesPages, q5, extW + hProjWidthKEYFRAME + convR, extW + hProjWidthKEYFRAME);

      t12 = getIntermediateTime(duration, bookWidth, bookHeight, startAngle, vertAngle2);
      keyframesPages.add(new Timeline(
          new KeyFrame(Duration.seconds(t12), keyValuesPages.toArray(KeyValue[]::new))
      ));
    }

    // Cover Keyframe 2
    List<KeyValue> keyValuesPages = new ArrayList<>();

    addXAnimation(keyValuesPages, line5, 0);
    addCurveXAnimation(keyValuesPages, q4, 0, 0);
    addCurveXAnimation(keyValuesPages, q5, 0, 0);
    addXAnimation(keyValuesPages, line6, 0);
    addCurveXAnimation(keyValuesPages, q6, 0, 0);
    addCurveXAnimation(keyValuesPages, q7, 0, 0);

    keyframesPages.add(new Timeline(
        new KeyFrame(Duration.seconds(duration - t12), keyValuesPages.toArray(KeyValue[]::new))
    ));

    SequentialTransition seq2 =
        new SequentialTransition(keyframesPages.toArray(Timeline[]::new));
    seq2.setCycleCount(-1);
    seq2.setAutoReverse(true);
    seq2.play();

    return new Group(path1, path2);
  }

  private double getVerticalDiagonalAngle(double base, double height) {
    return Math.toDegrees(Math.atan(base/height));
  }

  private double getIntermediateTime(double finalTime, double base, double height, double startAngle, double vertAngle) {
    return finalTime *  (1 - vertAngle/startAngle);
  }

  private void addXAnimation(List<KeyValue> kvs, LineTo line, double targetX) {
    kvs.add(new KeyValue(line.xProperty(), targetX));
  }

  private void addCurveXAnimation(List<KeyValue> kvs, QuadCurveTo curve, double targetControlX, double targetX) {
    kvs.add(new KeyValue(curve.controlXProperty(), targetControlX));
    kvs.add(new KeyValue(curve.xProperty(), targetX));
  }

  /**
   * Calcola la proiezione verticale della base e dell'altezza di un rettangolo
   * di base {@code width} e altezza {@code height} ruotato secondo il parametro
   * {@code angleDeg}.
   * @param width Base del rettangolo
   * @param height Altezza del rettangolo
   * @param angleDeg Angolo di rotazione
   * @return Map dei valori risultanti, aventi come chiavi
   * {@code "projected_width"} e {@code "projected_height"}
   */
  private Map<String, Double> getVerticalProjections(double width, double height, double angleDeg) {
    double phi = Math.toRadians(angleDeg);
    double c = Math.cos(phi), s = Math.sin(phi);

    BigDecimal verticalProjWidth = BigDecimal.valueOf(Math.abs(width * s)).setScale(3, RoundingMode.HALF_EVEN);
    BigDecimal verticalProjHeight = BigDecimal.valueOf(Math.abs(height * c)).setScale(3, RoundingMode.HALF_EVEN);

    Map<String, Double> result = new HashMap<>();
    result.put("projected_width", verticalProjWidth.doubleValue());
    result.put("projected_height", verticalProjHeight.doubleValue());
    return result;
  }

  // todo removable?
  /**
   * Calcola la proiezione verticale massima della base del rettangolo
   * di base {@code width} e altezza {@code height}.
   * @param width Base del rettangolo
   * @param height Altezza del rettangolo
   * @return Valore della proiezione verticale del segmento
   */
  double getMaxBaseProjection(double width, double height) {
    double hyp = Math.hypot(width, height);
    return width * Math.sqrt(1 - Math.pow((height / hyp), 2));
  }
}
