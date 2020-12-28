package cn.visolink.utils.flowpojo;

public class FlowDefined {
    /**
     * 定调价相关流程
     **/
    public static  final String FLOWKEY_MYSL_SALES_PRICEZD = "My_Sales_PriceZD";
    /**
     * 定调价相关流程
     **/
    public static  final String FLOWKEY_MYSL_SALES_PRICEZD_EDIT = "My_Sales_PriceZD2";

    /**
     * 明源费用，电商管理费
     **/
    public static  final String FLOWKEY_MYFY_DSMANAGEFEEOUT = "MY_FYs_DSManageFeeOut";

    /**
     * 明源费用，合同流程
     **/
    public static final String FLOWKEY_MYFY_CONTRACTSP = "MY_FYs_CONTRACTSP";

    /**
     * 周上报相关流程
     **/
    public static final String FLOWKEY_WEEK_AREA_REPORT = "week_area_report";

    /**
     * 首开相关流程
     **/
    public static final String FLOWKEY_Plicy = "My_Sales_policy";

    /**
     * 首开相关流程
     **/
    public static final String FLOWKEY_FP = "FP";

    /**
     * OA流程实例被删除
     */
    public static final int OA_EVENT_TYPE_DELETED = 0;

    /**
     * OA流程未发起
     */
    public static final int OA_EVENT_TYPE_UNSTARTED = 1;
    /**
     * OA流程草稿
     */
    public static final int OA_EVENT_TYPE_EDIT = 2;
    /**
     * OA流程发起成功,审批中
     */
    public static final int OA_EVENT_TYPE_START = 3;

    /**
     * OA流程审批通过
     */
    public static final int OA_EVENT_TYPE_CONFIRMED = 4;

    /**
     * OA流程:驳回发起人
     */
    public static final int OA_EVENT_TYPE_DISMISS = 5;
    /**
     * OA流程:撤回发起
     */
    public static final int OA_EVENT_TYPE_BACK = 6;
    /**
     * OA流程:流程废弃
     */
    public static final int OA_EVENT_TYPE_DISABLED = 7;
    /**
     * OA流程:开始专业审核
     */
    public static final int OA_EVENT_TYPE_ZYCONFIRM_START = 8;
    /**
     * OA流程:专业审核节点被撤回
     */
    public static final int OA_EVENT_TYPE_ZYCONFIRM_BACK = 9;


    /** ************** 各区域事业部标准code *****************/
    public static  final String FLOWKEY_CODE_SD = "QDSYB";
    public static  final String FLOWKEY_CODE_ZJ = "ZJSYB";
    public static  final String FLOWKEY_CODE_SN = "SNSYB";
    public static  final String FLOWKEY_CODE_CQ = "CQSYB";
    public static  final String FLOWKEY_CODE_SH = "SHSYB";
    public static  final String FLOWKEY_CODE_HF = "hfsyb";


    /**
     * 推演流程模板--变更-付款申请审批-山东
     * */
    public static  final String FLOWKEY_My_Sales_Payment_SD = "My_Sales_Payment_SD";
    /**
     * 推演流程模板--变更-付款申请审批
     * */
    public static  final String FLOWKEY_My_Sales_Payment = "My_Sales_Payment";
    /**
     * 推演流程模板--变更-退卡审批-山东
     * */
    public static  final String FLOWKEY_My_Sales_card_SD = "My_Sales_card_SD";
    /**
     * 推演流程模板--变更-退卡审批
     * */
    public static  final String FLOWKEY_My_Sales_card = "My_Sales_card";

    /**
     * 特例审批
     * */
    public static  final String FLOWKEY_My_SALES_SALESPECIAL = "My_Sales_SaleSpecial";
    /**
     * 推演流程模板--特例审批-浙江，西南，皖赣，事业部，上海，苏南，山东
     * */
    public static  final String FLOWKEY_My_SALES_SALESPECIAL_ZJ = "My_Sales_SaleSpecial_ZJ";
    public static  final String FLOWKEY_My_SALES_SALESPECIAL_XN = "My_Sales_SaleSpecial_XN";
    public static  final String FLOWKEY_My_SALES_SALESPECIAL_WG = "My_Sales_SaleSpecial_WG";
    public static  final String FLOWKEY_My_SALES_SALESPECIAL_SYB = "My_Sales_SaleSpecial_SYB";
    public static  final String FLOWKEY_My_SALES_SALESPECIAL_SH = "My_Sales_SaleSpecial_SH";
    public static  final String FLOWKEY_My_SALES_SALESPECIAL_SN = "My_Sales_SaleSpecial_SN";
    public static  final String FLOWKEY_My_SALES_SALESPECIAL_SD = "My_Sales_SaleSpecial_SD";

    /**
     * 一揽子分期折扣
     * */
    public static  final String FLOWKEY_MY_PACKAGE_STAGE = "My_Package_Stage";


}
