package com.example.bookrecommenderdev.model;

/**
 * Classe utilizzata per definire le caratteristiche delle valutazioni,
 * in termini di stile, contenuto, gradevolezza, originalita' ed edizione.
 * Vi è la possibilità che una valutazione abbia una recensione testuale,
 * della dimensione massima di 256 caratteri
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Valutazione {
  final static int MAX_SIZE_RECENSIONE = 256;

  int id_libro;
  int id_utente;
  int stile;
  int contenuto;
  int gradevolezza;
  int originalita;
  int edizione;
  String recensione_stile;
  String recensione_contenuto;
  String recensione_gradevolezza;
  String recensione_originalita;
  String recensione_edizione;

  /**
   * Costruttore.
   * Crea una istanza vuota
   */
  public Valutazione() { }

  /**
   * Costruttore.
   * I parametri passati vengono utilizzati per creare un'istanza.
   * @param id_libro String
   * @param id_utente String
   * @param stile int punteggio
   * @param contenuto int punteggio
   * @param gradevolezza int punteggio
   * @param originalita int punteggio
   * @param edizione int punteggio
   * @param recStile String recensione testuale
   * @param recContenuto String recensione testuale
   * @param recGradevolezza String recensione testuale
   * @param recOriginalita String recensione testuale
   * @param recEdizione String recensione testuale
   */
  public Valutazione(int id_libro, int id_utente, int stile, int contenuto, int gradevolezza, int originalita,
                     int edizione, String recStile, String recContenuto, String recGradevolezza, String recOriginalita,
                     String recEdizione) {

    this.id_libro = id_libro;
    this.id_utente = id_utente;
    this.stile = stile;
    this.contenuto = contenuto;
    this.gradevolezza = gradevolezza;
    this.originalita = originalita;
    this.edizione = edizione;
    setRecensione("stile", recStile);
    setRecensione("contenuto", recContenuto);
    setRecensione("gradevolezza", recGradevolezza);
    setRecensione("originalita", recOriginalita);
    setRecensione("edizione", recEdizione);
  }

  public Valutazione(int stile, int contenuto, int gradevolezza, int originalita,
                     int edizione, String recStile, String recContenuto, String recGradevolezza, String recOriginalita,
                     String recEdizione) {

    this.stile = stile;
    this.contenuto = contenuto;
    this.gradevolezza = gradevolezza;
    this.originalita = originalita;
    this.edizione = edizione;
    setRecensione("stile", recStile);
    setRecensione("contenuto", recContenuto);
    setRecensione("gradevolezza", recGradevolezza);
    setRecensione("originalita", recOriginalita);
    setRecensione("edizione", recEdizione);
  }


  /**
   * @return id libro */
  public int getIdLibro() { return id_libro; }
  /**
   * @return id utente */
  public int getIdUtente() { return id_utente; }
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
  public String getRecensioneStile() { return recensione_stile; }
  /**
   * @return recensione testuale*/
  public String getRecensioneContenuto() { return recensione_contenuto; }
  /**
   * @return recensione testuale*/
  public String getRecensioneGradevolezzo() { return recensione_gradevolezza; }
  /**
   * @return recensione testuale*/
  public String getRecensioneOriginalita() { return recensione_originalita; }/**
   * @return recensione testuale*/
  public String getRecensioneEdizione() { return recensione_edizione; }


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
   * @param nomeCampo nome campo recensito
   * @param testo recensione
   */
  public void setRecensione(String nomeCampo, String testo) {
    if (testo != null && testo.length() > MAX_SIZE_RECENSIONE) {
      testo = testo.substring(0, MAX_SIZE_RECENSIONE);
    }

    switch (nomeCampo) {
      case "stile":
        this.recensione_stile = testo;
        break;
      case "contenuto":
        this.recensione_contenuto = testo;
        break;
      case "gradevolezza":
        this.recensione_gradevolezza = testo;
        break;
      case "originalita":
        this.recensione_originalita = testo;
        break;
      case "edizione":
        this.recensione_edizione = testo;
        break;
      default:
        break;
    }
  }
  /**
   * Metodo per settare il campo: idLibro.
   * @param idLibro int*/
  public void setIdLibro(int idLibro) { this.id_libro = idLibro; }
  /**
   * Metodo per settare il campo: idUtente.
   * @param idUtente int*/
  public void setIdUtente(int idUtente) { this.id_utente = idUtente; }

    /** aaaaaaaaa */
}
