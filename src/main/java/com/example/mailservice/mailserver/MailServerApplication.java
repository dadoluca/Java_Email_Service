package com.example.mailservice.mailserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MailServerApplication extends Application {
   /* Socket socket = null;
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    MailServerModel model = null;

    List<Runnable> pool_thread_server;
    EmailController email_controller;*/

    MailServerModel model;
    List<ClientRequestHandler> pool_requestHandler_threads;

    /**
     * Il server si mette in ascolto su una determinata porta e serve i client.
     *
     * NB: Ogni volta che ricevo un client devo creare un thread che lo serva
     *
     * @param port la porta su cui è in ascolto il server.

    public void listen(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                ///devo creare un thread che lo serva
                getRequest(serverSocket);
                //serveClient(serverSocket);
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
    } */
/**
 * Genera un thread che serve la richiesta ricevuta
    private void getRequest(ServerSocket serverSocket) throws IOException {
        openStreams(serverSocket);
        Runnable clientHandler = () -> {
            serveClient(serverSocket);
        };
        //creiamo un thread che rimane in ascolto di richieste
        Thread thread = new Thread(clientHandler);
        thread.start();
        pool_thread_server.add(thread);
        System.out.println("dimensione lista thread a servire client: "+pool_thread_server.size());
    }*/
    /**
     * usa java reflection per identificare tipo di messaggio ricevuto
    private void serveClient(ServerSocket serverSocket) {
        try {
            Object message=inStream.readObject();
            if(message instanceof String){
                /**
                 * Riceve un email address da un client che vuole loggarsi,
                 * verifica che sia loggato, e restituisce "TRUE" se si è loggati, "FALSE" altrimenti
                 *
                System.out.println(message + " vuole loggarsi");
                String email_addr = message.toString();
                Mailbox mb_client = model.getMailbox(email_addr);
                if(mb_client == null)
                    outStream.writeObject("FALSE");
                else{
                    outStream.writeObject("TRUE");
                    outStream.writeObject(mb_client.getEmailList());
                }
            }
            if(message instanceof Email){
                /**
                 * Riceve una mail da un client
                 *
                Email to_forward= (Email) message;
                System.out.println("Ho ricevuto la mail: "+to_forward);
                // aggiorno il model
                model.receiveEmail(to_forward);
                /**
                 * Scriviamo nel log
                 * preleva i destinatari e inoltra la mail
                 *
                //TODO scriverla nel file
            }
            outStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeStreams();
        }
    }

    // apre gli stream necessari alla connessione corrente
    private void openStreams(ServerSocket serverSocket) throws IOException {
        socket = serverSocket.accept();

        inStream = new ObjectInputStream(socket.getInputStream());
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.flush();
    }

    // Chiude gli stream utilizzati durante l'ultima connessione
    private void closeStreams() {
        try {
            if(inStream != null) {
                inStream.close();
            }

            if(outStream != null) {
                outStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Il server si mette in ascolto su una determinata porta e serve i client.
     * <p>
     * NB: Ogni volta che ricevo un client devo creare un thread che lo serva
     *
     * @param port la porta su cui è in ascolto il server.
     */

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
        FXMLLoader fxmlLoader = new FXMLLoader(MailServerApplication.class.getResource("email-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        model= new MailServerModel();
        pool_requestHandler_threads = new ArrayList<>();
        /**Thread che si mette in ascolto delle richieste*/
        Runnable server = () -> {
            //Ci mettiamo in ascolto
            listen(4440);
        };
        Thread requestsListener = new Thread(server);
        requestsListener.start();

        EmailController email_controller = fxmlLoader.getController();
        email_controller.initModel(model);
    }

    public static void main(String[] args) {
        launch();
    }
}