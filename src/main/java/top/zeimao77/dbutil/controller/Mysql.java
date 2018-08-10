package top.zeimao77.dbutil.controller;

import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.*;
import top.zeimao77.dbutil.export.Column;
import top.zeimao77.dbutil.export.Table;
import top.zeimao77.dbutil.export.TableFactory;
import top.zeimao77.dbutil.export.XlsxView;
import top.zeimao77.dbutil.ui.App;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Mysql {

    private static final Logger logger = Logger.getLogger(Mysql.class.getName());

    @FXML
    private TextArea textArea_sql;

    @FXML
    private TextArea textArea_res;

    @FXML
    private ChoiceBox choiceBox_config;

    @FXML
    private Button button_export;

    @FXML
    private Button button_show;

    @FXML
    private Button button_select;

    @FXML
    private ProgressIndicator pro_indicator1;

    private TableFactory tableFactory;

    private JdbcTemplate template;

    private String serviceId;

    volatile private List<Map<String, Object>> select_data = new ArrayList<>(1);

    final Service<Integer> query_service = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() {
                    try{
                        button_select.setDisable(true);
                        updateProgress(-1,1);
                        String sql = textArea_sql.getText().trim();
                        if(StringUtils.isEmpty(sql)) {
                            Assert.notNull(serviceId,"请选择业务");
                            sql = tableFactory.getTableByKey(serviceId).getSql();
                            logger.info("[SQL:]" + sql);
                            Assert.isTrue(StringUtils.hasLength(sql.trim()),"SQL语句不允许为空");
                        }
                        select_data = template.queryForList(sql);
                        textArea_res.setText(String.format("查询到[%d]条记录",select_data.size()));
                        updateProgress(1,1);
                        return 0;
                    }catch (Exception e){
                        textArea_res.setText(String.format("[查询异常]:%s",e.getMessage()));
                        updateProgress(0,1);
                        e.printStackTrace();
                    }finally {
                        button_select.setDisable(false);
                    }
                    return 10001;
                }
            };

        }
    };

    public void button_show_click() throws IOException {
        Stage dataTableView = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("tabview.fxml"));
        AnchorPane pane = fxmlLoader.load();
        TabView tabView = fxmlLoader.getController();
        Table table = tableFactory.getTableByKey(this.serviceId);
        dataTableView.setTitle(table.getTableName());
        Map<String, Column> columns = table.getColumnMap();
        tabView.setHeader(columns);
        tabView.setBody(this.select_data);
        Scene scene = new Scene(pane);
        dataTableView.setScene(scene);
        dataTableView.showAndWait();
    }


    public void button_select_click() throws InterruptedException {
        pro_indicator1.progressProperty().bind(query_service.progressProperty());
        query_service.restart();
    }

    public void button_export_click() {
        Assert.notNull(serviceId,"请选择业务");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导出位置");
        String homePath = "C:"+ SystemPropertyUtils.resolvePlaceholders("${HOMEPATH}")+"\\Desktop";
        fileChooser.setInitialDirectory(new File(homePath));
        fileChooser.setInitialFileName(serviceId+".xlsx");
        File file = fileChooser.showSaveDialog(MainApp.rootStage);
        if(file != null) {
            XlsxView xlsxView = new XlsxView();
            Workbook workbook = xlsxView.create(this.tableFactory.getTableByKey(serviceId),select_data);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.flush();
                fos.close();
                logger.info(String.format("导出成功:%s",file.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void init() throws IOException {
        File file = new File("dbresource.xml");
        PropertiesPersister pp = new DefaultPropertiesPersister();
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(file);
        pp.loadFromXml(properties,fileInputStream);
        source(properties);
        file = new File(App.TABLECONFIG_FILE);
        if(!file.exists()) {
            Assert.isTrue(file.createNewFile(),String.format("创建新的配置文件[%s失败",App.TABLECONFIG_FILE));
            InputStream is =getClass().getClassLoader().getResourceAsStream("tableconfig.xml");
            FileCopyUtils.copy(is,new FileOutputStream(file));
            logger.info("请先配置配置文件");
            System.exit(0);
        }
        this.tableFactory = new TableFactory(file);
        choiceBox_config.setItems(FXCollections.observableArrayList(tableFactory.serviceList()));
        logger.info("读取默认配置成功");

        choiceBox_config.getSelectionModel().selectedItemProperty().addListener((o1,o2,o3)->{
            this.serviceId = o3.toString();
            this.select_data.clear();
        });
        choiceBox_config.getSelectionModel().select(0);
    }

    public void source(Properties properties) {
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName(properties.getProperty("driver"));
        source.setUrl(properties.getProperty("url"));
        source.setUsername(properties.getProperty("username"));
        source.setPassword(properties.getProperty("password"));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
        jdbcTemplate.setQueryTimeout(30);
        this.template = jdbcTemplate;
    }

    public void resetSource(Properties properties) {
        source(properties);
        File file = new File(App.DBSOURCE_FILE);
        if(file.exists()){
            Assert.isTrue(file.delete(),String.format("删除源配置文件[%s]失败",App.DBSOURCE_FILE));
        }
        FileOutputStream fos = null;
        try {
            Assert.isTrue(file.createNewFile(),"创建新的配置文件[dbresource.xml]失败");
            fos = new FileOutputStream(file);
            PropertiesPersister pp = new DefaultPropertiesPersister();
            pp.storeToXml(properties,fos,"dbresource","utf8");
            logger.info("重新生成配置源成功");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
