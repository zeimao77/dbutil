package top.zeimao77.dbutil.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import top.zeimao77.dbutil.dbcompare.CompareServiceImpl;
import top.zeimao77.dbutil.model.MysqlTable;
import top.zeimao77.dbutil.ui.MainApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public class DbCompare {

    @FXML
    private TextArea textArea_dbsourceConfig;

    @FXML
    private TextArea textArea_includeTable;

    @FXML
    private TextArea textArea_excludeTable;

    @FXML
    private Button button_compare;

    public void setButton_compare_click() throws FileNotFoundException {
        String dbsourceConfig = textArea_dbsourceConfig.getText();
        String includeTableText = textArea_includeTable.getText();
        String excludeTableText = textArea_excludeTable.getText();
        String[] includeTable = null;
        String[] excludeTable = null;
        if(StringUtils.isEmpty(includeTableText)) {
            if(!StringUtils.isEmpty(excludeTableText)) {
                excludeTable = excludeTableText.split(",");
            }
        }else {
            includeTable = includeTableText.split(",");
        }
        JSONObject dbsourceJson = JSON.parseObject(dbsourceConfig);
        System.out.println(dbsourceJson);
        String url1 = dbsourceJson.getString("url1");
        String url2 = dbsourceJson.getString("url2");
        String dbname1 = dbsourceJson.getString("dbname1");
        String dbname2 = dbsourceJson.getString("dbname2");
        String username1 = dbsourceJson.getString("username1");
        String username2 = dbsourceJson.getString("username2");
        String password1 = dbsourceJson.getString("password1");
        String password2 = dbsourceJson.getString("password2");
        List<MysqlTable> tableList1 = CompareServiceImpl.getdbTables(url1,dbname1,username1,password1);
        List<MysqlTable> tableList2 = CompareServiceImpl.getdbTables(url2,dbname2,username2,password2);
        if(includeTable != null) {
            BiConsumer<List<MysqlTable>,String[]> con = (o1,o2) -> {
                for(Iterator<MysqlTable> ite = o1.iterator();ite.hasNext();) {
                    boolean flag = true;
                    MysqlTable table = ite.next();
                    for(String str : o2) {
                        str = str.replaceAll("`","");
                        if(table.getTableName().equalsIgnoreCase(str)) {
                            flag = false;
                            break;
                        }
                    }
                    if(flag) {
                        ite.remove();
                    }
                }
            };
            con.accept(tableList1,includeTable);
            con.accept(tableList2,includeTable);
        }
        if(excludeTable != null) {
            BiConsumer<List<MysqlTable>,String[]> con = (o1,o2) ->{
                for(Iterator<MysqlTable> ite = o1.iterator();ite.hasNext();) {
                    MysqlTable table = ite.next();
                    for(String str : o2) {
                        str = str.replaceAll("`","");
                        if(table.getTableName().equalsIgnoreCase(str)) {
                            ite.remove();
                            break;
                        }
                    }
                }
            };
            con.accept(tableList1,excludeTable);
            con.accept(tableList2,excludeTable);
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导出位置");
        String homePath = "/";
        if(System.getProperty("os.name").startsWith("Win")) {
            homePath = "C:"+ SystemPropertyUtils.resolvePlaceholders("${HOMEPATH}")+"\\Desktop";
        }else if(System.getProperty("os.name").startsWith("Linux")) {
            homePath = "/home";
        }
        fileChooser.setInitialDirectory(new File(homePath));
        fileChooser.setInitialFileName("dbcompare.log");
        File file = fileChooser.showSaveDialog(MainApp.getControllerUiContext().getRootStage());
        PrintStream printStream = new PrintStream(new FileOutputStream(file));
        printStream.println("左库："+url1+dbname1);
        printStream.println("右库："+url2+dbname2);
        printStream.println("=================================");
        CompareServiceImpl.print(printStream,tableList1,tableList2);
        printStream.close();
    }

    public void button_example_click() {
        this.textArea_dbsourceConfig.setText("{\n" +
                "  \"url1\": \"jdbc:mysql://172.17.13.51:3308/\",\n" +
                "  \"url2\": \"jdbc:mysql://172.17.13.80:3306/\",\n" +
                "  \"dbname1\": \"order\",\n" +
                "  \"dbname2\": \"order\",\n" +
                "  \"username1\": \"root\",\n" +
                "  \"username2\": \"root\",\n" +
                "  \"password1\": \"ULP3Z68ejCbY4pGgwasN\",\n" +
                "  \"password2\": \"123456\"\n" +
                "}");
    }

}
