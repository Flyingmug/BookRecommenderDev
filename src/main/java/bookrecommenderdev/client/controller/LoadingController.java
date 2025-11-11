package bookrecommenderdev.client.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class LoadingController {


  @FXML public Group animationGroup;

  @FXML
  public void initialize() {
//    Group root = new Group();

    // === Create 4 groups, each with 2 paths ===
    for (int i = 0; i < 1; i++) {
      Group group = createPathGroup(300, 100, 5, 10, 25, 15, 90, 175);
      group.setTranslateY(150 + i * 160);

      // Animate the entire group (movement + rotation)
//      Timeline groupTimeline = new Timeline(
//          new KeyFrame(Duration.seconds(0),
//              new KeyValue(group.translateYProperty(), 150)
//          ),
//          new KeyFrame(Duration.seconds(2/* + i * 0.5*/), // stagger slightly
//              new KeyValue(group.translateYProperty(), 150 /*+ 50 * Math.sin(i * Math.PI / 2)*/)
//          )
//      );
//      groupTimeline.setCycleCount(Animation.INDEFINITE);
//      groupTimeline.setAutoReverse(true);
//      groupTimeline.play();

      animationGroup.getChildren().add(group);
    }

  }

  // === Helper: create a group with 2 related paths ===
  private Group createPathGroup(double width, double height, double extendH, double extendW, double convexR, double concaveR, double angle, double faceWidth) {

    double extW = Math.min(extendW, width);
    double convR = Math.min(convexR, width);
    double concR = Math.min(concaveR, width);
//    double tmp = convR;
//    convR = Math.min(convR, w - concR);
//    concR = Math.min(concR, w - tmp);

    LineTo line0 = new LineTo(width - convR, 0);
    QuadCurveTo qCurve0 = new QuadCurveTo(width, 0, width, height/2);
    QuadCurveTo qCurve1 = new QuadCurveTo(width, height, width - convR, height);
    LineTo line1 = new LineTo(0, height);
    LineTo line2 = new LineTo(0, height - extendH);
    LineTo line3 = new LineTo(extW, height - extendH);
    QuadCurveTo qCurve2 = new QuadCurveTo(extW + concR, height - extendH, extW + concR, height/2);
    QuadCurveTo qCurve3 = new QuadCurveTo(extW + concR, extendH, extW, extendH);
    LineTo line4 = new LineTo(0, extendH);

    Path path1 = new Path(
        new MoveTo(0, 0),
        line0,
        qCurve0,
        qCurve1,
        line1,
        line2,
        line3,
        qCurve2,
        qCurve3,
        line4,
        new ClosePath()
    );
    path1.setStrokeWidth(0);
    path1.setFill(Color.TURQUOISE);

    LineTo line5 = new LineTo(width - convR, extendH);
    QuadCurveTo qCurve4 = new QuadCurveTo(width - extendH, extendH, width - extendH, height/2);
    QuadCurveTo qCurve5 = new QuadCurveTo(width - extendH, height - extendH, width - convR, height - extendH);
    LineTo line6 = new LineTo(extW, height - extendH);
    QuadCurveTo qCurve6 = new QuadCurveTo(extW + concR, height - extendH, extW + concR, height/2);
    QuadCurveTo qCurve7 = new QuadCurveTo(extW + concR, extendH, extW, extendH);

    Path path2 = new Path(
        new MoveTo(extW, extendH),
        line5,
        qCurve4,
        qCurve5,
        line6,
        qCurve6,
        qCurve7,
        new ClosePath()
    );
    path2.setStrokeWidth(0);
    path2.setFill(Color.IVORY);


    // Path animation

    Map<String, Double> vals = getVerticalProjections(width, height, angle);
    double projHeight = vals.get("vertical_projection_width");
    double hyp = Math.hypot(width, vals.get("vertical_projection_height"));
    double maxProjection;
//    if (angle % 360 < 30) { // fixme 30 is a placeholder
//      maxProjection = getMaxVerticalProjection(width, height);
//    }

    Timeline pathTimeline1 = new Timeline(
        new KeyFrame(Duration.seconds(2),
          new KeyValue(line0.xProperty(), faceWidth)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(line3.xProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
          new KeyValue(qCurve0.controlXProperty(), faceWidth)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve0.xProperty(), faceWidth)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve1.controlXProperty(), faceWidth)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve1.xProperty(), faceWidth)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve2.controlXProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve2.xProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve3.controlXProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve3.xProperty(), 0)
        )
    );
    pathTimeline1.setCycleCount(1);
    pathTimeline1.setAutoReverse(true);
    pathTimeline1.play();

    Timeline pathTimeline2 = new Timeline(
        new KeyFrame(Duration.seconds(2),
            new KeyValue(line5.xProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(line6.xProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
          new KeyValue(qCurve4.controlXProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve4.xProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve5.controlXProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve5.xProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve6.controlXProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve6.xProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve7.controlXProperty(), 0)
        ),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(qCurve7.xProperty(), 0)
        )
    );
    pathTimeline2.setCycleCount(1);
    pathTimeline2.setAutoReverse(true);
    pathTimeline2.play();

    return new Group(path1, path2);

  }

  Map<String, Double> getVerticalProjections(double width, double height, double angleDeg) {
    double phi = Math.toRadians(angleDeg);
    double c = Math.cos(phi), s = Math.sin(phi);

    BigDecimal verticalProjWidth = BigDecimal.valueOf(Math.abs(width * s)).setScale(3, RoundingMode.HALF_EVEN);
    BigDecimal verticalProjHeight = BigDecimal.valueOf(Math.abs(height * c)).setScale(3, RoundingMode.HALF_EVEN);

    Map<String, Double> result = new HashMap<>();
    result.put("vertical_projection_width", verticalProjWidth.doubleValue());
    result.put("vertical_projection_height", verticalProjHeight.doubleValue());
    return result;
  }

  double getMaxVerticalProjection(double width, double height) {
    double hyp = Math.hypot(width, height);
    return width * Math.sqrt(1 - Math.pow((height / hyp), 2));
  }
}
