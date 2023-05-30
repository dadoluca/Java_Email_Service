package com.example.mailservice.mailclient;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(ClientApplication.class.getResource(""));
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        scene.setFill(Color.rgb(140,45,159));
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();


        LoginController controller =  fxmlLoader.getController();
    }

    public static void main(String[] args) {
        launch();
    }
}