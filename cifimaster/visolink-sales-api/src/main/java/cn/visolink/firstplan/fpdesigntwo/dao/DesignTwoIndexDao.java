package cn.visolink.firstplan.fpdesigntwo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/*顶设2 */
@Mapper
@Repository
public interface DesignTwoIndexDao {

    /*搜索来自顶设1的产品类型,投资版货值，战归版货值，全盘量价规划里
    整盘投资，整盘战规，投资版总货值，战规总货值，在顶设2全盘量价规划里用*/
    List<Map> selectDesignOneType(Map map);

/*搜索顶设2核心指标里，
  核心指标里首开均价，首开均价战归是手填,顶设2物业类型默认是顶设1的，全盘费率手填*/
List<Map> selectAllCodeIndexType(Map map);

/*仅搜索时间表，在核心指标里当库里没有数据里时使用*/
   Map  selectAllCodeIndexTime(Map map);

/*搜索顶设2核心指标里的信息，顶设2-核心指标-量+利,以及时间表*/
    Map selectAllCodeIndex(Map map);

    /*搜索顶设2核心指标里的信息，顶设2-核心指标-价*/
    List<Map> selectAllCodeIndexPrice(Map map);

/*更新顶设2核心指标里的信息，顶设2-核心指标-价*/
Integer updateAllCodeIndexPrice(Map map);

/*删除核心指标的价*/
 Integer deleteIndexPrice(Map map);

 /*删除全盘量价规划的年份信息*/
 Integer deleteAllYear(Map map);

    /*更新顶设2核心指标里的信息，顶设2-核心指标-量+利*/
   Integer   updateAllCodeIndex(Map map);

   /*仅更新顶设2核心指标里的时间表*/
    Integer  updateAllCodeIndexTime(Map map);

    /*标注状态 如果修改了时间 把状态修改1  初始化费用*/
    int updateInitOpenCostStatus(Map map);
    /**
    * 获取顶设2 开盘时间
    * */
    Map getDesignTwoAndOpenDate(Map map);
    /*初始化顶设2核心指标里的信息，顶设2-核心指标-量+利*/
    Integer  insertAllCodeIndex(Map map);

    /*初始化顶设2核心指标里的信息，顶设2-核心指标-价*/
    Integer insertAllCodeIndexPrice(List list);

    /*顶设2全盘量价规划查找和大定价版本对标*/
    List<Map> selectAllPlan(Map map);

    /*顶设2全盘量价规划里的年份信息查找*/
    List<Map> selectAllPlanYear(Map map);

    /*顶设2查找全盘量价规划里大定价版本对标*/
    List<Map> selectAllPlanBig(Map map);


    /*顶设2全盘量价规划更新和大定价版本对标*/
    Integer   updateAllPlan(Map map);

    /*顶设2全盘量价规划更新和大定价版本对标*/
    Integer   updateAllPlanYear (Map map);

    /*顶设2更新大定价版本对标*/
    Integer   updateAllPlanBig(Map map);

    /*初始化全盘量价规划里的大定价全盘量价规划*/
    Integer  insertAllPlan(List list);

    /*初始化全盘量价规划里的大定价全盘量价规划里的年份信息*/
    Integer  insertAllPlanYear(List list);

    /*初始化全盘量价规划里的大定价版本对标*/
    Integer  insertAllPlanBig(List list);

    /*顶设2楼栋大定价*/
    List<Map> selectBigPrice(Map map);

    /*顶设2查找客储计划周拆分和节点储客计划*/
    List<Map> selectStorageNodePlan(Map map);

    /*顶设2查找客储计划周拆分*/
    List<Map>  selectStorageweek(Map map);

    /*顶设2更新客储计划节点储客计划*/
    Integer updateStorageNodePlan(Map map);

    /*顶设2更新客储计划周拆分*/
    Integer updateStorageWeek(Map map);



    /*仅搜索时间表，满足顶设2储客计划页面里的天数*/
    List<Map> selectAllnodeplanTime(Map map);

    /*顶设2初始化客储计划周拆分*/
    Integer  insertStorageWeek(List list);

    /*顶设2初始化客储计划节点储客计划*/
    Integer insertStorageNodePlan(List list);

     /*查找顶设2左上角的版本选择*/
    List<Map> selectPlanNode(Map map);

    /*创建版本*/
 Integer insertPlanNode(Map map);

 /*判断是否可以创建新版本*/
 Map judgeVersion(Map map);

 /*仅初始化顶设2核心指标里的时间表*/
 Integer insertAllCodeIndexTime(Map map);

 /*仅搜索顶设2时间表*/
Map  selectDesignIndexTime(Map map);
 /*更新完储客计划后要检查一下时间节点和计划表能否对的上，对不上需要重新初始化并保留原有数据*/
 Map selectPlanTime(Map map);

/*暂存过后将状态设置为草稿*/
 Integer updateLightStuat(Map map);

 /*判断顶设2是否可以发起一个新的流程,若有返回值说明不合格*/
 List<Map> designTwoCan(Map map);
/*查找来自顶设1的业态类型*/
 List<Map> selectDesignOperation(Map map);

 /*删除客储计划表*/
 Integer deleteStorageNodePlan(Map map);
 /*删除周拆分表*/
 Integer deleteStorageweek(Map map);

 /*搜索计划ID的时间表*/
 Map selectPlanReal(Map map);

 /*转换计划表当前节点*/
Integer updateNodeName(Map map);

/*初始化储客计划FLOW表*/
 Integer insertNodeFlow(Map map);


 /*查找表单版本*/
 List<Map> selectStorageFlow(Map map);

 /*流程表的状态更改*/
 Integer upateFlow(Map map);

 /*审批后将储客表的数据更新到对应的节点表*/
 Integer updateForTest(String id);

/*判断顶设2是否可以发起一个新的流程,若没有返回值说明不合格*/
 List<Map> designTwoCanElse(Map map);

 List<Map>  selectStorageFlowTwo(Map map);

 /*审批完成后自动算灯的状态*/
 Integer updateRealDate(Map map);

/*更新开盘时间*/
Integer updatePlanTime(Map map);

/*查找顶设1的年份*/
List<String> selectDesignOneYear(Map map);

/*先删除当前节点所有的附件，然后再选择哪些附件是留下的*/
 Integer DeleteAttach(@Param("id") String id);
/*查找项目名*/
Map selectProjectName(@Param("id") String id);

 List<Map> selectDesignOneTypeBig(Map map);

 /*更改审批完成后流程状态*/
 Integer updateRealLight(Map map);
/*查找该FLOW表的节点表的ID*/
Map selectPlanNodeId(Map map);
 /*查找PLAN_ID*/
 Map selectPlanId(Map map);
 /*开盘前3修改*/
 Integer updateThreeMonthsType(Map map);

 /*取拿地后的营销费率*/
String selectLandPer(Map map);

 void insertOpenCost(@Param("list") List list,@Param("commun") String commun,@Param("rescommun") String rescommun,@Param("version") String version);

 void updateOpenCost(Map params);

 List selectOpenCostByPlanNodeId(String plan_node_id);

 List selectNewOpenCost(@Param("plan_node_id") String plan_node_id,@Param("version") String version);

 /*
 * 获取最新版本的科目
 * */
 List<Map> getNewSubject(String version);

 String getSubjectByVersion(String version);

 List<Map<String,Object>> getOpenCostByMonth(@Param("months") String months,@Param("plan_node_id") String plan_node_id);

 List<Map<String,Object>> getNewOpenCostByMonth(@Param("months") String months,@Param("plan_node_id") String plan_node_id,@Param("version") String version);
 int delOpenCostByMonth(@Param("months") String months,@Param("plan_node_id") String plan_node_id);

 void delOpenCostByPlanNodeId(String plan_node_id);

 /*查找顶设2暂存的版本*/
 String selectApprovalTen(Map map);

 List<Map> selectProjectCode(Map map);

 Map selectAllArea(Map map);

 Map selectDeviationRate(Map map);

 /*若顶设2重新铺排时间，那么往后的节点已经提报的都要变*/
 Integer UpdateAllNodeTime(Map map);

 /*查找已经提交的且要重新初始化的节点*/
 List<Map> selectEffectiveNode(Map map);

 /*插入开盘后一个月版本*/
 Integer insertBroadcast(Map map);

/*判断上一个节点是否有被略过*/
 String judgeEtc(Map map);

 /*查找最近的完成时间给新节点赋上*/
 Map selectDesignTwoTime(Map map);

 /*查找已经提交的且没有版本需要新创建版本的节点*/
 List<Map> selectNeedNewNode(Map map);

 /*判断当前节点是否是被略过节点，若是，即便下面的节点已经提报，改节点也可以编辑*/
 Map judgeCanWrite(Map map);

 /*要提报的时候修改顶设2的计划完成时间*/
Integer updateRealTwoTime(Map map);

 Map selectPlanIDElse(Map map);
/*顶设2重新铺排时间后将延期开盘申请表里的流程不作数*/
 Integer updateDelaySeven(Map map);
/*废弃已经掠过的节点的历史版本*/
 Integer deleteForNo(Map map);
/*专用于创版逻辑-*/
 Integer forNewNodePlanOne(Map map);
 Integer forNewNodePlanTwo(Map map);
 Integer forNewNodePlanThree(Map map);
 Integer forNewNodePlanFour(Map map);

 List<Map> selectStorageweekFake(Map map);

 List<Map> selectStorageNodePlanFake(Map map);
 Integer updatePlanForBack(Map map);
 Integer updateFlowForBack(Map map);

  String selectFlowCode(String s);

/*顶设2货值结构部分*/
List<Map> getDesignTwoRoom(Map map);
 List<Map> getDesignTwoValue(Map map);

 List<Map> getTopOneValue(Map map);
 List<Map>    getTopOneRoom(Map map);

 Integer deleteDesignTwoValue(Map map);

 Integer deleteDesignTwoRoom(Map map);

 Integer insertDesignTwoValue(List<Map> list);
 Integer insertDesignTwoRoom(List<Map> list);
 /*推翻顶设2*/
 Integer backDesignTwo(Map map);
/*来自顶设2货值结构的产品类型*/
 List<Map> selectTwoOperation(Map map);

 List<Map> selectTwoProductType(Map map);

 List<Map> selectDesignTwoTypeBig(Map map);

 Integer deleteAllPlan(Map map);

 Integer deleteAllPlanBig(Map map);

 List<Map>  selectAllCodeTypeForPrice(Map map);

 int insertSubject(Map map);

 int getMaxVersion();

 int getSubjectName(String subjectName);

 int addCommunCost(Map map);

 List<Map> getSubject();

 int updateSubject(Map map);
}
