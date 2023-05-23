package com.example.mailservice.mailclient;

import javafx.fxml.FXML;
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
    private TextField txtError;
    private Client client;
    private String host;

    private int port;

    public void initModel(Client client) {
        // ensure model is only set once:
        if (this.client != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.client = client;
        // Inizializza la textbox con il valore iniziale della proprietà error
        txtError.setText(client.mailbox.getObsError().get());

        // Crea un binding tra il valore della proprietà error e il testo del TextField
        txtError.textProperty().bind(client.mailbox.getObsError());

        host = "127.0.0.1";
        port = 3456;
    }

    @FXML private void onBtnInviaClick(){
        String oggetto=txtOggetto.getText();
        String destinatari=txtDestinatari.getText();
        String contenuto=txtContenuto.getText();
        ArrayList<String>dest=new ArrayList<>();
        String[]splitted=destinatari.split(",");
        for (int i=0;i<splitted.length;i++){
            dest.add(splitted[i]);
        }
        client.newEmail(host, port,dest,oggetto,contenuto);
    }

}
