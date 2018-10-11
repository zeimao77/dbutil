package top.zeimao77.dbutil.controller;

import com.alibaba.fastjson.JSON;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import top.zeimao77.dbutil.comdata.TableFac;
import top.zeimao77.dbutil.export.Table;
import top.zeimao77.dbutil.export.TableFactory;
import top.zeimao77.dbutil.export.XlsxView;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlImport implements TabViewAble{

    private static final Logger logger = Logger.getLogger(TableFactory.class.getName());
    @FXML
    private TextField textField_file;

    @FXML
    private ChoiceBox choiceBox_config;

    @FXML
    private ChoiceBox choiceBox_insertMode;

    @FXML
    private TextArea textArea_res;

    @FXML
    private Button button_import;

    @FXML
    private ProgressIndicator pro_indicator1;

    private String serviceId;

    /**
     * insert_mode = 0 异常回滚
     * insert_mode = 1 异常停止
     * insert_mode = 2 异常继续
     * insert_mode = 3 仅打印SQL
     */
    private Integer insert_mode = 3;

    /**
     * 插入
     */
    private final Service<Integer> insert_service = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws SQLException {
            textArea_res.setText("导入过程中，请勿操作......");
            JdbcTemplate template = MainApp.getControllerUi().getTemplate();
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(template.getDataSource());
            TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
            try{
                choiceBox_insertMode.setDisable(true);
                button_import.setDisable(true);
                Table table = TableFac.getTableFactory().getTableByKey(serviceId);
                String sqlTemplate = table.getInsert();
                for(int i=0;i<insert_data.size();i++) {
                    Map<String,Object> map = insert_data.get(i);
                    String sql = sqlTemplate.trim();
                    Pattern pattern = Pattern.compile("\\$\\{([\\S]+?)\\}");
                    Matcher matcher = pattern.matcher(sql);
                    while(matcher.find()) {
                        String field = matcher.group(1).trim();
                        sql = matcher.replaceFirst("'"+map.get(field).toString()+"'");
                        matcher = pattern.matcher(sql);
                    }
                    updateProgress(i+1,insert_data.size());
                    if(insert_mode == 3) {
                        System.out.println(sql);
                    }else {
                        try{
                            template.update(sql);
                        }catch (Exception e) {
                            textArea_res.setText(String.format("[行:] %d\n",i));
                            textArea_res.appendText(String.format("[SQL:] %s\n",sql));
                            textArea_res.appendText(String.format("[数据:] %s\n",JSON.toJSONString(map)));
                            textArea_res.appendText(String.format("[异常:] %s\n",e.getMessage()));
                            switch (insert_mode) {
                                case 0:
                                    logger.warning("即将异常回滚");
                                    transactionManager.rollback(transactionStatus);
                                    return 10002;
                                case 1:
                                    System.out.println(String.format("-- 异常停止于第[%d]行",i));
                                    System.out.println("-- [数据]："+JSON.toJSONString(map));
                                    System.out.println(sql);
                                    transactionManager.commit(transactionStatus);
                                    return 10002;
                                case 2:
                                    System.out.println("-- 数据插入失败在第[%d]行");
                                    System.out.println(sql);
                            }
                        }
                    }
                }
                transactionManager.commit(transactionStatus);
                textArea_res.setText("已经全部导入");
            }finally {
                choiceBox_insertMode.setDisable(false);
                button_import.setDisable(false);
                template.getDataSource().getConnection().setAutoCommit(true);
            }
            return 10001;
            }
        };
        }
    };

    private void setServiceId(String serviceId) {
        this.serviceId = serviceId;
        MainApp.getControllerUi().getTabView().setHeader(this.serviceId);
        this.setInsert_data(new ArrayList<>(1));
    }

    volatile private List<Map<String, Object>> insert_data;

    public void init(){
        textArea_res.setWrapText(true);
        textField_file.setDisable(true);
        choiceBox_insertMode.setItems(FXCollections.observableArrayList(Arrays.asList("异常回滚","异常停止","异常继续","仅打印SQL")));
        choiceBox_insertMode.getSelectionModel().selectedIndexProperty().addListener((o1,o2,o3)->{
            this.insert_mode = o3.intValue();
        });
        choiceBox_insertMode.getSelectionModel().select(3);
        choiceBox_config.setItems(FXCollections.observableArrayList(TableFac.getTableFactory().serviceList()));
        choiceBox_config.getSelectionModel().selectedItemProperty().addListener((o1,o2,o3)->{
            this.setServiceId(o3.toString());
            this.textField_file.setText(null);
            this.pro_indicator1.setProgress(0);
            this.textArea_res.clear();
            this.insert_data.clear();
        });

        choiceBox_config.getSelectionModel().select(0);
    }

    public void setInsert_data(List<Map<String, Object>> insert_data) {
        this.insert_data = insert_data;
        MainApp.getControllerUi().getTabView().refreshView(this.serviceId,this.insert_data);
    }

    public void chooseXlsx() throws IOException, InvalidFormatException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导入Excel");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX","*.xlsx")
        );
        File file = fileChooser.showOpenDialog(MainApp.getControllerUi().getRootStage());
        if(file != null && file.exists()) {
            textField_file.setText(file.getPath());
            Table table = TableFac.getTableFactory().getTableByKey(this.serviceId);
            List<Map<String,Object>> insertData = null;
            try{
                insertData = XlsxView.parseXlsx(file,table);
                this.textArea_res.setText(String.format("成功读入[%d]行",insertData.size()));
            }catch (Exception e) {
                logger.log(Level.WARNING,String.format("解析失败：%s",file.getName()));
                this.textArea_res.setText(String.format("解析失败：%s",file.getName()));
                return;
            }
            this.setInsert_data(insertData);
        }
    }

    public void button_insert_click() {
        textArea_res.clear();
        pro_indicator1.progressProperty().bind(insert_service.progressProperty());
        insert_service.restart();
    }

    @Override
    public void refresh() {
        MainApp.getControllerUi().getTabView().refreshView(this.serviceId,this.insert_data);
    }

}
