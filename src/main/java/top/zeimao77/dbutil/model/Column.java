package top.zeimao77.dbutil.model;

public class Column {

    private String fieldName;
    private String type;
    private String defaultValue;
    private String comment;
    private boolean notNull;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getDefaultValue() {
        return defaultValue==null?"NULL":defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getComment() {
        return comment == null?"[NULL]":comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String compare(Column column) {
        if(column.fieldName.equalsIgnoreCase(this.getFieldName())) {
            StringBuilder sBuilder = new StringBuilder();
            if(!getType().equalsIgnoreCase(column.getType())) {
                sBuilder.append(String.format("类型不同[%s|%s];",this.getType(),column.getType()));
            }
            if(!getDefaultValue().equals(column.getDefaultValue())) {
                sBuilder.append(String.format("默认值不同[%s|%s];",this.getDefaultValue(),column.getDefaultValue()));
            }
            if(this.isNotNull() != column.isNotNull()) {
                sBuilder.append(String.format("状态不同[%s|%s];",this.isNotNull(),column.isNotNull()));
            }
            if(sBuilder.length() > 0) {
                return getFieldName()+"结果："+sBuilder.toString();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Column{" +
                "fieldName='" + fieldName + '\'' +
                ", type='" + type + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", comment='" + comment + '\'' +
                ", notNull=" + notNull +
                '}';
    }
}
