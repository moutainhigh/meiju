package cn.visolink.firstplan.planmonitoring.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/2/25 2:09 下午
 */
@Mapper
public interface PlanMontitorDao {

    //统计每个计划的当前节点数量
    List<Map> getPlanNodeData(Map map);

    //获取状态的统计数据
    List<Map> statisticsStatusData(Map map);

    //获取项目数据
    List<Map> getProjectData(Map map);
    //获取项目数据总数
    Map getProjectDataCount(Map map);

    //获取项目下节点数据
    List<Map> getPlanNodeByPlan(Map map);

    //获取每个计划的当前节点
    List<Map> selectAllPlanNowNode();
    //查询出各个计划对应的当前节点-->
    Map selectAllNodeByPlan(Map map);
    //更改节点的亮灯状态
    void updateNodeLightStatus();
    //查询区域列表
    List<Map>  getIdmBuinessData();

    //查询项目关系表
    List<String> selectProjectRelationship();

    //查询主数据项目表
    List<Map> selectMainDataProject(Map map);
    //更新数据
    void addProjectRelationship(Map map);

    //更新计划表的项目名称
    void  updatePlanProjectName();
    /**
     * 获取最新节点的信息
     */
    Map getNewestPlanNode(Map map);
    /**
     * 获取当前节点的数据
     */
    Map getThisNodePlanData(Map map);
    /**
     * 获取当前节点的实际数据
     */
    Map getThisNodeActualData(Map map);
    /**
     * 获取首开的计划数据
     */
    Map getOpenPlanData(Map map);
    /**
     * 获取计划数据
     */
    public List<String> getPlanData();
    /**
     * 修改即将填报的节点
     */
    public void updateSoonNode(String plan_id);
    /**
     * 定时更新项目管理的项目名称
     */
    public void updateProjectFlagName();
    /**
     * 更新首开计划的区域信息（合并）
     */
    public void updatePlanAreaData();
}
