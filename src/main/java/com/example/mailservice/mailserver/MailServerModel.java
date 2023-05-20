package com.example.mailservice.mailserver;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;

public class MailServerModel {


    /**
     * lista di caselle di posta
     */

    private ObservableList<String> logRecords = FXCollections.observableArrayList();

    public void addLogRecords(String record) {
        logRecords.add(record);
    }

    public ObservableList<String> getLogRecords() {
        return this.logRecords;
    }

    private List<Mailbox> mailboxes = new ArrayList<>();

    //--------- oggetto condiviso che contiene le connessioni sockets con i clients -----------
    private Map<String, SocketInfo> clients_sockets = new HashMap<>();

    public synchronized Socket getClientSocket(String email_addr) {
        SocketInfo socketInfo = clients_sockets.get(email_addr);
        return (socketInfo != null) ? socketInfo.socket : null;
    }
    public synchronized ObjectOutputStream getClientObjectOutputStream(String email_addr) {
        SocketInfo socketInfo = clients_sockets.get(email_addr);
        return (socketInfo != null) ? socketInfo.outStream : null;
    }
    public synchronized ObjectInputStream getClientObjectInputStream(String email_addr) {
        SocketInfo socketInfo = clients_sockets.get(email_addr);
        return (socketInfo != null) ? socketInfo.inStream : null;
    }
    public synchronized Map<String, SocketInfo> getClientsSockets() {
        return clients_sockets;
    }

    public synchronized void addClientSocket(String email_addr, Socket socket, ObjectOutputStream outStream, ObjectInputStream inStream) {
        SocketInfo socketInfo = new SocketInfo(socket, outStream, inStream);
        clients_sockets.put(email_addr, socketInfo);
        System.out.println("--- sockets: " + getClientsSockets());
    }

    private class SocketInfo {
        public Socket socket;
        public ObjectOutputStream outStream;
        public ObjectInputStream inStream;

        public SocketInfo(Socket socket, ObjectOutputStream outStream, ObjectInputStream inStream) {
            this.socket = socket;
            this.outStream = outStream;
            this.inStream = inStream;
        }
    }

    private int nextId = 0; //prossimo email id da assegnare

    public synchronized int getNextId() {
        nextId++;
        return nextId - 1;
    }

    public synchronized void setNextId(int start_value) {
        nextId = start_value;
    }

    public MailServerModel() {
        loadData();
    }

    public void loadData() {

        /******************************** LETTURA UTENTI **************************************/
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("src/main/java/com/example/mailservice/mailserver/data/user.txt"));
            String line = reader.readLine();
            int client_port = 4450;
            while (line != null) {
                mailboxes.add(new Mailbox(line));
                //System.out.println(line);
                // read next line
                line = reader.readLine();
                client_port++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /******************************* LETTURA EMAIL **************************************/
        ArrayList<String> recipientArrayList;

        try {
            CSVReader reader2 = new CSVReader(new FileReader("src/main/java/com/example/mailservice/mailserver/data/email.csv"));
            System.out.println(System.getProperty("user.dir"));
            //salto l'intestazione
            String[] line = reader2.readNext();
            Email e = null;
            while ((line = reader2.readNext()) != null) {
                int id = Integer.parseInt(line[0]);
                int replyID = Integer.parseInt(line[1]);
                String sender = line[2];
                String recipients = line[3];
                String[] recipientList = recipients.split(",");
                recipientArrayList = new ArrayList<>();
                for (String recipient : recipientList) {
                    recipientArrayList.add(recipient);
                }

                String subject = line[4];
                String text = line[5];
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime date = LocalDateTime.parse(line[6], formatter);
                // Do something with the values
                e = new Email(id, replyID, sender, recipientArrayList, subject,
                        text, date);
                receiveEmail(e, false);
            }
            setNextId(e != null ? e.getId() + 1 : 0);
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized void removeMailbox(Mailbox mailbox) {
        this.mailboxes.remove(mailbox);
    }

    public List<Mailbox> getMailboxes() {
        return this.mailboxes;
    }

    public synchronized Mailbox getMailbox(String em_addr) {
        for (Mailbox mailbox : mailboxes) {
            if (mailbox.getEmailAddress().equals(em_addr)) {
                return mailbox;
            }
        }
        return null;
    }

    //metodo per registrare una mail in tutte le mailbox dei destinatari
    public synchronized void receiveEmail(Email email,boolean isNew) throws IOException {
        String message="";//stringa di messaggio composta, in modo che il server possa inviare al client l''errore
        boolean founded=false;//per ogni destinatario controllo se è stata trovata una corrispondenza con gli altri utenti del servizio
        System.out.println("lista destinatari: "+email.getRecipientsList().toString());
        System.out.println("nella posizione 0 di RecipientList"+email.getRecipientsList().get(0));
        for (String recipient : email.getRecipientsList()) {
            System.out.println("recipiente x: "+recipient);
            founded=false;//nuovo destinatario esaminato, faccio ripartire founded a false
            if(!isValidEmail(recipient)){
                message+=recipient+" non è una mail sintatticamente giusta, ";
            }
            else {//se la mail è sintatticamente sbagliata non controllo nemmeno se il destinatario esiste
                for (Mailbox mailbox : mailboxes) {
                    if (mailbox.getEmailAddress().equals(recipient)) {
                        founded=true;
                        if(isNew){/** nuova mail */
                            /**
                             * la mail viene stampata nel log
                             * */
                            Platform.runLater(() -> this.addLogRecords("Email da: "+email.getSender()+" a: "+recipient+" subject: "+email.getSubject()+" Oraio: "+email.getDate()));

                            /**
                             *  la mail viene salvata nel csv
                             **/
                            PrintWriter writer = new PrintWriter(new FileWriter("src/main/java/com/example/mailservice/mailserver/data/email.csv",true));

                            /**
                             * Gestione accesso alla variabile condivisa nextId
                             *
                             synchronized (this){*/
                            writer.println();
                            writer.print(email.toCSV(nextId));
                            //System.out.println("scrivo "+email.toCSV(nextId));
                            nextId++;
                            /* }*/
                            writer.close();

                        }
                        mailbox.addEmail(email);
                        break;
                    }
                }
                if(!founded){
                    message+=recipient+" non è un cliente esistente, ";
                }
            }
        }
        System.out.println(" messaggio che dovrei restituire al client::::: "+message);
        if(!message.equals("")){
            //TODO DEVO DIRE AL CLIENTE CHE LA SUA MAIL PRESENTA ERRORI
        }

    }
    public static boolean isValidEmail(String email) {//Funzione per verificare la correttezza sintattica di una mail
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(regex, email);
    }

}

