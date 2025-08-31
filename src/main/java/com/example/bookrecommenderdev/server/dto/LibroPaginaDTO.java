package com.example.bookrecommenderdev.server.dto;

import com.example.bookrecommenderdev.model.Libro;

public class LibroPaginaDTO {
  private Libro libro;
  private float[] valutazioniAggregate;
  private Libro[] gruppoConsigli;

  /**
   * Oggetto contenente i dati completi di un libro
   * @param libro libro riferito
   * @param medie medie dei punteggi dalle valutazioni
   * @param consigli consigli di lettura
   */
  public LibroPaginaDTO(Libro libro, float[] medie, Libro[] consigli) {
    this.libro = libro;
    this.valutazioniAggregate = medie;
    this.gruppoConsigli = consigli;
  }

}
