package cn.visolink.salesmanage.groupmanagement.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author  wjc
 * @date 2019-09-17
 *
 *
 *
 *
 */
@Mapper
public interface GroupManageDao {
    /**
     * 获取事业部数据
     * @param map
     * @return
     */
    int getBusiness(Map map);
    int deleteBasic(Map map);
    int deleteBasicIndex(Map map);

    /**
     * 修改区域前三月的成交率
     * @param map
     * @return
     */
    int updateRegionThreeMonth(Map map);

    /**
     * 修改区域前一月的成交率
     * @param map
     * @return
     */
    int updateRegionOneMonth(Map map);

    /**
     * 修改项目前三月的成交率
     * @param map
     * @return
     */
    int updateProjectThreeMonth(Map map);

    /**
     * 修改项目前一月的成交率
     * @param map
     * @return
     */
    int updateProjectOneMonth(Map map);

    /**
     * 修改集团前三月的成交率
     * @param map
     * @return
     */
    int updateGroupThreeMonth(Map map);

    /**
     * 修改集团前一月的成交率
     * @param map
     * @return
     */
    int updateGroupOneMonth(Map map);

    /**
     * 修改集团的年度计划签约
     *
     * @return
     */
    int updateGroupYearSign(Map map);
    /**
     * 修改区域的年度计划签约
     *
     * @return
     */
    int updateRegionYearSign(Map map);
    /**
     * 修改项目的年度计划签约
     *
     * @return
     */
    int updateProjectYearSign();

    /**
     * 修改集团供货值
     * @param map
     * @return
     */
    int updateGroupSupply(Map map);

    /**
     * 修改区域供货值
     * @param map
     * @return
     */
    int updateRegionSupply(Map map);

    /**
     * 修改项目供货值
     * @param map
     * @return
     */
    int updateProjectSupply(Map map);

    /**
     *修改分期供货值
     * @param map
     * @return
     */
    int updateInstallmentSupply(Map map);

    /**
     * 修改组团供货值
     * @param map
     * @return
     */
    int updateGroupcodeSupply(Map map);

    /**
     * 修改业态供货值
     * @param map
     * @return
     */
    int updateProduceSupply(Map map);
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
     * 添加月度计划主表
     * @param map
     * @return
     */
    int insertMonthPlan(Map map);
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
     * 暂存，下达，上报 （修改状态）
     * @param map
     * @return
     */
    int updatePlanStatus(Map map);

    /**
     * 修改状态 （生效未生效）
     * @param map
     * @return
     */
    int updateIsEffevtive(Map map);
    /**
     * 区域月度计划查询
     * @param map
     * @return
     */
    List<Map> getRegionalMonthPlan(Map map);

    /**
     * 获取集团给区域下的数据
     * @param map
     * @return
     */
    int getGroupReleaseRegional(Map map);

    /**
     * 获取区域的合计
     * @param map
     * @return
     */
    int getRegionalAggregate(Map map);
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
     * @param
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

    /**
     * 修改集团的明源数据
     * @param map
     * @return
     */
    int updateGroupmy(Map map);

    /**
     * 修改区域的明源数据
     * @param map
     * @return
     */
    int updateRegionmy(Map map);

    /**
     * 修改为空的值
     * @param map
     * @return
     */
    int updateIsnull(Map map);


    /**
     * 修改项目供货值、套数、版本
     * @param map
     * @return
     */
    int updateProjectValue(Map map);
    int updateTotalPlan(Map map);
    /**
     * 修改组团供货值、套数、版本
     * @param map
     * @return
     */
    int updateGroupcodeValue(Map map);

    /**
     * 修改分期供货值、套数、版本
     * @param map
     * @return
     */
    int updateInstallmentValue(Map map);

    /**
     * 修改业态供货值、套数、版本
     * @param map
     * @return
     */
    int updateProductValue(Map map);

    /**
     * 修改面地段供货值、套数、版本
     * @param map
     * @return
     */
    int updateHousePackageValue(Map map);

    /**
     * 修改区域供货值、套数、版本
     * @param map
     * @return
     */
    int updateAreaValue(Map map);

    /**
     * 修改集团供货值、套数、版本
     * @param map
     * @return
     */
    int updateGroupValue(Map map);


    /**
     * 初始化年度签约金额
     */
    void initYearPlanSignData(Map map);
    /*
     * 通过集团状态确认是否可以跳供消存
     * */
    List<Map> getGroupMonthPlanForProject(Map map);

    Integer updateMonthlyPlanBasis(Map map);
}
