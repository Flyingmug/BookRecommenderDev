package com.example.bookrecommenderdev.model;

public class ConsigliLettura {

  long idLibro;
  long idUtente;
  long idConsiglio1;
  long idConsiglio2;
  long idConsiglio3;

  public ConsigliLettura(long idLibro, long idUtente, long idConsiglio1, long idConsiglio2, long idConsiglio3) {
    this.idLibro = idLibro;
    this.idUtente = idUtente;
    this.idConsiglio1 = idConsiglio1;
    this.idConsiglio2 = idConsiglio2;
    this.idConsiglio3 = idConsiglio3;
  }

  public void setConsiglio1(long idConsiglio) {
    idConsiglio1 = idConsiglio;
  }
  public void setConsiglio2(long idConsiglio) {
    idConsiglio2 = idConsiglio;
  }
  public void setConsiglio3(long idConsiglio) {
    idConsiglio3 = idConsiglio;
  }
}
