package cn.visolink.salesmanage.plandatainterface.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 数据接口
 *
 * @author lihuan
 * @date 2019-11-18
 */

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Mapper
@Repository
public interface PlanDataInterfaceDao {

    /**
     * 删除当月的数据
     */
    void deleteMonthPlan(Map map);

    /**
     * 删除当周的数据
     * 当传进来的周参数为12则将第一周以及第二周的数据删除
     */
    void deleteWeekPlan(Map map);

    /**
     * 删除当周的数据
     * 当传进来的周参数为12则将第一周以及第二周的数据删除
     */
    void deleteBasic();

    /**
     * 新增mm_monthly_plan_index_detail_mingyuan
     */
    Integer insertMonthPlanCurrent(List<Map<String, Object>>  list);

    /**
     * 新增mm_monthly_plan_weekly_plan_mingyuan
     * 当传进来的周参数为12则将第一周以及第二周的数据并成一条数据
     */
    Integer insertWeekPlan(List<Map<String, Object>>  list);

    /**
     * 新增mm_basic_trader_mingyuan
     */
     Integer insertBasic(List<Map<String, Object>>  list);


    /**
     * 明源或者本地月数据查询
     * @param map 包含月份，项目名称项目名称是否有并不确定
     * @return
     */
    List<Map> reportMonthSelect(Map map);

    /**
     * 明源或者本地周数据查询
     * @param map
     * @return
     */
    List<Map> reportWeekSelect(Map map);
}
