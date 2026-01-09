package bookrecommenderdev.client.routing.layout;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry responsabile di associare valori {@link LayoutType} con la loro
 * definizione corrispondente JavaFX FXML.
 *
 * <p>I layout devono essere registrati esplicitamente durante l'inizializzazione
 * dell' applicazione prima che avvenga qualunque navigazione.</p>
 *
 *
 * <p>Note: Un elemento {@link LayoutType} può essere associato al massimo a una
 * definizione fxml.</p>
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
   * @return Riferimento all'istanza del registry
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
