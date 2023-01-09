package com.himank.automation.writer;

import com.himank.automation.excelreader.utils.AssertReportModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.himank.automation.properties.ApplicationProperties;

public class ExcelWriter {

    private static final int START_HEADER_ROW = 1;
    private static final int HEADER_COLUMN_GAP = 5;

    public static void writeResults(String fileName, Map<String, List<AssertReportModel>> data) {
        XSSFWorkbook excelWorkbook = new XSSFWorkbook();
        XSSFSheet spreadSheet = excelWorkbook.createSheet("Results");

        int tableHeaderColumn = 1;
        int headerColumn = 1;
        int dataColumn = 1;

        for (Map.Entry<String, List<AssertReportModel>> testResult : data.entrySet()) {
            XSSFRow row = spreadSheet.getRow(START_HEADER_ROW) == null ?
                    spreadSheet.createRow(START_HEADER_ROW) : spreadSheet.getRow(START_HEADER_ROW);
            Cell cell = row.createCell(tableHeaderColumn);
            String tableHeader = testResult.getKey();
            cell.setCellValue(tableHeader);

            // write headers for assert result
            row = spreadSheet.getRow(2) == null ? spreadSheet.createRow(2) : spreadSheet.getRow(2);

            cell = row.createCell(headerColumn++);
            cell.setCellValue("ELEMENT");

            cell = row.createCell(headerColumn++);
            cell.setCellValue("ACTUAL");

            cell = row.createCell(headerColumn++);
            cell.setCellValue("EXPECTED");

            cell = row.createCell(headerColumn++);
            cell.setCellValue("RESULT");

            // write the data in tabular format
            int assertDataStartingRow = 3;

            for (AssertReportModel assertResult : testResult.getValue()) {

                int currentCellColumn = dataColumn;
                XSSFRow dataRow = spreadSheet.getRow(assertDataStartingRow) == null ?
                        spreadSheet.createRow(assertDataStartingRow) : spreadSheet.getRow(assertDataStartingRow);
                assertDataStartingRow++;

                Cell cell1 = dataRow.createCell(currentCellColumn++);
                cell1.setCellValue(assertResult.getElement());

                cell1 = dataRow.createCell(currentCellColumn++);
                cell1.setCellValue(assertResult.getActual());

                cell1 = dataRow.createCell(currentCellColumn++);
                cell1.setCellValue(assertResult.getExpected());

                cell1 = dataRow.createCell(currentCellColumn);
                cell1.setCellValue(assertResult.isResult());
            }

            tableHeaderColumn += HEADER_COLUMN_GAP;
            dataColumn += HEADER_COLUMN_GAP;
            headerColumn++;

        }
        writeExcel(fileName, excelWorkbook);
    }

    private static void writeExcel(String excelName, XSSFWorkbook excelWorkbook) {
        String path = ApplicationProperties.getProperties().getProperty("output.test.file.path");

        try {

            FileOutputStream out = new FileOutputStream(new File(path + excelName + ".xlsx"));

            excelWorkbook.write(out);
            out.close();
        } catch (Exception ex) {
            System.out.println("Unable to write excel.");
        }
    }
}
