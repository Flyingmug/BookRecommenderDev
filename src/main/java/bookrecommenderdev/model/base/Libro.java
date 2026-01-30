package bookrecommenderdev.model.base;

import java.io.Serial;
import java.io.Serializable;

/**
 * Questa classe definisce le caratteristiche dei libri, composti da titolo, autore, editore, categoria,
 * e fornisce i metodi necessari per:
 * - settare alcuni parametri (setEditore / setCategorie)
 * - o reperirli (getTitolo)
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Libro implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  int id_libro;
  int anno_pubblicazione;
  String titolo;
  String autori;
  String editore;
  String categorie;

  /**
   * Costruttore Libro
   * @param autori autori del libro
   * @param titolo titolo del libro
   * @param anno_pubblicazione anno di pubblicazione
   */
  public Libro(int id_libro, int anno_pubblicazione, String titolo, String autori, String editore, String categorie) {
    this.id_libro = id_libro;
    this.anno_pubblicazione = anno_pubblicazione;
    this.titolo = titolo;
    this.autori = autori;
    this.editore = editore;
    this.categorie = categorie;
  }

  /**
   * Costruttore di un'istanza di un libro.
   * @param id_libro id del libro
   * @param titolo titolo del libro
   * @param autori autori del libro
   * @param anno_pubblicazione anno di pubblicazione
   */
  public Libro(int id_libro, String titolo, String autori, int anno_pubblicazione) {
    this.id_libro = id_libro;
    this.titolo = titolo;
    this.autori = autori;
    this.anno_pubblicazione = anno_pubblicazione;
  }

  /**
   * Costruttore di un'istanza di un libro minimale, contenente solo id e titolo.
   * @param id_libro id del libro
   * @param titolo titolo del libro
   */
  public Libro(int id_libro, String titolo) {
    this.id_libro = id_libro;
    this.titolo = titolo;
  }

  /**
   * Costruttore di un'istanza di un libro minimale, contenente id, titolo e anno di pubblicazione.
   * @param id_libro id del libro
   * @param titolo titolo del libro
   * @param annoPubblicazione anno di pubblicazione
   */
  public Libro(int id_libro, int annoPubblicazione, String titolo) {
    this.id_libro = id_libro;
    this.anno_pubblicazione = annoPubblicazione;
    this.titolo = titolo;
  }

  /**
   * @return id libro
   */
  public int getIdLibro() { return id_libro; }
  /**
   * @return titolo libro
   */
  public String getTitolo() { return titolo; }
  /**
   * @return autori
   */
  public String getAutori() { return autori; }
  /**
   * @return anno di pubblicazione
   */
  public int getAnnoPubblicazione() { return anno_pubblicazione; }
  /**
   * @return editore del libro
   */
  public String getEditore() { return editore; }
  /**
   * @return categorie del libro
   */
  public String getCategorie() { return categorie; }

  /**
   * @param obj oggetto da confrontare
   * @return {@code true} se gli id corrispondono, {@code false} altrimenti
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Libro other)) return false;
    return this.id_libro == other.id_libro;
  }
}

