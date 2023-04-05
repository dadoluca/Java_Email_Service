package prog3.project.mailserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import prog3.project.mailserver.models.Email;
import prog3.project.mailserver.models.MailServerModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MailServerApplication extends Application {
    Socket socket = null;
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;


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
                serveClient(serverSocket);
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

    private void serveClient(ServerSocket serverSocket) {
        try {
            System.out.println("Server in attesa di una richiesta...");
            openStreams(serverSocket);

            Email em =(Email) inStream.readObject();
            //String em = (String) inStream.readObject();
            System.out.println(em.getText());

           /* outStream.writeObject("ricevuto");
            outStream.flush();*/

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
        FXMLLoader fxmlLoader = new FXMLLoader(MailServerApplication.class.getResource("email-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        Runnable server = () -> {
            //Ci mettiamo in ascolto
            listen(4440);
        };
        //creiamo un thread che rimane in ascolto di richieste
        Thread listenRequests = new Thread(server);
        listenRequests.start();

        EmailController email_controller = fxmlLoader.getController();
        MailServerModel model= new MailServerModel();
        model.loadData();
        email_controller.initModel(model);



    }

    public static void main(String[] args) {
        launch();
    }
}