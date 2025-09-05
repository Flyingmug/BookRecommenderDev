package com.example.bookrecommenderdev.server.dto;

import com.example.bookrecommenderdev.model.Libro;

import java.io.Serial;
import java.io.Serializable;

public class PaginaLibro implements Serializable {
  @Serial
  private final static long serialVersionUID = 1L;
  private final Libro libro;
  private final double[] valutazioniAggregate;
//  private List<Integer> gruppoConsigli;

  /**
   * Oggetto contenente i dati completi di un libro
   * @param libro libro riferito
   * @param medie medie dei punteggi dalle valutazioni
   */
  public PaginaLibro(Libro libro, double[] medie) {
    this.libro = libro;
    this.valutazioniAggregate = medie;
//    this.gruppoConsigli = consigli;
  }

  public Libro getLibro() { return libro; }
  public double[] getValutazioniAggregate() { return valutazioniAggregate; }
//  public List<Integer> getGruppoConsigli() { return gruppoConsigli; }
}
