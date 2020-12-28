package cn.visolink.firstplan.openbeforetwentyone.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/6 1:59 下午
 */
//开盘前21天--延期开盘申请
@Mapper
public interface OpenbeforetwentyoneDao {
    //查询延期开盘申请表中是否有数据
    List<Map> selectIsHaveDelayApplyData(Map map);
    //查询版本数据
    List<Map> selectVersionData(Map map);
    List<Map> getVsersionData2(Map map);
    //查询延期开盘申请表数据
    Map selectExtensionOpenApplyData(String id);
    //查询客储计划调整数据
    List<Map> selectCustomerStorageNodeData(String id);
    //查询顶设2的战规版开盘日期
    Map getDingsheTwoDateData(Map map);
    Map getDingsheTwoOpenTime(Map map);
    //查询开盘前三个月的客储计划调整数据
   Map getPlanCustomerData(Map map);
   //查询旭客客储计划调整数据
    Map getXukeCustomerData(Map map);
    //获取延期开盘申请的周拆分数据
    List<Map> selectWeekData(String delay_id);
    //保存延期开盘申请数据
    void  insertDelayApplyData(Map map);
    //修改延期开盘数据
    void updateDelayApplyData(Map map);
    //创建版本
    Map createVserion(Map map);
    //添加客储计划数据
    void insertCustomerNodeStorage(Map map);
    //修改客储计划数据
    void updateCustomerNodeStorage(Map map);
    //添加周拆分数据

    void insertWeekData(Map map);
    //修改周拆分数据
    void updateWeekData(Map map);

    //删除周拆分数据
    Integer delteWeekData(Map map);
   Integer delteWeekDataNodePlan(Map map);
    void updatePlanTime(Map map);

    List<Map> getCustomerStorageNodeData(Map map);
    void createFLowVersion(Map map);

    Map getWaringDay(Map map);
    void updateNodesPlanStartTime(Map map);
    String  getFlowId(String  id);
    void updateThisNodeforTwenDay(Map map);
    Map getAppllayDataInfo(String id);
    String getFinshTimeTwentyDay(String id);
    void updateApplayStatus(Map map);
    void updateFinishDate(String id);
    Map getApplayDateTime(String id);

/*查找该节点下每个流程表最大的周*/
  List<Map>  selectMaxWeek(List<Map> map);

  /*查找该节点下流程表ID*/
    List<Map>   selectAllFlowId(Map map);

    /*通过flowId来查找周拆分数据*/
    Map  selectWeekFlowData(String id);

    /*延期开盘申请表里的客储计划固定的只有三条，那么就以此为条件，找出延期开盘的flow_id*/
    String  selectDelayID(String id);

    /*根据延期开盘而新增一些周计划的数据*/
  Integer  insertDelayStorageWeek(Map map);

  /*更改没有week的数据,插入week*/
    Integer  insertWeekForDelay(Map map);
    /*更改没有week的数据,插入week*/
    List<String> selectAllNoWeek(Map map);
        /*更改客储计划因为延期开盘新增的部分*/
    Integer updateNewNodePlan(Map map);


    /*测试是否要初始化开盘前21天的客储达成进度*/
    Map selectTestOneTwo(Map map);
    /*查找所有客储计划的实际和偏差率需要进行更新的NODEPLAN*/
    List<Map> selectAllNeedUpdate(Map map);
        /*初始化节点储客计划延期开盘专用*/
    Integer insertNewStorage(Map map);
    Integer deleteNewStorage(Map map);
/*更改开盘时间*/
    Integer  updateOpenTime(Map map);

        /*deletePlusWeek*/
    Integer   deletePlusWeek(Map map);

   String selectStartTime(String map);

    Integer  updateSixLight(String map);

    Integer  updateSevenLight(Map map);

    List<Map> getSubsidiaryProject(String project_id);
    String selectWeekDataTotal(String id);

    //获取当前节点已经审批通过的最新数据
    Map getApplayDepotTwentyData(Map map);
    //获取当前节点已经审批通过的最新客储数据
    List<Map> getApplayDepotCustomerStorge(String id);
    /**
     * 修改计划界别的开盘日期
     */
    public void updateOpenTimeForPlan(Map map);
}
