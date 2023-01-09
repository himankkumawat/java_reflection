package com.himank.automation.excelreader.config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.himank.automation.annotations.InnerModelHeader;
import com.himank.automation.annotations.ColumnHeader;
import com.himank.automation.annotations.ModelHeader;

public class MetaConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaConfig.class);

    private static Map<String, List<ModelMetaData>> modelMetaDataMap;

    /**
     * This method is used to get metadata information about the model class.
     * @return  Map         -       Map containing model class name as key and list of ModelMetaData as value.
     */
    public static Map<String, List<ModelMetaData>> getModelMetaDatas() {
        if (modelMetaDataMap != null && !modelMetaDataMap.isEmpty())
            return modelMetaDataMap;

        modelMetaDataMap = new HashMap<>();
        initializeModelMetaMap();

        return modelMetaDataMap;
    }

    /**
     * This method is used to initialize the metadata(variable name, variable data type, column header etc.) of all the Model classes.
     */
    private static void initializeModelMetaMap() {
        Set<Class<?>> allModelClasses = ModelNames.getModelClasses();
        allModelClasses.addAll(ModelNames.getInnerModelClasses());

        for (Class<?> modelClass : allModelClasses) {
            String[] modelHeaders = modelClass.getAnnotation(ModelHeader.class) != null ?
                    modelClass.getAnnotation(ModelHeader.class).name() : modelClass.getAnnotation(InnerModelHeader.class).name();
            Object modelClassObject;
            try {
                for (String modelHeader : modelHeaders) {
                    modelClassObject = modelClass.getDeclaredConstructor().newInstance();
                    modelMetaDataMap.put(modelHeader, getColumnMetaDataList(modelClassObject));
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Unable to create a new instance for class: " + modelClass.getSimpleName());
            }
        }
    }

    /**
     * This method is ued to get the list of metadata for all the fields/variables of a model class.
     * @param targetModel       -       Object of model class for which metadata has to be initialized.
     * @return  List            -       List of ModelMetaData for the given model class object.
     */
    private static List<ModelMetaData> getColumnMetaDataList(Object targetModel) {
        List<ModelMetaData> modelMetaDataList = new ArrayList<>();
        for (Field field : targetModel.getClass().getDeclaredFields()) {
            ColumnHeader columnHeader = field.getAnnotation(ColumnHeader.class);
            InnerModelHeader innerModelHeader = field.getAnnotation(InnerModelHeader.class);
            String variableName = field.getName();
            Class<?> variableDataType = field.getType();
            ModelMetaData modelMetaData = new ModelMetaData(columnHeader == null ? innerModelHeader.name()[0] :
                    columnHeader.name(), variableName, variableDataType);
            modelMetaDataList.add(modelMetaData);
        }
        return modelMetaDataList;
    }


}
