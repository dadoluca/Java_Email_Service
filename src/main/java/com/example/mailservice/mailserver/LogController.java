package com.example.mailservice.mailserver;

import com.example.mailservice.lib.Email;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class LogController {
    @FXML
    private ListView<String> listViewLog;

    private MailServerModel model;

    private Stage primaryStage;


    @FXML
    private TextArea txtLogRecordDetails;

    String selected;

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

        listViewLog.setCellFactory(lv -> new ListCell<String>() {
            @Override
            public void updateItem(String log_message, boolean empty) {
                super.updateItem(log_message, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String[] my_message=log_message.split("&&");
                    Text content = new Text(my_message[0]);
                    content.setFont(Font.font("Arial", FontWeight.BOLD,12));

                    VBox vbox = new VBox(content);
                    vbox.setSpacing(5); // Spazio tra i componenti all'interno del VBox
                    vbox.setPadding(new Insets(5)); // Padding intorno al VBox

                    setGraphic(vbox);
                }
            }
        });
        selected = "";
        listViewLog.setOnMouseClicked(this::showSelectedLogRecord);
        txtLogRecordDetails.setEditable(false);

        Platform.runLater(() -> {
            Stage primaryStage = (Stage) txtLogRecordDetails.getScene().getWindow();
            setPrimaryStage(primaryStage);
        });
    }


    protected void showSelectedLogRecord(MouseEvent mouseEvent) {
        String logRecord= listViewLog.getSelectionModel().getSelectedItem();
        if(logRecord.contains("&&")){
            String[]splittedLogRecords=logRecord.split("&&");
            selected=logRecord;
            updateDetailView(splittedLogRecords[1]);
        }
        else //user login
            txtLogRecordDetails.setVisible(false);

    }

    protected void updateDetailView(String logRecord) {
        if(logRecord != "") {
            txtLogRecordDetails.setText(logRecord);
        }
        txtLogRecordDetails.setVisible(true);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setWindowCloseEventHandler();
    }

    private void setWindowCloseEventHandler() {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume(); // Consuma l'evento per evitare la chiusura immediata della finestra

                // Mostra una finestra di conferma
                Alert confirmationDialog = new Alert(Alert.AlertType.WARNING);
                confirmationDialog.setTitle("Confirm closure");
                confirmationDialog.setHeaderText("You are about to shut down the server!");
                confirmationDialog.setContentText("Are you sure?");
                Optional<ButtonType> result = confirmationDialog.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    model.logout();
                    Platform.exit(); // Chiudi l'applicazione
                }
            }
        });
    }

}