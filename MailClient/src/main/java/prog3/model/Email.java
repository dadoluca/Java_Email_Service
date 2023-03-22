package prog3.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Email {
    private StringProperty mittente = new SimpleStringProperty();
    private StringProperty argomento = new SimpleStringProperty();
    private StringProperty testo = new SimpleStringProperty();
    private IntegerProperty idEmail = new SimpleIntegerProperty();

    private StringProperty destinatario = new SimpleStringProperty();


    public final Integer getIdEmail() {
        return this.idEmailProperty().get();
    }

    public final void setIdEmail(final int n) {
        this.idEmailProperty().set(n);
    }

    public final IntegerProperty idEmailProperty() {
        return this.idEmail;
    }

    public final StringProperty mittProperty() {
        return this.mittente;
    }

    public final String getMitt() {
        return this.mittProperty().get();
    }

    public final void setMittente(final String name) {
        this.mittProperty().set(name);
    }

    public final StringProperty argomentoProperty() {
        return this.argomento;
    }

    public final String getArgomento() {
        return this.argomentoProperty().get();
    }

    public final void setArgomento(final String argomento) {
        this.argomentoProperty().set(argomento);
    }

    public final StringProperty testoProperty() {
        return this.testo;
    }

    public final String getTesto() {
        return this.testoProperty().get();
    }

    public final void setTesto(final String text) {
        this.testoProperty().set(text);
    }

    public final StringProperty destProperty() {
        return this.destinatario;
    }

    public final String getDest() {
        return this.destProperty().get();
    }

    public final void setDest(final String dest) {
        this.destProperty().set(dest);
    }

    public Email(String mittente, String argomento, String testo, int idEmail, String destinatario) {
        setMittente(mittente);
        setArgomento(argomento);
        setIdEmail(idEmail);
        setTesto(testo);
        setDest(destinatario);
    }

}
