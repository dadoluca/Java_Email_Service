package com.example.mailservice.mailclient;

import com.example.mailservice.lib.Email;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class MailboxController {
    @FXML
    private ListView<Email> lstEmails;
    @FXML
    private Label lblUsername;

    @FXML
    private Button btnRispondi;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnReplyAll;

    @FXML
    private Button btnForward;
    private Client model;

    private Email selected;

    @FXML
    private TextArea txtEmailDetails;

    private Stage primaryStage;


    public void initModel(Client client) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = client;
        lblUsername.setText(model.mailbox.getEmailAddress());

        //order with decrescent Data
        lstEmails.setItems(model.mailbox.getObsEmailList().sorted(Comparator.comparing(Email::getDate).reversed()));


        lstEmails.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            public void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String from = email.getSender();
                    String subject = email.getSubject();

                    Text fromText = new Text(from);
                    fromText.setFont(Font.font("Arial", FontWeight.BOLD,12));

                    Label fromLabel = new Label();
                    fromLabel.setGraphic(fromText); // Imposta il testo "From" come grafica nella Label

                    Label subjectLabel = new Label(subject);

                    VBox vbox = new VBox(fromLabel, subjectLabel);
                    vbox.setSpacing(5); // Spazio tra i componenti all'interno del VBox
                    vbox.setPadding(new Insets(5)); // Padding intorno al VBox

                    setGraphic(vbox);

                    // Gestione dello stato di selezione
//                    if (isSelected()) {
//                        setBackground(new Background(new BackgroundFill(Color.rgb(35, 97, 118), CornerRadii.EMPTY, Insets.EMPTY)));
//                    } else {
//                        setBackground(Background.EMPTY);
//                    }
                }
            }
        });
        selected = null;
        lstEmails.setOnMouseClicked(this::showSelectedEmail);
        txtEmailDetails.setEditable(false);

        Platform.runLater(() -> {
            Stage primaryStage = (Stage) lblUsername.getScene().getWindow();
            setPrimaryStage(primaryStage);
        });

    }
    @FXML
    private void onBtnDeleteClick(){
        model.deleteEmail(selected);
//        lstEmails.refresh();
    }

    private void redirectToNewMailView(ActionEvent e, int action) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("new-mail-view.fxml"));
        Parent root = loader.load();

        NewMailController controller =  loader.getController();
        controller.initModel(model);
        if(action>0){
            if(selected!=null){
                //imposta alla nuova vista l'email a cui deve rispondere
                controller.setEmailtoReply(selected,action);
            }
            else {
                //TODO STAMPO UN MESSAGGIO:"SELEZIONARE LA MAIL PRIMA"
            }
        }
        Scene scene = ((Node) e.getSource()).getScene();
        scene.setRoot(root);
    }
    @FXML
    private void onBtnNewEmailClick(ActionEvent e) throws IOException {
        redirectToNewMailView(e,0);
    }


    @FXML
    protected void onBtnReplyClick(ActionEvent e)throws IOException{
        redirectToNewMailView(e,1);
    }
    @FXML
    protected void onBtnReplyAllClick(ActionEvent e)throws IOException{
        redirectToNewMailView(e,2);
    }

    @FXML
    private void onBtnForwardClick(ActionEvent e) throws IOException {
        redirectToNewMailView(e,3);
    }

    protected void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = lstEmails.getSelectionModel().getSelectedItem();
        selected = email;
        updateDetailView(email);
    }

    protected void updateDetailView(Email email) {
        if(email != null) {
            String text = String.format("From: %s\nSubject: %s\n\n%s", email.getSender(), email.getSubject(),email.getText().replaceAll("@@","\n"));
            txtEmailDetails.setText(text);
        }
        btnRispondi.setVisible(true);
        btnDelete.setVisible(true);
        btnReplyAll.setVisible(true);
        btnForward.setVisible(true);
        txtEmailDetails.setVisible(true);
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
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationDialog.setTitle("Confirm closure");
                confirmationDialog.setHeaderText("Are you sure?");
                confirmationDialog.setContentText("You will be logged out.");
                Optional<ButtonType> result = confirmationDialog.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    model.logout();
                    Platform.exit(); // Chiudi l'applicazione
                }
            }
        });
    }


}
