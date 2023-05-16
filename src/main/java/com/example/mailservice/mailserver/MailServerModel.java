package com.example.mailservice.mailserver;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class MailServerModel {


 /**lista di caselle di posta*/

    private ObservableList<String> logRecords= FXCollections.observableArrayList();
    public void addLogRecords(String record){
        logRecords.add(record);
    }

    public ObservableList<String> getLogRecords(){
        return this.logRecords;
    }

    private final ObservableList<Mailbox> mailboxes = FXCollections.observableArrayList(mailbox ->
            new Observable[] {mailbox.emailAddressProperty()});

    private int nextId; //prossimo email id da assegnare

    public MailServerModel(){
        loadData();
    }
    public void loadData() {

        /******************************** LETTURA UTENTI **************************************/
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("src/main/java/com/example/mailservice/mailserver/data/user.txt"));
            String line = reader.readLine();

            while (line != null) {
                mailboxes.add(new Mailbox(line));
                //System.out.println(line);
                // read next line
                line = reader.readLine();
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
            String[] line  = reader2.readNext();
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
                e = new Email(id, replyID,sender,recipientArrayList,subject,
                        text, date);
                receiveEmail(e,false);
            }
            nextId = e != null ? e.getId()+1 : 0;
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

    public ObservableList<Mailbox> getMailboxes() {
        return this.mailboxes;
    }

    public synchronized Mailbox getMailbox(String em_addr) {
        for(Mailbox mailbox: mailboxes){
            if (mailbox.getEmailAddress().equals(em_addr)){
                return mailbox;
            }
        }
        return null;
    }

    //metodo per registrare una mail in tutte le mailbox dei destinatari
    public synchronized void receiveEmail(Email email,boolean isNew) throws IOException {
        for (String recipient : email.getRecipientsList()) {
            for (Mailbox mailbox : mailboxes) {
                if (mailbox.getEmailAddress().equals(recipient)) {
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
        }
    }

}

