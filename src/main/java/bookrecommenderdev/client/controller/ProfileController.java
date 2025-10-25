package bookrecommenderdev.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ProfileController {

  @FXML
  private Button logoutButton;  // might be unnecessary, only thing needed is the onAction
  @FXML
  private VBox profilePage;


  @FXML
  protected void onLogout() {
    // delete file
//    currentUser = null;
//    navbarControls.getChildren().removeAll(librariesButton, profileButton);
//    navbarControls.getChildren().addFirst(loginButton);
//    navbarControls.getChildren().addFirst(registerButton);
//    FileManager.delete(LOCAL_CREDENTIALS);
//    onHomepage();
  }
}
