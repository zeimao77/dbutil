package top.zeimao77.dbutil.export;

import top.zeimao77.dbutil.context.AppResourceContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 处理xml配置文件  初始化Tabel
 */
public class TableFactory {

    private static final Logger logger = Logger.getLogger(TableFactory.class.getName());

    private Tables tables;

    public TableFactory(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(Tables.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream stream = new FileInputStream(file);
            this.tables = (Tables) unmarshaller.unmarshal(stream);
            AppResourceContext.setTableFactory(this);
            logger.info("初始化table工厂结束...");
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("初始化失败，请检查[tableconfig.xml]配置文件");
            System.exit(0);
        }
    }

    public Table getTableByKey(String key) {
        return this.tables.getTableByKey(key);
    }

    public Table getTableByName(String key) {
        return this.tables.getTableByName(key);
    }

    public int count() {
        return this.tables.getTableList().size();
    }

    public List<String> serviceList(){
        List<String> list= new ArrayList<>(count());
        for(Table table:this.tables.tableList) {
            list.add(table.getServiceId());
        }
        return list;
    }

    @XmlRootElement(name = "tableFactory")
    static class Tables{

        //@XmlElementWrapper
        @XmlElement(name="table")
        private List<Table> tableList;

        @XmlTransient
        public List<Table> getTableList() {
            return tableList;
        }

        public void setTableList(List<Table> tableList) {
            this.tableList = tableList;
        }

        public Table getTableByKey(String key){
            for(Table t:tableList){
                if(key.equals(t.getServiceId())){
                    return t;
                }
            }
            return null;
        }

        public Table getTableByName(String key){
            for(Table t:tableList){
                if(key.equals(t.getTableName())){
                    return t;
                }
            }
            return null;
        }

    }
}
