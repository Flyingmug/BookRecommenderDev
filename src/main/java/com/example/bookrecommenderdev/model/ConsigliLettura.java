package com.example.bookrecommenderdev.model;

public class ConsigliLettura {

  int id_utente;
  int id_libro;
  int idconsiglio1;
  int idconsiglio2;
  int idconsiglio3;

  public ConsigliLettura(int id_libro,int id_utente,int idconsiglio1,int idconsiglio2,int idconsiglio3) {
    this.id_libro = id_libro;
    this.id_utente = id_utente;
    this.idconsiglio1 = idconsiglio1;
    this.idconsiglio2 = idconsiglio2;
    this.idconsiglio3 = idconsiglio3;
  }

  public void setConsiglio1(int idConsiglio) {
    idconsiglio1 = idConsiglio;
  }
  public void setConsiglio2(int idConsiglio) {
    idconsiglio2 = idConsiglio;
  }
  public void setConsiglio3(int idConsiglio) {
    idconsiglio3 = idConsiglio;
  }



  /**
   * @return id utente */
  public int getIdUtente() { return id_utente; }
  /**
   * @return id libro */
  public int getIdLibro() { return id_libro; }
  /**
   * @return primo consiglio*/
  public int getIdconsiglio1() { return idconsiglio1; }
  /**
   * @return secondo consiglio*/
  public int getIdconsiglio2() { return idconsiglio2; }
  /**
   * @return terzo consiglio*/
  public int getIdconsiglio3() { return idconsiglio3; }
}
