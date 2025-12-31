package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.client.routing.Router;
import javafx.fxml.FXML;

public class HomeLinkController {


  @FXML public void onHomepage() { Router.go("/"); }


}
