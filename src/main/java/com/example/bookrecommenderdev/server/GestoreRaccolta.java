package com.example.bookrecommenderdev.server;

import com.example.bookrecommenderdev.model.CriterioRicerca;
import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.model.RichiestaRicerca;

import java.util.LinkedList;
import java.util.List;

/**
 * Classe che gestisce una raccolta di libri
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class GestoreRaccolta {

  List<Libro> elenco;

  /**
   * Costruttore RaccoltaLibri.
   * Istanzia una lista di libri vuota,*/
  public GestoreRaccolta() {
    elenco = new LinkedList<>();
  }

//  /**
//   * Costruttore RaccoltaLibri.
//   * Istanzia e riempe una lista di libri tramite il path {@code pathLibri},*/
//  public GestoreRaccolta(String pathLibri) {
//    caricaDati(pathLibri);
//  }

  /**
   * Crea un'istanza e setta il valore in input come elenco di libri.
   * @param elenco Lista di libri */
  public GestoreRaccolta(List<Libro> elenco) { this.elenco = elenco; }

  /**
   * @return lista di libri gestita*/
  public List<Libro> getElenco() { return elenco; }

//
//  /**
//   * Preleva i dati dei libri tramite la classe {@link CSVFileManager}
//   */
//  public void caricaDati(String pathLibri) {
//    elenco = CSVFileManager.leggiDatiCsv(pathLibri, Libro.class);
//  }

  /**
   * Dopo aver verificato che l'utente abbia scelto un criterio adeguato, viene effettuata la ricerca dei libri risultanti.
   * @param richiesta richiesta di ricerca
   * @return lista di libri risultante
   */
  public List<Libro> cercaLibro(RichiestaRicerca richiesta) {

    List<Libro> risultato = new LinkedList<>();

    if (richiesta.getCriterio() == null) {
      return risultato;
    }
    switch (richiesta.getCriterio()) {
      case CriterioRicerca.TITOLO:
        risultato = elenco.stream()
            .filter(x -> x.getTitolo().toLowerCase().contains(richiesta.getTitolo().toLowerCase()))
            .toList();
        break;
      case CriterioRicerca.AUTORE:
        System.out.println("\nRicerca per autore");
        risultato = elenco.stream()
            .filter(x -> x.getAutori().toLowerCase().contains(richiesta.getAutore().toLowerCase()))
            .toList();
        break;
      case CriterioRicerca.AUTORE_ANNO:
        risultato =  elenco.stream()
            .filter(x -> x.getAutori().toLowerCase().contains(richiesta.getAutore().toLowerCase())
                && x.getAnnoPubblicazione() == richiesta.getAnnoPubblicazione())
            .toList();
        break;
      default:
        break;
    }
    return risultato;
  }

  public List<Libro> cercaLibro(String titolo) {
    return null;
  }
  public List<Libro> cercaLibro(List<String> autori) {
    return null;
  }
  public List<Libro> cercaLibro(String nomeAutore, String cognomeAutore, int annoPubblicazione) {
    return null;
  }

}
