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
import java.util.*;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import javafx.util.Pair;

public class MailServerModel {


    /**
     * lista di caselle di posta
     */
    public List<ClientRequestHandler> pool_requestHandler_threads;
    public MailServerModel(){
        pool_requestHandler_threads = new ArrayList<>();
        Platform.runLater(() -> this.addLogRecords("Server is running.."));
        loadData();
    }

    private ObservableList<String> logRecords = FXCollections.observableArrayList();

    public synchronized void addLogRecords(String record) {
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
        System.out.println("--- socket opened: " + email_addr);
    }
    public synchronized void removeClientSocket(String email_addr) {
        clients_sockets.remove(email_addr);
        System.out.println("--- socket closed: " + email_addr);
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


    public void loadData() {

        /*
         *  Lettura users
         *  */
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

        /*
         * Lettura deleted emails
         * */
        Map<Integer,String> deleted = new HashMap<>();
        try {
            CSVReader reader3 = new CSVReader(new FileReader("src/main/java/com/example/mailservice/mailserver/data/deleted_emails.csv"));
            //salto l'intestazione
            String[] line = reader3.readNext();
            Email e = null;
            while ((line = reader3.readNext()) != null) {
                int id = Integer.parseInt(line[0]);
                String email_addr = line[1];
//                System.out.println(id + "" + email_addr);
//                this.getMailbox(email_addr).removeEmail(id);//elimino dalla casella di posta
                deleted.put(id,email_addr);
            }
            reader3.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        /*
         * Lettura emails
         * */
        try {
            ArrayList<String> recipientArrayList;
            CSVReader reader2 = new CSVReader(new FileReader("src/main/java/com/example/mailservice/mailserver/data/emails.csv"));
            //System.out.println(System.getProperty("user.dir"));
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
                    if(deleted.get(id) == null){
                        recipientArrayList.add(recipient);
                    } else if (!deleted.get(id).equals(recipient)) {
                        recipientArrayList.add(recipient);
                    }

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

    public synchronized void deleteEmail(Email email, String email_addr) throws IOException {
        this.getMailbox(email_addr).removeEmail(email);
        /**
         *  la mail eliminata dalla mailbox del client email_addr
         *  viene salvata nel csv
         **/
        PrintWriter writer = new PrintWriter(new FileWriter("src/main/java/com/example/mailservice/mailserver/data/deleted_emails.csv",true));
        writer.println();
        writer.print(email.getId()+","+email_addr);
        writer.close();
    }

    //metodo per registrare una mail in tutte le mailbox dei destinatari
    public synchronized Pair<Integer,String> receiveEmail(Email email, boolean isNew) throws IOException {//HO MODIFICATO A STRING IN MODO CHE POSSA RITORNARE IL MESSAGGIO IN CASO DI ERRORE
        String message="SEND_OK";//stringa di messaggio composta, in modo che il server possa inviare al client l'errore
        boolean written_in_csv = false;

        for(String recipient: email.getRecipientsList()){
            if(this.getMailbox(recipient) == null){
                message="ERROR_RECIPIENT: Recipient doesn't exist";
                return new Pair<>(-2,message);
            }
        }

        for (String recipient : email.getRecipientsList()) {
            Mailbox mailbox = this.getMailbox(recipient);
                if(isNew){/** nuova mail */
                    /**
                     * la mail viene stampata nel log
                     * */
                    final String logDetail = email.getSender()+"@@"+
                            email.getRecipientsString()+"@@"+
                            email.getSubject()+"@@"+
                            email.getText().replaceAll("@@","\n")+"@@"+
                            email.getDate()+"@@";

                    if(email.getText().contains("------- Forward message -------")){
                        Platform.runLater(() -> this.addLogRecords("FORWARD: " + email.getSender()+"--->"+recipient+"&&\n"+
                                "FORWARD&&"+
                                logDetail));
                    }
                    else if(email.getReplyId()!=-1){
                        Platform.runLater(() -> this.addLogRecords("REPLY: " + email.getSender()+"--->"+recipient+"&&\n"+
                                "REPLY&&"+
                                logDetail));
                    }
                    else{
                        Platform.runLater(() -> this.addLogRecords("NEW MAIL: " + email.getSender()+"--->"+recipient+"&&\n"+
                                "NEW EMAIL&&"+
                                logDetail));
                    }
                    /**
                     *  la mail viene salvata nel csv
                     **/
                    if(!written_in_csv){
                        PrintWriter writer = new PrintWriter(new FileWriter("src/main/java/com/example/mailservice/mailserver/data/emails.csv",true));
                        writer.println();
                        writer.print(email.toCSV(nextId));
                        nextId++;
                        writer.close();
                        written_in_csv = true;
                    }
                }
                mailbox.addEmail(email);
            }
        return new Pair<>(nextId,message);
    }

    public void logout(){
        for (Thread thread : pool_requestHandler_threads) {
            thread.interrupt();
        }

        Iterator<String> iterator = clients_sockets.keySet().iterator();
        while (iterator.hasNext()) {
            String email_addr = iterator.next();
            iterator.remove();
        }

        System.exit(0);
    }
}

