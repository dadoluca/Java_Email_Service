package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import com.example.mailservice.lib.Mailbox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;

public class ClientController {
    @FXML
    private ListView<Email> lstEmails;
    @FXML
    private Label lblUsername;
    private Client model;
    private String host;
    private Email selected;


    private int port;
    public void initModel(Client client) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = client;

        host = "127.0.0.1";
        port = 4440;


        lblUsername.setText(model.mailbox.getEmailAddress());
        lstEmails.setItems(model.mailbox.getEmailList());


        lstEmails.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            public void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(email.toString());
                }
            }
        });
    }
    @FXML
    private void onBtnEliminaClick(){
        model.deleteEmail(lstEmails.getSelectionModel().getSelectedItem());
    }
    @FXML
    private void onBtnNuovaEmailClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("new-mail-view.fxml"));
        Parent root = loader.load();

        NewMailController controller =  loader.getController();
        controller.initModel(model);

        Scene scene = ((Node) e.getSource()).getScene();
        scene.setRoot(root);
    }
}
