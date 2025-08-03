package com.example.bookrecommenderdev.model;

/**
 * Classe utilizzata per definire le caratteristiche delle valutazioni,
 * in termini di stile, contenuto, gradevolezza, originalita' ed edizione.
 * Vi è la possibilità che una valutazione abbia una recensione testuale,
 * della dimensione massima di 256 caratteri
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Valutazione {
  String idLibro;
  String idUtente;
  int stile;
  int contenuto;
  int gradevolezza;
  int originalita;
  int edizione;
  String recensione;

  /**
   * Costruttore.
   * Crea una istanza vuota
   */
  public Valutazione() { }

  /**
   * Costruttore.
   * I parametri passati vengono utilizzati per creare un'istanza.
   * @param idLibro String
   * @param idUtente String
   * @param stile int
   * @param contenuto int
   * @param gradevolezza int
   * @param originalita int
   * @param edizione int
   * @param recensione String
   */
  public Valutazione(String idLibro, String idUtente, int stile, int contenuto, int gradevolezza, int originalita, int edizione, String recensione) {
    this.idLibro = idLibro;
    this.idUtente = idUtente;
    this.stile = stile;
    this.contenuto = contenuto;
    this.gradevolezza = gradevolezza;
    this.originalita = originalita;
    this.edizione = edizione;
    setRecensione(recensione);
  }

  /**
   * @return id libro */
  public String getIdLibro() { return idLibro; }
  /**
   * @return id utente */
  public String getIdUtente() { return idUtente; }
  /**
   * @return punteggio stile */
  public int getStile() { return stile; }
  /**
   * @return punteggio contenuto */
  public int getContenuto() { return contenuto; }
  /**
   * @return punteggio gradevolezza */
  public int getGradevolezza() { return gradevolezza; }
  /**
   * @return punteggio originalita */
  public int getOriginalita() { return originalita; }
  /**
   * @return punteggio edizione */
  public int getEdizione() { return edizione; }
  /**
   * @return punteggio finale */
  public int getVotoFinale() { return (stile+contenuto+gradevolezza+originalita+edizione)/5; }
  /**
   * @return recensione testuale*/
  public String getRecensione() { return recensione; }

  /**
   * @param stile punteggio stile */
  public void setStile(int stile) { this.stile = stile; }
  /**
   * @param contenuto punteggio contenuto */
  public void setContenuto(int contenuto) { this.contenuto = contenuto; }
  /**
   * @param gradevolezza punteggio gradevolezza */
  public void setGradevolezza(int gradevolezza) { this.gradevolezza = gradevolezza; }
  /**
   * @param originalita punteggio originalita */
  public void setOriginalita(int originalita) { this.originalita = originalita; }
  /**
   * @param edizione punteggio edizione */
  public void setEdizione(int edizione) { this.edizione = edizione; }
  /**
   * Imposta la recensione e rimuove ogni carattere dal 256° in poi
   * @param recensione recensione
   */
  public void setRecensione(String recensione) {
    if (recensione.length() > 256) {
      this.recensione = recensione.substring(0, 255);
    } else {
      this.recensione = recensione;
    }
  }
  /**
   * Metodo per settare il campo: idLibro.
   * @param idLibro int*/
  public void setIdLibro(String idLibro) { this.idLibro = idLibro; }
  /**
   * Metodo per settare il campo: idUtente.
   * @param idUtente int*/
  public void setIdUtente(String idUtente) { this.idUtente = idUtente; }

}
