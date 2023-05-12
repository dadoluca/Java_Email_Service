package com.example.mailservice.mailserver;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class LogController {
    @FXML
    private Label welcomeText;

    @FXML
    private ListView<String> listViewLog;

    private MailServerModel model;

    //ObservableList<String> logRecords;

    public void initModel(MailServerModel model) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;
        /**
         * Agganciamo la ListView all'observableList in modo che modificando logRecords nel model
         * la ListView si aggiorni
         * */
        listViewLog.setItems(model.getLogRecords());


    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Log");
    }
}