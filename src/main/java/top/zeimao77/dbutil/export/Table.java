package top.zeimao77.dbutil.export;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.LinkedHashMap;

@XmlType(propOrder = { "serviceId","select","insert", "columnMap"})
public class Table {

    @XmlAttribute
    private String tableName;

    @XmlAttribute
    private String serviceId;

    private String select;

    private String insert;

    @XmlJavaTypeAdapter(ColumnMapAdapter.class)
    @XmlElement(name="columnlist")
    private LinkedHashMap<String,Column> columnMap;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    public void setColumnMap(LinkedHashMap<String, Column> columnMap) {
        this.columnMap = columnMap;
    }

    @XmlTransient
    public String getTableName() {
        return tableName;
    }
    @XmlTransient
    public String getServiceId() {
        return serviceId;
    }

    @XmlTransient
    public LinkedHashMap<String, Column> getColumnMap() {
        return columnMap;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public void setInsert(String insert) {
        this.insert = insert;
    }

    public String getSelect() {
        return select;
    }

    public String getInsert() {
        return insert;
    }
}