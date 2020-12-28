package cn.visolink.firstplan.opening.service;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * OpenThisDay服务类
 * </p>
 *
 * @author autoJob
 * @since 2020-02-19
 */
public interface OpenThisDayService {


    /**
     * 修改节点审批信息
     * @return
     */
     int updatePlanNodeFlowApproval(String id);

    int updatePlanThisNodeById(Map params);



    int insertOpenThisDay(Map params);

    int updateOpenThisDay(Map params);


    Map selectOpenThisDayInfo(Map params);
    /**
     * 新增次日播报
     * @param params
     * @return
     */
    int insertOpenMorrowBroadcast(Map params);

    /**
     * 修改次日播报
     * @param params
     * @return
     */
    int updateOpenMorrowBroadcast(Map params);

    /**
     * 获取操盘手
     * @param params
     * @return
     */
    Map getOpenPageInfo(Map params);

    /**
     * 产品类型
     * @return
     */
    List selectProductTypeByPlanId(String plan_id);

    /**
     * 初始化次日播报数据
     * @param plan_id
     * @return
     */
    Map initOperMorrow(String plan_id);

    Map selectOpenMorrowDayInfo(Map params);

    Map getWeekSplit(Map params);

    int insertPlanNodeOpen(Map params);

    Map selectOpenThisDayByNodeId(String plan_node_id);

    int getWeekSplitList(Map params);

    Map initGuestStorage(Map params);

    Map selectGuestStorageFlow(String flow_id);

    int insertGuestStorage(Map params);

    List getOpenMorrowBroadcastByPlanId(String plan_id);

    Map getOpenMorrowDayIByIdnfo(String id);

    List getOpenVersionByPlanId(String plan_id);

    List selectPlanNodeVersionByPlanId(Map params);


    List selectBuildingName(String project_id);

    Map approveOpenNodeInfo(Map params);

    Map selectOpenFileFlow(String plan_node_id);

    Map selectFlowMove(String plan_id);

    String selectProjectNameById(String projectId);

    Map selectLastOpeningService(Map map);

    //生成消息
    Map generateMessage(Map map);

    //校验首开当日播报表是否提报
    Map queryOpenThisDayIsSubmit(Map map);


}

