package bookrecommenderdev.client.routing.layout;


/**
 * Enumerazione dei tipi di layout supportati dall'applicazione.
 *
 * <p>Ogni valore rappresenta una diversa struttura grafica
 * all'interno della quale le pagine possono essere montate.</p>
 *
 * <p>I layout vengono risolti tramite {@link LayoutRegistry}
 * e istanziati dal router durante la navigazione.</p>
 */
public enum LayoutType {
  DEFAULT,    // controls only
  INTEGRATED,   // searchbar in navbar
  EMPTY   // no navbar
}
