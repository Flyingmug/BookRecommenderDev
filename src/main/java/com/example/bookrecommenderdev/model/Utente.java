package com.example.bookrecommenderdev.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Classe utilizzata per definire le caratteristiche degli utenti: nome, cognome, codFiscale, email, userId, password.
 * @author Selimi Sebian
 * @author Moscatelli Alexander*/
public class Utente implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  int id_utente;  // id nel database
  String nome;
  String cognome;
  String email;
  String codiceFiscale;
  String password;
  UUID userId;  // id dell' account


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
  public Utente(int id_utente, String nome, String cognome, String email, String codiceFiscale, String password, String userId ) {
    this.id_utente = id_utente;
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
    this.codiceFiscale = codiceFiscale;
    this.password = password;
    this.userId = UUID.fromString(userId);
  }


  /**
   * @return nome */
  public int getId_utente() { return id_utente; }
  /**
   * @return nome */
  public String getNome() { return nome; }
  /**
   * @return cognome */
  public String getCognome() { return cognome; }
  /**
   * @return email */
  public String getEmail() { return email; }
  /**
   * @return codice fiscale */
  public String getCodiceFiscale() { return codiceFiscale; }
  /**
   * @return password */
  public String getPassword() { return password; }
  /**
   * @return id utente*/
  public String getUserId() { return userId.toString(); }
}
