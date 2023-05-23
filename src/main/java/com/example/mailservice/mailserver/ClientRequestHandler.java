package com.example.mailservice.mailserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import javafx.application.Platform;

import java.io.IOException;
import java.net.SocketException;

public class ClientRequestHandler extends Thread {
    MailServerModel model;
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    Socket socket;


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
        }
        finally {
            closeStreams();
        }
    }

    /**
     * usa java reflection per identificare tipo di messaggio ricevuto
     */
    private void serveClient() {
        try {
             while (true) {
                    Object message = inStream.readObject();
                    if (message instanceof String) {
                        /**
                         * Riceve un email address dal client che vuole loggarsi,
                         * verifica che sia loggato, e restituisce "TRUE" se si è loggati, "FALSE" altrimenti
                         **/
                        System.out.println(message + " vuole loggarsi");
                        String email_addr = message.toString();
                        Mailbox mb_client = model.getMailbox(email_addr);
                        if (mb_client == null)
                        {
                            outStream.writeObject("FALSE");
                            outStream.flush();
                        }

                        else {//l'utente esiste
                            /**
                             * aggiungiamo il socket del client alla mappa dei email_addr - sockets
                             * */
                            model.addClientSocket(email_addr, this.socket, this.outStream, this.inStream);

                            //stampa nel log
                            Platform.runLater(() -> model.addLogRecords("L'utente " + mb_client.getEmailAddress() + " si è loggato"));

                            //invia al client la sua mailbox
                            outStream.writeObject("TRUE");
                            outStream.writeObject(mb_client.getEmailList());
                            outStream.flush();
                        }
                    }
                    if (message instanceof Email) {
                        /**
                         * Riceve una mail dal client
                         **/
                        Email to_forward = (Email) message;
                        System.out.println("Ho ricevuto la mail: " + to_forward);
                        String resulOfReceviveEmail=model.receiveEmail(to_forward, true);
                        if(!resulOfReceviveEmail.equals("RECIPPIENTS_ERROR:"))//scrive nel log e su csv e controlla eventuali errori
                        {
                            tryErrorCommunicationEmail(resulOfReceviveEmail, to_forward.getSender());
                        }
                        /**
                         * preleva i destinatari e inoltra la mail
                         **/
                        else{// se non ci sono errori procede all'inoltro
                            for (String recepient : to_forward.getRecipientsList()) {
                                tryCommunicationEmail(to_forward, recepient);
                            }
                        }
                    }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // apre gli stream necessari alla connessione corrente
    private void openStreams() throws IOException {
        inStream = new ObjectInputStream(socket.getInputStream());
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.flush();
    }

    // Chiude gli stream utilizzati durante l'ultima connessione
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

    //--------------------------- inoltro mail ai clients
    private synchronized void tryCommunicationEmail(Email to_send, String recipient) {
        try {
            model.getClientObjectOutputStream(recipient).flush();
            model.getClientObjectOutputStream(recipient).writeObject(to_send);
            model.getClientObjectOutputStream(recipient).flush();

            /** TODO per sapere se l'invio è avvenuto con successo
            String success = (String) inStream.readObject();
            if(Objects.equals(success, "TRUE")){
                model.addLogRecords("L'utente "+recipient+" ha ricevuto la mail da "+to_send.getSender());
            }*/
        } catch (SocketException e) {
            //eccezione quando il socket è chiuso
            System.err.println("Impossibile inviare la mail a " + recipient + " perché: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private synchronized void tryErrorCommunicationEmail(String to_send, String recipient) {
        try {
            model.getClientObjectOutputStream(recipient).flush();
            model.getClientObjectOutputStream(recipient).writeObject(to_send);
            model.getClientObjectOutputStream(recipient).flush();

            /** TODO per sapere se l'invio è avvenuto con successo
             String success = (String) inStream.readObject();
             if(Objects.equals(success, "TRUE")){
             model.addLogRecords("L'utente "+recipient+" ha ricevuto la mail da "+to_send.getSender());
             }*/
        } catch (SocketException e) {
            //eccezione quando il socket è chiuso
            System.err.println("Impossibile inviare la mail a " + recipient + " perché: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}