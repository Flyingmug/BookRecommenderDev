package bookrecommenderdev.routing.layout;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * Layout utilizzabile nella definizione di un percorso.
 */
public interface LayoutController {
  Pane getContent();
  void setContent(Parent page);
}
