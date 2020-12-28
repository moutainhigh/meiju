package cn.visolink.utils;

/**
 * @Auther: Gr
 * @Date: 2019/9/27 14:41
 * @Description:
 */
public class Constant {

    public static final String GROUP_EXCEL_NAME = "集团月度签约计划模板";
    public static final String REGION_EXCEL_NAME = "区域月度签约计划模板";
    public static final String PROJECT_EXCEL_NAME = "项目月度签约计划模板";


    /**
     * 1 集团 2 事业部 3 项目  prepared_by_unit_type
     */
    public static final int PREPARED_BY_UNIT_TYPE_GROUP = 1;
    public static final int PREPARED_BY_UNIT_TYPE_REGION = 2;
    public static final int PREPARED_BY_UNIT_TYPE_PROJECT = 3;

    /**
     * excel 起始行
     */
    public static final Integer START_ROW = 3;

    /**
     * 周销售excel 起始行
     */
    public static final Integer WEEK_START_ROW = 3;


    /**
     * 编制层级   1 集团 2 区域 3 项目 4 分配 5 批次 6 组团 7 业态 8 面积段
     */
    public static final Integer PREPARED_BY_LEVEL_GROUP = 1;
    public static final Integer PREPARED_BY_LEVEL_REGION = 2;
    public static final Integer PREPARED_BY_LEVEL_PROJECT = 3;
    public static final Integer PREPARED_BY_LEVEL_DISTRIBUTION = 4;
    public static final Integer PREPARED_BY_LEVEL_BATCH = 5;
    public static final Integer PREPARED_BY_LEVEL_BLOCK = 6;
    public static final Integer PREPARED_BY_LEVEL_FORMAT = 7;
    public static final Integer PREPARED_BY_LEVEL_AREA_SEGMENT = 8;


}
