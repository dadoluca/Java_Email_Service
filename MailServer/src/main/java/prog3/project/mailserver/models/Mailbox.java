package prog3.project.mailserver.models;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import prog3.project.mailserver.models.Email;

import java.util.ArrayList;
import java.util.List;

public class Mailbox {
    private final StringProperty emailAddress = new SimpleStringProperty();
    public final StringProperty emailAddressProperty(){return this.emailAddress;}
    public String getEmailAddress() {
        return emailAddressProperty().get();
    }
    public final void setEmailAddress(final String emailAddress){
        this.emailAddressProperty().set(emailAddress);
    }
    private final ObservableList<Email> emailsList = FXCollections.observableArrayList(email ->
            new Observable[] {email.idProperty(), email.senderProperty(), email.subjectProperty()});
    public ObservableList<Email> getEmailsList() {
        return emailsList ;
    }

    public Mailbox(String emailAddress) {
        setEmailAddress(emailAddress);
    }
    public void addEmail(Email email) {
        this.emailsList.add(email);
    }
}
