package cn.visolink.salesmanage.weeklymarketingplan.dao;

import cn.visolink.salesmanage.weeklymarketingplan.model.WeekMarkting;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
/**
 * @author 孙林
 * @date:2019-10-15
 * */
@Mapper
@Repository
public interface WeeklyMarketingDao {


    /**
     * 初始化营销周计划详情表
     *
     * @return
     */
    public Integer weekMarketingPlanInitial(Map<String, Object> map);

    /**
     * 营销周计划详情上月未转签约认购金额（万元）或 大定未签（万元）月
     *
     * @return
     */
    public List<Map> WeeklyPlanRoomTotal(Map<String, Object> map);

    /**
     *  营销周计划详情月度签约金额（系统签约）或 周度签约金额（万元）-->
     *
     * @return
     */
    public  List<Map> WeeklyPlanRmbTotal(Map<String, Object> map);

    /**
     *  -营销周计划详情月度累计来人量（组）或周度来人量组-
     *
     * @return
     */
    public  List<Map> WeeklyPlanCnt(Map<String, Object> map);

    /**
     * 营销周计划详情月度目标部分字段
     *-- 月度目标 来人量（组）-- 月度目标 -- 签约（万元）
     * @return
     */
    public  List<Map> WeeklyPlanMonthsTarget(Map<String, Object> map);

    /**
     * 营销周计划详情月度目标部分字段
     *目标签约金额 来人量（组）
     * @return
     */
    public  List<Map> WeeklyPlanWeekklyTarget(Map<String, Object> map);

    /**
     * 营销周计划详情启动时间
     *
     * @return
     */
    public Map weekMarketingWeekRule();
    /**
     * 营销周计划详情启动时间(过度)
     *
     * @return
     */
    public Map weekMarketingWeekRuleReal();


    /**
     * 营销周计划得到某个月的所有周
     *
     * @return
     */
    public List<Map> weekMarketingWeekSelect(Map<String, Object> map);

    /**
     * 营销周计划所有项目ID
     *
     * @return
     */
    public List<Map> weekMarketingbusinessName();

    /**
     * 提报营销周计划详情表
     *
     * @return
     */
    public Integer weekMarketingPlanEffective(Map<String, Object> map);

    /**
     * 跟新营销周计划详情表
     *
     * @return
     */
    public Integer weekMarketingPlanUpdate(Map<String, Object> map);


    /**
     * 项目查看周计划
     *
     * @return
     */
    public List<Map> ProjectSelectWeekly(Map<String, Object> map);

    /**
     * 集团查看周计划
     *
     * @return
     */
    public List<Map> groupSelectWeekly(Map<String, Object> map);
    /**
     * 区域查看周计划
     *
     * @return
     */
    public List<Map> regionSelectWeekly(Map<String, Object> map);


    /**
     * 营销周计划的明细查看
     *
     * @return
     */
    public List<Map> ProjectExamineWeekly(Map<String, Object> map);

    /**
     * 查找集团项目名
     *
     * @return
     */
    public Map weekMarketingGroupName();

    /**
     * 将周计划营销表里所有已提交的项目合计到事业部去
     *
     * @return
     */
    public Integer ProjectSelectupdatesum(Map<String, Object> map);

    /**
     * 将周计划营销表里所有已提交的项目合计到集团去
     *
     * @return
     */
    public Integer AreaSelectupdatesum(Map<String, Object> map);
    /**
     * 根据projectID查询销售周计划数据
     * @param temMap
     * @return
     */
    Map selectWeeklyMarketPlanByProjectId(Map temMap);



    /**
     * 查询数据所有子数据
     * @param
     * @return
     */
    List<Map>  selectGapList();
/*
* 跟新已阅未阅状态
* */
   Integer updateCheckeds(Map map);

    /*
     * 查找某个区域下未上报项目
     * */
   void  selectAreaProject(Map map);

    /*
     * 查找项目project——code
     * */
    String selectCode(String s);

/*
* 初始化之前先删除
* */
    List<Map>  deleteBeforeInsert(Map map);
/*把插入改为更新*/
   Integer updateBeforeInsert(Map map);
    /*
    查找该条周数据是否已复核上报等
    */
    Map selectCheckeds(Map map);
    /*
    得到审批流里的部分数据
    */
    Map  getFlowStatus(String id);
        /*
        得到审批流里的部分数据
        */
    Map  getFlowId(String id);
    /*
    更新审批流*
    */
    Integer updateFlowData(Map map);
        /*
        查找窗口期*/

    Map  selectWindowTime(Map map);
    /*
           查找所有的操盘数据*/
      List<Map>   selectBasicTrader();

   /* 根据projectId查询周销售计划数据子数据,导出专用-*/
   List selectWeekMarketByFatherIdByExport(WeekMarkting map);
    /* 根据projectId查询周销售计划数据,导出专用-*/


    WeekMarkting  selectWeeklyMarketPlanByProjectIdByExport(Map map);

}
