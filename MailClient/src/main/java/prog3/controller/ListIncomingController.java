package prog3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import prog3.model.DataModel;
import prog3.model.Email;

public class ListIncomingController {
    @FXML
    private ListView<Email> lstIncomingMail;
    @FXML
    private TextField txtMittenteIncomingEmail ;
    @FXML
    private TextField textArgumentIncomingEmail ;

    private DataModel model ;

    public void initModel(DataModel model) {

        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model ;
        lstIncomingMail.setItems(model.getMailsList());

        lstIncomingMail.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                model.setCurrentEmail(newSelection));//
        model.currentEmailProperty().addListener((obs, oldMail, newMail) -> {
        /*if (newMail == null) {
        listView.getSelectionModel().clearSelection();
        } else {
        listView.getSelectionModel().select(newMail);
        }*/
            model.getMailsList().remove(newMail);
        });

        lstIncomingMail.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            public void updateItem(Email mail, boolean empty) {
                super.updateItem(mail, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(mail.getMitt() + " " + mail.getDest());
                }
            }
        });

        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
        model.loadData(null);//  aggiunto per far caricare i dati delle person
        model.currentEmailProperty().addListener((obs, oldUser, newUser) -> {
            if (oldUser != null) {
                //userField.textProperty().unbindBidirectional(oldUser.getMitt());
                txtMittenteIncomingEmail.setText(oldUser.getMitt());
                textArgumentIncomingEmail.setText(oldUser.getTesto());
            }
            if (newUser == null) {
                txtMittenteIncomingEmail.setText("");
                textArgumentIncomingEmail.setText("");
            } else {
                txtMittenteIncomingEmail.setText(newUser.getMitt());
                textArgumentIncomingEmail.setText(newUser.getTesto());
            }
        });
    }
}
