package prog3.project.mailclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import prog3.controller.ListIncomingController;
import prog3.model.DataModel;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        root.setCenter(listLoader.load());
        ListIncomingController listController = listLoader.getController();

        DataModel model = new DataModel();
        listController.initModel(model);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}