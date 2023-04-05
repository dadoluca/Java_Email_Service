package prog3.project.mailclient;

import prog3.project.mailclient.models.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Client {
    Socket socket = null;
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;

    String username;
    final int MAX_ATTEMPTS = 5;

    /**
     * Costruisce un nuovo client.
     * @param id identificatore numerico, utile solamente per la stampa dei messaggi.
     */

    /**
     * Fa fino a 5 tentativi per comunicare con il server. Dopo ogni tentativo fallito
     * aspetta 1 secondo.
     * @param host l'indirizzo sul quale il server è in ascolto.
     * @param port la porta su cui il server è in ascolto.
     */
    public void communicate(String host, int port) throws IOException {
        int attempts = 0;

        boolean success = false;
        while(attempts < MAX_ATTEMPTS && !success) {
            attempts += 1;
            System.out.println();

            success = tryCommunication(host, port);

            if(success) {
                System.out.println("Ho comunicato!!");
                continue;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Tenta di comunicare con il server. Restituisce true se ha successo, false altrimenti
    private boolean tryCommunication(String host, int port) {
        try {
            connectToServer(host, port);
            //List<Student> students = generateStudents(3);

            Thread.sleep(5000);

            ArrayList<String> recipients = new ArrayList<>();
            recipients.add("davide.benotto@gmail.com");
            Email em = new Email(0, -1,"luca.dadone01@gmail.com",recipients,"Soggetto di prova",
                    "Oggetto di prova",  LocalDateTime.now());
            //List<Email> emailList = new ArrayList<>();
            //emailList.add(em);
            outputStream.writeObject(em);
            //outputStream.writeObject("ciao sono il client");
            outputStream.flush();

            return true;
        } catch (ConnectException ce) {
            // nothing to be done
            return false;
        } catch (IOException se) {
            se.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnections();
        }
    }

    private void closeConnections() {
        if (socket != null) {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());

        // Dalla documentazione di ObjectOutputStream
        // callers may wish to flush the stream immediately to ensure that constructors for receiving
        // ObjectInputStreams will not block when reading the header.
        outputStream.flush();

        inputStream = new ObjectInputStream(socket.getInputStream());

        //System.out.println("[Client luca.dadone01@gmail.com] Connesso");
    }
}
