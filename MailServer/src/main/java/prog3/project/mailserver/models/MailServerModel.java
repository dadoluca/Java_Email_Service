package prog3.project.mailserver.models;

import com.opencsv.exceptions.CsvValidationException;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.opencsv.CSVReader;

public class MailServerModel {

 //lista di caselle di posta
    private final ObservableList<Mailbox> mailboxes = FXCollections.observableArrayList(mailbox ->
            new Observable[] {mailbox.emailAddressProperty()});

    public void loadData() {

        /******************************** LETTURA UTENTI **************************************/
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("data/user.txt"));
            String line = reader.readLine();

            while (line != null) {
                mailboxes.add(new Mailbox(line));
                System.out.println(line);
                // read next line
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /******************************* LETTURA EMAIL **************************************/
        ArrayList<String> recipientArrayList = new ArrayList<>();

        try {
            CSVReader reader2 = new CSVReader(new FileReader("data/email.csv"));
            String[] line = null;
            //salto l'intestazione
            line = reader2.readNext();
            while ((line = reader2.readNext()) != null) {
                int id = Integer.parseInt(line[0]);
                int replyID = Integer.parseInt(line[1]);
                String sender = line[2];
                String recipients = line[3];
                String[] recipientList = recipients.split(",");
                for (String recipient : recipientList) {
                    recipientArrayList.add(recipient);
                }

                String subject = line[4];
                String text = line[5];
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime date = LocalDateTime.parse(line[6], formatter);
                // Do something with the values
                Email e = new Email(id, replyID,sender,recipientArrayList,subject,
                        text, date);
                receiveEmail(e);
            }
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

    }

    public void removeMailbox(Mailbox mailbox) {
        this.mailboxes.remove(mailbox);
    }

    public ObservableList<Mailbox> getMailboxes() {
        return this.mailboxes;
    }

    //metodo per registrare una mail in tutte le mailbox dei destinatari
    public void receiveEmail(Email email) {
        for (String recipient : email.getRecipientsList()) {
            for (Mailbox mailbox : mailboxes) {
                if (mailbox.getEmailAddress().equals(recipient)) {
                    mailbox.addEmail(email);
                    break;
                }
            }
        }
    }

}

