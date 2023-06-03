package com.example.mailservice.lib;

import com.example.mailservice.mailclient.Client;
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
        ArrayList<Email> list = new ArrayList<>();
        list.addAll(emailList);
        return list;
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
//        System.out.println("ID mail " + email.getId());
    }

    public void removeEmail(int id){
        Email to_remove=null;
        int index=0;
        for(int i=0;i<this.emailList.size();i++){
            if(this.emailList.get(i).getId()==id){
                to_remove=this.emailList.get(i);
                index=i;
            }
        }
        final Email finalTo_remove = to_remove;
        System.out.println(this.emailList.size());
        Platform.runLater(() -> this.emailList.remove(finalTo_remove));
        System.out.println(this.emailList.size());

    }
    public void removeEmail(Email email){
        Platform.runLater(() -> {
            this.emailList.remove(email);
        });

    }

    public void removeAll(){
        int length = this.emailList.size();
            for (int i = 0; i<length; i++){
                Platform.runLater(() -> {this.emailList.remove(0);        });
            }
    }

}


