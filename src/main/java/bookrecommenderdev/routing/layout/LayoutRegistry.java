package bookrecommenderdev.routing.layout;

import java.util.EnumMap;
import java.util.Map;

/**
 * Definisce i layout disponibili in un {@link Map}.
 */
public final class LayoutRegistry {

  private final Map<LayoutType, String> layouts = new EnumMap<>(LayoutType.class);

  public LayoutRegistry register(LayoutType type, String fxml) {
    layouts.put(type, fxml);
    return this;
  }

  public String fxml(LayoutType type) {
    String fxml = layouts.get(type);
    if (fxml == null) {
      throw new IllegalStateException("No layout registered for " + type);
    }
    return fxml;
  }
}
