package top.zeimao77.dbutil.export;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

@XmlType(propOrder = { "serviceId","sql", "columnMap"})
public class Table {

    @XmlAttribute
    private String tableName;

    @XmlAttribute
    private String serviceId;

    private String sql;

    @XmlJavaTypeAdapter(ColumnMapAdapter.class)
    @XmlElement(name="columnlist")
    private Map<String,Column> columnMap;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setColumnMap(Map<String, Column> columnMap) {
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

    public String getSql() {
        return sql;
    }
    @XmlTransient
    public Map<String, Column> getColumnMap() {
        return columnMap;
    }


}