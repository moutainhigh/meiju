package cn.visolink.salesmanage.GroupMonthlyPlan2.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author  wjc
 * @date 2019-09-17
 */
@Mapper
public interface GroupMonthlyPlanDao {

//GetMonthlyPlanByTheMonthCount

    //MonthlyPlanService
    //MonthlyPlanImpl
    /**
     * 查询本月数量
     * @return
     */
    public int GetMonthlyPlanByTheMonthCount(String months);

    /**
     * 查询本月数量
     * @return
     */
    public int SetMonthlyPlanInsert(String months);

    /**
     * 添加月度计划主表
     * @param map
     * @return
     */
    public int insertMonthPlan(Map map);
}
