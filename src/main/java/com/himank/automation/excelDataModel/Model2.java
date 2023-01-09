package com.himank.automation.excelDataModel;

import com.himank.automation.annotations.ColumnHeader;
import com.himank.automation.annotations.InnerModelHeader;
import com.himank.automation.annotations.ModelHeader;
import lombok.Data;

@Data
@ModelHeader(name = {"MODEL2_1", "MODEL2_2"})
public class Model2 {

    @ColumnHeader(name = "FIELD1")
    private String field1;

    @ColumnHeader(name = "FIELD2")
    private String field2;

    @ColumnHeader(name = "FIELD3")
    private String field3;

    @ColumnHeader(name = "FIELD4")
    private String field4;

    @InnerModelHeader(name = "INNER_MODEL")
    private InnerModel innerModel;
}
