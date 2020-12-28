package cn.visolink.firstplan.TaskLand.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 *  * @author wyg
 *  * @since 2020-02-17
 */
@Mapper
public interface TakeLandDao {

    /**
     * 查询区域名称
     * */
    public List<Map> queryRegion(Map map);
    /*
    * 查询佣金
    * */
    public List<Map> queryCMRegion(Map map);

    /**
     * 查询四级组织
     * */
    public List<Map> queryOrgListByOrgId(String orgId);

    public List<Map> queryOrgListByCityOrgId(String orgId);

    /**
     * 查询城市名称
     * */
    public List<Map> queryCitys(Map map);

    /**
     *查询区域项目
     * @param map
     * @return
     */
    public Map queryPrpject(Map map);

    /**
     *通过项目查询id
     * @param map
     * @return
     */
    public List<Map> queryPlan(Map map);

    public Map queryPlanValue(Map map);
    /**
     *添加计划
     * @param map
     * @return
     */
    public void insertPlan(Map map);

    /**
     *添加计划节点
     * @return
     */
    public void insertPlNde(List<Map> list);

    /**
     *添加货值结构
     * @return
     */
    public void insertValueStructure(@Param("tructureList") List<Map> list);

    /**
     * 添加户型
     * */
    public void insertRoom(@Param("roomList") List<Map> list);

    /**
     * 添加费用
     * */
    public void insertCost(List<Map> list);

    /**
     * 添加时间节点
     * */
    public void insertTimeNode(Map map);

    /**
     * 添加销售目标
     * */
    public void insertSales(@Param("salesList") List<Map> list);

    /**
     * 查询节点信息
     * */
    public List<Map> selectPlanNode(Map map);

    /**
     * 查询附件表
     * */
    public Map queryAttach(Map map);

    /**
     *
     * 以下接口皆是查询拿地后数据
     * */
    public List<Map> queryValueStructure(Map map);
    public List<Map> queryApartment(Map map);
    public List<Map> queryTimeNode(Map map);
    public List<Map> querySalesTarget(Map map);
    public List<Map> queryCost(Map map);

    /**
     *查询工作内容、成果模板等
     * */
    public List<Map> queryContent();

    /**
     * 查询首页数据
     * */
    public List<Map> queryByProjectId(Map map);

    /**
     * 添加顶设一货值结构
     * */
    public void insertTopOneValues(@Param("topOneList") List<Map> list);

    /**
     * 添加顶设一户型
     * */
    public void insertTopOneApartment(List<Map> list);
    /**
     * 添加顶设一销售目标
     * */
    public void insertTopOneSales(List<Map> list);

    /**
     * 查询节点版本
     * */
    public List<Map> selectNodeVersion(Map map);

    /**
     * 删除拿地版数据
     * */
    void deleteTakeLandValue(Map map);
    void deleteTakeLandRoom(Map map);
    void deleteTakeLandDate(Map map);
    void deleteTakeLandSales(Map map);
    void deleteTakeLandCost(Map map);

    void updateNodeTime(Map map);

    void updateTakeLandStatus(Map map);

    List<Map> queryPlanNodeId(Map map);


    /**
     * 删除顶设一数据
     * */
    void deleteDesignoneValue(Map map);
    void deletedesignoneRoom(Map map);
    void deletedeSalesTarget(Map map);

    public List<Map> queryByTopOne(Map map);

    //获取文件数据
    public List getFileLists(@Param("id") String id);

    /**
     * 顶设一数据查询
     * */
    public List<Map> queryTopOneValue(Map map);

    public List<Map> queryTopOneRoom(Map map);

    public List<Map> queryTopOneSales(Map map);

    Map querytakeLandNdId(Map map);

    Map queryTopOneOnly(Map map);

    void updateSattach(Map map);

    void delFile(@Param("BizID") String BizID);

    void updateNodeEffective(Map map);

    Map queryPlanNodeNum(Map map);

    List queryTopNumber(Map map);

    public void insertFlow(Map map);

    public  int updateFlowInfoByJsonId(Map map);

    void insertflowLog(@Param("flow_param") String flow_param);

    void updateNdostatus(Map map);

    void updatePlan(Map map);

    Map selectFlowInfoByJsonId(String json_id);

    Map selectTmmProjectByProjectId(String project_id);

    void updateTimeArrangementTakeLand(Map map);
    void updateTimeArrangement(Map map);

    Map queryPeopleMessage(Map map);

    Map selectIndexTime(Map map);

    List<Map> queryPlanNodeMessage(Map map);

    void insertBugLog(@Param("messages") String messages);

    public Map selectTwoMonth(@Param("plan_id")String plan_id,@Param("node_level")String node_level);

    public Map getSevenDay(String plan_id);

    public Map getOpenDay(String plan_id);
}
