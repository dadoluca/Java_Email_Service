package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewMailController {
    @FXML
    private TextField txtDestinatari;
    @FXML
    private TextField txtOggetto;
    @FXML
    private TextField txtContenuto;
    @FXML
    private Label txtDestinatariError;
    private Client client;
    private String host;

    private int port;
    private Email to_reply = null;

    public void initModel(Client client) {
        // ensure model is only set once:
        if (this.client != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.client = client;
        host = "127.0.0.1";
        port = 3456;
    }

    public void setEmailtoReply(Email e, boolean isAReplyAll) {
        this.to_reply = e;
        String destinatari = e.getSender();
        if (isAReplyAll) {
            for (String recipient : e.getRecipientsList()) {
                if(!recipient.equals(this.client.mailbox.getEmailAddress()))
                    destinatari += ",   " + recipient;
            }
        }
        txtDestinatari.setText(destinatari);
    }

    @FXML
    private void onBtnSendClick(ActionEvent e) throws IOException {
        String destinatari = txtDestinatari.getText().replaceAll(" ","");
        ArrayList<String> dest = new ArrayList<>();
        boolean correctEmail = true;
        String[] splitted = destinatari.split(",");
        for (int i = 0; i < splitted.length && correctEmail; i++) {
            if (Client.isValidEmail(splitted[i]))
                dest.add(splitted[i]);
            else
                correctEmail = false;
        }
        if (correctEmail) {
            String oggetto = txtOggetto.getText();
            String contenuto = txtContenuto.getText();

            /**
             * Controllo se è una mail di risposta
             * */
            if (to_reply != null && to_reply.getSender() == splitted[0]) {
                client.newEmail(host, port, to_reply.getId(), dest, oggetto, contenuto);
            } else {
                client.newEmail(host, port, dest, oggetto, contenuto);
            }
            redirectToClientView(e);
        } else {
            txtDestinatariError.setText("Mail non valida");
        }
    }

    @FXML
    protected void onBtnGoBackClick(ActionEvent e) throws IOException {
        redirectToClientView(e);
    }

    private void redirectToClientView(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mailbox-view.fxml"));
        Parent root = loader.load();
        MailboxController c = loader.getController();
        c.initModel(client);
        Scene scene = ((Node) e.getSource()).getScene();
        scene.setRoot(root);
    }

}
