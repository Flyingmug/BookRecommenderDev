package bookrecommenderdev.model.base;

import java.io.Serial;
import java.io.Serializable;

import static bookrecommenderdev.model.base.CampoValutazione.*;

/**
 * Classe utilizzata per definire le caratteristiche delle valutazioni,
 * in termini di stile, contenuto, gradevolezza, originalita' ed edizione.
 * Vi è la possibilità che una valutazione abbia una recensione testuale,
 * della dimensione massima di 256 caratteri
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Valutazione implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
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
  String recensione_generale;

  /**
   * Costruttore.
   * Crea una istanza vuota
   */
  public Valutazione() { }

  /**
   * Costruttore.
   * I parametri passati vengono utilizzati per creare un'istanza.
   * @param id_libro id libro riferito
   * @param id_utente id utente riferito
   * @param stile punteggio
   * @param contenuto punteggio
   * @param gradevolezza punteggio
   * @param originalita punteggio
   * @param edizione punteggio
   * @param recStile recensione testuale
   * @param recContenuto recensione testuale
   * @param recGradevolezza recensione testuale
   * @param recOriginalita recensione testuale
   * @param recEdizione recensione testuale
   */
  public Valutazione(int id_libro, int id_utente, int stile, int contenuto, int gradevolezza, int originalita,
                     int edizione, String recStile, String recContenuto, String recGradevolezza, String recOriginalita,
                     String recEdizione, String recGenerale) {

    this.id_libro = id_libro;
    this.id_utente = id_utente;
    this.stile = stile;
    this.contenuto = contenuto;
    this.gradevolezza = gradevolezza;
    this.originalita = originalita;
    this.edizione = edizione;
    setRecensione(STILE, recStile);
    setRecensione(CONTENUTO, recContenuto);
    setRecensione(GRADEVOLEZZA, recGradevolezza);
    setRecensione(ORIGINALITA, recOriginalita);
    setRecensione(EDIZIONE, recEdizione);
    setRecensione(GENERALE, recGenerale);
  }

  public Valutazione(int stile, int contenuto, int gradevolezza, int originalita,
                     int edizione, String recStile, String recContenuto, String recGradevolezza, String recOriginalita,
                     String recEdizione, String recGenerale) {

    this.stile = stile;
    this.contenuto = contenuto;
    this.gradevolezza = gradevolezza;
    this.originalita = originalita;
    this.edizione = edizione;
    setRecensione(STILE, recStile);
    setRecensione(CONTENUTO, recContenuto);
    setRecensione(GRADEVOLEZZA, recGradevolezza);
    setRecensione(ORIGINALITA, recOriginalita);
    setRecensione(EDIZIONE, recEdizione);
    setRecensione(GENERALE, recGenerale);
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
  public double getVotoFinale() { return (stile+contenuto+gradevolezza+originalita+edizione)/5.0; }
  /**
   * @return recensione testuale*/
  public String getRecensioneStile() { return recensione_stile; }
  /**
   * @return recensione testuale*/
  public String getRecensioneContenuto() { return recensione_contenuto; }
  /**
   * @return recensione testuale*/
  public String getRecensioneGradevolezza() { return recensione_gradevolezza; }
  /**
   * @return recensione testuale*/
  public String getRecensioneOriginalita() { return recensione_originalita; }
  /** @return recensione testuale*/
  public String getRecensioneEdizione() { return recensione_edizione; }
  /** @return recensione restuale */
  public String getRecensioneGenerale() { return recensione_generale; }

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
  public void setRecensione(CampoValutazione nomeCampo, String testo) {
    if (testo != null && testo.length() > MAX_SIZE_RECENSIONE) {
      testo = testo.substring(0, MAX_SIZE_RECENSIONE);
    }

    switch (nomeCampo) {
      case STILE -> recensione_stile = testo;
      case CONTENUTO ->  recensione_contenuto = testo;
      case GRADEVOLEZZA -> recensione_gradevolezza = testo;
      case ORIGINALITA -> recensione_originalita = testo;
      case EDIZIONE -> recensione_edizione = testo;
      case GENERALE -> recensione_generale = testo;
      default -> throw new IllegalStateException("Unexpected value: " + nomeCampo); // todo handle
    }
  }
  public void setPunteggio(CampoValutazione nomeCampo, int punteggio) {
    if (punteggio < 0) { punteggio = 0; }
    else if (punteggio > 5) { punteggio = 5; }

    switch (nomeCampo) {
      case STILE -> stile = punteggio;
      case CONTENUTO ->  contenuto = punteggio;
      case GRADEVOLEZZA -> gradevolezza = punteggio;
      case ORIGINALITA -> originalita = punteggio;
      case EDIZIONE -> edizione = punteggio;
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
}
