package com.himank.automation.excelDataModel;

import com.himank.automation.annotations.ColumnHeader;
import com.himank.automation.annotations.InnerModelHeader;
import lombok.Data;

@Data
@InnerModelHeader(name = "INNER_MODEL")
public class InnerModel {

    @ColumnHeader(name = "INNER_FIELD1")
    private String field1;

    @ColumnHeader(name = "INNER_FIELD2")
    private String field2;

    @ColumnHeader(name = "INNER_FIELD3")
    private String field3;

    @ColumnHeader(name = "INNER_FIELD4")
    private String field4;

    @ColumnHeader(name = "INNER_FIELD5")
    private String field5;
}
