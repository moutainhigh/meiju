package cn.visolink.activeMQ.ActivePojo;

/**
 *
 *  * @author wuyaoguang
 *  * @since 2020-04-16
 *  此文件暂未使用，文件内容为 回调明源各接口，预计流程改造时统一修改，将配置文件中的回调改为使用此文件
 */
public class ActivePublicUtil {

    /*
    * ****明源【定调价、售楼】回调接口******/
    /**
     * 流程发起
     */
    private static final String URL_START = "/api/service/Mysoft.Slxt.Common.Plugin.AppServices.BPMWorkFlowAppService/CreateResult/";
    /**
     * 异常结束（驳回、撤回）
     */
    private static final String URL_HISTORY = "/api/service/Mysoft.Slxt.Common.Plugin.AppServices.BPMWorkFlowAppService/Audit/";
    /**
     * 正常结束，归档
     */
    private static final String URL_END = "/api/service/Mysoft.Slxt.Common.Plugin.AppServices.BPMWorkFlowAppService/ApproveClose/";

    /*
    * **********明源费用回调接口******************/
    /**
     * 费用校验
     * */
    private static final String SEND_APPROVE_CHECK = "/service/Mysoft.Fygl.Services.BPMWorkflowServices.BPMWorkflow/SendApproveCheck.aspx";
    /**
     * 流程发起
     * */
    private static final String ADJUSTMENT_START = "/api/FyglService/CreateResult/";
    /**
     * 异常结束（驳回、撤回）
     * */
    private static final String ADJUSTMENT_HISTORY = "/api/FyglService/Audit/";
    /**
     * 正常结束，归档
     * */
    private static final String ADJUSTMENT_END = "/api/FyglService/ApproveClose/";


}
