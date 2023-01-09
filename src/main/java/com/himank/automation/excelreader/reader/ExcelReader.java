package com.himank.automation.excelreader.reader;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelReader {

    private static Map<String, XSSFSheet> excelSheets;

    /**
     * This method is used to read the excel sheet from the given location.
     * @param path      -       location of the excel sheet from where excel sheet has to be read.
     * @return  Map     -       Map containing sheet name as key and XSSFSheet as value.
     */
    public static Map<String, XSSFSheet> excelReader(String path) {
        if (!ObjectUtils.isEmpty(excelSheets))
            return excelSheets;
        excelSheets = new LinkedHashMap<>();
        XSSFWorkbook workbook = null;
        try {
            FileInputStream file = new FileInputStream(new File(path));

            //Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                excelSheets.put(workbook.getSheetName(i), workbook.getSheetAt(i));
            }

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelSheets;
    }
}
