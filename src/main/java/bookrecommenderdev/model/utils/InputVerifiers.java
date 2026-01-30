package bookrecommenderdev.model.utils;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

import static bookrecommenderdev.Constants.*;

/**
 * Utility di validazione e vincolo input (lato client).
 * <p>
 * Contiene:
 * <ul>
 *   <li>Validator puri (ritornano boolean) per campi testuali;</li>
 *   <li>Helper per {@link TextInputControl} basati su {@link TextFormatter}, che vincolano
 *       la digitazione rifiutando modifiche non valide (non troncano il testo).</li>
 * </ul>
 * Nota: i metodi che applicano {@link TextFormatter} modificano lo stato del controllo UI.
 */
public class InputVerifiers {
  private static final String FISCAL_REGEX = "^[A-Z]{6}[0-9]{2}[ABCDEHLMPRST][0-9]{2}[A-Z][0-9]{3}[A-Z]";

  /**
   * Verifica se una stringa rispetta il formato del codice fiscale italiano.
   * <p>
   * La verifica è sintattica (pattern regex) e richiede:
   * <ul>
   *   <li>Stringa non nulla;</li>
   *   <li>Formato esatto (16 caratteri, maiuscole e cifre secondo standard).</li>
   * </ul>
   * Non esegue calcolo/validazione del carattere di controllo.
   *
   * @param codf codice fiscale da verificare
   * @return {@code true} se non nullo e conforme al pattern; {@code false} altrimenti
   */
  public static boolean verCodiceFiscale(String codf) {
    return codf != null && codf.matches(FISCAL_REGEX);
  }

  /**
   * Verifica la validità di un nome (vincoli base).
   * <p>
   * Regole applicate:
   * <ul>
   *   <li>Non nullo</li>
   *   <li>Non vuoto</li>
   *   <li>Lunghezza massima: {@link bookrecommenderdev.Constants#MAX_NAME_LENGTH}</li>
   * </ul>
   *
   * @param name stringa nome
   * @return {@code true} se valida secondo i vincoli; {@code false} altrimenti
   */
  public static boolean verifyName(String name) {
    return name != null && !name.isEmpty() && name.length() <= MAX_NAME_LENGTH;
  }

  /**
   * Verifica la validità di una password (vincoli base).
   * <p>
   * Regole applicate:
   * <ul>
   *   <li>Non nulla</li>
   *   <li>Lunghezza minima: 8</li>
   *   <li>Lunghezza massima: {@link bookrecommenderdev.Constants#MAX_NAME_LENGTH}</li>
   * </ul>
   * Nota: qui si verificano solo vincoli di lunghezza, non complessità.
   *
   * @param password password da verificare
   * @return {@code true} se valida secondo i vincoli; {@code false} altrimenti
   */
  public static boolean verifyPassword(String password) {
    return password != null && password.length() >= 8 && password.length() <= MAX_NAME_LENGTH;
  }

  /**
   * Verifica la validità di uno userId (vincoli base).
   * <p>
   * Regole applicate:
   * <ul>
   *   <li>Non nullo</li>
   *   <li>lunghezza minima: 8</li>
   *   <li>lunghezza massima: {@link bookrecommenderdev.Constants#MAX_USERID_LENGTH}</li>
   * </ul>
   * Nota: qui si verificano solo vincoli di lunghezza, non caratteri ammessi/unicità.
   *
   * @param userId userId da verificare
   * @return {@code true} se valido secondo i vincoli; {@code false} altrimenti
   */
  public static boolean verifyUserId(String userId) {
    return userId != null && userId.length() >= 8 && userId.length() <= MAX_USERID_LENGTH;
  }

  /**
   * Verifica la validità di una email (vincoli base).
   * <p>
   * Regole applicate:
   * <ul>
   *   <li>Non nulla</li>
   *   <li>Non vuota</li>
   *   <li>Lunghezza massima: {@link bookrecommenderdev.Constants#MAX_EMAIL_LENGTH}</li>
   * </ul>
   * Nota: non verifica la sintassi RFC dell’email, solo presenza e lunghezza.
   *
   * @param email email da verificare
   * @return {@code true} se valida secondo i vincoli; {@code false} altrimenti
   */
  public static boolean verifyEmail(String email) {
    return email != null && !email.isEmpty() && email.length() <= MAX_EMAIL_LENGTH;
  }

  /**
   * Applica un vincolo di input testuale:
   * <ul>
   *   <li>Lunghezza massima ({@code maxLength})</li>
   *   <li>Niente spazi iniziali</li>
   *   <li>Nessuna doppia spaziatura</li>
   * </ul>
   * La modifica viene rifiutata (ritornando {@code null} nel formatter) invece di troncare.
   *
   * @param input     controllo di input a cui applicare il vincolo
   * @param maxLength lunghezza massima consentita
   */
  public static void preventMultipleSpacesAndLimit(TextInputControl input, int maxLength) {
    input.setTextFormatter(new TextFormatter<String>(change -> {
      String text = change.getControlNewText();

      // length limit (reject rather than truncate)
      if (text.length() > maxLength) return null;

      // no leading whitespace
      if (text.matches("^\\s+.*")) return null;

      // no double spaces anywhere
      if (text.contains("  ")) return null;

      return change;
    }));
  }

  /**
   * Applica un vincolo di sola lunghezza massima ({@code maxLength}).
   * <p>
   * La modifica viene rifiutata se supera la lunghezza massima.
   *
   * @param input     controllo di input a cui applicare il vincolo
   * @param maxLength lunghezza massima consentita
   */
  public static void ensureLimit(TextInputControl input, int maxLength) {
    input.setTextFormatter(new TextFormatter<String>(change -> {
      String text = change.getControlNewText();

      // length limit (reject rather than truncate)
      if (text.length() > maxLength) return null;

      return change;
    }));
  }

  /**
   * Applica un vincolo che consente solo cifre e impone una lunghezza massima.
   * <p>
   * Qualsiasi inserimento di caratteri non numerici viene rifiutato.
   *
   * @param input     controllo di input a cui applicare il vincolo
   * @param maxLength lunghezza massima consentita
   */
  public static void numericOnlyAndLimit(TextInputControl input, int maxLength) {
    TextFormatter<String> formatter = new TextFormatter<>(change -> {
      String newText = change.getControlNewText();

      // Reject non-digits
      if (!newText.matches("\\d*")) {
        return null;
      }

      // Enforce max length
      if (newText.length() > maxLength) {
        return null;
      }

      return change;
    });

    input.setTextFormatter(formatter);
  }

  /**
   * Applica un vincolo per l’inserimento progressivo del codice fiscale:
   * <ul>
   *   <li>Forza le lettere in maiuscolo;</li>
   *   <li>Permette solo prefissi che possono ancora diventare un CF valido (validazione incrementale);</li>
   *   <li>Rifiuta immediatamente caratteri/posizioni non compatibili;</li>
   *   <li>Limite massimo 16 caratteri.</li>
   * </ul>
   * Utile per guidare l’utente durante la digitazione senza dover validare solo a fine input.
   *
   * @param field campo di input a cui applicare il vincolo
   */
  public static void restrictLooseFiscalCodeInput(TextInputControl field) {
    field.setTextFormatter(new TextFormatter<>(change -> {
      String newText = change.getControlNewText().toUpperCase();
      if (isPartialFiscalCode(newText)) {
        change.setText(change.getText().toUpperCase());
        return change;
      }
      return null;
    }));
  }

  /**
   * Verifica se una stringa è un prefisso valido (parziale) di un codice fiscale.
   * <p>
   * La funzione è usata per validazione incrementale durante la digitazione:
   * accetta stringhe di lunghezza 0..16 che rispettano i vincoli di formato
   * per la porzione già inserita.
   *
   * @param s stringa (già normalizzata in maiuscolo)
   * @return {@code true} se può essere un prefisso di un CF valido; {@code false} altrimenti
   */
  private static boolean isPartialFiscalCode(String s) {
    int len = s.length();

    if (len > 16) return false;

    return switch (len) {
      case 0 -> true;

      case 1, 2, 3, 4, 5, 6 ->
          s.matches("[A-Z]{0," + len + "}");

      case 7, 8 ->
          s.matches("[A-Z]{6}[0-9]{0," + (len - 6) + "}");

      case 9 ->
          s.matches("[A-Z]{6}[0-9]{2}[ABCDEHLMPRST]");

      case 10, 11 ->
          s.matches("[A-Z]{6}[0-9]{2}[ABCDEHLMPRST][0-9]{0," + (len - 9) + "}");

      case 12 ->
          s.matches("[A-Z]{6}[0-9]{2}[ABCDEHLMPRST][0-9]{2}[A-Z]");

      case 13, 14, 15 ->
          s.matches("[A-Z]{6}[0-9]{2}[ABCDEHLMPRST][0-9]{2}[A-Z][0-9]{0," + (len - 12) + "}");

      case 16 ->
          s.matches("[A-Z]{6}[0-9]{2}[ABCDEHLMPRST][0-9]{2}[A-Z][0-9]{3}[A-Z]");

      default -> false;
    };
  }

  /**
   * Normalizza una stringa rendendola non nulla e trimmandola.
   *
   * @param s stringa (può essere {@code null})
   * @return stringa non nulla, con {@link String#trim()} applicato; stringa vuota se {@code null}
   */
  public static String notNull(String s) {
    return s == null ? "" : s.trim();
  }

  /**
   * Verifica se una stringa è nulla o composta solo da whitespace (dopo trim).
   *
   * @param s stringa (può essere {@code null})
   * @return {@code true} se nulla o blank; {@code false} altrimenti
   */
  public static boolean isBlank(String s) {
    return s == null || s.trim().isBlank();
  }

  /**
   * Converte una stringa in intero in modo “sicuro”.
   * <p>
   * Non solleva eccezioni: in caso di {@code null} o formato non valido ritorna {@code null}.
   *
   * @param s stringa da convertire
   * @return valore intero, oppure {@code null} se la conversione fallisce
   */
  public static Integer safeParseInt(String s) {
    if (s == null) return null;
    try { return Integer.parseInt(s); }
    catch (NumberFormatException e) { return null; }
  }
}
