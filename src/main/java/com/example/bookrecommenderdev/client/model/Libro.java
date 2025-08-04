package com.example.bookrecommenderdev.client.model;

import java.util.UUID;

/**
 * Questa classe definisce le caratteristiche dei libri, composti da titolo, autore, editore, categoria,
 * e fornisce i metodi necessari per:
 * - settare alcuni parametri (setEditore / setCategorie)
 * - o reperirli (getTitolo / toShortHandFullString)
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Libro {

  String idLibro;
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
  public Libro(String idLibro, String titolo, String autori, int annoPubblicazione, String editore, String categorie) {
    this.idLibro = idLibro;
    this.titolo = titolo;
    this.autori = autori;
    this.annoPubblicazione = annoPubblicazione;
    this.editore = editore;
    this.categorie = categorie;
  }

  /**
   * @return id libro
   */
  public String getIdLibro() { return idLibro; }
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
   * Imposta un nuovo id al libro, eventualmente sovrascrivendo quello presente
   */
  public void setNewId() {
    UUID uuid = UUID.randomUUID();
    this.idLibro = uuid.toString();
  }

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
    return obj.getClass() == Libro.class && this.idLibro.equals(((Libro) obj).getIdLibro());
  }
}

