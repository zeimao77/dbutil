package top.zeimao77.dbutil.controller;

import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.transaction.annotation.Transactional;
import top.zeimao77.dbutil.comdata.TableFac;
import top.zeimao77.dbutil.export.Table;
import top.zeimao77.dbutil.export.TableFactory;
import top.zeimao77.dbutil.export.XlsxView;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private TextArea textArea_res;

    @FXML
    private Button button_import;

    @FXML
    private ProgressIndicator pro_indicator1;

    private String serviceId;

    /**
     * 插入
     */
    private final Service<Integer> insert_service = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() {
                    try{
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
                            System.out.println(sql);
                        }
                    }catch (Exception e){
                        textArea_res.setText(String.format("[导入异常]:%s",e.getMessage()));
                        updateProgress(0,1);
                        e.printStackTrace();
                    }finally {
                        button_import.setDisable(false);
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
        textField_file.setDisable(true);
        choiceBox_config.setItems(FXCollections.observableArrayList(TableFac.getTableFactory().serviceList()));
        choiceBox_config.getSelectionModel().selectedItemProperty().addListener((o1,o2,o3)->{
            textArea_res.setDisable(true);
            this.setServiceId(o3.toString());
            this.textField_file.setText(null);
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

    @Transactional
    public void button_insert_click() {
        pro_indicator1.progressProperty().bind(insert_service.progressProperty());
        insert_service.restart();
    }

    @Override
    public void refresh() {
        MainApp.getControllerUi().getTabView().refreshView(this.serviceId,this.insert_data);
    }

}
