package cn.visolink.firstplan.plannode.service;

import cn.visolink.exception.ResultBody;

import java.util.List;
import java.util.Map;

/**
 * @author bao
 * @date 2020-04-14
 */

public interface PlanNodeService {

    /**
     * 节点验证
     *
     * @param map projectId、planNodeId、node_level
     * @return Map
     */
    Map<String, Object> getPlanNodePower(Map<String, Object> map);

    /**
     * 定时任务,每月1号0点生成周上报
     *
     * */
    void insertCommonWeekPlan();

    /**
     * 删除首开草稿版数据
     *
     * @param map planNodeId
     * @return Map
     */
    ResultBody delPlanNodePower(Map<String, Object> map);

}
