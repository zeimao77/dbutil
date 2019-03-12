package top.zeimao77.dbutil.context;

import com.alibaba.fastjson.JSONArray;
import top.zeimao77.dbutil.export.TableFactory;

public class AppResourceContext {

    //配置
    private static TableFactory tableFactory;

    //数据源列表
    private static JSONArray sourceList;

    public static TableFactory getTableFactory() {
        return tableFactory;
    }

    public static void setTableFactory(TableFactory tableFactory) {
        AppResourceContext.tableFactory = tableFactory;
    }

    public static JSONArray getSourceList() {
        return sourceList;
    }

    public static void setSourceList(JSONArray sourceList) {
        AppResourceContext.sourceList = sourceList;
    }
}
