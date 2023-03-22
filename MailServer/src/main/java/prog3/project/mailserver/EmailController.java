package prog3.project.mailserver;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import prog3.project.mailserver.models.Email;
import prog3.project.mailserver.models.MailServerModel;
import prog3.project.mailserver.models.Mailbox;

public class EmailController {
    @FXML
    private Label welcomeText;

    @FXML
    private ListView<Mailbox> listViewMailBoxes;

    private MailServerModel model;

    public void initModel(MailServerModel model) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;
        listViewMailBoxes.setItems(model.getMailboxes());

        listViewMailBoxes.setCellFactory(lv -> new ListCell<Mailbox>() {
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
    }
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}