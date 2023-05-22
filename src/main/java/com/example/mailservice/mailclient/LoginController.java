package com.example.mailservice.mailclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private Stage primaryStage;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField txtEmail;
    private Client client;
    private String host;

    private int port;

    private Client model=null;

    private void initModel(Client client) {
        // ensure model is only set once:
        /*if (this.client != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }*/
        //else
        this.client = client;
        host = "127.0.0.1";
        port = 4440;
    }

    @FXML
    protected void onBtnLoginClick(ActionEvent e) throws IOException {
        String mail_addr = txtEmail.getText();
        if (model == null) {
            model = new Client(mail_addr);
            initModel(model);
        }
        boolean success = client.login(host, port);
        if (success) {
            switchToNewView(e);
        } else {
            welcomeText.setText("Utente non registrato");
            model = null;
            txtEmail.setText("");
        }
    }


    @FXML
    private void switchToNewView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client-view.fxml"));
        Parent root = loader.load();

        ClientController controller = loader.getController();
        controller.initModel(client);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close(); // Chiudi la finestra precedente

        Stage newStage = new Stage();
        Scene scene = new Scene(root, 600, 450);
        newStage.setScene(scene);
        newStage.show();
    }


}