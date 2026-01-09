package bookrecommenderdev.client.routing.layout;

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

  /**
   * Determina se un determinato tipo di layout è definito.
   *
   * @param type tipo
   * @return {@code true} se almeno un layout esiste, altrimenti {@code false}
   */
  public boolean contains(LayoutType type) {
    return layouts.containsKey(type);
  }
}
