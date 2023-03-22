module prog3.project.mailserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens prog3.project.mailserver to javafx.fxml;
    exports prog3.project.mailserver;
}