package com.example.mailservice.mailserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import javafx.application.Platform;

import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

public class ClientRequestHandler extends Thread {
    MailServerModel model;
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    Socket socket;
    boolean logout = false;


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
            closeConnection();
        }
    }

    /**
     * usa java reflection per identificare tipo di messaggio ricevuto
     */
    private void serveClient() {
        try {
            while (!logout) {
                Object message = inStream.readObject();
                if (message instanceof String) {
                    if(message.equals("DELETE")){
                        Object author = inStream.readObject();
                        if(author instanceof String){
                            Object email = inStream.readObject();
                            if(email instanceof Email){
                                Email to_delete = (Email) email;
                                /*
                                 * informa il log della delete
                                 * rimuove dal model
                                 */
                                Platform.runLater(() -> model.addLogRecords("Elimino mail da "+to_delete.getSender()+ " a "+to_delete.getRecipientsString()+" per "+author));
                                model.deleteEmail(to_delete, author.toString());
                            }
                        }

                    }
                    else if(message.equals("LOGOUT")){
                        Object author = inStream.readObject();
                        if (author instanceof String){
                            this.model.removeClientSocket(author.toString());
                            //closeConnection();
                            this.logout=true;
                        }
                    }
                    else{
                            /*
                             * Riceve un email address dal client che vuole loggarsi,
                             * verifica che sia loggato, e restituisce "TRUE" se si è loggati, "FALSE" altrimenti
                                */
                        System.out.println(message + " vuole loggarsi");
                        String email_addr = message.toString();
                        Mailbox mb_client = model.getMailbox(email_addr);
                        if (mb_client == null)
                        {
                            outStream.writeObject("FALSE");
                            outStream.flush();
                        }

                        else {//l'utente esiste
                                /*
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
                }
                else if (message instanceof Email) {
                        /*
                         * Riceve una mail dal client
                          */
                    Email to_forward = (Email) message;
                    System.out.println("Ho ricevuto la mail: " + to_forward);
                    String resulOfReceviveEmail=model.receiveEmail(to_forward, true);
                    if(!resulOfReceviveEmail.equals("RECIPPIENTS_ERROR:"))//scrive nel log e su csv e controlla eventuali errori
                    {
                        tryErrorCommunicationEmail(resulOfReceviveEmail, to_forward.getSender());
                    }
                        /*
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
    private void closeConnection() {
        try {
            if (inStream != null) {
                inStream.close();
            }

            if (outStream != null) {
                outStream.close();
            }

            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //--------------------------- inoltro mail ai clients
    private synchronized void tryCommunicationEmail(Email to_send, String recipient) {

        if(model.getClientSocket(recipient)!=null){ //recipient online
            try {
                model.getClientObjectOutputStream(recipient).writeObject(to_send);
                model.getClientObjectOutputStream(recipient).flush();

                /** TODO per sapere se l'invio è avvenuto con successo*/
//                 String success = (String) inStream.readObject();
//                 if(Objects.equals(success, "TRUE")){
//                     model.addLogRecords("L'utente "+recipient+" ha ricevuto la mail da "+to_send.getSender());
//                 }else {
//                     model.addLogRecords("L'utente " +recipient + "non ha ricevuto la mail da " + to_send.getSender());
//                 }
            } catch (SocketException e) {
                //eccezione quando il socket è chiuso
                System.err.println("Impossibile inviare la mail a " + recipient + " perché: "+e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
//            catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
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