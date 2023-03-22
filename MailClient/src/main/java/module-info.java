module prog3.project.mailclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens prog3.project.mailclient to javafx.fxml;
    exports prog3.project.mailclient;
    exports prog3.controller;
    opens prog3.controller to javafx.fxml;
    exports prog3.model;
    opens prog3.model to javafx.fxml;
}