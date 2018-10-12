package top.zeimao77.dbutil.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.Assert;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.PropertiesPersister;
import top.zeimao77.dbutil.comdata.App;
import top.zeimao77.dbutil.comdata.ControllerUi;
import top.zeimao77.dbutil.controller.*;
import top.zeimao77.dbutil.export.TableFactory;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    private static ControllerUi controllerUi;

    public static void main(String[] args){
        controllerUi = new ControllerUi();
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage){
        controllerUi.setMainApp(this);
        try {
            app_init();
            controllerUi.setRootStage(primaryStage);
            primaryStage.setTitle("ZEIMAO77_DBUTIL");
            initRootStage();
            initTabPane();
        } catch (IOException e) {
            logger.log(Level.WARNING,"配置失败，需要重新检查配置文件");
            e.printStackTrace();
        }
    }

    private void initRootStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/root.fxml"));
        BorderPane rootPane = fxmlLoader.load();
        Root root = fxmlLoader.getController();
        root.init();
        controllerUi.setRoot(root);
        Scene scene = new Scene(rootPane);
        controllerUi.getRootStage().setScene(scene);
        controllerUi.getRootStage().show();
    }

    private void initTabPane() throws IOException {
        //初始化导出界面
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/mysql.fxml"));
        AnchorPane mysqlPane = fxmlLoader.load();
        Mysql mysql = fxmlLoader.getController();
        controllerUi.setMysql(mysql);
        controllerUi.getRoot().getExportPane().setContent(mysqlPane);
        //初始化导入界面
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/mysqlimport.fxml"));
        AnchorPane importPane = fxmlLoader.load();
        MysqlImport mysqlImport = fxmlLoader.getController();
        controllerUi.setMysqlImport(mysqlImport);
        controllerUi.getRoot().getImportPane().setContent(importPane);
        //初始化表格界面
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/tabview.fxml"));
        AnchorPane tabViewPane = fxmlLoader.load();
        TabView tabView = fxmlLoader.getController();
        controllerUi.setTabView(tabView);
        controllerUi.getRoot().getDataPane().setContent(tabViewPane);
        mysql.init();
        mysqlImport.init();
    }

    public void showDbSourceConfigPane(Properties properties) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/dbsourceconfig.fxml"));
        AnchorPane page = fxmlLoader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("配置数据源");
        dialogStage.initOwner(controllerUi.getRootStage());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        DbSourceConfig dbSourceConfig = fxmlLoader.getController();
        dbSourceConfig.setDialogStage(dialogStage);
        dbSourceConfig.setData(properties);
        dialogStage.showAndWait();
    }

    public void showDbSourceConfPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/dbsourceconf.fxml"));
        AnchorPane page = fxmlLoader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("配置数据源");
        dialogStage.initOwner(controllerUi.getRootStage());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        DbSourceConf dbSourceConf = fxmlLoader.getController();
        dbSourceConf.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }

    private void app_init() throws IOException {
        File file = new File(App.DBSOURCE_FILE);
        if(file.exists()) {
            PropertiesPersister pp = new DefaultPropertiesPersister();
            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(file);
            pp.loadFromXml(properties,fileInputStream);
            DriverManagerDataSource source = new DriverManagerDataSource();
            source.setDriverClassName(properties.getProperty("driver"));
            source.setUrl(properties.getProperty("url"));
            source.setUsername(properties.getProperty("username"));
            source.setPassword(properties.getProperty("password"));
            JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
            jdbcTemplate.setQueryTimeout(30);
            controllerUi.setTemplate(jdbcTemplate);
        }
        file = new File(App.TABLECONFIG_FILE);
        if(!file.exists()) {
            Assert.isTrue(file.createNewFile(),String.format("创建新的配置文件[%s]失败",App.TABLECONFIG_FILE));
            InputStream is =getClass().getClassLoader().getResourceAsStream("tableconfig.xml");
            FileCopyUtils.copy(is,new FileOutputStream(file));
            logger.info(String.format("请先修改配置文件[%s]",App.TABLECONFIG_FILE));
            System.exit(0);
        }
        new TableFactory(file);
    }
    public static ControllerUi getControllerUi() {
        return controllerUi;
    }
}
