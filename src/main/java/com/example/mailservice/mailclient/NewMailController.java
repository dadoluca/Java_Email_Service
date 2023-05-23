package com.example.mailservice.mailclient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    public void initModel(Client client) {
        // ensure model is only set once:
        if (this.client != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.client = client;
        host = "127.0.0.1";
        port = 3456;
    }

    @FXML private void onBtnInviaClick(){
        String destinatari=txtDestinatari.getText();
        ArrayList<String>dest=new ArrayList<>();
        boolean correctEmail = true;
        String[]splitted=destinatari.split(",");
        for (int i=0;i<splitted.length && correctEmail;i++){
            if(Client.isValidEmail(splitted[i]))
                dest.add(splitted[i]);
            else
                correctEmail = false;
        }
        if(correctEmail){
            String oggetto=txtOggetto.getText();
            String contenuto=txtContenuto.getText();
            client.newEmail(host, port,dest,oggetto,contenuto);
        }else{
            txtDestinatariError.setText("Mail non valida");
        }

    }

}
