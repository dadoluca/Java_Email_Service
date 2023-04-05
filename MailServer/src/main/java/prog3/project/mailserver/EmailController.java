package prog3.project.mailserver;
import prog3.project.lib.models.Email;
import prog3.project.lib.models.Mailbox;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import prog3.project.mailserver.models.MailServerModel;

public class EmailController {
    @FXML
    private Label welcomeText;

    @FXML
    private ListView<Mailbox> listViewLog;

    private MailServerModel model;



    public void initModel(MailServerModel model) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;
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

    }
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Log");
    }
}