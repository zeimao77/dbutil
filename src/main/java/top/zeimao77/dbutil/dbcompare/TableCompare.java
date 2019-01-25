package top.zeimao77.dbutil.dbcompare;

import top.zeimao77.dbutil.model.MysqlTable;
import top.zeimao77.dbutil.model.Column;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableCompare {

    public static MysqlTable parse(String createSql) {
        BufferedReader sReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(createSql.getBytes())));
        Pattern tableNamePattern = Pattern.compile("CREATE TABLE `(.*)` \\(");
        MysqlTable table = new MysqlTable();
        try {
            String lineStr = sReader.readLine();
            Matcher matcher1 = tableNamePattern.matcher(lineStr);
            if(matcher1.matches()) {
                String tableName = matcher1.group(1);
                table.setTableName(tableName);
                while(!(lineStr = sReader.readLine()).startsWith(") ENGINE=")) {
                    if(lineStr.indexOf("PRIMARY KEY (") > 0) {
                        String primaryKey = lineStr.replace("PRIMARY KEY (`","").replace("`)","");
                        table.setPrimaryKey(primaryKey);
                    }else if(lineStr.startsWith("  `")) {
                        Column column = new Column();
                        String[] columnContext = lineStr.split(" ");
                        for(int i=0;i<columnContext.length;i++) {
                            String context = columnContext[i];
                            if(context.startsWith("`")){
                                context = context.replaceAll("`","");
                                column.setFieldName(context);
                                column.setType(columnContext[i+1]);
                            }
                            if(context.equalsIgnoreCase("COMMENT")) {
                                column.setComment(columnContext[i+1].replaceAll("'",""));
                            }
                            if(context.equalsIgnoreCase("DEFAULT")) {
                                column.setDefaultValue(columnContext[i+1]);
                            }
                        }
                        if(lineStr.indexOf("NOT NULL") > 0) {
                            column.setNotNull(true);
                        }else {
                            column.setNotNull(false);
                        }
                        table.addColumn(column);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }





}
