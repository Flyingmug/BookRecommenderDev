package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Libro;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO che rappresenta la pagina di dettaglio di un libro.
 * <p>
 * Combina le informazioni del {@link Libro} con le medie aggregate
 * delle valutazioni ricevute.
 */
public record PaginaLibro(Libro libro, double[] valutazioniAggregate) implements Serializable {
  @Serial
  private final static long serialVersionUID = 1L;

  /**
   * Crea una pagina di dettaglio per un libro.
   *
   * @param libro                libro di riferimento
   * @param valutazioniAggregate array delle medie dei punteggi di valutazione.
   * L’array contiene 5 valori, in quest’ordine:
   * <ol>
   *   <li>stile</li>
   *   <li>contenuto</li>
   *   <li>gradevolezza</li>
   *   <li>originalità</li>
   *   <li>edizione</li>
   * </ol>
   * Può essere {@code null} se il libro non ha valutazioni.
   */
  public PaginaLibro {
  }

  /**
   * @return libro associato alla pagina
   */
  @Override
  public Libro libro() {
    return libro;
  }

  /**
   * @return array delle medie aggregate dei punteggi, oppure {@code null}
   * se non sono presenti valutazioni
   */
  @Override
  public double[] valutazioniAggregate() {
    return valutazioniAggregate;
  }
}
