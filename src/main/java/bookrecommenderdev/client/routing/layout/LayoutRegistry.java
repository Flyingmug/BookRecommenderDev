package bookrecommenderdev.client.routing.layout;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry responsible for associating {@link LayoutType} values with their
 * corresponding JavaFX FXML layout definitions.
 *
 * <p>The registry acts as a central configuration component used by the routing
 * system to resolve which layout should be instantiated when navigating between
 * pages.</p>
 *
 * <p>Layouts must be explicitly registered during application startup before
 * any navigation occurs.</p>
 *
 * <h2>Design guarantees</h2>
 * <ul>
 *   <li>Each {@link LayoutType} can be associated with at most one FXML file</li>
 *   <li>Layout resolution fails fast if a layout is missing</li>
 *   <li>A DEFAULT layout can be required and validated at startup</li>
 * </ul>
 */
public final class LayoutRegistry {

  private final Map<LayoutType, String> layouts = new EnumMap<>(LayoutType.class);

  /**
   * Registra un layout FXML per il tipo di layout specificato.
   *
   * <p>Una ridefinizione di un layout presente verrà ignorata.</p>
   *
   * @param type Tipo di layout
   * @param fxml path FXML relativo al classpath
   * @return Un riferimento all'istanza del registry
   */
  public LayoutRegistry register(LayoutType type, String fxml) {
    if (!layouts.containsKey(type)) {
      layouts.put(type, fxml);
    }
    return this;
  }

  /**
   * Ritorna il filepath FXML relativo associato al tipo di layout richiesto
   *
   * @param type tipo di layout
   * @return path FXML relativo al classpath
   * @throws IllegalStateException Se non è registrato alcun layout del tipo specificato
   */
  public String fxml(LayoutType type) {
    String fxml = layouts.get(type);
    if (fxml == null) {
      throw new IllegalStateException("Nessun percorso registrato per " + type);
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
