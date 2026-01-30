package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Libro;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO che rappresenta un libro consigliato e il numero di utenti che lo hanno consigliato
 * (per un determinato libro base).
 *
 * @param libro           libro consigliato
 * @param countConsigliato numero di consigli ricevuti (>= 0)
 */
public record LibroConsigliato(Libro libro, int countConsigliato)
  implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
