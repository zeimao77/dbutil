package top.zeimao77.dbutil.dbutil;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import top.zeimao77.dbutil.model.Sql;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JdbcRunSql {

    private static DriverManagerDataSource getDataSource(String url,String username,String passwrod) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(passwrod);
        return dataSource;
    }

    public static Map<String,Object> sqlMap(String url,String username,String passwrod,String sql) {
        DriverManagerDataSource dataSource = getDataSource(url,username,passwrod);
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate.queryForMap(sql);
    }

    public static List<Map<String,Object>> sqlList(String url,String username,String passwrod,String sql) {
        DriverManagerDataSource dataSource = getDataSource(url,username,passwrod);
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate.queryForList(sql);
    }


    public static List<Sql> runMapSql(String url, String username, String passwrod, List<Sql> sqls, Function<Map<String,Object>,String> fun) {
        DriverManagerDataSource dataSource = getDataSource(url,username,passwrod);
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        for(Sql sql : sqls) {
            System.out.println(sql.getSql());
            Map<String,Object> map = jdbcTemplate.queryForMap(sql.getSql());
            String resultStr = fun.apply(map);
            sql.setResultStr(resultStr);
        }
        return sqls;
    }

}
