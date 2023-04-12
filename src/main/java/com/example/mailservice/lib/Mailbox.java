package com.example.mailservice.lib;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    /*private final ObservableList<Email> emailsList = FXCollections.observableArrayList(email ->
            new Observable[] {email.getId(), email.getSender(), });*/
    //private final ObservableList<Email> emailsList;
    private final List<Email> emailList;


//    private final ListProperty<Email> emailListProperty;
//    public ObservableList<Email> getEmailsList() {
//        return emailsList ;
//    }
//    public ListProperty<Email> emailListProperty() {
//        return emailListProperty;
//    }
    public List<Email> getEmailsList() {
        return emailList;
    }

    public Mailbox(String emailAddress) {
        setEmailAddress(emailAddress);
        this.emailList = new ArrayList<>();
//        this.emailListProperty = new SimpleListProperty<>();
//        this.emailListProperty.set(emailsList);
    }
    public void addEmail(Email email) {
        this.emailList.add(email);
    }
}
