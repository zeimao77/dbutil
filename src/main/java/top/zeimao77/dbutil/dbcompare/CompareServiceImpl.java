package top.zeimao77.dbutil.dbcompare;

import top.zeimao77.dbutil.dbutil.JdbcRunSql;
import top.zeimao77.dbutil.model.MysqlTable;
import top.zeimao77.dbutil.model.Sql;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CompareServiceImpl {

    static String url = "jdbc:mysql://www.22cq.top:3306/";
    static String username = "root";
    static String password = "123";

    static String SHOWTABLE = "SHOW TABLES;";
    static String CREATETABLE = "SHOW CREATE TABLE `%s`;";


    public static void main(String[] args) {
        List<MysqlTable> tableList1 = getdbTables(url,"guitar",username,password);
        List<MysqlTable> tableList2 = getdbTables(url,"mytest",username,password);
        print(System.out,tableList1,tableList2);
    }

    public static void print(PrintStream printStream,List<MysqlTable> tableList1,List<MysqlTable> tableList2) {
        for(MysqlTable table1 : tableList1) {
            boolean flag = false;
            for(MysqlTable table2 : tableList2) {
                if(table1.getTableName().equalsIgnoreCase(table2.getTableName())) {
                    flag = true;
                    List compareTable = table1.compare(table2);
                    if(compareTable != null) {
                        printStream.println(String.format("==============[%s有差异且比较结果如下]===============",table1.getTableName()));
                        compareTable.forEach(printStream::println);
                        printStream.println(String.format("==================[%s表的比较结束]==================",table1.getTableName()));
                    }
                    break;
                }
            }
            if(!flag) {
                printStream.println(String.format(">>>>>>>>>>>>>>左库中存在表[%s]右库中未找到",table1.getTableName()));
            }
        }
        for(MysqlTable table2 : tableList2) {
            boolean flag = false;
            for(MysqlTable table1 : tableList1) {
                if(table1.getTableName().equalsIgnoreCase(table2.getTableName())) {
                    flag = true;
                    break;
                }
            }
            if(!flag) {
                printStream.println(String.format("<<<<<<<<<<<<<<右库中存在表[%s]左库中未找到",table2.getTableName()));
            }
        }
    }

    public static List<MysqlTable> getdbTables(String url,String dbName,String username,String password) {
        List<MysqlTable> tables = new ArrayList<>();
        List<Sql> sqls = new ArrayList<>();
        List<Map<String,Object>> tableList = JdbcRunSql.sqlList(url+dbName,username,password,SHOWTABLE);
        for(Map<String,Object> o : tableList) {
            String tableName = o.get("Tables_in_"+dbName).toString();
            String uuid = UUID.randomUUID().toString();
            Sql sql = new Sql();
            sql.setSqlId(uuid);
            sql.setSql(String.format(CREATETABLE,tableName));
            sqls.add(sql);
        }
        sqls = JdbcRunSql.runMapSql(url+dbName,username,password,sqls,o->o.get("Create Table").toString());
        for(Sql sql : sqls) {
            System.out.println(sql.getResultStr());
            MysqlTable table = TableCompare.parse(sql.getResultStr());
            tables.add(table);
        }
        return tables;
    }

}
