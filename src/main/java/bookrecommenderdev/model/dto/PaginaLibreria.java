package bookrecommenderdev.model.dto;

import bookrecommenderdev.model.base.Libreria;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO che rappresenta una libreria dell’utente con il conteggio dei libri contenuti.
 *
 * @param library   libreria
 * @param bookCount numero di libri presenti nella libreria (>= 0)
 */
public record PaginaLibreria(Libreria library, int bookCount) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
}
