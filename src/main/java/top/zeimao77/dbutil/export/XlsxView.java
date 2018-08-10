package top.zeimao77.dbutil.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

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

}
