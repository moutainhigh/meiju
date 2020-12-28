package cn.visolink.salesmanage.plandatainterface.service;

import java.util.List;
import java.util.Map;
/**
 * @author 李欢
 * @date 2019-11-18
 */
public
interface PlanDataInterfaceservice {
    /**
     * 新增MonthPlan
     */
    Map<String , Object> insertMonthPlan(Map bats);
    /**
     * 新增WeekPlan
     * @return
     */
//    void insertWeekPlan(Map bats);


    /**
     * 新增basicInfo
     * @return
     */
    void insertBasic();


    /**
     * 根据传参是否携带项目ID来判断是否查询全部项目的月报表
     * @return
     */
    List<Map> reportMonthSelect(Map map);

    /**
     * 根据传参是否携带项目ID来判断是否查询全部项目的周报表
     * @return
     */
    List<Map> reportWeekSelect(Map map);

    /**
     * 根据传参是否携带项目ID来判断是否查询全部项目的周报表
     * @return
     */
    public List<Map> reportBasicSelect(Map map);



    }
