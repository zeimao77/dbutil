package top.zeimao77.dbutil.export;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class ColumnMapAdapter extends XmlAdapter<Column[],Map<String,Column>> {

    @Override
    public Map<String, Column> unmarshal(Column[] v) {
        Map<String,Column> map = new HashMap<>();
        for(Column c:v){
            map.put(c.getField(),c);
        }
        return map;
    }

    @Override
    public Column[] marshal(Map<String, Column> v) {
        Column[] columns = new Column[v.size()];
        int i=0;
        for (Map.Entry<String, Column> entry : v.entrySet()) {
            columns[i] = entry.getValue();
            i++;
        }
        return columns;
    }

}
