package top.zeimao77.dbutil.export;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class Column implements Comparable<Column> {

    @XmlAttribute
    private Integer index;
    @XmlAttribute
    private String title;
    @XmlAttribute
    private String field;
    @XmlAttribute
    private Integer width;
    @XmlAttribute
    private String format;

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @XmlTransient
    public Integer getIndex() {
        return index;
    }

    @XmlTransient
    public String getTitle() {
        return title;
    }

    @XmlTransient
    public String getField() {
        return field;
    }

    @XmlTransient
    public Integer getWidth() {
        return width;
    }

    @XmlTransient
    public String getFormat() {
        return format;
    }

    @Override
    public int compareTo(Column o) {
        return Integer.compare(this.getIndex(),o.getIndex());
    }
}
