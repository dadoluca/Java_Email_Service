package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewMailController {
    @FXML
    private Label LabelEmptyFields;
    @FXML
    private TextField txtDestinatari;
    @FXML
    private TextField txtOggetto;
    @FXML
    private TextArea txtContenuto;
    @FXML
    private Label txtSendEmailResult;
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

    public void setEmailtoReply(Email e, int action) {
        this.to_reply = e;
        String destinatari = e.getSender();
        switch (action) {
            case 1:
                txtDestinatari.setText(destinatari);
                break;
            case 2:
                for (String recipient : e.getRecipientsList()) {
                    if (!recipient.equals(this.client.mailbox.getEmailAddress()))
                        destinatari += ",   " + recipient;
                }
                txtDestinatari.setText(destinatari);
                break;
            case 3:
                txtOggetto.setText(e.getSubject());
                String text = String.format("------- Forward message -------\n" +
                        "From: " + e.getSender() + "\n" +
                        "Date: " + e.getDate().toString() + "\n" +
                        "Subject: " + e.getSubject() + "\n" +
                        "To: " + e.getRecipientsString().replaceAll("\"", "") + "\n\n" +
                        e.getText().replaceAll("@@", "\n"));
                txtContenuto.setText(text);
        }


    }

    @FXML
    private void onBtnSendClick(ActionEvent e) throws IOException {
        if(txtDestinatari.getText().equals("")||txtContenuto.getText().equals("")||txtOggetto.getText().equals("")){
            LabelEmptyFields.setVisible(true);
        }else{
            LabelEmptyFields.setVisible(false);
            String destinatari = txtDestinatari.getText().replaceAll(" ", "");
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
                String result;
                if (to_reply != null && to_reply.getSender().equals(splitted[0]))
                    result = client.newEmail(to_reply.getId(), dest, oggetto, contenuto);
                else
                    result = client.newEmail(dest, oggetto, contenuto);
                txtSendEmailResult.setText(result);
                if(result.equals("Email succesfully sent"))
                    redirectToClientView(e);
            } else {
                txtSendEmailResult.setText("Mail not valid");
            }
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
