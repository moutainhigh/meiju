package cn.visolink.firstplan.fpdesigntwo.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DesignSevenDao {



    /*开盘前7天数据，首开审批表数据*/
    Map selectSevenDayIndex(Map map);

    /*初始化开盘前7天数据，首开审批表数据*/
   Integer insertSevenDayIndex(Map map);

    /*跟新开盘前7天数据，首开审批表数据*/
    Integer updateSevenDayIndex(Map map);

    /*初始化7天开盘的竞品情况*/
    Integer insertSevenCompet(List<Map> list);

    /*更新7天开盘的竞品情况*/
    Integer updateSevenCompet(Map map);

    /*搜索7天开盘的竞品情况*/
    List<Map>  selectSevenCompet(Map map);

    /*初始化开盘前7天里面的首开均价*/
    Integer insertSevenPrice(Map map);

    /*搜索开盘前7天里面的首开均价*/
    List<Map>  selectSevenPrice(Map map);

    /*搜索开盘前7天里面的首开均价的比值*/
    Map   selectSevenPriceAvg(Map map);

    /*搜索开盘前7天里面的首开均价的产品类型*/
    List<String>   selectSevenType(Map map);
    /*更新开盘前7天里面的首开均价*/
    Integer updateSevenPrice(Map map);



/*初始化节点储客计划表*/
Integer insertNewStorage(Map map);

/*查找节点储客计划 */
 List<Map>  selectStoragePlan(Map map);

 /*查找周计划拆分计划 */
 List<Map> selectStorageWeek(Map map);

 /*初始化周计划拆分计划*/
 Integer  insertNewStorageWeek(Map map);

 /*初始化7天开盘的竞品情况*/
 Integer insertSevenCompet(Map map);

/*通过前端传值来初始化7天开盘的首开均价，适用于开一个新版本*/
 Integer insertSevenPriceNew(List<Map> list);

 /*初始化开盘前7天数据，首开审批表数据,发起新版本适用*/
 Integer insertSevenDayIndexNew(Map map);
/*更新开盘前7天的时候同时要更新计划表开盘时间*/
 Integer updateOpenTime(Map map);

/*找到首开审批表数据，来自别的表*/
Map  selectNewSevenDayIndex(Map map);

/*搜索可以初始化的开盘前7天里面的首开均价，来自别的表*/
List<Map> selectNewSevenPrice(Map map);

/*删除前7天里面的首开均价，用于特殊逻辑*/
 Integer deleteSevenPriceAvg(Map map);

 /*判断节点是否可以发起一个新的流程,若有返回值说明不合格*/
 List<Map> designAllCan(Map map);

 /*用于初始化后删除库里数据*/
 Integer deleteForPlanId(String id);

 /*更改开盘前3个月等页面的变更客储计划字段*/
 Integer updateChangeState(Map map);

 Integer selectChangeState(Map map);

 Integer insertStorageNodePlan(List<Map> list);
/*检查顶设2是否有一个审批通过的版本*/
 Integer selectDesignTwoFour(Map map);
/*判断有做过延期开盘这件事没21天专用*/
 Map selectCountSix(Map map);
/*查找开盘前21天里最新的版本*/
List<Map> selectBestNewSix(Map map);
/*查找开盘前21天里的周拆分*/
List<Map> selectSixWeekList(Map map);

 Integer insertNewStorageWeekFake(Map map);

 Integer insertNewStorageFake(Map map);

 Integer deleteOnlyNode(Map map);
 /**
  * 获取三大件时间节点
  */
 public Map getThreepiecesForSalesTimes(Map map);
 public Map getThreepiecesForSampleTimes(Map map);


 /**
  * 更新客储节点
  */
 public void initCustomerStorageData(Map map);
 /**
  * 清空新添加的数据
  */
 public void clearNewData(String flow_id,String plan_node_id);
 /**
  * 查询新生成的客储数据
  */
 public List<Map> getCustomerStorageByFlow(String flow_id);
 /**
  * 查询当前版本状态
  */
 public String getCustomerFlowStatus(String thisNode_flow_id);
 /**
  * 查询最新的周拆分数据
  */
 public List<Map> getWeekDataByPlanNode(String plan_node_id);
 /**
  * 若项目进行过抢开，使用这个更新客储数据
  */
 public void initCustomerStorageDataQk(Map map);
}
