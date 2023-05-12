package com.example.mailservice.mailclient;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewMailController {
    @FXML
    private TextField txtDestinatari;
    @FXML
    private TextField txtOggetto;
    @FXML
    private TextField txtContenuto;
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
        port = 4440;
    }

    @FXML private void onBtnInviaClick(){
        String oggetto=txtOggetto.getText();
        String destinatari=txtDestinatari.getText();
        String contenuto=txtContenuto.getText();

        client.newEmail(host, port,destinatari,oggetto,contenuto);
    }

}
