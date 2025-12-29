package bookrecommenderdev.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Rappresenta una libreria con i rispettivi metodi di aggiunta
 * @author Selimi Sebian
 * @author Moscatelli Alexander
 */
public class Libreria implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  int id_libreria;
  int id_utente;
  String nome;

  /**
   * Restituisce una istanza della classe Libreria, con elenco di libri vuoto.
   * @param id_libreria id libreria
   * @param nome nome libreria
   */
  public Libreria(int id_libreria, int id_utente, String nome) {
    this.id_libreria = id_libreria;
    this.id_utente = id_utente;
    this.nome = nome;
  }

  public Libreria(int id_libreria, String nome) {
    this.id_libreria = id_libreria;
    this.nome = nome;
  }

  /**
   * @return id libreria
   */
  public int getIdLibreria() { return id_libreria; }

  /**
   * @return nome libreria
   */
  public String getNome() { return nome; }


  /**
   * @param obj oggetto da confrontare
   * @return {@code true} se il nome corrisponde, {@code false} altrimenti
   */
  @Override
  public boolean equals(Object obj) {
    return obj.getClass() == Libreria.class && this.nome.equals(((Libreria) obj).getNome());
  }
}
