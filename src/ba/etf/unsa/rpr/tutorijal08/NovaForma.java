package ba.etf.unsa.rpr.tutorijal08;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class NovaForma {
    public TextField imeField;
    public TextField prezimeField;
    public TextField adresaField;
    public TextField gradField;
    public TextField brojField;

    public SimpleStringProperty ime;
    public SimpleStringProperty prezime;
    public SimpleStringProperty adresa;
    public SimpleStringProperty grad;
    public SimpleStringProperty postanskiBroj;

    PostanskiBrojValidator validator;

    public NovaForma() {
        ime = new SimpleStringProperty("");
        prezime = new SimpleStringProperty("");
        adresa = new SimpleStringProperty("");
        grad = new SimpleStringProperty("");
        postanskiBroj = new SimpleStringProperty("");
        validator = new PostanskiBrojValidator("");
    }

    @FXML
    public void initialize() {
        imeField.textProperty().bindBidirectional(ime);
        prezimeField.textProperty().bindBidirectional(prezime);
        adresaField.textProperty().bindBidirectional(adresa);
        gradField.textProperty().bindBidirectional(grad);
        brojField.textProperty().bindBidirectional(postanskiBroj);

        dodajListenere();
    }

    private void dodajListenere() {
        brojField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (aBoolean && !t1) {
                    validator.setBroj(brojField.getText());

                    Task<Boolean> task = new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            System.out.println("calling");
                            return validator.provjeriPostanskiBroj(brojField.getText());
                        }
                    };

                    task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent) {
                            Boolean value = task.getValue();
                            System.out.println(value);
                            if (value) {
                                System.out.println("test 1");
                                brojField.getStyleClass().removeAll("poljeNijeIspravno");
                                brojField.getStyleClass().add("poljeIspravno");
                            } else {
                                brojField.getStyleClass().removeAll("poljeIspravno");
                                brojField.getStyleClass().add("poljeNijeIspravno");
                            }
                        }
                    });

                    new Thread(task).start();

                }
            }
        });

    }
}