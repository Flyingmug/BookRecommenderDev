module com.example.bookrecommenderdev {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bookrecommenderdev to javafx.fxml;
    exports com.example.bookrecommenderdev;
}