package top.zeimao77.dbutil.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.Assert;
import top.zeimao77.dbutil.ui.MainApp;

import java.util.Properties;

public class DbSourceConf {

    @FXML
    private TextArea sourceArea;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleconfigSource() {
        String configStr = this.sourceArea.getText();
        JSONObject jsonConfig = JSON.parseObject(configStr);
        Assert.isTrue(jsonConfig.containsKey("url"),"缺少参数[url]");
        Assert.isTrue(jsonConfig.containsKey("driver"),"缺少参数[driver]");
        Assert.isTrue(jsonConfig.containsKey("username"),"缺少参数[username]");
        Assert.isTrue(jsonConfig.containsKey("password"),"缺少参数[password]");
        Properties properties = new Properties();
        properties.setProperty("driver",jsonConfig.getString("driver"));
        properties.setProperty("url",jsonConfig.getString("url"));
        properties.setProperty("username",jsonConfig.getString("username"));
        properties.setProperty("password",jsonConfig.getString("password"));
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName(properties.getProperty("driver"));
        source.setUrl(properties.getProperty("url"));
        source.setUsername(properties.getProperty("username"));
        source.setPassword(properties.getProperty("password"));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
        jdbcTemplate.setQueryTimeout(30);
        MainApp.getControllerUi().setTemplate(jdbcTemplate);
        dialogStage.close();
    }


}
