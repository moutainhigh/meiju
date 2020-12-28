package cn.visolink.salesmanage.packageanddiscount.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/25 11:20 上午
 */
@Mapper
@Repository
public interface PackageanddiscountDao {

    //查询当前登陆人有权限的项目
    List<Map> getJurisdictionProjects(String userId);
    //查询原有分期政策明细数据
    List<Map> getStageOriginalItemData(String project_id);
    //查询原有折扣政策明细数据
    List<Map> getDisOriginalItemData(String project_id);
    //查询当前项目下的所有楼栋
    List<Map> getBuildDataByProject(String project_id);
    //查询一揽子分期/折扣主数据
    Map getPackageStageMainData(String json_id);
    //查询一揽子分期明细数据（原有/新）
    List<Map> getStageOldAndNewData(@Param("json_id")String json_id,@Param("type")String type);
    //查询一揽子折扣明细数据（原有/新）
    List<Map> getDisOldAndNewData(@Param("json_id")String json_id,@Param("type")String type);
    //查询当前登陆人详细数据
    Map getUserInfo(String userid);

    //修改一揽子分期/折扣主数据
    void updatePackageStageMainData(Map map);

    /**
     * 修改政策主表
     * bql 2020/07/20
     *
     * @param map map
     * */
    void updateCmPlicySales(Map map);

    /**
     * 将销售政策数据添加中间表
     * bql 2020/07/20
     *
     * @param map map
     * */
    void insertPolicyOrgRel(Map map);

    /**
     * 将销售政策数据添加到政策主表
     * bql 2020/07/19
     *
     * @param map map
     * */
    void insertCmPolicySales(Map map);

    //添加一揽子分期/折扣主数据
    void insertPackageStageMainData(Map map);
    //清楚一揽子分期/折扣附属数据
    void clearSubsidiaryData(String id);

    //添加一揽子分期数据
    void insertStageItem(Map map);

    //修改一揽子分期数据
    void updateStageItem(Map map);

    //添加一揽子折扣数据
    void insertStageDisItem(Map map);

    //修改一揽子折扣数据
    void updateStageDisItem(Map map);
    //获取明源已售房间数量
    Map  getSoldBuildData(Map map);

    //添加流程数据
    void insertFlowData(Map map);


    /**
     * 查询一揽子分期折扣申请列表数据
     * @param map
     * @return
     */

    List<Map> getApplayList(Map map);

    Integer getApplayListCount(Map map);

    /**
     * 查询一揽子分期折扣申请列表数据加权限查询
     *  bql 2020.07.21
     *
     * @param map map
     * @return list
     */
    List<Map> getApplayPolicyList(Map map);

    /**
     * 销售政策新查询总量加权限
     *  bql 2020.07.21
     *
     * @param map map
     * @return list
     */
    Integer getApplayPolicyListCount(Map map);


    //查询事业部数据
    Map getBuinessData(String project_id);

    Map getBuinessDataByOrgId(String orgId);


    //查询是否存在审批中的申请单
    Map getIsHaveApplay(String project_id);

    Map getFlowData(String json_id);

    void updateFlowData(Map map);


    //获取流程json数据
    Map getFlowJsonData(String flow_id);

    List<Map> getStageItemPushData(String json_id);
    List<Map> getDisItemPushData(String json_id);
    String  getJDProjectID(String project_id);
    void insertParamLog(String params);


    void updateStageItemPrimaryKey(Map map);
    void updateDisItemPrimaryKey(Map map);

    void updatePackageStageId(Map map);

    Map getFlowStatus(String json_id);
    //清楚当前流程添加的新增分期数据
    void clearAddStageItemData(@Param("json_id") String json_id);
    void clearAddDisItemData(@Param("json_id")String json_id);
    //查询当前流程是否审批通过
    public Map getShenpiTongGuo(@Param("json_id")String json_id);
}
