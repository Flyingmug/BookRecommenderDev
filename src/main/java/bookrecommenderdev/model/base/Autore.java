package bookrecommenderdev.model.base;

/**
 * Autore di un libro
 */
public class Autore {
  int id_autore;
  String nome_autore;

  /** Costruttore di un'istanza di un'autore. */
  public Autore(int id_autore, String  nome_autore){
    this.id_autore=id_autore;
    this.nome_autore=nome_autore;
  }
}
