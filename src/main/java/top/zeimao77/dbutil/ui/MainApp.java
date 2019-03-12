package top.zeimao77.dbutil.ui;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import top.zeimao77.dbutil.context.App;
import top.zeimao77.dbutil.context.ControllerUiContext;
import top.zeimao77.dbutil.controller.*;
import top.zeimao77.dbutil.export.TableFactory;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    private static ControllerUiContext controllerUiContext;

    public static void main(String[] args){
        controllerUiContext = new ControllerUiContext();
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage){
        controllerUiContext.setMainApp(this);
        try {
            app_init();
            controllerUiContext.setRootStage(primaryStage);
            primaryStage.setWidth(650);
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
        controllerUiContext.setRoot(root);
        Scene scene = new Scene(rootPane);
        controllerUiContext.getRootStage().setScene(scene);
        controllerUiContext.getRootStage().show();
    }

    private void initTabPane() throws IOException {
        //初始化导出界面
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/mysql.fxml"));
        AnchorPane mysqlPane = fxmlLoader.load();
        Mysql mysql = fxmlLoader.getController();
        controllerUiContext.setMysql(mysql);
        controllerUiContext.getRoot().getExportPane().setContent(mysqlPane);
        //初始化导入界面
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/mysqlimport.fxml"));
        AnchorPane importPane = fxmlLoader.load();
        MysqlImport mysqlImport = fxmlLoader.getController();
        controllerUiContext.setMysqlImport(mysqlImport);
        controllerUiContext.getRoot().getImportPane().setContent(importPane);
        //初始化表格界面
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/tabview.fxml"));
        AnchorPane tabViewPane = fxmlLoader.load();
        TabView tabView = fxmlLoader.getController();
        controllerUiContext.setTabView(tabView);
        controllerUiContext.getRoot().getDataPane().setContent(tabViewPane);
        mysql.init();
        mysqlImport.init();
        //初始化库字段比较页面
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/dbcompare.fxml"));
        AnchorPane comparePane = fxmlLoader.load();
        //DbCompare dbCompare = fxmlLoader.getController();
        controllerUiContext.getRoot().getComparePane().setContent(comparePane);

    }

    public void showDbSourceConfigPane(JSONObject properties) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("fxml/dbsourceconfig.fxml"));
        AnchorPane page = null;
        try {
            page = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage dialogStage = new Stage();
        dialogStage.setTitle("配置数据源");
        dialogStage.initOwner(controllerUiContext.getRootStage());
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
        dialogStage.initOwner(controllerUiContext.getRootStage());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setWidth(500);
        dialogStage.setHeight(400);
        dialogStage.setResizable(false);
        dialogStage.setScene(scene);
        DbSourceConf dbSourceConf = fxmlLoader.getController();
        dbSourceConf.init();
        dbSourceConf.setStage_dialog(dialogStage);
        dialogStage.showAndWait();
    }

    /**
     * 初始化 数据源  读入表格配置
     * @throws IOException
     */
    private void app_init() throws IOException {
        File file = new File(App.TABLECONFIG_FILE);
        if(!file.exists()) {
            Assert.isTrue(file.createNewFile(),String.format("创建新的配置文件[%s]失败",App.TABLECONFIG_FILE));
            InputStream is =getClass().getClassLoader().getResourceAsStream("tableconfig.xml");
            FileCopyUtils.copy(is,new FileOutputStream(file));
            logger.info(String.format("请先修改配置文件[%s]",App.TABLECONFIG_FILE));
            System.exit(0);
        }
        new TableFactory(file);
    }

    public static ControllerUiContext getControllerUiContext() {
        return controllerUiContext;
    }

}
