package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import com.example.mailservice.mailserver.ClientRequestHandler;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import org.controlsfx.control.Notifications;

public class Client {
    Socket socket = null;
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Mailbox mailbox;
    private boolean is_logged_out = false;
    private boolean is_logged = false;
    String host;
    int port;
    private String resultOfSendEmail = "";
    boolean available_resultOfSendEmail = false;

    final int MAX_ATTEMPTS = 5;

    private final Object lock = new Object(); // Create an object for synchronization


    public Client(String em_addr, String host, int port) {
        mailbox = new Mailbox(em_addr);
        this.host = host;
        this.port = port;
    }


    /**
     * Fa fino a 5 tentativi per comunicare con il server. Dopo ogni tentativo fallito
     * aspetta 1/10 secondo.
     */
    public String login() throws IOException, InterruptedException {
        int attempts = 0;

        String result = "";
        while (attempts < MAX_ATTEMPTS && !is_logged) {
            attempts += 1;
            System.out.println();

            result = tryLoginCommunication();

            if (result.equals("LOGGED")) {
                System.out.println("Sono loggato come: " + this.mailbox.getEmailAddress());
                /**
                 * Thread che si mette in ascolto della ricezione di email
                 * */
                Runnable listener = () -> {
                    //Ci mettiamo in ascolto
                    try {
                        listenForEmails();
                    } catch (InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }

                };
                Thread serverListener = new Thread(listener);
                is_logged = true;
                serverListener.start();
            } else try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // Tenta di comunicare con il server. Restituisce true se ha successo, false altrimenti
    private String tryLoginCommunication() throws InterruptedException {
        try {
            connectToServer();

            outStream.writeObject(this.mailbox.getEmailAddress());
            //outputStream.writeObject("ciao sono il client");
            outStream.flush();
            List<Email> emailsList;

            String success = (String) inStream.readObject();
            if (Objects.equals(success, "TRUE")) {
                if(this.mailbox!=null){
                    this.mailbox.removeAll();
                    Thread.sleep(200);
                }
                //legge la sua mailbox inviata dal server
                emailsList = (List<Email>) inStream.readObject();
                for (Email em : emailsList) {
                    this.mailbox.addEmail(em);
                    //this.inboxContent.add(em);
                }
                //System.out.println(emailsList);
                return "LOGGED";
            } else if (Objects.equals(success, "FALSE")) {
                return "NOT_FOUND";
            }

        } catch (ConnectException ce) {
            // non riusciamo a connetterci a al server in quanto offline
            Thread.sleep(200);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "SERVER_OFFLINE";

    }

    private String tryCommunicationEmail(Email to_send) {
        if (!is_logged) {
            showAlert("Server offline.. ");
            return "";
        }
        try {
            outStream.writeObject(to_send);
            outStream.flush();

            // per sapere se l'invio è avvenuto con successo NON NECESSARIO
            //String result = (String) inStream.readObject();

            synchronized (lock) {//attendo ricezione result
                while (!available_resultOfSendEmail) {
                    lock.wait();
                }
            }
            available_resultOfSendEmail = false;
            return resultOfSendEmail.equals("SEND_OK") ? "Email succesfully sent" : "Recipient doesn't exist!";

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //closeStreams();
        }
        return "email successfully sent!";
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

    private void connectToServer() throws IOException {
        socket = new Socket(host, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());

        // Dalla documentazione di ObjectOutputStream
        // callers may wish to flush the stream immediately to ensure that constructors for receiving
        // ObjectInputStreams will not block when reading the header.
        outStream.flush();

        inStream = new ObjectInputStream(socket.getInputStream());

        //System.out.println("[Client luca.dadone01@gmail.com] Connesso");
    }

    public String newEmail(ArrayList<String> dest, String oggetto, String contenuto) {
        Email to_send = new Email(1010, -1, this.mailbox.getEmailAddress().toString(), dest, oggetto, contenuto, LocalDateTime.now());
        return tryCommunicationEmail(to_send);
    }

    public String newEmail(int replyId, ArrayList<String> dest, String oggetto, String contenuto) {
        Email to_send = new Email(1010, replyId, this.mailbox.getEmailAddress().toString(), dest, oggetto, contenuto, LocalDateTime.now());
        return tryCommunicationEmail(to_send);
    }

    public void deleteEmail(Email e) {
        /**
         * Come gmail eliminiamo la mail in locale lato client e poi proviamo a comunicarlo al server
         * (non attendiamo risposta)
         * */
        this.mailbox.removeEmail(e);
        String delete_msg = "DELETE";
        tryCommunicationDeleteEmail(delete_msg, e);
    }

    private void tryCommunicationDeleteEmail(String delete_msg, Email email) {
        if (!is_logged) {
            showAlert("Server offline.. ");
            return;
        }
        try {
            outStream.writeObject(delete_msg);
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
    public void listenForEmails() throws InterruptedException, IOException {
        try {
            while (!is_logged_out) {
                Object message = inStream.readObject();
                if (message instanceof Email received_email) {
                    /**
                     * Riceve una mail dal server
                     **/
                    System.out.println("Ho ricevuto la mail: " + received_email);
                    this.mailbox.addEmail(received_email);
                    Platform.runLater(() -> Client.showAlert(received_email, this.mailbox.getEmailAddress()));
                    outStream.writeObject("RECEIVED_OK");
                    outStream.writeObject(received_email);
                    outStream.flush();
                } else { //Ricevuto risultato dell'invio di una mail
                    synchronized (lock) {
                        resultOfSendEmail = message.toString();
                        available_resultOfSendEmail = true;
                        lock.notify();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            //chiusura socket
            is_logged = false;
            //attesa riconnessione con il server
            while (!is_logged_out && !is_logged) {
                //server offline
                Thread.sleep(1000);
                this.login();
            }
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

    public static void showAlert(Email e, String client) {

        String imagePath = System.getProperty("user.dir") + "/src/main/java/com/example/mailservice/mailclient/assets/email.png";

        ImageView imageView = new ImageView(imagePath);
        imageView.setFitWidth(50); // Imposta la larghezza desiderata dell'immagine
        imageView.setFitHeight(50); // Imposta l'altezza desiderata dell'immagine
        String notificationText = "From: " + e.getSender() + "\n" +
                "Subject: " + (e.getSubject().length() >= 30 ? e.getSubject().substring(0, 30) : e.getSubject());

        Notifications.create()
                .title(client)
                .text(notificationText)
                .graphic(imageView)
//                .darkStyle()
                .hideAfter(Duration.INDEFINITE)
                .show();
    }

    public static void showAlert(String msg) {

        // Mostra una finestra di conferma
        Alert confirmationDialog = new Alert(Alert.AlertType.ERROR);
        confirmationDialog.setTitle("Sorry!");
        confirmationDialog.setHeaderText(msg);
        confirmationDialog.setContentText("try later.");
        Optional<ButtonType> result = confirmationDialog.showAndWait();
    }

    public void logout() {
        if (!is_logged) {
            is_logged_out = true;
            return;
        }
        try {
            outStream.writeObject("LOGOUT");
            outStream.flush();
            is_logged_out = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStreams();
        }
    }

}
