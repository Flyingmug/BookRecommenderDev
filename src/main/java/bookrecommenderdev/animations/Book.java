package bookrecommenderdev.animations;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Book {
  BookRectangle cover;
  BookRectangle pages;

  public Book(BookRectangle cover, double pagesW, double pagesConvexR, double baseRotation) {
    this.cover = cover;


    //
    // requires code fixing because of new approach
    //

    double ceilConvexR = Math.min(pagesConvexR, cover.baseConvexR());
    double ceilWidth = Math.min(pagesW, cover.baseWidth() - cover.baseExtendW() - cover.baseConvexR() + pagesConvexR);
    this.pages = new BookRectangle(
        cover.baseX() - cover.baseWidth()/2 + cover.baseExtendW() + ceilWidth/2,
        cover.baseY(),
        ceilWidth,
        cover.baseHeight() - 2 * cover.baseExtendH(),
        ceilConvexR,
        cover.baseConcaveR(),
        0,
        0,
        Color.MINTCREAM);

  }

  public void addRotationKeyframe(double time, double r) {

    // evaluate values
    // TODO: required BOOK HEIGHT PROPERTY
    Map<String, Double> vals = getVerticalProjections(cover.baseWidth(), cover.baseHeight(), r);

    // insert into cover properties animations
    // width, convexR, concaveR, extW
    // TODO: Verify correctness and other approaches

    double width = vals.get("vertical_projection_width");
    double hyp = Math.hypot(width, vals.get("vertical_projection_height"));
    cover.getWidth().addKeyframe(time, hyp);
    pages.getWidth().addKeyframe(time, width);

    // Binding rappresentativo della posizione dei due oggetti componenti



    // TODO: ConvexR and ConcaveR

    // insert into pages properties animations
    // next day note: automatic rebuilding every time book updates?
  }

  public static Map<String, Double> getVerticalProjections(double width, double height, double angleDeg) {
    double phi = Math.toRadians(angleDeg);
    double c = Math.cos(phi), s = Math.sin(phi);

    BigDecimal verticalProjWidth = BigDecimal.valueOf(Math.abs(width * s)).setScale(3, RoundingMode.HALF_EVEN);
    BigDecimal verticalProjHeight = BigDecimal.valueOf(Math.abs(height * c)).setScale(3, RoundingMode.HALF_EVEN);

    Map<String, Double> result = new HashMap<>();
    result.put("vertical_projection_width", verticalProjWidth.doubleValue());
    result.put("vertical_projection_height", verticalProjHeight.doubleValue());
    return result;
  }

  /*
    double phi = Math.toRadians(angleDeg);

    // The long side’s vertical projection (book block rising)
    double verticalBlock = Math.abs(width * Math.sin(phi));

    // The short side’s vertical projection (cover thickness reducing)
    double verticalCover = Math.abs(height * Math.cos(phi));

    // Use BigDecimal for rounding consistency
    BigDecimal blockProj = BigDecimal.valueOf(verticalBlock).setScale(3, RoundingMode.HALF_EVEN);
    BigDecimal coverProj = BigDecimal.valueOf(verticalCover).setScale(3, RoundingMode.HALF_EVEN);

    Map<String, Double> result = new HashMap<>();
    result.put("vertical_projection_block", blockProj.doubleValue());
    result.put("vertical_projection_cover", coverProj.doubleValue());

    return result;
   */

  /**
   * todo
   * @param gc
   */
  public void draw(GraphicsContext gc) {
    cover.draw(gc, 0);
    pages.draw(gc, 0);
  }

  /**
   * Rappresenta i componenti nel contesto grafico dato.
   * Vengono utilizzati i keyframe eventualmente presenti nella computazione dei valori da utilizzare.
   * @param gc contesto grafico
   * @param currentTime istante di tempo
   */
  public void draw(GraphicsContext gc, double currentTime) {
    cover.draw(gc, currentTime);
    pages.draw(gc, currentTime);
  }
}
