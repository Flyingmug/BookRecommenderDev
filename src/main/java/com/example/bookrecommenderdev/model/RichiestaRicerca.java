package com.example.bookrecommenderdev.model;

/**
 * Classe rappresentante una richiesta di ricerca,
 * comprendente delle chiavi di ricerca e criterio da utilizzare.
 * @author Sebian Selimi
 * @author Alexander Moscatelli
 */
public class RichiestaRicerca {
  String titolo;
  String autore;
  int annoPubblicazione;
  CriterioRicerca criterio;

  /**
   * Costruttore della classe RichiestaRicerca.
   * Restituisce un istanza
   * @param titolo titolo
   * @param autore autore o autori
   * @param annoPubblicazione anno di pubblicazione
   * @param criterio criterio da utilizzare
   */
  public RichiestaRicerca(String titolo, String autore, int annoPubblicazione, CriterioRicerca criterio) {
    this.titolo = titolo;
    this.autore = autore;
    this.annoPubblicazione = annoPubblicazione;
    this.criterio = criterio;
  }

  /**
   * @return chiave "titolo"
   */
  public String getTitolo() {
    return titolo;
  }

  /**
   * @return chiave "autore"
   */
  public String getAutore() {
    return autore;
  }

  /**
   * @return chiave "anno di pubblicazione"
   */
  public int getAnnoPubblicazione() {
    return annoPubblicazione;
  }

  /**
   * @return criterio da utilizzare nella ricerca
   */
  public CriterioRicerca getCriterio() {
    return criterio;
  }
}
