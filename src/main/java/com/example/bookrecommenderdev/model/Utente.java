package com.example.bookrecommenderdev.model;

import java.io.Serializable;

/**
 * Classe utilizzata per definire le caratteristiche degli utenti: nome, cognome, codFiscale, email, userId, password.
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Utente implements Serializable {
  String nome;
  String cognome;
  String codiceFiscale;
  String email;
  String userId;
  String password;


  /**
   * Costruttore della classe Utente.
   * I parametri passati vengono utilizzati per creare un'istanza.
   * @param nome String
   * @param cognome String
   * @param codiceFiscale String
   * @param email String
   * @param userId String
   * @param password String
   */
  public Utente(String nome, String cognome, String codiceFiscale, String email, String userId, String password) {
    this.nome = nome;
    this.cognome = cognome;
    this.codiceFiscale = codiceFiscale;
    this.email = email;
    this.userId = userId;
    this.password = password;
  }


  /**
   * @return nome */
  public String getNome() { return nome; }
  /**
   * @return cognome */
  public String getCognome() { return cognome; }
  /**
   * @return codice fiscale */
  public String getCodiceFiscale() { return codiceFiscale; }
  /**
   * @return email */
  public String getEmail() { return email; }
  /**
   * @return id utente*/
  public String getUserId() { return userId; }
  /**
   * @return password */
  public String getPassword() { return password; }

}
