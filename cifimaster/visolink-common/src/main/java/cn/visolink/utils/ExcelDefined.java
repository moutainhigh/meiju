package cn.visolink.utils;

public class ExcelDefined {
    public static int EXCEL_TYPE_2003_XLS = 1;
    public static int EXCEL_TYPE_2007_XLSX = 2;

    public static String EXCEL_TYPE_DESC_2003_XLS = "xls";
    public static String EXCEL_TYPE_DESC_2007_XLSX = "xlsx";

    /**
     * 指定EXCEL2003最大行数
     */
    public static Integer EXCEL2003_MAX_LINE = 65535;

    /**
     * 指定EXCEL2007最大行数
     */
    public static Integer EXCEL_MAX_LINE = 1048575;


    /**
     * 在XSSFWorkbook中，可以使用SXSSFWorkbook优化内存占用，该值就是一个触发条件，如果总行数大于该值，那么开启内存优化
     */
    public static Integer EXCEL_2007_OPTIMIZE_LINE = 3000;

}
