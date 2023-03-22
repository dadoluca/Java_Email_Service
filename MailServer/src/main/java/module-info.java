module prog3.project.mailserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens prog3.project.mailserver to javafx.fxml;
    exports prog3.project.mailserver;
    exports prog3.project.mailserver.models;
    opens prog3.project.mailserver.models to javafx.fxml;
}