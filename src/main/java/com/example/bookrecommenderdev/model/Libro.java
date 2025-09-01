package com.example.bookrecommenderdev.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Questa classe definisce le caratteristiche dei libri, composti da titolo, autore, editore, categoria,
 * e fornisce i metodi necessari per:
 * - settare alcuni parametri (setEditore / setCategorie)
 * - o reperirli (getTitolo / toShortHandFullString)
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Libro implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  int id_libro;
  int annoPubblicazione;
  String titolo;
  String autori;
  String editore;
  String categorie;

  /**
   * Costruttore Libro
   * @param autori String
   * @param titolo String
   * @param annoPubblicazione int
   */
  public Libro(int id_libro, int annoPubblicazione, String titolo, String autori, String editore, String categorie) {
    this.id_libro = id_libro;
    this.annoPubblicazione = annoPubblicazione;
    this.titolo = titolo;
    this.autori = autori;
    this.editore = editore;
    this.categorie = categorie;
  }



  public Libro(int id_libro, String titolo) {
    this.id_libro = id_libro;
    this.titolo = titolo;
  }

  /**
   * @return id libro
   */
  public long getIdLibro() { return id_libro; }
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
  public int getAnnoPubblicazione() { return annoPubblicazione; }
  /**
   * @return editore del libro
   */
  public String getEditore() { return editore; }
  /**
   * @return categorie del libro
   */
  public String getCategorie() { return categorie; }

  /**
   * Reperisce la stringa titolo+autore+annoPubblicazione
   * @return String*/
  public String toShortHandFullString() {
    return titolo + ", " + autori + ", " + annoPubblicazione;
  }

  /**
   * @param obj oggetto da confrontare
   * @return {@code true} se gli id corrispondono, {@code false} altrimenti
   */
  @Override
  public boolean equals(Object obj) {
    return obj.getClass() == Libro.class && this.id_libro == ((Libro) obj).getIdLibro();
  }
}

