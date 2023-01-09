package com.himank.automation.excelreader.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelMetaData {

    private String headerName;

    private String variableName;

    private Class<?> variableDataType;

    public String getSetterName() {
        return "set" + this.variableName.substring(0, 1).toUpperCase() + this.variableName.substring(1);
    }
}
