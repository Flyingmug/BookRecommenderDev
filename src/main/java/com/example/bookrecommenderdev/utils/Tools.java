package com.example.bookrecommenderdev.utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.LinkedList;
import java.util.List;

/**
 * Classe contenente metodi generici con vari campi di utilizzo
 * @author Selimi Sebian
 * @author Moscatelli Alexander
 */
public class Tools {
  private static final String INTEGER_REGEX = "^-?\\d+$"; // Match per gli interi
  private static final String FISCAL_REGEX = "^[A-Z]{6}[0-9]{2}[ABCDEHLMPRST][0-9]{2}[A-Z][0-9]{3}[A-Z]";

  /**
   * Verifica se la stringa contiene un numero intero
   * @param str stringa
   * @return boolean
   */
  public static boolean isInteger(String str) {
    return str != null && str.matches(INTEGER_REGEX);
  }

  /**
   * Verifica la correttezza del parametro secondo il formato codice fiscale italiano
   * @param codf codice fiscale
   * @return boolean
   */
  public static boolean verCodiceFiscale(String codf) {
    return codf != null && codf.matches(FISCAL_REGEX);
  }

  public static boolean verifyName(String name) {
    return name != null && !name.isEmpty() && name.length() < 64;
  }

  public static boolean verifyPassword(String password) {
    return password != null && password.length() >= 8 && password.length() < 64;
  }

  /**
   * Suddivide una stringa in blocchi da "chunkSize"
   * @param str String
   * @param chunkSize dimensione della suddivisione
   * @return List<String> lista dei blocchi suddivisi
   */
  public static List<String> stringSplitter(String str, int chunkSize) {
    List<String> chunks = new LinkedList<>();

    // Se gli input sono nulli o vuoti, ritorna una lista vuota
    if (str == null || str.isEmpty() || chunkSize <= 0) {
      return chunks;
    }

    for (int i = 0; i < str.length(); i += chunkSize) {

      int end = Math.min(str.length(), i + chunkSize);
      chunks.add(str.substring(i, end));
    }

    return chunks;
  }

  /**
   * Metodo utilizzato per ottenere un sottoinsieme di una lista di libri.
   * Viene usato nella paginazione dei risultati, per mostrare solo {@code limite} libri alla volta.
   * @param list lista di elementi
   * @param salto posizione di inizio
   * @param limite numero di elementi da selezionare
   * @return lista sezionata
   */
  public static <T> List<T> listSection(List<T> list, int salto, int limite) {
    // Verify that the input list is not null
    if (list == null) {
      throw new IllegalArgumentException("La lista in input non può essere null.");
    }

    // Verify that 'salto' (start index) is not negative
    if (salto < 0) {
      throw new IllegalArgumentException("L'indice di inizio non può essere negativo");
    }

    // Verify that 'limite' (number of elements to retrieve) is not negative
    if (limite < 0) {
      throw new IllegalArgumentException("Il limite non può essere negativo.");
    }

    return list.subList(salto, Math.min(salto + limite, list.size()));
  }

  public static void setPreventMultipleSpacesAndLimit(TextField textField, int maxLength) {
    TextFormatter<String> formatter = new TextFormatter<>(change -> {
      String newText = change.getControlNewText();

      // Step 1: Remove leading spaces
      newText = newText.replaceAll("^\\s+", "");

      // Step 2: Replace multiple spaces between words with a single space
      newText = newText.replaceAll("\\s{2,}", " ");

      // Step 3: Limit to max length
      if (newText.length() > maxLength) {
        newText = newText.substring(0, maxLength);
      }

      // If modified, replace entire text
      if (!newText.equals(change.getControlNewText())) {
        change.setText(newText);
        change.setRange(0, change.getControlText().length());
      }

      return change;
    });

    textField.setTextFormatter(formatter);
  }
}