package bookrecommenderdev.model.data;

import java.io.Serial;
import java.io.Serializable;

public final class SearchRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public enum Tipo {
    TITOLO,
    AUTORE,
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

  public static SearchRequest perTitolo(String title) {
    return new SearchRequest(Tipo.TITOLO, title, null, null);
  }

  public static SearchRequest perAutore(String author) {
    return new SearchRequest(Tipo.AUTORE, null, author, null);
  }

  public static SearchRequest perAutoreAnno(String author, int year) {
    return new SearchRequest(Tipo.AUTORE_ANNO, null, author, year);
  }

  public Tipo getTipo() { return tipo; }
  public String getTitolo() { return titolo; }
  public String getAutore() { return autore; }
  public Integer getAnno() { return anno; }
}