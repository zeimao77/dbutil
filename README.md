# dbutil

mysql数据库excel导出导入工具

使用gradle build命令编译  解压zip即可使用  windows/linux测试过
## 配置源使用json格式：
* {
  "url": "jdbc:mysql://localhost:3306/samp_db",
  "driver": "com.mysql.jdbc.Driver",
  "username ": "root",
  "password": ""
}

## 使用教程
### 先配置数据源
文件  配置数据源
### 然后修改tableconfig.xml文件
* tableconfig.xml 说明：
1.  table        一个表业务
2.    service      业务名称
3.    tableName    生成excel表名称
4.  select       导出默认查询SQL语句
5.  insert       导入默认SQL语句  字段用${field}代表
6.  columnlist   字段列表
7.  item         字段列
8.    index        导出到EXCEL第几列
9.    title        导出到EXCEL的标题
10.   field        对应SQL查询的字段
11.   width        EXCEL的列宽设置
12.   format       EXCEL单元格格式设置

### 导出
选择业务   查询    查看数据    导出
### 导入
选择业务   文件    查看数据    选择事务类型    导入



