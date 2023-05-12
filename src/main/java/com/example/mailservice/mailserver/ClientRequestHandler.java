package com.example.mailservice.mailserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;

import java.io.IOException;

public class ClientRequestHandler extends Thread{
    MailServerModel model;
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    Socket socket;

    public ClientRequestHandler(Socket socket, MailServerModel model){
        this.socket = socket;
        this.model = model;
    }

    public void run() {
        try {
            openStreams();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        serveClient();
        closeStreams();
    }

    /**
     * usa java reflection per identificare tipo di messaggio ricevuto
     */
    private void serveClient() {
        try {
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
                    outStream.writeObject("FALSE");
                else {
                    outStream.writeObject("TRUE");
                    outStream.writeObject(mb_client.getEmailList());
                }
            }
            if (message instanceof Email) {
                /**
                 * Riceve una mail dal client
                 **/
                Email to_forward = (Email) message;
                System.out.println("Ho ricevuto la mail: " + to_forward);
                // aggiorno il model
                model.receiveEmail(to_forward);
                /**
                 * Scriviamo nel log
                 * preleva i destinatari e inoltra la mail
                 **/
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
}
