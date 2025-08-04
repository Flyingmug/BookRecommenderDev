module com.example.bookrecommenderdev {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bookrecommenderdev.client to javafx.fxml;
    exports com.example.bookrecommenderdev.client;
  exports com.example.bookrecommenderdev.client.controller;
  opens com.example.bookrecommenderdev.client.controller to javafx.fxml;
}