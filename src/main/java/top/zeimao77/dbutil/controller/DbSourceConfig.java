package top.zeimao77.dbutil.controller;

import com.alibaba.fastjson.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Properties;

/**
 * 查看数据源
 */
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

    public void setData(JSONObject data) {
        this.driverField.setText(data.getString("driver"));
        this.urlField.setText(data.getString("url"));
        this.usernameField.setText(data.getString("username"));
        this.passwordField.setText(data.getString("password"));
    }

    public void handleCancel() {
        dialogStage.close();
    }


}
