package com.example.bookrecommenderdev.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Rappresenta una libreria con i rispettivi metodi di aggiunta
 * @author Selimi Sebian
 * @author Moscatelli Alexander
 */
public class Libreria {

  int id_libreria;
  int id_utente;
  String nome;

  /**
   * Restituisce una istanza della classe Libreria, con elenco di libri vuoto.
   * @param id_libreria id libreria
   * @param nome nome libreria
   */
  public Libreria(int id_libreria,int id_utente, String nome) {
    this.id_libreria = id_libreria;
    this.id_utente = id_utente;
    this.nome = nome;
  }

  public Libreria() {

  }

  /**
   * @return id libreria
   */
  public int getIdLibreria() { return id_libreria; }

  /**
   * @return nome libreria
   */
  public String getNomeLibreria() { return nome; }


  /**
   * @param obj oggetto da confrontare
   * @return {@code true} se il nome corrisponde, {@code false} altrimenti
   */
  @Override
  public boolean equals(Object obj) {
    return obj.getClass() == Libreria.class && this.nome.equals(((Libreria) obj).getNomeLibreria());
  }
}
