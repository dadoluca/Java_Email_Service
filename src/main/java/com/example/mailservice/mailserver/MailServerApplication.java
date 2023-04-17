package com.example.mailservice.mailserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import com.example.mailservice.lib.MailServerModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MailServerApplication extends Application {
    Socket socket = null;
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    MailServerModel model = null;

    List<Runnable> pool_thread_server;


    /**
     * Il server si mette in ascolto su una determinata porta e serve i client.
     *
     * NB: Ogni volta che ricevo un client devo creare un thread che lo serva
     *
     * @param port la porta su cui è in ascolto il server.
     */
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
    }

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
    }

    private void serveClient(ServerSocket serverSocket) {
        try {
            System.out.println("Server in attesa di una richiesta...");

            //Email em =(Email) inStream.readObject();
            Object message;
            /*********************************** RICEVO EMAIL LOGIN *************************/
            message=inStream.readObject();
            if(message instanceof String){
                System.out.println("E UNA STRINGA");
                System.out.println(message);
                Mailbox mb_client = model.getMailbox(message.toString());
                if(mb_client == null)
                    outStream.writeObject("FALSE");
                else{
                    outStream.writeObject("TRUE");
                    outStream.writeObject(mb_client.getEmailsList());
                }
            }
            if(message instanceof Email){
                System.out.println("E UNA MAIL");
                Email to_forward= (Email) message;
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
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(MailServerApplication.class.getResource(""));
        FXMLLoader fxmlLoader = new FXMLLoader(MailServerApplication.class.getResource("email-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        pool_thread_server = new ArrayList<>();
        Runnable server = () -> {
            //Ci mettiamo in ascolto
            listen(4440);
        };
        //creiamo un thread che rimane in ascolto di richieste
        Thread listenRequests = new Thread(server);
        listenRequests.start();

        EmailController email_controller = fxmlLoader.getController();
        model= new MailServerModel();
        model.loadData();
        email_controller.initModel(model);
    }

    public static void main(String[] args) {
        launch();
    }
}