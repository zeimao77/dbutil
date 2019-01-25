package top.zeimao77.dbutil.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.*;
import top.zeimao77.dbutil.comdata.App;
import top.zeimao77.dbutil.ui.MainApp;
import top.zeimao77.dbutil.util.SerializableUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 数据源配置页面
 */
public class DbSourceConf {

    //序列化文件
    private static final String SOURCELISTFILE  = "sourceListFile.json";
    private static final String SOURCEFILE = "/sourceList.jobj";
    //数据源列表
    JSONArray sourceList = null;

    @FXML
    private TextArea sourceArea;

    private Stage dialogStage;

    @FXML
    private ChoiceBox choiceBox_dbsourceSelect;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleconfigSource() throws IOException {
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
        File file = new File(App.DBSOURCE_FILE);
        if(file.exists()){
            file.delete();
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        PropertiesPersister pp = new DefaultPropertiesPersister();
        pp.storeToXml(properties,fos,App.DBSOURCE_FILE,"UTF-8");
        dialogStage.close();
    }

    public void init() throws IOException {
        //读列表 初始化下拉选择
        BufferedReader bReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(SOURCELISTFILE)));
        StringBuilder textBuilder = new StringBuilder();
        String line = null;
        while(!StringUtils.isEmpty((line = bReader.readLine()))) {
            textBuilder.append(line);
        }
        bReader.close();
        System.out.println(textBuilder.toString());
        sourceList = JSON.parseArray(textBuilder.toString());
        ArrayList<String> itemList = new ArrayList<>();
        for(int i=0;i<sourceList.size();i++) {
            JSONObject source = sourceList.getJSONObject(i);
            itemList.add(source.getString("name"));
        }
        /*choiceBox_dbsourceSelect.setItems(FXCollections.observableArrayList(itemList));
        //定义下拉事件
        choiceBox_dbsourceSelect.getSelectionModel().selectedIndexProperty().addListener((o1,o2,o3)->{
            System.out.println(o1);
            System.out.println(o2);
        });*/
        File file = new File(SOURCEFILE);
        file.delete();
        file.createNewFile();
        System.out.println(file.getPath());
        SerializableUtil.serializeObject(sourceList,file);


    }

    public void button_addList_click() {
        //将源添加到列表

    }

    public void button_removeList_click() {
        //从列表中移除选中的源

    }
}



