package com.example.mailservice.mailserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MailServerApplication extends Application {
    MailServerModel model;
    List<ClientRequestHandler> pool_requestHandler_threads;


    public void listen(int port) {
        Socket socket = null;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                ClientRequestHandler requestHandler = new ClientRequestHandler(socket,model);
                requestHandler.start();
                pool_requestHandler_threads.add(requestHandler);
                System.out.println("dimensione del pool di threads a servire i client: "
                        +pool_requestHandler_threads.size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket!=null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(MailServerApplication.class.getResource(""));
        FXMLLoader fxmlLoader = new FXMLLoader(MailServerApplication.class.getResource("log_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 600);
        stage.setTitle("SERVER_LOG");
        stage.setScene(scene);
        stage.show();

        model= new MailServerModel();
        pool_requestHandler_threads = new ArrayList<>();
        /**Thread che si mette in ascolto delle richieste*/
        Runnable server = () -> {
            //Ci mettiamo in ascolto
            listen(3456);
        };
        Thread requestsListener = new Thread(server);
        requestsListener.start();

        LogController email_controller = fxmlLoader.getController();
        email_controller.initModel(model);
    }

    public static void main(String[] args) {
        launch();
    }
}