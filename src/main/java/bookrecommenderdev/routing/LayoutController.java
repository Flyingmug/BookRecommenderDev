package bookrecommenderdev.routing;

import javafx.scene.Parent;

/**
 * Layout utilizzabile nella definizione di un percorso.
 */
public interface LayoutController {
  Parent getRoot();
  Parent getContent();
  void setContent(Parent page);
}
