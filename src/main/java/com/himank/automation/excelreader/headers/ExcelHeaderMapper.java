package com.himank.automation.excelreader.headers;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.himank.automation.excelreader.config.MetaConfig;
import com.himank.automation.excelreader.config.ExcelConstants;

public class ExcelHeaderMapper {

    private static Map<String, Map<String, Integer>> allHeaders;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelHeaderMapper.class);

    /**
     * This method scan all the model class and map all the excel(provided in annotation) ModelHeaders and ColumnHeaders with the variable name
     */
    public static void initializeHeaders() {
        MetaConfig.getModelMetaDatas();
    }

    /**
     * This method is used to read all the excel Model and column headers and map the column headers with ModelHeaders and its column index.
     *
     * @param excelSheet        -      excel sheet for which we have to map column headers with its column index.
     * @return Map              -      Map containing Model name as key and Map<String, Integer>(column header and its column index) as value.
     */
    public static Map<String, Map<String, Integer>> getAllHeaders(XSSFSheet excelSheet) {
        if (!ObjectUtils.isEmpty(allHeaders))
            return allHeaders;

        initializeHeaders();

        allHeaders = new LinkedHashMap<>();
        Map<String, Integer> columnIndexMapper = null;

        XSSFRow modelHeadersRow = excelSheet.getRow(ExcelConstants.MODEL_HEADER_ROW);
        XSSFRow innerModelHeadersRow = excelSheet.getRow(ExcelConstants.INNER_MODEL_HEADER_ROW);
        XSSFRow columnHeaders = excelSheet.getRow(ExcelConstants.COLUMN_HEADER_ROW);

        int lastHeaderIndex = columnHeaders.getLastCellNum() - 1;

        String modelHeader = null;
        String previousModelHeader = null;
        String innerModelHeader;
        for (int i = 0; i <= lastHeaderIndex; i++) {
            String columnHeaderString = columnHeaders.getCell(i) == null ? null : columnHeaders.getCell(i).getStringCellValue();

            innerModelHeader = innerModelHeadersRow.getCell(i) == null ? null : innerModelHeadersRow.getCell(i).getStringCellValue();
            modelHeader = StringUtils.isEmpty(modelHeader) && !StringUtils.isEmpty(innerModelHeader) ? innerModelHeader : modelHeader;
            if (!StringUtils.isEmpty(innerModelHeader)) {
                if (!StringUtils.isEmpty(previousModelHeader) && !ObjectUtils.isEmpty(columnIndexMapper)) {
                    allHeaders.put(previousModelHeader, columnIndexMapper);
                    columnIndexMapper = null;
                }
                modelHeader = previousModelHeader + "-" + innerModelHeader;
            }
            previousModelHeader = !StringUtils.isEmpty(modelHeader) ? modelHeader : previousModelHeader;
            modelHeader = modelHeadersRow.getCell(i).getStringCellValue();

            if (!StringUtils.isEmpty(modelHeader) || !StringUtils.isEmpty(innerModelHeader)) {

                if (!StringUtils.isEmpty(previousModelHeader) && !ObjectUtils.isEmpty(columnIndexMapper)) {
                    allHeaders.put(previousModelHeader, columnIndexMapper);
                }

                columnIndexMapper = new LinkedHashMap<>();

                columnIndexMapper.put(columnHeaderString, columnHeaders.getCell(i).getColumnIndex());
            } else if (!StringUtils.isEmpty(previousModelHeader) && StringUtils.isEmpty(modelHeader) && !StringUtils.isEmpty(columnHeaderString)) {
                columnIndexMapper.put(columnHeaderString, columnHeaders.getCell(i).getColumnIndex());
            }
        }
        if (!StringUtils.isEmpty(previousModelHeader) && !ObjectUtils.isEmpty(columnIndexMapper)) {
            allHeaders.put(previousModelHeader, columnIndexMapper);
        }
        return allHeaders;
    }

}
