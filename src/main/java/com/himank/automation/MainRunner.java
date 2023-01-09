package com.himank.automation;

import com.himank.automation.excelreader.utils.AssertReportModel;
import com.himank.automation.writer.ExcelWriter;
import com.himank.automation.excelreader.reader.ExcelReader;
import com.himank.automation.excelreader.utils.ExcelDataReaderUtils;
import com.himank.automation.properties.ApplicationProperties;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainRunner.class);

    public static void main(String[] args) {

        String path = ApplicationProperties.getProperties().getProperty("input.test.file.path");
        LOGGER.info("Started");

        // Excel reader
        Map<String, XSSFSheet> excelSheets = ExcelReader.excelReader(path);

        Map<String, Map<Integer, Map<String, Object>>> excelModelDatas = ExcelDataReaderUtils.bindModelFromExcelSheets(excelSheets);

        LOGGER.info("Total sheets mapped : " + excelModelDatas.size());

        // Excel writer
        ExcelWriter.writeResults("Himank-Output", getData());

        LOGGER.info("Finished");
    }

    private static Map<String, List<AssertReportModel>> getData() {
        Map<String, List<AssertReportModel>> data = new LinkedHashMap<>();
        List<AssertReportModel> list = new ArrayList<>();

        AssertReportModel report = new AssertReportModel();
        report.setElement("LOGIN Submit Data");
        report.setActual("LOGIN Actual Data");
        report.setExpected("LOGIN Expected Data");
        report.setResult(false);
        list.add(report);

        report = new AssertReportModel();
        report.setElement("LOGIN1 Submit Data");
        report.setActual("LOGIN1 Actual Data");
        report.setExpected("LOGIN1 Expected Data");
        report.setResult(true);
        list.add(report);

        data.put("LOGIN", list);


        report = new AssertReportModel();
        report.setElement("DASHBOARD Radio Data");
        report.setActual("DASHBOARD Actual Data2");
        report.setExpected("DASHBOARD Expected Data2");
        report.setResult(true);
        list = new ArrayList<>();
        list.add(report);
        data.put("DASHBOARD", list);

        return data;
    }

}
