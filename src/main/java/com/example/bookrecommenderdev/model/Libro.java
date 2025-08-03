package com.example.bookrecommenderdev.model;

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

}
