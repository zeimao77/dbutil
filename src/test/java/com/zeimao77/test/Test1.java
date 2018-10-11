package com.zeimao77.test;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test1 {

    @Test
    public void test() {
        String str = "INSERT INTO `score`(`id`,`name`,`kecheng`,`fenshu`) VALUES (${id5},${name},${kecheng},${fenshu});";
        Pattern pattern = Pattern.compile("\\$\\{([\\S]+?)\\}");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()) {
            System.out.println(str.substring(matcher.start(),matcher.end()));
            String field = matcher.group(1).trim();
            str = matcher.replaceFirst("'"+field+"'");
            matcher = pattern.matcher(str);
        }
        System.out.println(str);
    }


    @Test
    public void tt() throws IOException, InvalidFormatException {
        File file = new File("C:\\Users\\zeimao77\\Desktop\\score.xlsx");
        List<Map<String,Object>> returnList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        for(int i=0;i<4;i++) {
            String text = row.getCell(i).getStringCellValue();
            System.out.print(text+"\t");
        }
        for(int i=1;i<sheet.getLastRowNum();i++) {
            row = sheet.getRow(i);
            for(int j=0;j<4;j++) {
                Cell cell = row.getCell(j);
                Object val = null;
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        val = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        val = cell.getNumericCellValue();
                        break;
                    case BOOLEAN:
                        val = cell.getBooleanCellValue();
                    default:
                        break;
                }
                System.out.print(val+"\t");
            }
        }
    }
}
