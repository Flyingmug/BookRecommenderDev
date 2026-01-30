package bookrecommenderdev.model.base;

/**
 * Possibili valutazioni per un libro.
 */
public enum CampoValutazione {
  GENERALE("Generale"),
  STILE("Stile"),
  CONTENUTO("Contenuto"),
  GRADEVOLEZZA("Gradevolezza"),
  ORIGINALITA("Originalità"),
  EDIZIONE("Edizione");

  private final String label;

  /**
   * Costruttore di un'istanza rappresentante il nome dl campo di una valutazione.
   */
  CampoValutazione(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }
}
