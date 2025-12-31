package bookrecommenderdev.model.utils;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

import static bookrecommenderdev.Constants.*;

public class InputVerifiers {
  private static final String FISCAL_REGEX = "^[A-Z]{6}[0-9]{2}[ABCDEHLMPRST][0-9]{2}[A-Z][0-9]{3}[A-Z]";

  /**
   * Verifica la correttezza del parametro secondo il formato codice fiscale italiano
   * @param codf codice fiscale
   * @return boolean
   */
  public static boolean verCodiceFiscale(String codf) {
    return codf != null && codf.matches(FISCAL_REGEX);
  }


  /**
   * todo documentation
   * */
  public static boolean verifyName(String name) {
    return name != null && !name.isEmpty() && name.length() <= MAX_NAME_LENGTH;
  }


  /**
   * todo documentation
   * */
  public static boolean verifyPassword(String password) {
    return password != null && password.length() >= 8 && password.length() <= MAX_NAME_LENGTH;
  }

  /**
   * todo documentation
   * */
  public static boolean verifyUserId(String password) {
    return password != null && password.length() >= 8 && password.length() <= MAX_USERID_LENGTH;
  }

  /**
   * todo documentation
   * */
  public static boolean verifyEmail(String email) {
    return email != null && !email.isEmpty() && email.length() <= MAX_EMAIL_LENGTH;
  }

  /**
   * todo documentation
   * */
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
  public static void ensureLimit(TextInputControl input, int maxLength) {
    input.setTextFormatter(new TextFormatter<String>(change -> {
      String text = change.getControlNewText();

      // length limit (reject rather than truncate)
      if (text.length() > maxLength) return null;

      return change;
    }));
  }
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
  public static void restrictLooseFiscalCodeInput(TextInputControl field) {
    field.setTextFormatter(new TextFormatter<>(change -> {
      String newText = change.getControlNewText().toUpperCase(); // force uppercase
      // allow partial match so user can type gradually
      if (newText.matches("^[A-Z0-9]*$")) {
        change.setText(change.getText().toUpperCase()); // enforce uppercase while typing
        return change;
      }
      return null; // reject change
    }));
  }

  public static String notNull(String s) {
    return s == null ? "" : s.trim();
  }

  public static boolean isBlank(String s) {
    return s == null || s.trim().isBlank();
  }
}
