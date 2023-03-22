package prog3.project.mailserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import prog3.project.mailserver.models.MailServerModel;

import java.io.IOException;

public class MailServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MailServerApplication.class.getResource("email-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        EmailController email_controller = fxmlLoader.getController();
        MailServerModel model= new MailServerModel();
        model.loadData();
        email_controller.initModel(model);
    }

    public static void main(String[] args) {
        launch();
    }
}