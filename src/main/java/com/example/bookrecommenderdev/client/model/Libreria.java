package com.example.bookrecommenderdev.client.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Rappresenta una libreria con i rispettivi metodi di aggiunta
 * @author Selimi Sebian
 * @author Moscatelli Alexander
 */
public class Libreria {

  List<Libro> elenco;
  String idLibreria;
  String nome;

  /**
   * Restituisce una istanza della classe Libreria, con elenco di libri vuoto.
   * @param idLibreria id libreria
   * @param nome nome libreria
   */
  public Libreria(String idLibreria, String nome) {
    elenco = new LinkedList<>();
    this.idLibreria = idLibreria;
    this.nome = nome;
  }

  /**
   * @return id libreria
   */
  public String getIdLibreria() { return idLibreria; }

  /**
   * @return nome libreria
   */
  public String getNomeLibreria() { return nome; }

  /**
   * @return elenco dei libri contenuti
   */
  public List<Libro> getElencoLibri() { return elenco; }

  /**
   * @return conteggio dei libri presenti
   */
  public int getConteggio() { return elenco.size(); }

  /**
   * Aggiunge un libro alla libreria
   * @param libro libro
   */
  public void aggiungiLibro(Libro libro) {
    if (!elenco.contains(libro)) {
      elenco.add(libro);
    }
  }

  /**
   * Aggiunge una lista di libri alla libreria (concatenazione)
   * @param lista lista di libri
   */
  public void aggiungiListaLibri(List<Libro> lista) {
    if (!lista.isEmpty()) {
      elenco.addAll(lista);
    }
  }

  /**
   * @param obj oggetto da confrontare
   * @return {@code true} se il nome corrisponde, {@code false} altrimenti
   */
  @Override
  public boolean equals(Object obj) {
    return obj.getClass() == Libreria.class && this.nome.equals(((Libreria) obj).getNomeLibreria());
  }
}
