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
import org.springframework.util.*;
import top.zeimao77.dbutil.context.AppResourceContext;
import top.zeimao77.dbutil.export.Table;
import top.zeimao77.dbutil.export.XlsxView;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 引出页面
 */
public class Mysql implements TabViewAble{

    private static final Logger logger = Logger.getLogger(Mysql.class.getName());

    @FXML
    private TextArea textArea_sql;

    @FXML
    private TextArea textArea_res;

    @FXML
    private ChoiceBox choiceBox_config;

    @FXML
    private Button button_select;

    @FXML
    private ProgressIndicator pro_indicator1;


    private String serviceId;

    private void setServiceId(String serviceId) {
        this.serviceId = serviceId;
        MainApp.getControllerUiContext().getTabView().setHeader(this.serviceId);
        this.setSelect_data(new ArrayList<>(1));
    }

    private void setSelect_data(List<Map<String, Object>> select_data) {
        this.select_data = select_data;
        MainApp.getControllerUiContext().getTabView().setData(this.select_data);
    }

    volatile private List<Map<String, Object>> select_data = new ArrayList<>(1);

    private final Service<Integer> query_service = new Service<Integer>() {
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
                            sql = AppResourceContext.getTableFactory().getTableByKey(serviceId).getSelect();
                            logger.info("[SQL:]" + sql);
                            Assert.isTrue(StringUtils.hasLength(sql.trim()),"SQL语句不允许为空");
                        }
                        setSelect_data(MainApp.getControllerUiContext().getTemplate().queryForList(sql));
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
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("fxml/tabview.fxml"));
        AnchorPane pane = fxmlLoader.load();
        TabView tabView = fxmlLoader.getController();
        Table table = AppResourceContext.getTableFactory().getTableByKey(this.serviceId);
        dataTableView.setTitle(table.getTableName());
        tabView.setHeader(this.serviceId);
        tabView.refreshView(this.serviceId,this.select_data);
        Scene scene = new Scene(pane);
        dataTableView.setScene(scene);
        dataTableView.showAndWait();
    }

    public void button_select_click() {
        pro_indicator1.progressProperty().bind(query_service.progressProperty());
        query_service.restart();
    }

    public void button_export_click() {
        Assert.notNull(serviceId,"请选择业务");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导出位置");
        String homePath = "/";
        try{
            if(System.getProperty("os.name").startsWith("Win")) {
                homePath = "C:"+ SystemPropertyUtils.resolvePlaceholders("${HOMEPATH}")+"\\Desktop";
            }else if(System.getProperty("os.name").startsWith("Linux")) {
                homePath = "/home";
            }
        }catch ( IllegalArgumentException e) {
            homePath = "/";
        }
        fileChooser.setInitialDirectory(new File(homePath));
        fileChooser.setInitialFileName(serviceId+".xlsx");
        File file = fileChooser.showSaveDialog(MainApp.getControllerUiContext().getRootStage());
        if(file != null) {
            XlsxView xlsxView = new XlsxView();
            Workbook workbook = xlsxView.create(AppResourceContext.getTableFactory().getTableByKey(serviceId),select_data);
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

    public void init()  {
        choiceBox_config.setItems(FXCollections.observableArrayList(AppResourceContext.getTableFactory().serviceList()));
        logger.info("读取业务配置成功");
        choiceBox_config.getSelectionModel().selectedItemProperty().addListener((o1,o2,o3)->{
            this.setServiceId(o3.toString());
            this.select_data.clear();
        });
        choiceBox_config.getSelectionModel().select(0);
    }


    @Override
    public void refresh() {
        MainApp.getControllerUiContext().getTabView().refreshView(this.serviceId,this.select_data);
    }

}
