package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private TextField txtError;
    private Client client;
    private String host;

    private int port;
    private Email to_reply=null;

    public void setEmail(Email e){
        this.to_reply=e;
        txtDestinatari.setText(e.getSender());
    }

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
        port = 4440;
    }

    @FXML private void onBtnInviaClick(){

        if(to_reply==null){
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
        else{
            if(txtDestinatari.getText().equals(to_reply.getSender())){
                String oggetto=txtOggetto.getText();
                String destinatari=to_reply.getSender();
                String contenuto=txtContenuto.getText();
                ArrayList<String>dest=new ArrayList<>();
                String[]splitted=destinatari.split(",");
                for (int i=0;i<splitted.length;i++){
                    dest.add(splitted[i]);
                }
                client.replyEmail(host, port,dest,oggetto,contenuto,to_reply.getId());
            }
            else{
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
    }
    @FXML
    protected void onBtnTornaIndietroClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client-view.fxml"));
        Parent root = loader.load();
        Scene scene = ((Node) e.getSource()).getScene();
        scene.setRoot(root);
    }

}
