package top.zeimao77.dbutil.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import top.zeimao77.dbutil.comdata.App;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Root {

    @FXML
    private ScrollPane importPane;

    @FXML
    private ScrollPane exportPane;

    @FXML
    private ScrollPane dataPane;

    @FXML
    private TabPane tabPane_root;

    @FXML
    public void handleQuit() {
        System.exit(0);
    }

    public void init() {
        tabPane_root.getSelectionModel().selectedItemProperty().addListener((o1,o2,o3)->{
            if("导入".equals(o3.getText())){
                MainApp.getControllerUi().getMysqlImport().refresh();
            }else if("导出".equals(o3.getText())){
                MainApp.getControllerUi().getMysql().refresh();
            }
        });
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
            MainApp.getControllerUi().getMainApp().showDbSourceConfigPane(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleConfDbSource() throws IOException {
        MainApp.getControllerUi().getMainApp().showDbSourceConfPane();
    }


    public ScrollPane getExportPane() {
        return exportPane;
    }

    public ScrollPane getImportPane() {
        return importPane;
    }

    public ScrollPane getDataPane() {
        return dataPane;
    }
}
