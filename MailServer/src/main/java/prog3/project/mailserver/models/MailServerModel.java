package prog3.project.mailserver.models;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailServerModel {

 //lista di caselle di posta
    private final ObservableList<Mailbox> mailboxes = FXCollections.observableArrayList(mailbox ->
            new Observable[] {mailbox.emailAddressProperty()});

    public void loadData() {
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add("davide.benotto@gmail.com");
        recipients.add("luca.dadone01@gmail.com");
        Email e = new Email(0,"riad.muska@gmail.com",recipients,"Soggetto di prova",
                "Oggetto di prova", "2022-03-22");
        Mailbox mb_luca = new Mailbox("luca.dadone01@gmail.com");
        Mailbox mb_davide = new Mailbox("davide.benotto@gmail.com");
        this.mailboxes.setAll(mb_luca,mb_davide);
        //addMailbox(mb_luca);
        //addMailbox(mb_davide);
        //receiveEmail(e);
    }
/*
    public void addMailbox(Mailbox mailbox) {
        this.mailboxes.setAll(mailbox);
    }*/

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
        throw new RuntimeException("Mailbox not found for any recipient in the list " + email.getRecipientsList());
    }

}

