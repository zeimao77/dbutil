package top.zeimao77.dbutil.comdata;

import top.zeimao77.dbutil.export.TableFactory;

public class TableFac {

    private static TableFactory tableFactory;

    public static void setTableFactory(TableFactory tableFactory) {
        TableFac.tableFactory = tableFactory;
    }

    public static TableFactory getTableFactory() {
        return tableFactory;
    }
}
