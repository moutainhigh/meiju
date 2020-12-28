package cn.visolink.firstplan.preemptionopen.dao;

import java.util.List;
import java.util.Map;

/**
 * @author sjl 抢开申请
 * @Created date 2020/7/15 11:15 上午
 */
public interface PreemptionOpenDao {
    /**
     * 查询当前计划的抢开数据
     */
    public List<Map> getPreemptionOpenVersionData(Map map);
    /**
     * 查询当前计划抢开的部分数据
     */
    public Map getPreemptionOpenDelayData(Map map);
    /**
     * 查询是否有已经审批通过的数据
     */
    public Map getApplayadoptPreeOpen(String plan_node_id);
    /**
     * 生成一条周拆分数据
     */
    public Map insertGuestFlow(Map map);

    /**
     * 初始化周拆分数据
     */
    public List<Map> getGuestWeekSplitData(Map map);
    /**
     * 删除周拆分数据
     */
    public void deleteOldWeekData(Map map);
    /**
     * 查询首开前21天是否已完成
     */
    public Map openThefirstTwentydays(Map map);
    /*
     */
    public void clearFsData(String id);
    /**
     * 更改数据状态
     */
    public void updatePreemptionOpen(Map map);
    /**
     * 获取项目id
     */
    public String getProjectInfo(String plan_id);
    /**
     * 更改客储数据的状态
     */
    public void updateCustomerStorageStatus(Map map);
    /**
     * 查询顶设2的客储数据
     */
    public List<Map> designTwoCustomerStorageData(String plan_id, String count);
    /**
     * 获取首开前21天和当前时间的时间差
     */
    public String getTimeDifference(Map map);
    /**
     * 初始化客储数据
     */
    public void initCustomerStorageData(Map map);
    /**
     * 查询版本号
     */
    public Integer getFlowMaxVersion(String plan_node_id);
    /**
     * 初始化周拆分数据
     */
    public void initWeekSpiltStorageData(Map map);
    /**
     * 获取节点时间
     */
    public List<Map> getNodeTime(Map map);
    /**
     * 查询抢开的版本号
     */
    public Integer getVersionNumber(Map map);
    /**
     * 查询审批人信息
     */
    public Map getAppllayData(String id);
    /**
     * 删除原来的数据
     */
    public void deleteOladData(Map map);
    /**
     * 根据计划id获取节点id
     */
    public String getPlanNodeIdByPlan(String plan_id,String node_level);
    /**
     * 生成前7天主数据
     */
    public void createSevenDayData(Map map);
    /*
    生成前7天的附属数据-周拆分-客储数据
     */
    public void createCustomerAndWeekData(Map map);
    /**
     * 21天变更的数据-初始化
     */
    public void initCustomerStorageDataTwenty(Map map);
}
