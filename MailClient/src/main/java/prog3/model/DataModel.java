package prog3.model;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

import java.io.File;

public class DataModel {

    private final ObservableList<Email> emails = FXCollections.observableArrayList(email ->
            new Observable[] {email.mittProperty(), email.argomentoProperty()});

    private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);

    public ObjectProperty<Email> currentEmailProperty() {
        return currentEmail ;
    }

    public final Email getCurrentEmail() {
        return currentEmailProperty().get();
    }

    public final void setCurrentEmail(Email mails) {
        currentEmailProperty().set(mails);
    }


    public ObservableList<Email> getMailsList() {
        return emails;
    }

    public void loadData(File file) {
        emails.setAll(
            new Email("Jacob", "MAIL", "jacob.smith@example.com",0, "pippo"),
            new Email("Jack", "MAIL2", "jak.smitto@example.com",1, "pluto"),
            new Email("pippo", "MAIL3", "jacob.smith@example.com",2, "quick"),
            new Email("pluto", "MAIL4", "jacob.smith@example.com",3, "quack"),
            new Email("pippo de pippis", "MAIL5", "jacob.smith@example.com",4, "mk")
        );
    }

    public void saveData(File file) {

    }
}
