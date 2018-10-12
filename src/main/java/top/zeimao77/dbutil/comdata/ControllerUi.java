package top.zeimao77.dbutil.comdata;

import javafx.stage.Stage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import top.zeimao77.dbutil.controller.Mysql;
import top.zeimao77.dbutil.controller.MysqlImport;
import top.zeimao77.dbutil.controller.Root;
import top.zeimao77.dbutil.controller.TabView;
import top.zeimao77.dbutil.ui.MainApp;

public class ControllerUi {

    private JdbcTemplate template;

    private MainApp mainApp;
    private Stage rootStage;
    private Mysql mysql;
    private TabView tabView;
    private MysqlImport mysqlImport;
    private Root root;


    public void setRootStage(Stage rootStage) {
        this.rootStage = rootStage;
    }

    public void setMysql(Mysql mysql) {
        this.mysql = mysql;
    }

    public void setTabView(TabView tabView) {
        this.tabView = tabView;
    }

    public void setMysqlImport(MysqlImport mysqlImport) {
        this.mysqlImport = mysqlImport;
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    public Stage getRootStage() {
        return rootStage;
    }

    public Mysql getMysql() {
        return mysql;
    }

    public TabView getTabView() {
        return tabView;
    }

    public MysqlImport getMysqlImport() {
        return mysqlImport;
    }

    public Root getRoot() {
        return root;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    public JdbcTemplate getTemplate() {
        Assert.isTrue(template != null,"数据源为空");
        return template;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
