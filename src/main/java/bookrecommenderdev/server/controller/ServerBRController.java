package bookrecommenderdev.server.controller;

import bookrecommenderdev.model.utils.InputVerifiers;
import bookrecommenderdev.server.db.DatabaseConfig;
import bookrecommenderdev.server.rmi.ServerImplementation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Controller JavaFX del server.
 *
 * <p>Consente la specifica dell'indirizzamento del database per poterne usufruire dei servizi.
 */
public class ServerBRController {

  @FXML private Label statusLabel;
  @FXML private TextField hostField;
  @FXML private TextField userField;
  @FXML private TextField passwordField;

  @FXML
  public void onServerStart() {

    String host = InputVerifiers.notNull(hostField.getText());
    String user = InputVerifiers.notNull(userField.getText());
    String password = InputVerifiers.notNull(passwordField.getText());

    if (host.isEmpty() || user.isEmpty() || password.isEmpty()) {
      statusLabel.setText("Missing connection data.");
      return;
    }

    try {
      // 1. Configure database
      DatabaseConfig.setConfig(host, user, password);

      // 2. Create RMI server
      ServerImplementation server = new ServerImplementation();

      // 3. Start registry and bind
      Registry reg = LocateRegistry.createRegistry(1099);
      reg.rebind("serverBR", server);

      statusLabel.setText("Server running.");
      statusLabel.getStyleClass().add("server-status-running-label");

    } catch (Exception e) {
      statusLabel.setText("Server start failed.");
      statusLabel.getStyleClass().add("server-status-failed-label");
    }
  }
}