package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import com.example.mailservice.mailserver.ClientRequestHandler;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.controlsfx.control.Notifications;
public class Client {
    Socket socket = null;
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Mailbox mailbox;



    final int MAX_ATTEMPTS = 5;

    public Client(String em_addr){
        mailbox = new Mailbox(em_addr);
    }


    /**
     * Fa fino a 5 tentativi per comunicare con il server. Dopo ogni tentativo fallito
     * aspetta 1 secondo.
     * @param host l'indirizzo sul quale il server è in ascolto.
     * @param port la porta su cui il server è in ascolto.
     */
    public boolean login(String host, int port) throws IOException {
        int attempts = 0;

        boolean success = false;
        while(attempts < MAX_ATTEMPTS && !success) {
            attempts += 1;
            System.out.println();

            success = tryLoginCommunication(host, port);

            if(success) {
                System.out.println("Sono loggato come: "+this.mailbox.getEmailAddress());
                /**
                 * Thread che si mette in ascolto della ricezione di email
                 * */
                Runnable listener = () -> {
                    //Ci mettiamo in ascolto
                    listenForEmails();
                };
                Thread serverListener = new Thread(listener);
                serverListener.start();
                continue;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    // Tenta di comunicare con il server. Restituisce true se ha successo, false altrimenti
    private synchronized boolean tryLoginCommunication(String host, int port) {
        try {
            connectToServer(host, port);

            outStream.writeObject(this.mailbox.getEmailAddress());
            //outputStream.writeObject("ciao sono il client");
            outStream.flush();
            List<Email> emailsList;

            String success = (String) inStream.readObject();
            if(Objects.equals(success, "TRUE"))
            {
                //legge la sua mailbox inviata dal server
                emailsList = (List<Email>) inStream.readObject();
                for(Email em : emailsList){
                    this.mailbox.addEmail(em);
                    //this.inboxContent.add(em);
                }
                System.out.println(emailsList.toString());
            }

            else{
                return false;
            }
            return true;
        } catch (ConnectException ce) {
            // nothing to be done
            return false;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            //closeStreams();
        }
    }

    private synchronized void tryCommunicationEmail(String host, int port,Email to_send) {
        try {

            outStream.writeObject(to_send);
            outStream.flush();

            /* per sapere se l'invio è avvenuto con successo NON NECESSARIO
            String success = (String) inputStream.readObject();
            if(Objects.equals(success, "TRUE")){System.out.println("invio avvenuto con successo a: "+to_send.getRecipientsList().toString());}
            else{return false;}
            return true;*/

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //closeStreams();
        }
    }

    private void closeConnections() {
        if (socket != null) {
            try {
                inStream.close();
                outStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());

        // Dalla documentazione di ObjectOutputStream
        // callers may wish to flush the stream immediately to ensure that constructors for receiving
        // ObjectInputStreams will not block when reading the header.
        outStream.flush();

        inStream = new ObjectInputStream(socket.getInputStream());

        //System.out.println("[Client luca.dadone01@gmail.com] Connesso");
    }

    public void newEmail(String host, int port,ArrayList<String> dest,String oggetto,String contenuto){
        Email to_send= new Email(1010,-1,this.mailbox.getEmailAddress().toString(),dest,oggetto,contenuto, LocalDateTime.now());
        tryCommunicationEmail(host,port,to_send);
    }
    public void newEmail(String host, int port,int replyId, ArrayList<String> dest,String oggetto,String contenuto){
        Email to_send= new Email(1010,replyId,this.mailbox.getEmailAddress().toString(),dest,oggetto,contenuto, LocalDateTime.now());
        tryCommunicationEmail(host,port,to_send);
    }
    public void deleteEmail(String host, int port,Email e,String client_address){
        /**
         * Come gmail eliminiamo la mail in locale lato client e poi proviamo a comunicarlo al server
         * (non attendiamo risposta)
         * */
        this.mailbox.removeEmail(e);
        String delete_msg ="DELETE";
        tryCommunicationDeleteEmail(host,port,delete_msg,e);
    }

    private synchronized void tryCommunicationDeleteEmail(String host, int port,String delete_msg, Email email) {
        try {
            outStream.writeObject(delete_msg);
            outStream.flush();
            outStream.writeObject(this.mailbox.getEmailAddress());
            outStream.flush();
            outStream.writeObject(email);
            outStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //closeStreams();
        }
    }

    //------------------------------ ascolto della ricezione di email dal server
    public void listenForEmails() {
        try {
            while (true) {
                Object message = inStream.readObject();
                if (message instanceof Email received_email) {
                    /**
                     * Riceve una mail dal server
                     **/
                    System.out.println("Ho ricevuto la mail: " + received_email);
                    this.mailbox.addEmail(received_email);
                    Platform.runLater(()-> Client.showAlert(received_email,this.mailbox.getEmailAddress()));

                } else if (message instanceof String) {//HO RICEVUTO UN ERRORE SUI DESTINATARI ERRATI
                    System.out.println("ERRORE RICEVUTO::::::::: "+message.toString());
                } else {//errore
                    System.out.println(message.toString());
                }
                //outStream.flush();

            }
        } catch (IOException | ClassNotFoundException e) {
            closeStreams();
            throw new RuntimeException(e);
        } finally {
            closeStreams();
        }
    }

    private void closeStreams() {
        try {
            if (inStream != null) {
                inStream.close();
            }

            if (outStream != null) {
                outStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Funzione per verificare la correttezza sintattica di una mail
    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(regex, email);
    }

    public static void showAlert(Email e, String client){

        String imagePath = System.getProperty("user.dir") + "/src/main/java/com/example/mailservice/mailclient/assets/email.png";

        ImageView imageView = new ImageView(imagePath);
        imageView.setFitWidth(50); // Imposta la larghezza desiderata dell'immagine
        imageView.setFitHeight(50); // Imposta l'altezza desiderata dell'immagine
        String notificationText = "From: " + e.getSender() + "\n" +
                "Subject: " + ( e.getSubject().length() >= 30 ? e.getSubject().substring(0,30) : e.getSubject());

        Notifications.create()
                .title(client)
                .text(notificationText)
                .graphic(imageView)
//                .darkStyle()
                .hideAfter(Duration.INDEFINITE)
                .show();
    }

}
