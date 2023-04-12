module com.example.mailservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;

    opens com.example.mailservice to javafx.fxml;
    exports com.example.mailservice;
    exports com.example.mailservice.mailserver;
    exports com.example.mailservice.mailclient;
    opens com.example.mailservice.mailserver to javafx.fxml;
    opens com.example.mailservice.mailclient to javafx.fxml;
    opens com.example.mailservice.lib to javafx.fxml;

}