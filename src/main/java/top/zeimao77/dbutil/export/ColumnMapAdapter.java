package top.zeimao77.dbutil.export;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.*;

public class ColumnMapAdapter extends XmlAdapter<Column[],Map<String,Column>> {

    @Override
    public LinkedHashMap<String, Column> unmarshal(Column[] v) {
        Arrays.sort(v);
        LinkedHashMap<String,Column> treeMap = new LinkedHashMap<>(v.length);

        for(int i=0;i<v.length;i++) {
            treeMap.put(v[i].getField(),v[i]);
        }
        return treeMap;
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
