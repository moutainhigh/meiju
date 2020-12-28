package cn.visolink.firstplan.plannode.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author bao
 * @date 2020-04-14
 */


@Mapper
@Repository
public interface PlanNodeDao {


    /**
     * 根据 projectId、planNodeId 获取前后节点审批状态，即判断
     *
     * @param map map
     * @return List<Map>
     */
    List<Map<String, Object>> getPlanNodePower(Map<String, Object> map);

    /**
     * 获取当前项目顶设2是否处于审批中状态
     *
     * @param map map
     * @return Map
     */
    Map<String, Object> getOpenTwoApproval(Map<String, Object> map);


    /**
     * 获取当前节点是否有可读的申请
     *
     * @param map map
     * @return Map
     */
    Map<String, Object> getPlanNodeApproval(Map<String, Object> map);

    /**
     * 顶设2核心指标数据查询
     *
     * @param map map
     * @return Map
     */
    Map<String, Object> selectDesigntwoIndicators(Map<String, Object> map);

    /**
     * 周上报数据添加(当月)
     *
     * @param map map
     */
    void insertCommonWeekPlan(List<Map<String, Object>> map);

    /**
     * 周上报数据删除(当月)
     */
    void deleteCommonWeekPlan();

    /**
     * 周上报周日数据查询
     *
     * @return List<Map>
     */
    List<Map<String, Object>> selectMonthSundayDay();

    /**
     * 查询当前节点是否被略过节点
     *
     * @param map map
     * @return Map
     */
    Map<String, Object> queryThisNodeIsSkipped(Map<String, Object> map);

    /**
     * 查询当前节点的有效版本是否有多个有效版本
     *
     * @param map map
     * @return Map
     */
    Integer selectNodeNum(Map<String, Object> map);

    /**
     * 查询当前节点的flow有效版本是否有多个有效版本
     *
     * @param map map
     * @return Map
     */
    Integer selectflowNum(Map<String, Object> map);

    /**
     * 将当前选择的节点版本值为失效
     *
     * @param map map
     * @return Integer
     */
    Integer delPlanNode(Map<String, Object> map);

    /**
     * 将当前选择的节点版本值flow表数据删除
     *
     * @param map map
     * @return Map
     */
    Integer delPlanNodeFlow(Map<String, Object> map);
    /**
     * 将当前选择的节点版本值seven表数据删除
     *
     * @param map map
     * @return Map
     */
    Integer delNodeSeven(Map<String, Object> map);
}
