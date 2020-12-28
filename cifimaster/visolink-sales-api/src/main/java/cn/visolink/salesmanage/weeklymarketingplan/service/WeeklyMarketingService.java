package cn.visolink.salesmanage.weeklymarketingplan.service;

import cn.visolink.exception.ResultBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface WeeklyMarketingService {

    /**
     * 初始化营销周计划详情表
     *
     * @return
     */
    public void weekMarketingPlanInitial();

    /**
     * 营销周计划详情上月未转签约认购金额（万元）或 大定未签（万元）月
     *
     * @return
     */
    public List<Map> WeeklyPlanRoomTotal( String BeginDate, String EndDate,Integer time);

    /**
     *  营销周计划详情月度签约金额（系统签约）或 周度签约金额（万元）-->
     *
     * @return
     */
    public List<Map> WeeklyPlanRmbTotal( String BeginDate, String EndDate);

    /**
     *  -营销周计划详情月度累计来人量（组）或周度来人量组-
     *
     * @return
     */
    public List<Map> WeeklyPlanCnt( String BeginDate, String EndDate);

    /**
     * 营销周计划详情月度目标部分字段
     *-- 月度目标 来人量（组）-- 月度目标 -- 签约（万元）
     * @return
     */
    public List<Map> WeeklyPlanMonthsTarget( String months);

    /**
     * 营销周计划详情月度目标部分字段
     *目标签约金额 来人量（组）
     * @return
     */
    public List<Map> WeeklyPlanWeekklyTarget( String months, Integer weekSerialNumber);



    /**
     * 提报营销周计划详情表
     *
     * @return
     */
    public ResultBody weekMarketingPlanEffective(Map map);

    /**
     * 跟新营销周计划详情表
     *
     * @return
     */
    public Integer weekMarketingPlanUpdate(Map map);
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
     * 营销周计划的查看
     *
     * @return
     */
    public List<Map> ProjectExamineWeekly(Map<String, Object> map);
    /**
     * 营销周计划得到某个月的所有周
     *
     * @return
     */
    public List<Map> weekMarketingWeekSelect(Map<String, Object> map);

    void weeklyDataExport(HttpServletRequest request, HttpServletResponse response, String this_time, int how_week, int i, String projectId,String areaReport);

    /*
     * 跟新已阅未阅状态
     * */
    Map updateCheckeds(Map map);

    public String reportExcel(HttpServletRequest request, HttpServletResponse response,Map map);

}
