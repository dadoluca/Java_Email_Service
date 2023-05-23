package com.example.mailservice.lib;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email implements Serializable {
    private static final long serialVersionUID = 5950169519310163575L;
    private int id;
    private int replyId;
    private String sender;
    private List<String> recipients;
    private String subject;
    private String text;
    private LocalDateTime date;

    public Email(int id, int replyId, String sender, List<String> recipients, String subject, String text, LocalDateTime date) {
        setId(id);
        setReplyId(replyId);
        setSender(sender);
        this.recipients = new ArrayList<>();
        setRecipientsList(recipients);
        setSubject(subject);
        setText(text);
        setDate(date);
    }

    public int getId() {
        return this.id;
    }
    public final void setId(final int id){
        this.id = id;
    }
    ///-------------------------------------------------------------------------------------

    public int getReplyId() {
        return this.replyId;
    }
    public final void setReplyId(final int id){
        this.replyId= id;
    }
    //-------------------------------------------------------------------------------------
    public String getSender() {
        return this.sender;
    }
    public final void setSender(final String sender){
        this.sender = sender;
    }
    ///-------------------------------------------------------------------------------------
    public final List<String> getRecipientsList() {
        return this.recipients;
    }
    public final void setRecipientsList(List<String> recipientsList) {
        for(String recipient : recipientsList)
            this.recipients.add(recipient);
    }

    ///-------------------------------------------------------------------------------------

    public String getSubject() {
        return this.subject;
    }
    public final void setSubject(final String subject){
        this.subject = subject;
    }
    ///-------------------------------------------------------------------------------------

    public String getText() {
        return this.text;
    }
    public final void setText(final String text){
        this.text=text;
    }
    ///-------------------------------------------------------------------------------------
   /*
   private final StringProperty date = new SimpleStringProperty();
    public StringProperty dateProperty(){return this.date;}
    public String getDate() {
        return dateProperty().get();
    }
    public final void setDate(final String date){
        this.dateProperty().set(date);
    }*/
    public LocalDateTime getDate() {
        return this.date;
    }
    public final void setDate(final LocalDateTime _date){
        this.date = _date;
    }


    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Email em = (Email) o;

        if (getId() != em.getId())
            return false;

        return true;
    }

    public int hashCode() {
        return getId();
    }

    public String toString() {
        return "Sender: " + getSender() + "; Subject: " + getSubject();
    }

    public String getRecipientsString() {
        StringBuilder s = new StringBuilder("\"");
        for(int i = 0; i < recipients.size(); i++)
        {
            s.append("<"+recipients.get(i)+">");
            if(recipients.size()-1 > i)
                s.append(",");
        }

        s.append("\"");
        return s.toString();
    }
    public String toCSV(int id){
        return  id + "," + this.replyId + ",\"<" + this.sender + ">\"," + this.getRecipientsString() + ",\"" + this.subject + "\",\"" + this.text + "\"," + this.date.withSecond(0).withNano(0);  }

}
