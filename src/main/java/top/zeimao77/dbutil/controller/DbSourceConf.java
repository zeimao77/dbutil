package top.zeimao77.dbutil.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.*;
import top.zeimao77.dbutil.context.App;
import top.zeimao77.dbutil.context.AppResourceContext;
import top.zeimao77.dbutil.ui.MainApp;
import top.zeimao77.dbutil.util.SerializableUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * 数据源配置页面
 */
public class DbSourceConf {

    private static final Logger logger = Logger.getLogger(DbSourceConf.class.getName());


    private static File file;


    ObservableList<String> itemObList;
    @FXML
    private TextArea textArea_source;

    private Stage stage_dialog;

    @FXML
    private ChoiceBox choiceBox_dbsourceSelect;

    @FXML
    public void handleCancel() {
        stage_dialog.close();
    }

    public void setStage_dialog(Stage stage_dialog) {
        this.stage_dialog = stage_dialog;
    }

    @FXML
    private void handleconfigSource() throws IOException {
        String configStr = this.textArea_source.getText();
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
        logger.info("重新配置数据源成功");
        jdbcTemplate.setQueryTimeout(30);
        MainApp.getControllerUiContext().setTemplate(jdbcTemplate);
        stage_dialog.close();
    }

    public void init() throws IOException {
        JSONArray sourceList = AppResourceContext.getSourceList();
        file = new File(App.SOURCELISTFILE);
        //读列表 初始化下拉选择
        if(file.exists()) {
            try {
                sourceList = SerializableUtil.DeserializeObject(file,JSONArray.class);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ArrayList<String> itemList = new ArrayList<>();
            for(int i=0;i<sourceList.size();i++) {
                JSONObject source = sourceList.getJSONObject(i);
                itemList.add(source.getString("name"));
            }
            itemObList = FXCollections.observableArrayList(itemList);
            choiceBox_dbsourceSelect.setItems(itemObList);
            //定义下拉事件
            JSONArray finalSourceList = sourceList;
            choiceBox_dbsourceSelect.getSelectionModel().selectedIndexProperty().addListener((o1, o2, o3)->{
                JSONObject json = finalSourceList.getJSONObject(Integer.parseInt(o3.toString())<0?0:Integer.parseInt(o3.toString())).getJSONObject("db");
                if(json != null) {
                    textArea_source.setText(JSON.toJSONString(json,true));
                }else {
                    textArea_source.setText("");
                }
            });
            choiceBox_dbsourceSelect.getSelectionModel().select(0);
        }else {
            file.getParentFile().mkdirs();
            file.createNewFile();
            sourceList = new JSONArray(10);
            ArrayList<String> itemList = new ArrayList<>(10);
            for(int i=0;i<10;i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", UUID.randomUUID().toString());
                obj.put("name",String.format("默认00%d",i));
                sourceList.add(obj);
                itemList.add(obj.getString("name"));
            }
            itemObList = FXCollections.observableArrayList(itemList);
            choiceBox_dbsourceSelect.setItems(itemObList);
            SerializableUtil.serializeObject(sourceList,file);
        }

    }

    public void button_addList_click() throws IOException {
        JSONArray sourceList = AppResourceContext.getSourceList();
        //将源添加到列表
        int select_index = choiceBox_dbsourceSelect.getSelectionModel().getSelectedIndex();
        JSONObject sourceSelect = sourceList.getJSONObject(select_index);
        String text = textArea_source.getText();
        if(StringUtils.isEmpty(text)) {
            return;
        }
        sourceSelect.put("db",JSON.parseObject(text));
        TextInputDialog dialog = new TextInputDialog(sourceSelect.getString("name"));
        dialog.setTitle("提示");
        dialog.setHeaderText("请输入命名");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            itemObList.set(select_index,result.get());
            sourceSelect.put("name",result.get());
            choiceBox_dbsourceSelect.getSelectionModel().select(select_index);
        }
        SerializableUtil.serializeObject(sourceList,file);
    }

    public void button_removeList_click() throws IOException {
        JSONArray sourceList = AppResourceContext.getSourceList();
        //从列表中移除选中的源
        int select_index = choiceBox_dbsourceSelect.getSelectionModel().getSelectedIndex();
        JSONObject obj = sourceList.getJSONObject(select_index);
        obj.remove("db");
        SerializableUtil.serializeObject(sourceList,file);
        textArea_source.setText("");
    }
}



