package prog3.project.mailclient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class ClientController {
    @FXML
    private Label welcomeText;
    private Client client;
    private String host;

    private int port;

    public void initModel(Client client) {
        // ensure model is only set once:
        if (this.client != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.client = client;

        host = "127.0.0.1";
        port = 4440;
    }

    @FXML
    protected void onHelloButtonClick() throws IOException {
        welcomeText.setText("Provo a comunicare col server");
        client.communicate(host,port);
    }
}