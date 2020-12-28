package cn.visolink.firstplan.firstopen.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/7/8 11:13 上午
 */
@Mapper
public interface FirstOpenBroadcastDao {
    /**
     * 查询当前计划的首开节点是否已完成
     */
    public Map getOpenNodeInfo(Map map);
    /**
     * 查询次日播报表是否已完成
     */
    public Map getMorrowDataInfo(Map map);
    /**
     * 查询次日播报数据
     */
    public Map getOpenMorrowDayByPlanNodeId(Map map);

    /**
     * 初始化次日数据
     */
    public Map InitOpenMorrowDayData(Map map);
    /**
     * 初始化当日数据
     */
    public Map InitOpenThisDayData(Map map);
    /**
     * 获取实际客储数据,
     */
    public Map queryGuestAcutalData(Map map);
    /**
     * 查询节点状态
     */
    public Map getThisNodeStatus(String plan_node_id);

    /**
     * 保存次日数据
     */
    public void insertMorrowDayData(Map map);
    /**
     * 修改次日数据
     */
    public void updateMorrowDayData(Map map);
    /**
     * 保存当日数据
     */
    public void insertThisDayData(Map map);
    /**
     * 修改当日数据
     */
    public void updateThisDayData(Map map);
    /**
     * 清空均价数据
     */
    public void clearAvgPriceData(String plan_node_id);
    /**
     * 保存当日均价数据
     */
    public void insertThisDayAvg(Map map);
    /**
     * 保存次日均价数据
     */
    public void inertMorrowDayAvg(Map map);
    /**
     * 获取计划数据
     */
    public String getPlanDataByPlanNodeData(String plan_node_id);


}
