package prog3.project.mailserver.models;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    /*
    private final List<String> recipientsList = new SimpleListProperty<>();

    public final List<String> recipientsListProperty(){
        return recipientsList;
    }

    public final List<String> getRecipientsList() {
        return recipientsListProperty().stream().toList();
    }
    public final void setRecipientsList(List<String> recipientsList) {
        for(String recipient : recipientsList)
         recipientsListProperty().add(recipient);
    }*/


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
    private LocalDateTime date;
    public LocalDateTime getDate() {
        return this.date;
    }
    public final void setDate(final LocalDateTime _date){
        this.date = _date;
    }

}
