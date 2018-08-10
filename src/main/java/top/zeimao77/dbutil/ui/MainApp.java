package top.zeimao77.dbutil.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import top.zeimao77.dbutil.controller.DbSourceConf;
import top.zeimao77.dbutil.controller.DbSourceConfig;
import top.zeimao77.dbutil.controller.Mysql;
import top.zeimao77.dbutil.controller.Root;

import java.io.IOException;
import java.util.Properties;

public class MainApp extends Application {

    public static Stage rootStage;
    private BorderPane rootPane;
    public Mysql mysql;

    public static void main(String[] args){
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage){
        MainApp.rootStage = primaryStage;
        primaryStage.setTitle("ZEIMAO77_DBUTIL");
        try {
            initRootStage();
            initMysql();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initRootStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("root.fxml"));
        rootPane = fxmlLoader.load();
        Root root = fxmlLoader.getController();
        root.setMainApp(this);
        Scene scene = new Scene(rootPane);
        rootStage.setScene(scene);
        rootStage.show();
    }

    private void initMysql() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("mysql.fxml"));
        AnchorPane mysqlPane = fxmlLoader.load();
        mysql = fxmlLoader.getController();
        rootPane.setCenter(mysqlPane);
        try{
            mysql.init();
        }catch (Exception e){
            System.out.println("初始化失败");
            e.printStackTrace();
        }
    }

    public void showDbSourceConfigPane(Properties properties) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("dbsourceconfig.fxml"));
        AnchorPane page = fxmlLoader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("配置数据源");
        dialogStage.initOwner(rootStage);
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
        fxmlLoader.setLocation(MainApp.class.getClassLoader().getResource("dbsourceconf.fxml"));
        AnchorPane page = fxmlLoader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("配置数据源");
        dialogStage.initOwner(rootStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        DbSourceConf dbSourceConf = fxmlLoader.getController();
        dbSourceConf.setMainApp(this);
        dbSourceConf.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }


}
