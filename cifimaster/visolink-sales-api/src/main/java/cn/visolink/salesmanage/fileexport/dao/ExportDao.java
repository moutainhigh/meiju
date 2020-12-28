package cn.visolink.salesmanage.fileexport.dao;

import cn.visolink.salesmanage.fileexport.model.ListThree;
import cn.visolink.salesmanage.fileexport.model.MonthFour;
import cn.visolink.salesmanage.fileexport.model.MonthPlan;


import java.util.List;
import java.util.Map;

public interface ExportDao {

    List<MonthPlan> selectAll();

    Map<String,Object> selectMonthWeek(Map map);
    /**
     * 根据businessId 和month 查询出多表数据
     * @param map
     * @return
     */
    MonthPlan selectMonthPlanByGuid(Map map);

    /**
     * 根据父id查询子数据
     * @param map
     * @return
     */
    List selectAllMonthPlanByFatherId(MonthPlan map);

    /**
     * 根据父id查询所有子数据
     * bql 2020.08.10
     *
     * @param map map
     * @return List
     */
    List selectAllMonthPlanByFatherIdAll(MonthPlan map);

    /**
     * 根据businessId 和month 查询出多表数据，上报导出用
     * @param map
     * @return
     */
    MonthPlan selectMonthPlanUpByGuid(Map map);

    /**
     * 根据父id查询子数据，上报导出用
     * @param map
     * @return
     */
    List selectAllMonthPlanUpByFatherId(MonthPlan map);

    /*表三导出*/
    List selectMouthChannelDetail(Map map);
    /**
     * 获得时间范围
     *
     * @return
     */
    public Map<String,Object>  timeRangeSelect(Map<String, Object> map);

    public List  selectWeeklyPlan(Map map);

    public MonthFour mouthPlanSelect(Map map);
}
