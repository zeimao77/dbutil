package top.zeimao77.dbutil.controller;

import com.alibaba.fastjson.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import top.zeimao77.dbutil.context.App;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.IOException;

/**
 * 主页页面框架
 */
public class Root {

    @FXML
    private ScrollPane importPane;

    @FXML
    private ScrollPane exportPane;

    @FXML
    private ScrollPane dataPane;

    @FXML
    private ScrollPane comparePane;

    @FXML
    private TabPane tabPane_root;

    @FXML
    public void handleQuit() {
        System.exit(0);
    }

    public void init() {
        tabPane_root.getSelectionModel().selectedItemProperty().addListener((o1,o2,o3)->{
            if("导入".equals(o3.getText())){
                MainApp.getControllerUiContext().getMysqlImport().refresh();
            }else if("导出".equals(o3.getText())){
                MainApp.getControllerUiContext().getMysql().refresh();
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
        MainApp.getControllerUiContext().getMainApp().showDbSourceConfigPane(new JSONObject());
    }


    public void handleConfDbSource() throws IOException {
        MainApp.getControllerUiContext().getMainApp().showDbSourceConfPane();
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

    public ScrollPane getComparePane() {
        return comparePane;
    }
}
