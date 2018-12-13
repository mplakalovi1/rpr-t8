package ba.etf.unsa.rpr.tutorijal08;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller {
    public TextField traziStringField;
    public SimpleStringProperty traziString;
    public ListView listaDatotekaField;
    public SimpleListProperty<String> listaDatoteka;
    public Button traziBtn;
    public Button prekiniBtn;
    private List<String> rezultat;

    private Thread backgroundWorker;


    public Controller() {
        traziString = new SimpleStringProperty("");
        listaDatoteka = new SimpleListProperty<String>();
        rezultat = Collections.synchronizedList(new ArrayList<String>());
    }

    @FXML
    public void initialize() {
        traziStringField.textProperty().bindBidirectional(traziString);
        listaDatotekaField.itemsProperty().bindBidirectional(listaDatoteka);
        listaDatoteka.set(FXCollections.observableArrayList(rezultat));
        prekiniBtn.setDisable(true);

        listaDatotekaField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(listaDatotekaField.getSelectionModel()
                        .getSelectedItem());
                try {
                    // nadjeno na netu
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("novaForma.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.DECORATED);
                    stage.setTitle("Nova forma");
                    stage.setScene(new Scene(root1, 400, 300));
                    stage.show();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void pokreniPretragu(ActionEvent actionEvent) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                dobaviSveDatoteke(new File(System.getProperty("user.home")));
                traziBtn.setDisable(false);
                traziStringField.setDisable(false);
                prekiniBtn.setDisable(true);
            }
        };
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                traziBtn.setDisable(true);
                traziStringField.setDisable(true);
                prekiniBtn.setDisable(false);
            }
        });
        backgroundWorker = new Thread(task);
        backgroundWorker.setDaemon(true);
        backgroundWorker.start();
    }

    public void dobaviSveDatoteke(File dir) {
        // Nadjeno na netu
        try {

            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    dobaviSveDatoteke(file);
                } else {
                    if (file.getCanonicalPath().toLowerCase().contains(traziStringField.textProperty().getValue()
                            .toLowerCase())) {
                        //System.out.println(file.getCanonicalPath());
                        String res = file.getCanonicalPath();
                        rezultat.add(file.getCanonicalPath());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listaDatotekaField.getItems().add(res);
                            }
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prekiniPretragu(ActionEvent actionEvent) {
        if (backgroundWorker.isAlive()) {
            backgroundWorker.interrupt();
            traziBtn.setDisable(false);
            traziStringField.setDisable(false);
            prekiniBtn.setDisable(true);
        }
    }
}