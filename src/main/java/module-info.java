module com.example.bookrecommenderdev {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bookrecommenderdev to javafx.fxml;
    exports com.example.bookrecommenderdev;
  exports com.example.bookrecommenderdev.controller;
  opens com.example.bookrecommenderdev.controller to javafx.fxml;
}