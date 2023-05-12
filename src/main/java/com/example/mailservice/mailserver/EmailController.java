package com.example.mailservice.mailserver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class EmailController {
    @FXML
    private Label welcomeText;

    @FXML
    private ListView<String> listViewLog;

    private MailServerModel model;

    ObservableList<String> logRecords;

    public void initModel(MailServerModel model) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;

        logRecords= FXCollections.observableArrayList();
        listViewLog = new ListView<>(logRecords);

/* veniva usato per stampare nel log la lista di email_address registrati
        listViewLog.setItems(model.getMailboxes());

        listViewLog.setCellFactory(lv -> new ListCell<Mailbox>() {
            @Override
            public void updateItem(Mailbox mailbox, boolean empty) {
                super.updateItem(mailbox, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(mailbox.getEmailAddress() + " " );
                }
            }
        });
 */
    }

    public void addRecord(String record){
        logRecords.add(record);
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Log");
    }
}