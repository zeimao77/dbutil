package top.zeimao77.dbutil.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Properties;

public class DbSourceConfig {

    @FXML
    private TextField driverField;

    @FXML
    private TextField urlField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private Stage dialogStage;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(Properties data) {
        this.driverField.setText(data.getProperty("driver"));
        this.urlField.setText(data.getProperty("url"));
        this.usernameField.setText(data.getProperty("username"));
        this.passwordField.setText(data.getProperty("password"));
    }

    public void handleCancel() {
        dialogStage.close();
    }


}
