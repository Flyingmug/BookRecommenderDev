package bookrecommenderdev.model;

/**
 * Possibili campi di valutazione di un libro
 */
public enum CampoValutazione {
  GENERALE("Generale"),
  STILE("Stile"),
  CONTENUTO("Contenuto"),
  GRADEVOLEZZA("Gradevolezza"),
  ORIGINALITA("Originalità"),
  EDIZIONE("Edizione");

  private final String label;

  CampoValutazione(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }
}
