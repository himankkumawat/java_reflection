package com.himank.automation.excelreader.utils;

import com.himank.automation.annotations.InnerModelHeader;
import com.himank.automation.excelreader.config.ExcelConstants;
import com.himank.automation.excelreader.headers.ExcelHeaderMapper;
import com.himank.automation.excelreader.config.MetaConfig;
import com.himank.automation.excelreader.config.ModelMetaData;
import com.himank.automation.excelreader.config.ModelNames;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.himank.automation.annotations.ModelHeader;

public class ExcelDataReaderUtils {

    public static Map<Integer, Map<String, Object>> excelMappedObject;

    private static Map<String, Map<String, Integer>> modelHeaderMapper;

    private static Map<String, Map<Integer, Map<String, Object>>> excelModelData;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataReaderUtils.class);

    /**
     * This method is used to bind all the data from all the sheets of an excel.
     * @param excelSheets                   -       Map of excel sheet name as key and XSSFSheet object as value.
     * @return  Map                         -       Map containing sheet name as key and model object along with test case number in another Map as value.
     */
    public static Map<String, Map<Integer, Map<String, Object>>> bindModelFromExcelSheets(Map<String, XSSFSheet> excelSheets) {
        if(!ObjectUtils.isEmpty(excelModelData))
            return excelModelData;

        if(ObjectUtils.isEmpty(modelHeaderMapper))
            modelHeaderMapper = ExcelHeaderMapper.getAllHeaders(excelSheets.get(excelSheets.keySet().toArray()[0]));

        excelModelData = new LinkedHashMap<>();

        for (Map.Entry<String, XSSFSheet> xssfSheet : excelSheets.entrySet()) {
            Map<Integer, Map<String, Object>> sheetModelData = bindModelFromXSSFSheet(xssfSheet.getValue(), modelHeaderMapper);
            excelModelData.put(xssfSheet.getKey(), sheetModelData);
        }
        return excelModelData;
    }

    /**
     * This method is used to bind all the data of a particular sheet with its model class object.
     * @param xssfSheet                     -       XSSFSheet from which we have to bind the data.
     * @param headerColumnIndexMap          -       Map containing the column headers as key and its excel column index as value.
     * @return  Map                         -       Map containing test case number as key and all the model objects as value in another map.
     */
    private static Map<Integer, Map<String, Object>> bindModelFromXSSFSheet(XSSFSheet xssfSheet, Map<String, Map<String, Integer>> headerColumnIndexMap) {
        if(!ObjectUtils.isEmpty(excelMappedObject))
            return excelMappedObject;

        excelMappedObject = new HashMap<>();
        int rowNumberStart = ExcelConstants.TEST_DATA_START_ROW;
        int totalTestCase = xssfSheet.getLastRowNum();

        XSSFRow row;

        for (int i = rowNumberStart; i <= totalTestCase; i++) {
            row = xssfSheet.getRow(i);
            Integer testCaseNumber = (int) row.getCell(0).getNumericCellValue();
            Map<String, Object> currentRowMappedObjects = bindCurrentRow(row, headerColumnIndexMap);
            excelMappedObject.put(testCaseNumber, currentRowMappedObjects);
        }

        return excelMappedObject;
    }

    /**
     * This method is used to bind the bind the model class objects with the data present in the excel row.
     * @param row                           -       XSSFRow object containing data from the excel row.
     * @param headerColumnIndexMap          -       Map containing the column headers as key and its excel column index as value.
     * @return  Map                         -       Map of Model class name as key and its object as value.
     */
    private static Map<String, Object> bindCurrentRow(XSSFRow row, Map<String, Map<String, Integer>> headerColumnIndexMap) {
        Map<String, Object> currentRowObjects = new HashMap<>();

        Set<Class<?>> modelClasses = ModelNames.getModelClasses();
        for (Class<?> modelClass :  modelClasses) {
            String[] headerNames = modelClass.getAnnotation(ModelHeader.class) == null ? new String[0] : modelClass.getAnnotation(ModelHeader.class).name();

            for(String headerName : headerNames) {
                Object modelObject = null;
                try{
                    modelObject = modelClass.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    LOGGER.error("Unable to initiate a new object of class: " + modelClass.getSimpleName());
                }
                List<ModelMetaData> modelMetaDatas = MetaConfig.getModelMetaDatas().get(headerName);
                bindExcelDataTOObject(row, modelMetaDatas, headerColumnIndexMap, headerName, modelObject);
                currentRowObjects.put(headerName, modelObject);
            }
        }

        return currentRowObjects;
    }

    /**
     * This method is used to bind the excel row data to the Model class.
     * @param row                       -       Excel sheet row for which data has to be bind.
     * @param modelMetaDatas            -       Object which contains variable meta information like, datatype, variableName etc.
     * @param headerColumnIndexMap      -       Map containing the model header as key and column headers and its excel-column index as value.
     * @param mappedObject              -       object of the class for which data has to be bind.
     */
    private static void bindExcelDataTOObject(XSSFRow row, List<ModelMetaData> modelMetaDatas, Map<String, Map<String, Integer>> headerColumnIndexMap, String headerName, Object mappedObject) {

        if(headerColumnIndexMap == null || headerColumnIndexMap.get(headerName) == null) {
            return;
        }
        for (ModelMetaData modelMetaData : modelMetaDatas) {
            boolean isInnerModelClass;
            Integer dataColumnIndex;
            try {
                isInnerModelClass = mappedObject.getClass().getDeclaredField(modelMetaData.getVariableName()).getAnnotation(InnerModelHeader.class) != null;
                dataColumnIndex = headerColumnIndexMap.get(headerName).get(modelMetaData.getHeaderName());
                if(dataColumnIndex == null && !isInnerModelClass)
                    return;
                if(isInnerModelClass) {
                    String innerHeaderName = modelMetaData.getVariableDataType().getAnnotation(InnerModelHeader.class).name()[0];
                    String innerModelColumnHeaderName = headerName + "-" + innerHeaderName;
                    Object innerObject = modelMetaData.getVariableDataType().getDeclaredConstructor().newInstance();

                    List<ModelMetaData> innerModelMetaDatas = MetaConfig.getModelMetaDatas().get(modelMetaData.getVariableDataType().getAnnotation(InnerModelHeader.class).name()[0]);
                    bindExcelDataTOObject(row, innerModelMetaDatas, headerColumnIndexMap, innerModelColumnHeaderName, innerObject);

                    String setterMethodName = modelMetaData.getSetterName();
                    Method setterMethod = mappedObject.getClass().getDeclaredMethod(setterMethodName, modelMetaData.getVariableDataType());
                    setterMethod.invoke(mappedObject, innerObject);

                } else if (modelMetaData.getVariableDataType().isInstance("")) {
                    String stringValue = row.getCell(dataColumnIndex) == null ? null :
                            row.getCell(dataColumnIndex).getStringCellValue();
                    String setterMethodName = modelMetaData.getSetterName();
                    Method setterMethod = mappedObject.getClass()
                            .getDeclaredMethod(setterMethodName, modelMetaData.getVariableDataType());
                    setterMethod.invoke(mappedObject, stringValue);
                } else if (modelMetaData.getVariableDataType().isInstance(1L)) {
                    Long longValue = row.getCell(dataColumnIndex) == null ? null :
                            (long) row.getCell(dataColumnIndex).getNumericCellValue();
                    String longSetterMethodName = modelMetaData.getSetterName();
                    Method longSetterMethod = mappedObject.getClass()
                            .getDeclaredMethod(longSetterMethodName, modelMetaData.getVariableDataType());
                    longSetterMethod.invoke(mappedObject, longValue);
                } else if (modelMetaData.getVariableDataType().isInstance(new Date())) {
                    Date dateValue = row.getCell(dataColumnIndex) == null ? null :
                            row.getCell(dataColumnIndex).getDateCellValue();
                    String dateSetterMethodName = modelMetaData.getSetterName();
                    Method dateSetterMethod = mappedObject.getClass()
                            .getDeclaredMethod(dateSetterMethodName, modelMetaData.getVariableDataType());
                    dateSetterMethod.invoke(mappedObject, dateValue);
                }

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException | InstantiationException e) {
                LOGGER.error("Exception occurred while binding the data from excel to class "+ mappedObject.getClass().getSimpleName() + " object....  " + e.getMessage());
            }
        }
    }

}
