package bookrecommenderdev.utils;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

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
    return name != null && !name.isEmpty() && name.length() <= 64;
  }


  /**
   * todo documentation
   * */
  public static boolean verifyPassword(String password) {
    return password != null && password.length() >= 8 && password.length() <= 64;
  }


  /**
   * todo documentation
   * */
  public static boolean verifyEmail(String email) {
    return email != null && !email.isEmpty() && email.length() <= 255;
  }

  /**
   * todo documentation
   * */
  public static void preventMultipleSpacesAndLimit(TextInputControl textInput, int maxLength) {
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

    textInput.setTextFormatter(formatter);
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

}
