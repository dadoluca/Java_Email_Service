module prog3.project.mailclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens prog3.project.mailclient to javafx.fxml;
    exports prog3.project.mailclient;
}