package com.example.mailservice.mailserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

public class ClientRequestHandler extends Thread {
    MailServerModel model;
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    Socket socket;
    boolean logout = false;
    String email_addr_client_to_serve;


    public ClientRequestHandler(Socket socket, MailServerModel model) {
        this.socket = socket;
        this.model = model;
    }

    public void run() {
        try {
            openStreams();
            serveClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    /**
     * usa java reflection per identificare tipo di messaggio ricevuto
     */
    private void serveClient() {//si occupa di servire un certo client (sempre lo stesso)
        try {
            while (!logout) {
                Object message = inStream.readObject();
                if (message instanceof String) {
                    if (message.equals("DELETE")) {
                            Object email = inStream.readObject();
                            if (email instanceof Email) {
                                Email to_delete = (Email) email;
                                /*
                                 * informa il log della delete
                                 * rimuove dal model
                                 */
                                Platform.runLater(() -> model.addLogRecords("DELETED email from " + to_delete.getSender() + " to " + to_delete.getRecipientsString() + " for " + email_addr_client_to_serve));
                                model.deleteEmail(to_delete, email_addr_client_to_serve);
                            }

                    } else if (message.equals("LOGOUT")) {
                            this.model.removeClientSocket(email_addr_client_to_serve.toString());
                            Platform.runLater(() -> this.model.addLogRecords("User " + email_addr_client_to_serve + " is logged out!"));
                            this.logout = true;
                    } else if(message.equals("RECEIVED_OK")) {//per sapere se la ricezione è avvenuta con successo
                            Object email = inStream.readObject();
                            if (email instanceof Email) {
                                Email email_received_ok_by_client = (Email) email;//email che il client ha ricevuto correttamente
                                final String logDetail = email_received_ok_by_client.getSender() + "@@" +
                                        email_received_ok_by_client.getRecipientsString() + "@@" +
                                        email_received_ok_by_client.getSubject() + "@@" +
                                        email_received_ok_by_client.getText().replaceAll("@@", "\n") + "@@" +
                                        email_received_ok_by_client.getDate() + "@@";
                                Platform.runLater(() -> model.addLogRecords("EMAIL RECIVED BY " + email_addr_client_to_serve + " FROM " + email_received_ok_by_client.getSender() + "&&\nEMAIL RECIVED&&" + logDetail));
                            }
                    }
                    else {
                        /*
                         * Riceve un email address dal client che vuole loggarsi,
                         * verifica che sia loggato, e restituisce "TRUE" se si è loggati, "FALSE" altrimenti
                         */
                        email_addr_client_to_serve = message.toString();
                        Mailbox mb_client = model.getMailbox(email_addr_client_to_serve);
                        if (mb_client == null) {
                            outStream.writeObject("FALSE");
                            outStream.flush();
                        } else {//l'utente esiste
                            /*
                             * aggiungiamo il socket del client alla mappa dei email_addr - sockets
                             * */
                            model.addClientSocket(email_addr_client_to_serve, this.socket, this.outStream, this.inStream);

                            //stampa nel log
                            Platform.runLater(() -> model.addLogRecords("User " + mb_client.getEmailAddress() + " is logged in!"));

                            //invia al client la sua mailbox
                            outStream.writeObject("TRUE");
                            outStream.writeObject(mb_client.getEmailList());
                            outStream.flush();
                        }
                    }
                } else if (message instanceof Email) {
                    /*
                     * Riceve una mail dal client
                     */
                    Email to_forward = (Email) message;
                    System.out.println("Ho ricevuto la mail: " + to_forward);
                    Pair<Integer,String> resulOfReceviveEmail =model.receiveEmail(to_forward, true);
                    trySendResultCommunication(resulOfReceviveEmail.getValue(), to_forward.getSender());
                    to_forward.setId(resulOfReceviveEmail.getKey());
                    if (resulOfReceviveEmail.getValue().equals("SEND_OK"))//scrive nel log e su csv e controlla eventuali errori
                        /*
                         * preleva i destinatari e inoltra la mail
                         **/
                        for (String recepient : to_forward.getRecipientsList()) {
                            tryCommunicationEmail(to_forward, recepient);
                        }
                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            //throw new RuntimeException(ex);
            serveClient();
        }
    }

    // apre gli stream necessari alla connessione corrente
    private void openStreams() throws IOException {
        inStream = new ObjectInputStream(socket.getInputStream());
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.flush();
    }

    // Chiude gli stream utilizzati durante l'ultima connessione
    private void closeConnection() {
        try {
            if (inStream != null) {
                inStream.close();
            }

            if (outStream != null) {
                outStream.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //--------------------------- inoltro mail ai clients
    private synchronized void tryCommunicationEmail(Email to_send, String recipient) {

        if (model.getClientSocket(recipient) != null) { //recipient online
            try {
                synchronized (model.getClientObjectOutputStream(recipient)){//nel caso due client hander diversi volesso mandare la mail allo stesso utente
                    model.getClientObjectOutputStream(recipient).writeObject(to_send);
                    model.getClientObjectOutputStream(recipient).flush();
                }
            } catch (SocketException e) {
                //eccezione quando il socket è chiuso
                System.err.println("Impossibile inviare la mail a " + recipient + " perché: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    model.addLogRecords("ERROR delivering a message from: "+to_send.getSender()+" to:"+ recipient);
                });
            }
        }
    }
    //--------------------------- comunica al client se l'invio è avvenuto o se i destinatari non esistono
    private synchronized void trySendResultCommunication(String to_send, String sender) {
        try {
            model.getClientObjectOutputStream(sender).writeObject(to_send);
            model.getClientObjectOutputStream(sender).flush();

        } catch (SocketException e) {
            //eccezione quando il socket è chiuso
            System.err.println("Impossibile inviare la mail a " + sender + " perché: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}