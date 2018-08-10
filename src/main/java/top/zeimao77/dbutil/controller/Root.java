package top.zeimao77.dbutil.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import top.zeimao77.dbutil.ui.App;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Root {

    private MainApp mainApp;


    @FXML
    public void handleQuit() {
        System.exit(0);
    }

    @FXML
    public void handleabout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText(null);
        alert.setContentText(String.format("版本：[%s]\r\n", App.APP_VERSION));
        alert.showAndWait();
    }

    @FXML
    public void handleConfigDbSource() {
        File file = new File(App.DBSOURCE_FILE);
        PropertiesPersister pp = new DefaultPropertiesPersister();
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            pp.loadFromXml(properties,fileInputStream);
            mainApp.showDbSourceConfigPane(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void handleConfDbSource() throws IOException {
        mainApp.showDbSourceConfPane();
    }

}
