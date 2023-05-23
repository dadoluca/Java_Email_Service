package com.example.mailservice.lib;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;


public class Mailbox {
    private final StringProperty emailAddress = new SimpleStringProperty();
    public final StringProperty emailAddressProperty(){return this.emailAddress;}
    public String getEmailAddress() {
        return emailAddressProperty().get();
    }
    public final void setEmailAddress(final String emailAddress){
        this.emailAddressProperty().set(emailAddress);
    }
    private final ObservableList<Email> emailList;
    public ArrayList<Email> getEmailList() {
        return new ArrayList<Email>(emailList);
    }
    public ObservableList<Email> getObsEmailList() {
        return emailList;
    }



    public Mailbox(String emailAddress) {
        setEmailAddress(emailAddress);
        this.emailList = FXCollections.observableList(new LinkedList<>());

    }
    public void addEmail(Email email) {
        Platform.runLater(() -> this.emailList.add(email));
    }
}


