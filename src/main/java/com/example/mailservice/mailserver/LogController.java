package com.example.mailservice.mailserver;

import com.example.mailservice.lib.Email;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;

public class LogController {
    @FXML
    private ListView<String> listViewLog;

    private MailServerModel model;

    //ObservableList<String> logRecords;

    @FXML
    private TextFlow txtLogRecordDetails;

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
        BackgroundFill backgroundFill = new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);

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
                    content.setFill(Color.WHITE);
                    VBox vbox = new VBox(content);
                    vbox.setSpacing(5); // Spazio tra i componenti all'interno del VBox
                    vbox.setPadding(new Insets(5)); // Padding intorno al VBox

//                    BorderStroke borderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(0));
//                    Border border = new Border(borderStroke);
//                    vbox.setBorder(border);

                    vbox.setBackground(background);


                    setGraphic(vbox);
                }
            }
        });
        selected = "";
        listViewLog.setOnMouseClicked(this::showSelectedLogRecord);
        listViewLog.setBackground(background);
        txtLogRecordDetails.setBackground(background);
//        txtLogRecordDetails.setEditable(false);
    }

    protected void showSelectedLogRecord(MouseEvent mouseEvent) {
        String logRecord= listViewLog.getSelectionModel().getSelectedItem();
        String[]splittedLogRecords=logRecord.split("&&");
        selected=logRecord;
        if(splittedLogRecords.length>2)
            updateDetailView(splittedLogRecords[1], splittedLogRecords[2]);
    }

    protected void updateDetailView(String logRecord, String email) {

        if(logRecord != "") {
            String[]emailElements=email.split("@@");

            txtLogRecordDetails.getChildren().clear();

            Text action = new Text(logRecord);
            action.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
            action.setFill(Color.WHITE);

            Text from = new Text("From: ");
            from.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
            from.setFill(Color.WHITE);
            Text fromField = new Text(emailElements[0] + "\n");
            fromField.setFill(Color.WHITE);

            Text to = new Text("To: ");
            to.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
            to.setFill(Color.WHITE);
            Text toField = new Text(emailElements[1] + "\n");
            toField.setFill(Color.WHITE);

            Text subject = new Text("Subject: ");
            subject.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
            subject.setFill(Color.WHITE);
            Text subjectField = new Text(emailElements[2] + "\n");
            subjectField.setFill(Color.WHITE);
            Text textField = new Text("\n" + emailElements[3].replaceAll("@@","\n"));
            textField.setFill(Color.WHITE);

            Text date = new Text("Date: ");
            date.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
            date.setFill(Color.WHITE);
            Text dateField = new Text(emailElements[4].substring(0,10) + "\n");
            dateField.setTextAlignment(TextAlignment.RIGHT);
            dateField.setFill(Color.WHITE);

            txtLogRecordDetails.getChildren().addAll(from,fromField,to, toField,subject,subjectField,date, dateField,textField);

        }
        txtLogRecordDetails.setVisible(true);
    }
}