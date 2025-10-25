package bookrecommenderdev.server.dto;

import bookrecommenderdev.model.ConsigliLettura;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.Valutazione;

public class LibroPaginaPersonaleDTO {
  private Libro libro;
  private Valutazione valutazione;
  private ConsigliLettura consigliLettura;

  /**
   * Oggetto contenente i dati completi di un libro
   * @param libro libro riferito
   * @param valutazione valutazione dell'utente per il libro
   * @param consigliLettura consigli di lettura dell'utente per il libro
   */
  public LibroPaginaPersonaleDTO(Libro libro, Valutazione valutazione, ConsigliLettura consigliLettura) {
    this.libro = libro;
    this.valutazione = valutazione;
    this.consigliLettura = consigliLettura;
  }

}
