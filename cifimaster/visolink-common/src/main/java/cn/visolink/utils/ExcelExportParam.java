package cn.visolink.utils;

import cn.visolink.utils.StringUtils;
import lombok.Data;

/**
 * 导出Excel所使用的的参数
 * @Author Yangyz01
 * @Date 2020-01-21
 */
@Data
public class ExcelExportParam {
    /**
     * 导出Excel分类，包含：
     *   1  ExcelDefined.EXCEL_TYPE_2003_XLS
     *   2  ExcelDefined.EXCEL_TYPE_2007_XLSX
     */
    private int excelType;

    /**
     * 返回文件名称后缀，如果是2003格式，返回xls，否则返回xlsx
     * @return
     */
    public boolean isExcel2003(){
        return excelType == ExcelDefined.EXCEL_TYPE_2003_XLS ;
    }
    /**
     * 返回文件名称后缀，如果是2003格式，返回xls，否则返回xlsx
     * @return
     */
    public String getExcelTypeDesc(){
        return excelType == ExcelDefined.EXCEL_TYPE_2003_XLS ? ExcelDefined.EXCEL_TYPE_DESC_2003_XLS : ExcelDefined.EXCEL_TYPE_DESC_2007_XLSX;
    }

    /**
     * 导出Excel的sheet名称
     */
    private String sheetName;


    /**
     * 导出文件的名称
     */
    private String fileName;

    private String template;

    public boolean isUserTemplate(){
        return StringUtils.isNotBlank(template);
    }

    /**
     * 文件编码，默认为UTF-8
     */
    private String encoding = "UTF-8";

    /**
     * 导出文件中，日期对象格式，默认yyyy-MM-dd hh:mm:ss
     */
    private String dataPattern = "yyyy-MM-dd hh:mm:ss";

    /**
     * 导出时，数据对象默认开始的行数，默认为-1
     *      注意：先输出header，然后输出dataset，如果header为空，那么直接输出dateset
     */
    private Integer line = -1;

    /**
     * 导出时，数据对象默认开始的列数，默认为A列
     *      注意：先输出header，然后输出dataset，如果header为空，那么直接输出dateset
     */
    private Integer width = 0;

}
