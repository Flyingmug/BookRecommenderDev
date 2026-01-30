package bookrecommenderdev.model.data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Descrittore non modificabile di una richiesta di ricerca libri.
 * <p>
 * Incapsula il tipo di ricerca e i parametri associati in modo type-safe,
 * evitando combinazioni di campi non valide.
 * <p>
 * La classe è {@link Serializable} per consentire il passaggio tramite RMI
 * o altri meccanismi di comunicazione remota.
 */
public final class SearchRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Tipologia di ricerca supportata.
   */
  public enum Tipo {
    /** Ricerca per titolo del libro */
    TITOLO,
    /** Ricerca per autore */
    AUTORE,
    /** Ricerca per autore e anno di pubblicazione */
    AUTORE_ANNO
  }

  private final Tipo tipo;
  private final String titolo;
  private final String autore;
  private final Integer anno;

  private SearchRequest(Tipo tipo, String titolo, String autore, Integer anno) {
    this.tipo = tipo;
    this.titolo = titolo;
    this.autore = autore;
    this.anno = anno;
  }

  /**
   * Crea una richiesta di ricerca per titolo.
   *
   * @param title titolo del libro
   * @return richiesta di tipo {@link Tipo#TITOLO}
   */
  public static SearchRequest perTitolo(String title) {
    return new SearchRequest(Tipo.TITOLO, title, null, null);
  }

  /**
   * Crea una richiesta di ricerca per autore.
   *
   * @param author nome dell’autore
   * @return richiesta di tipo {@link Tipo#AUTORE}
   */
  public static SearchRequest perAutore(String author) {
    return new SearchRequest(Tipo.AUTORE, null, author, null);
  }

  /**
   * Crea una richiesta di ricerca per autore e anno di pubblicazione.
   *
   * @param author nome dell’autore
   * @param year   anno di pubblicazione
   * @return richiesta di tipo {@link Tipo#AUTORE_ANNO}
   */
  public static SearchRequest perAutoreAnno(String author, int year) {
    return new SearchRequest(Tipo.AUTORE_ANNO, null, author, year);
  }

  public Tipo getTipo() { return tipo; }
  public String getTitolo() { return titolo; }
  public String getAutore() { return autore; }
  public Integer getAnno() { return anno; }

  /**
   * Verifica se la richiesta è semanticamente valida.
   * <p>
   * Regole applicate:
   * <ul>
   *   <li>{@link Tipo#TITOLO}: titolo non nullo e non blank;</li>
   *   <li>{@link Tipo#AUTORE}: autore non nullo e non blank;</li>
   *   <li>{@link Tipo#AUTORE_ANNO}: autore non nullo e non blank, anno non nullo.</li>
   * </ul>
   * <p>
   * Il metodo non normalizza né valida il contenuto (es. lunghezza, charset),
   * ma verifica solo la coerenza logica della richiesta.
   *
   * @return {@code true} se la richiesta è coerente e utilizzabile
   */
  public boolean isValid() {
    return switch (getTipo()) {
      case TITOLO -> getTitolo() != null && !getTitolo().trim().isBlank();
      case AUTORE -> getAutore() != null && !getAutore().trim().isBlank();
      case AUTORE_ANNO -> getAutore() != null && !getAutore().trim().isBlank()
          && getAnno() != null;
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SearchRequest b)) return false;
    return tipo == b.tipo
        && java.util.Objects.equals(titolo, b.titolo)
        && java.util.Objects.equals(autore, b.autore)
        && java.util.Objects.equals(anno, b.anno);
  }
}