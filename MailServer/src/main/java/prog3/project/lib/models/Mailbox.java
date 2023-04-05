package prog3.project.lib.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Mailbox {
    private final StringProperty emailAddress = new SimpleStringProperty();
    public final StringProperty emailAddressProperty(){return this.emailAddress;}
    public String getEmailAddress() {
        return emailAddressProperty().get();
    }
    public final void setEmailAddress(final String emailAddress){
        this.emailAddressProperty().set(emailAddress);
    }
    private final ObservableList<Email> emailsList;
    public ObservableList<Email> getEmailsList() {
        return emailsList ;
    }

    public Mailbox(String emailAddress) {
        setEmailAddress(emailAddress);
        this.emailsList = FXCollections.observableList(new ArrayList<>());
    }

    public void addEmail(Email email) {
        this.emailsList.add(email);
    }
}
