package top.zeimao77.dbutil.export;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;

/**
 * 处理EXCEL
 */
public class XlsxView {

    private int i;

    private Map<String,CellStyle> styleMap = new HashMap<>();

    public XlsxView () {
        this.i = 0;
        this.styleMap.clear();
    }

    public Workbook create(Table table,List<Map<String,Object>> data) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(table.getTableName());
        this.setHead(sheet,table);
        if(data != null) {
            this.setBody(sheet,table,data);
        }
        return workbook;
    }

    private void setHead(Sheet sheet, Table config) {
        Row row = sheet.createRow(i++);
        Map<String, Column> columnMap = config.getColumnMap();
        for (String s : columnMap.keySet()) {
            Column column = columnMap.get(s);
            Cell cell = row.createCell(column.getIndex());
            cell.setCellValue(column.getTitle());
            sheet.setColumnWidth(column.getIndex(), column.getWidth() * 267);
        }
    }

    private void setBody(Sheet sheet,Table config,List<Map<String,Object>> datas) {
        Map<String, Column> columnMap = config.getColumnMap();
        for(Map<String,Object> data : datas) {
            Row row = sheet.createRow(i++);
            for (String s : columnMap.keySet()) {
                Column column = columnMap.get(s);
                Cell cell = row.createCell(column.getIndex());
                Object value = data.get(column.getField());
                if(value == null) {
                    value = "[NULL]";
                }
                if (value instanceof Number) {
                    cell.setCellValue(Double.valueOf(String.valueOf(value)));
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else if (value instanceof Calendar) {
                    cell.setCellValue((Calendar) value);
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else {
                    cell.setCellValue(String.valueOf(value));
                }
                CellStyle cellStyle;
                if (this.styleMap.containsKey(column.getFormat())) {
                    cellStyle = styleMap.get(column.getFormat());
                } else {
                    CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
                    DataFormat format = sheet.getWorkbook().createDataFormat();
                    cellStyle1.setDataFormat(format.getFormat(column.getFormat()));
                    cellStyle = cellStyle1;
                    styleMap.put(column.getFormat(), cellStyle);
                }
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(column.getIndex(), column.getWidth() * 267);
            }
        }
    }

    public static List<Map<String,Object>> parseXlsx(File file,Table config) throws IOException, InvalidFormatException, ParseException {
        LinkedHashMap<String, Column> column = config.getColumnMap();
        Function<Integer,Column> fun = o->{
            Collection<Column> columns = column.values();
            for(Column c : columns) {
                if(c.getIndex() == o.intValue()) {
                    return c;
                }
            }
            throw new IllegalArgumentException(String.format("没有找到配置行index:[%d]",o));
        };
        List<Map<String,Object>> returnList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        DecimalFormat decimalFormat = new DecimalFormat("0");
        Row row;
        for(int i=1;i<sheet.getLastRowNum();i++) {
            row = sheet.getRow(i);
            Map<String,Object> map = new HashMap<>(column.size());
            for(int j=0;j<column.size();j++) {
                Cell cell = row.getCell(j);
                Object val = null;
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        val = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        val = cell.getNumericCellValue();
                        val = decimalFormat.parse(decimalFormat.format(val));
                        break;
                    case BOOLEAN:
                        val = cell.getBooleanCellValue();
                        break;
                    default:
                        break;
                }
                if(val != null) {
                    Column c = fun.apply(j);
                    map.put(c.getField(),val);
                }
            }
            returnList.add(map);
        }
        return returnList;
    }

}
