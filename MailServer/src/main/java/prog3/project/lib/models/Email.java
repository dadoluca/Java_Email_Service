package prog3.project.lib.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Email implements Serializable {
    //private static final long serialVersionUID = 5950169519310163575L;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(sender, email.sender) && Objects.equals(recipients, email.recipients) && Objects.equals(subject, email.subject) && Objects.equals(text, email.text) && Objects.equals(date, email.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, recipients, subject, text, date);
    }

    /*
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Email em = (Email) o;

        if (getId() != em.getId())
            return false;

        return true;
    }*/
/*
    public int hashCode() {
        return getId();
    }*/

    public String toString() {
        return "Id = " + getId() + " ; Sender = " + getSender() + " ; Subject = " + getSubject();
    }

}








/*
           Email con Property
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Email {
    public Email(int id, int replyId, String sender, List<String> recipients, String subject, String text, LocalDateTime date) {
        setId(id);
        setReplyId(replyId); //-1 se non è una mail di risposta, altrimenti l'id della risposta
        setSender(sender);
        setRecipientsList(recipients);
        setSubject(subject);
        setText(text);
        setDate(date);
    }

    private final IntegerProperty id = new SimpleIntegerProperty();
    public final IntegerProperty idProperty(){return this.id;}
    public int getId() {
        return this.idProperty().get();
    }
    public final void setId(final int id){
        this.idProperty().set(id);
    }
    ///-------------------------------------------------------------------------------------
    private final IntegerProperty replyId = new SimpleIntegerProperty();
    public final IntegerProperty replyIdProperty(){return this.replyId;}
    public int getReplyId() {
        return this.replyIdProperty().get();
    }
    public final void setReplyId(final int id){
        this.replyIdProperty().set(id);
    }
    //-------------------------------------------------------------------------------------

    private final StringProperty sender = new SimpleStringProperty();
    public StringProperty senderProperty(){return this.sender;}
    public String getSender() {
        return senderProperty().get();
    }
    public final void setSender(final String sender){
        this.senderProperty().set(sender);
    }
    ///-------------------------------------------------------------------------------------
    private final List<String> recipientsList = new ArrayList<>();
    public final List<String> getRecipientsList() {
        return recipientsList;
    }
    public final void setRecipientsList(List<String> recipients) {
        for(String recipient : recipients)
            recipientsList.add(recipient);
    }


    ///-------------------------------------------------------------------------------------
    private final StringProperty subject = new SimpleStringProperty();
    public StringProperty subjectProperty(){return this.subject;}
    public String getSubject() {
        return subjectProperty().get();
    }
    public final void setSubject(final String subject){
        this.subjectProperty().set(subject);
    }
    ///-------------------------------------------------------------------------------------

    private final StringProperty text = new SimpleStringProperty();
    public StringProperty textProperty(){return this.text;}
    public String getText() {
        return textProperty().get();
    }
    public final void setText(final String text){
        this.textProperty().set(text);
    }

    private LocalDateTime date;
    public LocalDateTime getDate() {
        return this.date;
    }
    public final void setDate(final LocalDateTime _date){
        this.date = _date;
    }

}
*/