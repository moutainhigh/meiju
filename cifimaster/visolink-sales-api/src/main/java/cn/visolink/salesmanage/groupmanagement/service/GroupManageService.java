package cn.visolink.salesmanage.groupmanagement.service;

import java.util.List;
import java.util.Map;

/**
 * @author wjc
 */
public interface GroupManageService {
    /**
     * 获取事业部数据
     * @param map
     * @return
     */
    int getBusiness(Map map);
    /**
     * 查询月度计划表中是否存在这个月份
     * @param map
     * @return
     */
    List<Map> selectPlanMonth(Map map);
    /**
     * 集团月度计划查询
     * @param map
     * @return
     */
    List<Map> getGroupMonthPlan(Map map);
    /**
     * 添加月度计划
     * @param map
     * @return
     */
    int insertMonthPlanBasis(Map map);

    /**
     * 添加月度计划指标
     * @param map
     * @return
     */
    int insertMonthPlanIndex(Map map);

    /**
     * 查询集团月度计划的区域数据
     * @param map
     * @return
     */
    List<Map> getGroupAllMessage(Map map);

    /**
     * 查询集团下子级的数据
     * @param map
     * @return
     */
    List<Map> getGroupChildMessage(Map map);

    /**
     *
     * 暂存，下达，上报 （修改状态）
     * @param map
     * @return
     */
    int updatePlanStatus(Map map);


    /**
     * 区域月度计划查询
     * @param map
     * @return
     */
    List<Map> getRegionalMonthPlan(Map map);


    /**
     * 在区域里获取集团下达数据
     * @param map
     * @return
     */
    List<Map> getGroupReleaseInRegional(Map map);

    /**
     *根据区域id获取事业部数据
     * @param map
     * @return
     */
    List<Map> getBusinessForRegional(Map map);

    /**
     * 根据项目id获取事业部数据
     * @param map
     * @return
     */
    List<Map> getBusinessForProject(Map map);

    /**
     *添加区域月度计划指标
     * @param map
     * @return
     */
    int insertRegionalMonthPlanIndex(Map map);

    /**
     * 获取区域初始化数据
     * @param map
     * @return
     */
    List<Map> getRegionalMessage(Map map);
    /**
     * 判断区域月份是否存在
     * @param map
     * @return
     */
    int selectRegionalMonth(Map map);

    /**
     * 判断项目月份是否存在
     * @param map
     * @return
     */
    int selectProjectMonth(Map map);
    /**
     * 区域指标细化
     * @param map
     * @return
     */
    List<Map> getRegionChildMessage(Map map);
    /**
     * 项目月度计划查询
     * @param map
     * @return
     */
    List<Map> getProjectMonthPlan(Map map);
    /**
     * 从项目月度计划获取区域下达数据
     * @param map
     * @return
     */
    List<Map> getRegionalReleaseInProject(Map map);
    /**
     *添加项目月度计划指标
     * @param map
     * @return
     */
    int insertProjectMonthPlanIndex(Map map);

    /**
     * 获取项目初始化数据
     * @param map
     * @return
     */
    List<Map> getProjectMessage(Map map);

    /**
     *
     * @param projectId
     * @return
     */
    Map getProjectAreaID(String projectId);

    /**
     * 查询区域下项目未上报的条数
     * @param map
     * @return
     */
    int getRegionUnderProject(Map map);

    /**
     * 获取集团下区域未上报的条数
     * @param map
     * @return
     */
    int getGroupUnderRegion(Map map);
    /**
     *修改集团的状态
     * @param map
     * @return
     */
    int updateGroupPlanStatus(Map map);

    /**
     * 修改区域的费用
     * @param map
     * @return
     */
    int updateRegionFunds(Map map);

    /**
     * 修改项目的费用
     * @param map
     * @return
     */
    int updateProjectFunds(Map map);

}
