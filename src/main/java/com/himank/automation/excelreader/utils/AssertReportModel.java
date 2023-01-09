package com.himank.automation.excelreader.utils;

import lombok.Data;

@Data
public class AssertReportModel {

    private String element;

    private String actual;

    private String expected;

    private boolean result;
}
