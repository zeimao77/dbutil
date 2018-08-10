package top.zeimao77.dbutil.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import top.zeimao77.dbutil.export.Column;

import java.util.List;
import java.util.Map;

public class TabView {

    @FXML
    private TableView tableView;

    public void setHeader(Map<String,Column> columns) {
        for(Map.Entry<String, Column> entry : columns.entrySet()) {
            Column column = entry.getValue();
            TableColumn tableColumn = new TableColumn(column.getTitle());
            tableColumn.setMinWidth(column.getWidth()*10);
            tableColumn.setCellValueFactory(
                    new MapValueFactory<>(column.getField()));
            tableView.getColumns().add(tableColumn);
        }
    }

    public void setBody(List<Map<String, Object>> data) {
        ObservableList<Map<String,Object>> list = FXCollections.observableArrayList(data);
        tableView.setItems(list);
    }



}
