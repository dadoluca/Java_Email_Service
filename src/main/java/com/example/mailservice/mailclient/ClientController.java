package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ClientController {
    @FXML
    private ListView<String> lstEmails;
    private Client model;
    private String host;

    private int port;
    public void initModel(Client client) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = client;

        host = "127.0.0.1";
        port = 4440;


        lstEmails.setItems(model.getInboxContent());
        lstEmails.setCellFactory(lv -> new ListCell<String>() {
            @Override
            public void updateItem(String email, boolean empty) {
                super.updateItem(email, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(email);
                }
            }
        });

    }
}
