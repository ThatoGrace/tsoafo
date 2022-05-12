module com.example.grace {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.grace to javafx.fxml;
    exports com.example.grace;
}