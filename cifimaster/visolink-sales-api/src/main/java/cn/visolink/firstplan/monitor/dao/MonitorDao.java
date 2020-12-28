package cn.visolink.firstplan.monitor.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2019-09-17
 *
 */
@Mapper
public interface MonitorDao {

    /**
     * 监控详情
     */
    public Map selectMonitorPlan(String plan_id);

    /**
     * 监控左边节点
     */
    public List selectMonitorPlanNode(String plan_id);


    /**
     * 监控节点拿地
     */
    public Map selectMonitorLand(String plan_node_id);

    /**
     * 监控节点顶设1
     */
    public Map selectMonitorDesignone(String plan_node_id);

    /**
     * 监控节点顶设2
     */
    public Map selectMonitorDesigntwo(String plan_node_id);

    /**
     * 监控节点信息
     */
    public List selectMonitorThreeNode(Map params);


    /**
     * 监控节点周拆分信息
     */
    public List selectMonitorThreeWeek(Map params);



    /**
     * 监控实时节点信息
     */
    public List<Map> selectMonitorNewNode(String plan_id);


    /**
     * 监控实时节点信息
     */
    public List selectDesigntwoCodeIndexAvg(String plan_node_id);

    /**
     * 查询当前计划所属的项目
     */
    public String getProjectidByPlanId(String plan_id);

    public Map getActualCustomerStorage(Map paramMap);


    /**
     *获取当前计划有没有延期
     */
    public Map getDelayApplayOpenDate(String plan_id);
}
