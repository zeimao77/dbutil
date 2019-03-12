package top.zeimao77.dbutil.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import top.zeimao77.dbutil.context.AppResourceContext;
import top.zeimao77.dbutil.export.Column;
import top.zeimao77.dbutil.export.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据表格页面
 */
public class TabView {

    @FXML
    private TableView tableView;

    private List<Map<String, Object>> data;

    public void setHeader(String serviceId) {
        Table table = AppResourceContext.getTableFactory().getTableByKey(serviceId);
        tableView.getColumns().clear();
        for(Map.Entry<String, Column> entry : table.getColumnMap().entrySet()) {
            Column column = entry.getValue();
            TableColumn tableColumn = new TableColumn(column.getTitle());
            tableColumn.setMinWidth(column.getWidth()*10);
            tableColumn.setCellValueFactory(new MapValueFactory<>(column.getField()));
            tableView.getColumns().add(tableColumn);
        }
        this.setData(null);
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data==null?new ArrayList<>(0):data;
        ObservableList<Map<String,Object>> list = FXCollections.observableArrayList(this.data);
        tableView.setItems(list);
    }

    public void refreshView(String serviceId,List<Map<String, Object>> data) {
        this.setHeader(serviceId);
        this.setData(data);
    }

}
