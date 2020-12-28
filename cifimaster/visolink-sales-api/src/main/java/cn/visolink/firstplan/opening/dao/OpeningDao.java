package cn.visolink.firstplan.opening.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2019-09-17
 *
 */
@Mapper
public interface OpeningDao{
    /**
     * 新增
     * @return
     */
    public int insertOpenThisDay(Map params);

    /**
     * 修改开盘信息
     * @return
     */
    public int updateOpenThisDay(Map params);

    /**
     * 新增首开均价
     * @return
     */
    public int insertOpenAvg(Map params);

    /**
     * 删除首开均价
     * @return
     */
    public int delOpenAvgByPlanNodeId(String plan_node_id);

    /**
     * 获取IDM项目营销总操盘手
     * @return
     */
    public Map getTraderByProjectId(String  project_id);

    /**
     * 获取IDM项目营销总操盘手
     * @return
     */
    public List<Map> getBuildingNameInfo(Map params);

    /**
     * 获取签约金额跟套数
     * @return
     */
    public Map getOrderByProjectIdPriceNum(Map params);


    /**
     * 新增开盘次日信息
     * @return
     */
    public int insertOpenMorrowBroadcast(Map params);
    /**
     * 修改开盘次日信息
     * @return
     */
    public int updateOpenMorrowBroadcast(Map params);

    /**
     * 新增首开次日均价
     * @return
     */
    public int insertOpenMorrowAvg(Map params);

    /**
     * 删除次日首开均价
     * @return
     */
    public int delOpenMorrowByPlanNodeId(String morrow_id);

    /**
     * 获取首开当日播报表-均价
     * @return
     */
    public List<Map> selectOpenThisDayAvgByNodeId(String plan_node_id);

    /**
     * 初始化次日数据来源
     * @return
     */
    public Map selectSevenDayIndex(String plan_id);

    /**
     * 初始化次日数据来源首开均价（元）
     * @return
     */
    public List selectSevenDayAvg(String dayId);

    /**
     * 初始化当日数据来源 去化情况-目标（顶设2）
     * @return
     */
    public Map selectdesigntwoSellCase(String plan_id);

    /**
     * 初始化当日数据来源 去化情况-目标（顶设2）
     * @return
     */
    public List selectdesigntwoAvg(String plan_node_id);

    /**
     * 初始化当日数据来源 去化情况-目标（顶设2）
     * @return
     */
    public Map getGuestSum(Map params);

    /**
     * 初始化当日数据来源 去化情况-实际去化均价
     * @return
     */
    public Map getOrderByProjectIdAvg(Map params);

    /**
     * 获取首日填报页面的数据
     * @return
     */
    public Map selectOpenThisDayByNodeId(String plan_node_id);


    /**
     * 获取次日填报页面的数据
     * @return
     */
    public Map selectOpenMorrowDayByPlanId(Map parmas);


    /**
     * 获取次日填报页面的数据平均数
     * @return
     */
    public List selectOpenMorrowDayAvgByPlanId(String plan_id);


    /**
     * 获取次日填报页面的数据
     * @return
     */
    public int getAddVisit(Map params);

    /**
     * 修改累计统计
     * @return
     */
    public int updatePlanNodeOpenTotal(String id);

    /**
     * 修改客储状态
     * @return
     */
    public int updateGuestStorageFlowApprovalStuat(Map params);


    /**
     * 当前节点
     * @return
     */
    public int updatePlanThisNodeById(Map params);
    /**
     * 当前节点
     * @return
     */
    public int updatePlanThisNodeByProjectId(Map params);

    /**
     * 当前节点
     * @return
     */
    public int updatePlanThisNodeByPlanId(Map params);
    /**
     * 删除节点跟周拆分
     * @return
     */
    public int deleteNodeOrWeek(String flow_id);

    /**
     * 修改节点审批信息
     * @return
     */
    public int updatePlanNodeFlowApproval(String id);

    /**
     * 修改次日审批信息
     * @return
     */
    public int updateMorrowFlowApproval(String id);

    /**
     * 获取顶设2的产品类型
     * @return
     */
    public List selectProductTypeByPlanId(String plan_id);

    /**
     * 获取计划信息
     * @return
     */
    public Map selectPlanByIdInfo(String plan_id);

    /**
     * 添加计划
     * @return
     */
    public int insertPlanNodeOpen(Map params);

    /**
     * 查询最新有效节点
     */
    public Map selectPlanNodeByPlanId(Map params);

    /**
     * 节点储客计划
     */
    public int insertGuestStorageNodePlan(Map map);

    /**
     * 查询节点储客计划
     */
    public List viewGuestStorageNodePlan(String plan_node_id);

    /**
     * 查询节点储客计划
     */
    public List getGuestStorageNodeByPlanId(String plan_id);

    /**
     * 查询节点储客计划实际人数
     */
    public Map getGuestStorageSum(Map params);

    /**
     * 查询周拆分数据
     */
    public List getStorageWeekByPlanNodeId(String plan_node_id);

    /**
     * 查询周拆分数据
     */
    public List getStorageWeekByPlanNodeByPlanId(String plan_id);

    /**
     * 获取节点储客的流程信息
     * @return
     */
    public Map getGuestStorageFlowByPlanId(Map parmas);

    /**
     * 获取节点版本
     * @return
     */
    public List selectPlanNodeByPlanIdAndNodeLevle(String plan_id);

    /**
     * 新增周拆分数据
     */
    public int insertGuestStorageWeek(Map map);



    /**
     * 版本
     */
    public List selectPlanNodeVersionByPlanId(Map params);


    /**
     * 节点储客
     */
    public List getGuestStorageNodePlanByFlowId(String flow_id);


    /**
     * 节点储客版本
     */
    public List getGuestStorageFlowByPlanIdVersion(String plan_id);
    /**
     * 节点储客版本数
     */
    public Integer getGuestStorageFlowByPlanIdVersionNum(String plan_id);


    public int insertGuestStorageFlow(Map params);
    /**
     * 节点储客周拆分
     */
    public List getGuestStorageWeekByFlowId(String flow_id);


    /**
     * 节点储客周拆分
     */
    public Map getGuestStorageFlowById(String id);


    /**
     * 获取次日播报
     */
    public List getOpenMorrowBroadcastByPlanId(String params);



    /**
     * 获取次日播报
     */
    public Map selectOpenMorrowDayById(String id);

     /**
     * 获取次日播报平均数
     */
    public List selectOpenMorrowDayAvgById(String morrow_id);

    /**
     * 楼栋
     */
    public List selectBuildingName(String project_id);
    /**
     * 获取次日版本
     */
    public Integer getOpenMorrowBroadcastByPlanIdVersionNum(String plan_id);

    /**
     * 获取开盘后一个月
     */
    public Map selectOpenFileFlow(String plan_node_id);

    int updateMorrowFlowApprovalStatus(Map params);

    int updatePlanNodeFlowApprovalStatus(Map params);

    List<Map> selectDesignoneValueByPlanId(String plan_id);

    Map selectFlowMove(String plan_id);

    String selectProjectNameById(String projectId);


    Map selectLastOpening(String plan_id);

    //根据业态类型查询业态code
    Map getProductTypeCode(String product_type);

    //查询首开前两月的取证货值去化率
    Map getThisDayData(String plan_id);

    /**
     * 查询首开当日是否提报过
     */

    Map queryOpenThisDayIsSubmit(String plan_id);
    /**
     * 查询次日均价
     */
    public List<Map> selectOpenMorrowDayAvgByPlanNodeId(String plan_node_id);

    /**
     * 查询次日是否已完成
     */
    public List<Map> getMorryDaydataInfo(String plan_id);
    /**
     * 更改数据状态
     */

    public void updateNodeStatus(Map paramMap);
    /**
     * 获取审批信息
     */
    public Map getApplayData(String plan_node_id);
}
