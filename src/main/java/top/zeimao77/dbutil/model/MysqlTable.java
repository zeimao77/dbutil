package top.zeimao77.dbutil.model;

import java.util.ArrayList;
import java.util.List;

public class MysqlTable {

    private String tableName;
    private String primaryKey;
    private List<Column> columnList = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    public boolean addColumn(Column column) {
        return this.columnList.add(column);
    }

    public List<String> compare(MysqlTable table) {
        List<String> resultList = new ArrayList<>();
        if(!table.getTableName().equalsIgnoreCase(this.getTableName())) {
            return null;
        }
        /*if(!table.getPrimaryKey().equalsIgnoreCase(this.getPrimaryKey())) {
            resultList.add(String.format("主键不同[%s|%s]",this.getPrimaryKey(),table.getPrimaryKey()));
        }*/
        for(Column c1 : this.getColumnList()) {
            boolean flag = false;
            for(Column c2 : table.getColumnList()) {
                if(c1.getFieldName().equalsIgnoreCase(c2.getFieldName())) {
                    flag = true;
                    String compareResult = c1.compare(c2);
                    if(compareResult != null) {
                        resultList.add(compareResult);
                    }
                    break;
                }
            }
            if(!flag) {
                resultList.add(String.format(">>>左表存在字段[%s]，右表中不存在",c1.getFieldName()));
            }
        }
        for(Column c1 : table.getColumnList()) {
            boolean flag = false;
            for(Column c2 : this.getColumnList()) {
                if(c1.getFieldName().equalsIgnoreCase(c2.getFieldName())) {
                    flag = true;
                }
            }
            if(!flag) {
                resultList.add(String.format("<<<右表存在字段[%s]，左表中不存在",c1.getFieldName()));
            }
        }
        if(resultList.size()<1) {
            return null;
        }
        return resultList;
    }

    public void print() {
        System.out.println(getTableName() +"[PK]: " + getPrimaryKey());
        getColumnList().forEach(System.out::println);
    }

}
