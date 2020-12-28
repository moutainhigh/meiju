package cn.visolink.firstplan.openbeforeseven.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/9 12:26 下午
 */
@Mapper
public interface OpenBeforeSevenDayDao {
    //查询是否有此节点数据
    Map selectOpenApplyData(Map map);
    //获取当前计划节点下的版本数据
    List<Map> getVersionData(Map map);
    //获取首开审批主数据
    Map getOpenApplayMainData(String id);
    //获取首开均价数据
    List<Map> getFirstOpenAvgData(String id);
    //获取竞平情况数据
    List<Map> getCompetingpPoducts(String id);

    //获取顶设2核心指标数据
    Map getDesigntwoCoreData(String plan_id);
    //获取顶设2首开均价数据
    List<Map> getDesigntwoAvgPrice(String plan_id);
    //获取前两月客储达成进度数据
    List<Map> getCustomerStorageNode(String plan_id);
    List<Map> getDinsigTowCustomerPlan(String plan_id);
    //查询旭客汇客储达成进度数据
    Map getXukeCostomerStorageNode(Map map);
    //获取前两月周拆分数据
    List<Map> getWeekStroageData(String plan_id);


    //获取顶设2客储目标
    Map getCustomerInfoForDesigntwo(String plan_id);
    //获取旭客汇实际达成数据
    Map getXukeactCustomer(String  plan_id);

    List<Map> getCustomerStorageNodeData(Map map);
    List<Map> getWeekData(Map map);
    /**
     * 更新数据
     */
    void insertOpenApplayMainData(Map map);

    Map  createVserion(Map map);
    void insertCompetingpPoducts(Map map);
    void insertPriceAvg(Map map);
    void insertCustomerStoreFlow(Map map);
    void insertCustomerStore(Map map);
    void insertWeekData(Map map);
    void updateOpenApplayMainData(Map map);
    void clearSunData(Map map);

    String getPlanEndTime(String plan_node_id);

    Map getApplayDatas(String id);
    void updateApplayStatus(Map map);
    String getPlanNodeFinshTime(String id);
    void updatePlanNodeFinshTime(Map map);
    Map getApplayData(String id);

    void updateThisNodeforSevenDay(String plan_id);

    List<Map> getCustomerStorageNodeSeven(@Param("plan_id")String  plan_id,@Param("node_level") String node_level);

    List<Map> getWeekStroageDataSeven(@Param("plan_id")String  plan_id,@Param("node_level") String node_level);

    //查询审批上个版本通过的预估数据
    Map getApplayadopt(String plan_node_id);
    //查询审批通过的竞品情况
    List<Map> getApplayadotCompetingpPoducts(String plan_node_id);
    //查询延期开盘的日期
    String  getDelayOpenData(String plan_id);
    List<Map> getCustomerStorageNodeDataChange(Map map);
}
