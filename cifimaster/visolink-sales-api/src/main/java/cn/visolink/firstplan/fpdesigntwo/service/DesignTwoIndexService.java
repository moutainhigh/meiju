package cn.visolink.firstplan.fpdesigntwo.service;

import cn.visolink.salesmanage.fileexport.model.MonthPlan;
import cn.visolink.utils.Constant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.core.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


public interface DesignTwoIndexService {


    /*搜索顶设2核心指标里的信息，顶设2-核心指标-量+利*/
    Map selectAllCodeIndex(Map map);

    /*更新顶设2核心指标里的信息，顶设2-核心指标-量+利*/
    Integer   updateAllCodeIndex(Map map, HttpServletRequest request );


    /*初始化顶设2核心指标里的信息，顶设2-核心指标-量+利*/
    Integer  insertAllCodeIndex(Map map);

    /*顶设2全盘量价规划查找和大定价版本对标*/
    Map selectAllPlan(Map map);

    /*顶设2全盘量价规划更新和大定价版本对标*/
    Integer   updateAllPlan(Map map,HttpServletRequest request );

    /*顶设2楼栋大定价*/
    List<Map> selectBigPrice(Map map);

    /*顶设2查找客储计划周拆分和节点储客计划*/
    Map selectStorageNodePlan(Map map);

    /*顶设2更新客储计划周拆分和节点储客计划*/
    Integer updateStorageNodePlan(Map map,HttpServletRequest request );

    /*查找顶设2左上角的版本选择*/
    List<Map> selectPlanNode(Map map);

    /*插入一条版本信息*/
    public String forPlanNode(Map map,HttpServletRequest request);

    /*判断是否可以创建新版本*/
    Map judgeVersion(Map map);

 /*  *//*导出顶设2的楼栋大定价*//*
    public void monthlyPlanExport(HttpServletRequest request, HttpServletResponse response, String plan_id, String plan_node_id);
*/

     void updatePlanTime(Map map);

     /*提交审批后修改流程表的状态*/
     void forUpdateNode(Map map);

    /**
     *
     * @param params
     */
    void insertOrUpOpenCost(Map params);

    Map selectOpenCostByPlanNodeId(String plan_node_id,String plan_id);



    Map getDesignTwoValue(Map map);

     Integer updateDesignTwoValue(Map map,HttpServletRequest request);


    void backDesignTwo(String planId);
}
