package cn.visolink.firstplan.fpdesigntwo.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface DesignSevenSevice {



    /*开盘前7天数据，首开审批表数据*/
    Map selectSevenDayIndex(Map map);

    /*跟新开盘前7天数据，首开审批表数据*/
    Integer updateSevenDayIndex(Map map);

    /*顶设2查找客储计划周拆分和节点储客计划*/
    Map selectStorageNodePlan(Map map);

    /*顶设2更新客储计划周拆分和节点储客计划*/
    Integer updateStorageNodePlan(Map map, HttpServletRequest request);


    /*插入一条版本信息*/
    public String forPlanNode(Map map);

    /*判断是否可以创建新版本*/
    Map judgeVersion(Map map);

    /*查找顶设2左上角的版本选择*/
    List<Map> selectPlanNode(Map map);

    public void monthlyExport(HttpServletRequest request, HttpServletResponse response, String flowId);
}
