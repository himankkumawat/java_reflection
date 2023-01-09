package com.himank.automation.excelDataModel;

import com.himank.automation.annotations.ColumnHeader;
import com.himank.automation.annotations.ModelHeader;
import lombok.Data;

@Data
@ModelHeader(name = {"MODEL1"})
public class Model1 {

    @ColumnHeader(name = "FIELD1")
    private String field1;

    @ColumnHeader(name = "FIELD2")
    private String field2;

    @ColumnHeader(name = "FIELD3")
    private String field3;

    @ColumnHeader(name = "FIELD4")
    private Long field4;

    @ColumnHeader(name = "FIELD5")
    private Long field5;

    @ColumnHeader(name = "FIELD6")
    private Long field6;
}
