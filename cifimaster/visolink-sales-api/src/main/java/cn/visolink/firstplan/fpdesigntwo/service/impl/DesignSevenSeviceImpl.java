package cn.visolink.firstplan.fpdesigntwo.service.impl;

import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.firstplan.TaskLand.dao.TakeLandDao;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignSevenDao;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.fpdesigntwo.service.DesignSevenSevice;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.firstplan.openbeforeseven.dao.OpenBeforeSevenDayDao;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.firstplan.preemptionopen.dao.PreemptionOpenDao;
import cn.visolink.salesmanage.fileexport.model.MonthPlan;
import cn.visolink.utils.Constant;
import cn.visolink.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import io.swagger.models.auth.In;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DesignSevenSeviceImpl  implements DesignSevenSevice {
    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;
    @Autowired
    DesignSevenDao designSevenDao;

    @Autowired
    DesignTwoIndexDao designTwoIndexDao;

    @Autowired
    DesignTwoIndexServiceImpl designTwoIndexService;


    @Autowired
    private OpenBeforeSevenDayDao openBeforeSevenDayDao;

    @Autowired
    private PreemptionOpenDao preemptionOpenDao;

    @Autowired
    private TakeLandDao takeLandDao;

    /*首开前7天数据，首开审批表数据*/
     @Transactional(rollbackFor = Exception.class)
    @Override
    public Map selectSevenDayIndex(Map map) {
        Map result = new HashMap();
        /*无plan_node_id就要初始化*/
        Map test = designSevenDao.selectSevenDayIndex(map);
        if (test == null || test.size() < 1) {

            /*找到首开审批表数据，来自别的表并初始化*/
            designSevenDao.insertSevenDayIndex(map);

            /*搜索可以初始化的首开前7天里面的首开均价，来自别的表*/
            designSevenDao.insertSevenPrice(map);

            Map index = designSevenDao.selectSevenDayIndex(map);

            List<Map> listPrice = designSevenDao.selectSevenPrice(map);

            /*首开均价价格和对比，因为有比值，在这里计算并返回前端*/

            /*得到所有的产品类型*/

            List<String> listType = new ArrayList<>();
            if (listPrice != null && listPrice.size() > 0) {
                for (Map maplistPrice : listPrice) {
                    String product_type = maplistPrice.get("product_type") + "";

                    listType.add(product_type);
                }
                String listString = listType.toString();

                listString = listString.substring(1, listString.length() - 1);
                /*得到文字 例如：高层:小高:车位*/
                listString = listString.replace(", ", ":");
                listString = listString.replace("}", "");
                listString = listString.replace("{", "");
                /*计算比值*/
                String investcompare = null; //投资版比值
                String rulescompare = null; //战归版比值
                String designtwocompare = null; //顶设2版比值

                /*找到首开均价后计算比值*/

                if (listPrice != null && listPrice.size() > 0) {
                    for (int i = 0; i < listPrice.size(); i++) {
                        Map listAvg = designSevenDao.selectSevenPriceAvg(listPrice.get(i));
                        investcompare = investcompare + ":" + listAvg.get(investcompare);
                        rulescompare = rulescompare + ":" + listAvg.get(rulescompare);
                        designtwocompare = designtwocompare + ":" + listAvg.get(designtwocompare);
                    }
                }

                if(investcompare==null){
                    investcompare="";
                }
                investcompare = investcompare.substring(1, investcompare.length());

                if(rulescompare==null){
                    rulescompare="";
                }
                if(designtwocompare==null){
                    designtwocompare="";
                }
                rulescompare = rulescompare.substring(1, rulescompare.length());
                designtwocompare = designtwocompare.substring(1, designtwocompare.length());
                /*首开均价比值不同业态价差*/
                index.put("invest_operation_price", investcompare);
                index.put("rules_operation_price", rulescompare);
                index.put("designtwo_operation_price", designtwocompare);
                /*首开均价比值不同业态价差文字,数据库并没有此字段，在这里写上，*/
                index.put("priceWritten", listString);

            }

            result.put("index", index);


            result.put("price", listPrice);
        } else {
            /*直接查询数据*/
            Map index = designSevenDao.selectSevenDayIndex(map);

            List<Map> listPrice = designSevenDao.selectSevenPrice(map);

            List<String> listType = designSevenDao.selectSevenType(map);

            if (listType != null && listType.size() > 0) {

                String listString = listType.toString();

                listString = listString.substring(1, listString.length() - 1);
                /*得到文字 例如：高层:小高:车位*/
                listString = listString.replace(", ", ":");
                index.put("priceWritten", listString);
            }


            result.put("index", index);
            result.put("price", listPrice);

        }
        /*竞品,先查询竞品有没有，若没有就初始化并返给前端*/
        List<Map> indexCompet = designSevenDao.selectSevenCompet(map);

        if (indexCompet == null || indexCompet.size() < 1) {
            for (int i = 0; i < 3; i++) {
                Map map1 = new HashMap();
                map1.put("id", UUID.randomUUID().toString());
                map1.put("plan_id", map.get("plan_id"));
                map1.put("plan_node_id", map.get("plan_node_id"));
                if(indexCompet==null){
                    indexCompet=new ArrayList<>();
                }
                indexCompet.add(i, map1);
            }

        }
        result.put("compet", indexCompet);

        //  result.put("price",designSevenDao.selectAllCodeIndexPrice(map));
        /*判断是否可以发起一个新的创建版本*/
        /*   result.put("judgeVersion",1);
         *//*判断是否可以发起一条新的版本*//*
        Map map1=new HashMap();
        map1.put("plan_id",map.get("plan_id"));
        map1.put("isRight",0); // =
        map1.put("judgeOne",4);
        map1.put("judgeTwo",4); //审批成功
        map1.put("node_level",6); //开盘钱21天
        Map judgeone= designTwoIndexDao.judgeVersion(map1);
        if(judgeone==null || judgeone.size()<1){
            result.put("judgeVersion",-1);
        }
            map1.put("judgeOne",3);
            map1.put("judgeTwo",3); //审批成功
            map1.put("node_level",7); //开盘钱7天
            Map judgetwo= designTwoIndexDao.judgeVersion(map1);
        if(judgeone!=null || judgeone.size()>0){
            result.put("judgeVersion",-1);
        }
        map1.put("judgeOne",3);
        map1.put("judgeTwo",4); //审批成功
        map1.put("node_level",8); //开盘当天
        Map judgethree= designTwoIndexDao.judgeVersion(map1);
        if(judgeone!=null || judgeone.size()>0){
            result.put("judgeVersion",-1);
        }*/
        /*判断节点是否可以发起一个新的流程,若有返回值说明不合格*/

        return result;
    }

    /*更新数据,有两种情况
     * 1，初始化并返给前端，此时数据没有pian_node_id,需要更新
     * 2，前端发起一个新版本，此时pian_node_id对不上，需要插入*/
     @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateSevenDayIndex(Map map) {

        Integer result = 0;

        Map index = (Map) map.get("index");
        List<Map> compet = (List<Map>) map.get("compet");
        List<Map> price = (List<Map>) map.get("price");
        /*先查找竞品，若有就更新，没有则插入*/
        /*没有plan_node_id说明是初始化的数据，那就plan_node_id放入然后更新*/
        if ((index.get("plan_node_id") + "").equals(map.get("plan_node_id"))) {
            index.put("plan_node_id", map.get("plan_node_id"));
            result += designSevenDao.updateSevenDayIndex(index);
            /*更新开盘时间*/
            result += designSevenDao.updateOpenTime(index);
            /*竞品可以手动新增，所以需要判断库里是否有，没有就新增*/

            for (Map map1 : compet) {
                map1.put("plan_node_id", map.get("plan_node_id"));
                map1.put("plan_id", map.get("plan_id"));
                if (map1.get("id") != null) {
                    result += designSevenDao.updateSevenCompet(map1);
                } else {
                    List<Map> list1 = new ArrayList<>();
                    list1.add(map1);
                    designSevenDao.insertSevenCompet(list1);
                }


            }

            /*循环更新首开均价*/
            for (Map mapprice : price) {
                mapprice.put("plan_node_id", map.get("plan_node_id"));
                result += designSevenDao.updateSevenPrice(mapprice);
            }
            /*若两个plan_node_id对不上，则插入一条新的版本*/
        } else if (!((index.get("plan_node_id") + "").equals(map.get("plan_node_id")))) {
            index.put("plan_node_id", map.get("plan_node_id"));
            result += designSevenDao.insertSevenDayIndexNew(index);
            /*更新开盘时间*/
            result += designSevenDao.updateOpenTime(index);
            for (Map map1 : compet) {
                map1.put("plan_node_id", map.get("plan_node_id"));
                result += designSevenDao.insertSevenCompet(compet);
            }
            /*循环插入首开均价*/
            for (Map mapprice : price) {
                mapprice.put("plan_node_id", map.get("plan_node_id"));
            }
            result += designSevenDao.insertSevenPriceNew(price);

        }

        return result;
    }



    /*查找客储计划周拆分和节点储客计划*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map selectStorageNodePlan(Map map) {
        Map resultAll = new HashMap();
        Integer testFileList=0;
        /*若没有plan_node_id，说明要初始化数据*/
        List<Map> Test = designSevenDao.selectStoragePlan(map);
        List<Map>   mapFlows= designTwoIndexDao.selectStorageFlow(map);
        if ((map.get("newVersion") + "").equals("1") || Test == null || Test.size() < 1) {
            List<Map>  mapFlow= designTwoIndexDao.selectStorageFlow(map);
            List<Map> resultMap =new ArrayList<>();
            List<Map> resultMapWeek =new ArrayList<>();
            Map forNewMap=new HashMap();
            if(mapFlow!=null && mapFlow.size()>0){
                forNewMap.putAll(map);
                forNewMap.put("flow_id",mapFlow.get(0).get("flow_id"));
                resultMap= designSevenDao.selectStoragePlan(forNewMap);
            }
            //手动置空
            resultMap=null;
            if(resultMap!=null && resultMap.size()>0){
                resultMapWeek=designSevenDao.selectStorageWeek(forNewMap);
                for(Map map1:resultMap){
                   map1.remove("flow_id");
                }
                for(Map map1:resultMapWeek){
                    map1.remove("flow_id");
                }
                resultAll.put("fileList",takeLandDao.getFileLists(mapFlow.get(0).get("flow_id")+""));
                testFileList=1;
            }else{
                /*先放一假的plam_node_id进去，初始化完并返给前端后删光里面的plan_node_id*/
                Map fakeMap=new HashMap();
                fakeMap.putAll(map);
                String plan_node_id=   UUID.randomUUID().toString();
                fakeMap.put("plan_node_id",plan_node_id);
                fakeMap.remove("flow_id");
                /*判断上一个节点是否有略过，没有返回值说明有掠过，若略过直接取顶设2*/
                String etcNode= designTwoIndexDao.judgeEtc(map);
                if(etcNode!=null && etcNode!="" && Integer.parseInt(etcNode)<1 ){
                    fakeMap.put("node_level",4);
                }
                /*得到项目名*/
                Map  projectId=  designTwoIndexDao.selectProjectName(fakeMap.get("plan_id")+"");
                if(projectId!=null){
                    fakeMap.put("project_id",projectId.get("project_id"));
                }
                designSevenDao.insertNewStorage(fakeMap);

                designSevenDao.insertNewStorageWeek(fakeMap);
                /*传进去这个字段选择特殊条件*/
                fakeMap.put("node_level",map.get("node_level"));
                fakeMap.put("fake",1);
                resultMap = designSevenDao.selectStoragePlan(fakeMap);
                resultMapWeek = designSevenDao.selectStorageWeek(fakeMap);

                for(Map map1: resultMap){
                    map1.remove("plan_node_id");
                }
                for(Map map1: resultMapWeek){
                    map1.remove("plan_node_id");
                }
                /*初始化后删掉数据库那些数据*/
                designSevenDao.deleteForPlanId(plan_node_id);
            }
            /*若是开盘前21天，那么还要考虑一种情况，就是延期开盘后初始化出来的数据需要符合延期开盘*/
            if(map.get("node_level").equals("6")  ){
                Map test21=  designSevenDao.selectCountSix(map);
                if(test21!=null  &&  test21.size()>0 && Integer.parseInt (test21.get("one")+"")>0 && Integer.parseInt (test21.get("two")+"")>0  ){
                    resultMap= designSevenDao.selectBestNewSix(map);
                    resultMapWeek= designSevenDao.selectSixWeekList(map);
                }
            }
            resultAll.put("Week", resultMapWeek);
            resultAll.put("NodePlan", resultMap);
            resultAll.put("WeekWrite", resultMapWeek);
            resultAll.put("NodePlanWrite", resultMap);
        }else{
            List<Map> resultMap = designSevenDao.selectStoragePlan(map);
            List<Map> resultMapWeek = designSevenDao.selectStorageWeek(map);
            //获取用户动作
            String operation=map.get("operation")+"";
            if(!"view".equals(operation)){
                String flow_id=map.get("flow_id")+"";
                String approval_stuat="";
                if("".equals(flow_id)||"null".equals(flow_id)){
                    if(mapFlows!=null&&mapFlows.size()>0){
                        approval_stuat=mapFlows.get(0).get("approval_stuat")+"";
                        flow_id=mapFlows.get(0).get("flow_id")+"";
                    }
                }else{
                    //查询此流程的状态
                    approval_stuat = designSevenDao.getCustomerFlowStatus(flow_id);
                }
                if(!"3".equals(approval_stuat)&&!"4".equals(approval_stuat)&&!"7".equals(approval_stuat)){
                    map.put("thisNode_flow_id",flow_id);
                    updateData(resultMap,resultMapWeek,resultAll,map);
                }
            }
            resultAll.put("Week", resultMapWeek);
            resultAll.put("NodePlan", resultMap);
            resultAll.put("WeekWrite", resultMapWeek);
            resultAll.put("NodePlanWrite", resultMap);
        }


        /*判断节点是否可以发起一个新的流程,若有返回值说明不合格*/
        resultAll.put("judgeVersion", 1);
        if((map.get("newVersion")+"").equals("1")){
            resultAll.put("judgeVersion", -1);
        }else {
            List<Map> test = designSevenDao.designAllCan(map);
            /*判断是否可以发起一条新的版本*/
            if (test != null && test.size() > 0) {
                resultAll.put("judgeVersion", -1);
            }
            /*判断当前节点是否是被略过节点，若是，即便下面的节点已经提报但无对应数据，该节点也可以编辑*/
            /*判断条件：1.该节点已经审批完成，2.没有暂存中的版本，3.往后的节点储客表里没有审批通过或审批中的版本*/
            Map judge3=   designTwoIndexDao.judgeCanWrite(map);
            if (judge3 != null && judge3.get("idState")!=null  && (judge3.get("flowCount")+"").equals("0") && (judge3.get("storageCount")+"").equals("0") ) {
                resultAll.put("judgeVersion", 1);
            }
        }
        if((map.get("newVersion") + "").equals("1")){
            Map map1=new HashMap();
            map1.put("approval_stuat",10);
            mapFlows.add(0,map1);
        }
        /*判断一下，如果顶设2没有填报审批过，那么首开前几个节点不允许出现提报按钮*/
        Integer counttwo= designSevenDao.selectDesignTwoFour(map);
        if(counttwo<1 || counttwo==null){
            if(mapFlows==null || mapFlows.size()<1 ){
                mapFlows=new ArrayList<>();
                Map map1=new HashMap();
                mapFlows.add(map1);
            }
            mapFlows.get(0).put("approval_stuat",4);
        }
        resultAll.put("flow",mapFlows );
        /*是否变更客储计划*/
        Map flowMap=new HashMap();
        if(mapFlows!=null && mapFlows.size()>0){
            flowMap =mapFlows.get(0);
        }
        Integer change= designSevenDao.selectChangeState(flowMap );
        if(change==null){
            change=0;
        }
        resultAll.put("change", change);
        String flowId=null;
        if(testFileList==0){
            if(mapFlows!=null && mapFlows.size()>0 && !(mapFlows.get(0).get("flow_id")==null || mapFlows.get(0).get("flow_id")=="") ){
                flowId= mapFlows.get(0).get("flow_id")+"";
                resultAll.put("fileList",takeLandDao.getFileLists(flowId));
            }
        }
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("plan_id",map.get("plan_id"));
        paramMap.put("node_level",10);
        //获取三大件时间
        Map salesMap = designSevenDao.getThreepiecesForSalesTimes(paramMap);
        if(salesMap!=null&&salesMap.size()>0){
            paramMap.putAll(salesMap);
        }
        paramMap.put("node_level",11);
        Map sampleMap = designSevenDao.getThreepiecesForSampleTimes(paramMap);
        if(sampleMap!=null&&sampleMap.size()>0){
            paramMap.putAll(sampleMap);
        }
        resultAll.put("threepiecesTimes",paramMap);
        return resultAll;
    }

    public void updateData(List<Map> oldCsData,List<Map> oldWeekData, Map resultMap,Map paramMap){
        String flow_id = UUID.randomUUID().toString();
        String plan_node_id=paramMap.get("plan_node_id")+"";
        //查询此项目是否进行过抢开并且已经审批通过
        Map preeOpen = preemptionOpenDao.getApplayadoptPreeOpen(plan_node_id);

        plan_node_id = UUID.randomUUID().toString();
        paramMap.put("plan_node_id",plan_node_id);
        paramMap.put("flow_id",flow_id);
        String plan_id=paramMap.get("plan_id")+"";
        String status="10";
        //添加客储流程数据
        Map<Object, Object> storageNodeMap = new HashMap<>();
        storageNodeMap.put("change", 0);
        storageNodeMap.put("approval_stuat", status);
        storageNodeMap.put("node_level", 6);
        storageNodeMap.put("plan_node_id", plan_node_id);
        storageNodeMap.put("plan_id", plan_id);
        storageNodeMap.put("id", flow_id);
        openBeforeSevenDayDao.insertCustomerStoreFlow(storageNodeMap);

        paramMap.put("flow_id",flow_id);

        boolean isPreeOpen=false;
        if(preeOpen!=null&&preeOpen.size()>0){
            isPreeOpen=true;
            //查询此项目有没有进行过抢开
            List<Map> nodeTime = preemptionOpenDao.getNodeTime(paramMap);
            String threeTime="";
            String twoTime="";
            if(nodeTime!=null&&nodeTime.size()>0){
                threeTime  = nodeTime.get(0).get("node_time")+"";
                twoTime  = nodeTime.get(1).get("node_time")+"";
            }

            paramMap.put("threeTime",threeTime);
            paramMap.put("twoTime",twoTime);
            //生成抢开客储数据
            designSevenDao.initCustomerStorageDataQk(paramMap);
        }else{
            //生成客储数据
            designSevenDao.initCustomerStorageData(paramMap);
        }

        List<Map> customerStorage= designSevenDao.getCustomerStorageByFlow(flow_id);
        if(customerStorage!=null&&customerStorage.size()>0){
            for (int i=0;i<oldCsData.size();i++){
                Map oldMap = oldCsData.get(i);
                Map newMap = customerStorage.get(i);
                //将新数据替换到旧数据中
                oldMap.putAll(newMap);
            }
        }

        paramMap.remove("flow_id");
        //进行过抢开
        if(isPreeOpen){
            //生成最新的周拆分数据-抢开过后的
            // 生成周拆分数据
            paramMap.put("node_level",7);
            designSevenDao.insertNewStorageWeek(paramMap);
        }else{
            // 生成周拆分数据
            paramMap.put("flow_id",paramMap.get("thisNode_flow_id"));
            designSevenDao.insertNewStorageWeek(paramMap);
        }
        List<Map> weekDataByPlanNode = designSevenDao.getWeekDataByPlanNode(plan_node_id);
        if(weekDataByPlanNode!=null&&weekDataByPlanNode.size()>0){
            System.err.println(JSON.toJSONString(weekDataByPlanNode));
            System.err.println(JSON.toJSONString(weekDataByPlanNode));
            for (int i=0;i<oldWeekData.size();i++){
                Map oldWeekMap = oldWeekData.get(i);
                Map newWeekMap = weekDataByPlanNode.get(i);
                //将新数据替换到旧数据中
                oldWeekMap.putAll(newWeekMap);
            }
        }
        //清空数据
        designSevenDao.clearNewData(flow_id,plan_node_id);
    }
    /*更新客储计划周拆分和节点储客计划*/
     @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateStorageNodePlan(Map map, HttpServletRequest request) {



        Integer result = 0;
        List<Map> week = (ArrayList) map.get("Week");
        List<Map> NodePlan = (ArrayList) map.get("NodePlan");

        String plan_node_id = map.get("plan_node_id")+"";
            /*判断开盘前21天*/
        Map test21=  designSevenDao.selectCountSix(map);
        Integer testtwenty1=0;
         if(test21!=null  &&  test21.size()>0  && Integer.parseInt (test21.get("two")+"")>0 && map.get("flow_id")!=null  &&  map.get("flow_id")!="" && map.get("node_level").equals("6")){
            Map map1=  NodePlan.get(NodePlan.size()-1 );
            map1.put("delay_time",map1.get("node_time")+"") ;
            map1.put("flow_id",map.get("flow_id"));
            designSevenDao.deleteOnlyNode(map);
            List<Map> planNodeList=new ArrayList<>();
            for(Map map2:NodePlan){
                if((map2.get("level")+"").equals("1")){
                    planNodeList.add(map2);
                }
            }
            designSevenDao.insertStorageNodePlan(planNodeList);
            openbeforetwentyoneDao.insertNewStorage(map1);
            testtwenty1=1;
        }



        if (map.get("flow_id")==null  ||  map.get("flow_id")=="") {
            String uuid=UUID.randomUUID().toString();
           Map flowMap=new HashMap();
            flowMap.putAll(map);
            flowMap.put("uuid",uuid);
            designTwoIndexDao.insertNodeFlow(flowMap);
            map.put("flow_id",uuid);}

            /*先删光再初始化*/
        /*开盘前21天的暂存需要特殊处理*/
       if(testtwenty1==0){
            designTwoIndexDao.deleteStorageNodePlan(map);
       }


        designTwoIndexDao.deleteStorageweek(map);


            /*涉及到计算，思路：将未计算的值先插入库里，在进行计算后插入新值并删除未计算的值*/
              String fakeId=  UUID.randomUUID().toString();
              if(week!=null&&week.size()>0){
                  for (Map weekmap : week) {
                      weekmap.put("plan_node_id",fakeId);
                      weekmap.put("flow_id",map.get("flow_id"));
                  }
              }


            if(week!=null&&week.size()>0){
                result += designTwoIndexDao.insertStorageWeek(week);
            }
                /*插入被计算后的值*/
       Map projectID= designTwoIndexDao.selectProjectName(map.get("plan_id")+"");
       if(projectID!=null) {
           map.put("project_id", projectID.get("project_id") + "");
       }

                    designSevenDao.insertNewStorageWeek(map);
                    /*再删除未计算过的值*/
                Map deleteMap=new HashMap();
            deleteMap.put("plan_node_id",fakeId);
            designTwoIndexDao.deleteStorageweek(deleteMap);

            /*NODEPLAN*/
        if(testtwenty1==0){
                    List RealNode=new ArrayList();
            for (Map NodePlanmap : NodePlan) {
                NodePlanmap.put("plan_node_id",plan_node_id);
                NodePlanmap.put("flow_id",map.get("flow_id"));
                if( (NodePlanmap.get("level")+"").equals("1")){
                    RealNode.add(NodePlanmap);
                }
            }
            /*涉及到计算，先暂存所有的计划数据，然后通过SQL算好存到库里*/
            if(RealNode!=null&&RealNode.size()>0){
                result += designSevenDao.insertStorageNodePlan(RealNode);
            }
            Map insertNode=new HashMap();
            insertNode.putAll(map);

            designSevenDao.insertNewStorage(insertNode);}


            /*更改是否勾选变更状态*/
            designSevenDao.updateChangeState(map);


        /*更新完后更改节点灯状态*/

        map.put("light_stuat", 0);
        designTwoIndexService.updateLightStuat(map,request);
        //给已经提交的文件做关联
        List<Map> fileList = (List<Map>) map.get("fileList");

        if(fileList!=null &&  fileList.size()>0){
            designTwoIndexDao.DeleteAttach(map.get("flow_id")+"");
            for (int i = 0; i < fileList.size(); i++) {
                Map fileMap = fileList.get(i);
                fileMap.put("BizID",map.get("flow_id")+"");
                takeLandDao.updateSattach(fileMap);
            }}

        return result;
    }


    /*插入一条版本信息，需要
     * plan_node_id
     * plan_id
     * node_level  等级
     *days 首开前多少天
     * */
     @Transactional(rollbackFor = Exception.class)
    @Override
    public String forPlanNode(Map map) {

        if (map.get("plan_node_id") == null || map.get("plan_node_id") == "") {

            /*查该计划ID下有几个版本，用来拼凑版本名字*/
            Map mapExist = new HashMap();
            mapExist.put("plan_id", map.get("plan_id"));
            mapExist.put("node_level", map.get("node_level"));

            List listAllPlan = designTwoIndexDao.selectPlanNode(mapExist);
            /*新创建的版本ID*/
            String uuid = UUID.randomUUID().toString();
            map.put("uuid", uuid);
            if (listAllPlan == null) {
                map.put("VERSION", "V1");
            } else {
                map.put("VERSION", "V" + (listAllPlan.size() + 1));
            }
            /*插入结束时间，顶设2结束时间是首开前120天*/

            designTwoIndexDao.insertPlanNode(map);

            return uuid;
        } else {
            return map.get("plan_node_id") + "";
        }
    }

    /*判断是否可以创建新版本*/
     @Transactional(rollbackFor = Exception.class)
    @Override
    public Map judgeVersion(Map map) {
        return designTwoIndexDao.judgeVersion(map);
    }

    /*查找顶设2左上角的版本选择*/
     @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Map> selectPlanNode(Map map) {

        if (map.get("plan_node_id") == null) {

            Integer node_level = Integer.parseInt(map.get("node_level") + "");
            /*创建一个临时版本*/
            String node_name = null;
            switch (node_level) {
                /*首开前三个月*/
                case 4:
                    node_name = "首开前3个月V1";
                    break;
                /*首开前二个月*/
                case 5:
                    node_name = "首开前2个月V1";
                    break;
                /*首开前21天*/
                case 6:
                    node_name = "首开前21天V1";
                    break;
                /*首开前7天*/
                case 7:
                    node_name = "首开前7天V1";
                    break;
            }
            /*插入临时版本名*/
            Date date = new Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");

            node_name = node_name + sd.format(date);
            Map versionMap = new HashMap();
            versionMap.put("node_name", node_name);
            List<Map> list = new ArrayList<>();
            list.add(versionMap);
            return list;
        } else {
            return designTwoIndexDao.selectPlanNode(map);
        }
    }

    public String flowName(Map map) {
        int node_level = Integer.parseInt((map.get("node_level") + ""));
        String node_name = null;
        switch (node_level) {
            /*首开前三个月*/
            case 4:
                node_name = "首开前3个月";
                break;
            /*首开前二个月*/
            case 5:
                node_name = "首开前2个月";
                break;
            /*首开前21天*/
            case 6:
                node_name = "首开前21天";
                break;
            /*首开前7天*/
            case 7:
                node_name = "首开前7天";
                break;
        }
        return node_name;
    }



     @Transactional(rollbackFor = Exception.class)
    @Override
    public void monthlyExport(HttpServletRequest request, HttpServletResponse response, String flowId) {
        String planName;
        String basePath;
        String templatePath;
        String targetFileDir;
        String targetFilePath;
        FileInputStream templateInputStream = null;
        FileOutputStream fileOutputStream = null;
        Workbook targetWorkBook;
        XSSFSheet targetSheet;
        Map mapTemp = new HashMap();
        mapTemp.put("flow_id", flowId);
        Map map1 = designTwoIndexDao.selectPlanNodeId(mapTemp);
        mapTemp.put("plan_node_id", map1.get("plan_node_id"));
        mapTemp.put("plan_id", map1.get("plan_id"));
        mapTemp.put("node_level", map1.get("node_level"));
        /*preparedByUnitType=0是集团编制导出*/
        Map tempResult = selectStorageNodePlan(mapTemp);
        /*无数据直接返回*/
        if (tempResult == null || tempResult.size() < 1) {
            return;
        }
        List<Map> mapFlowList = (ArrayList) tempResult.get("flow");
        Map mapFlow = mapFlowList.get(0);

        planName = mapFlow.get("name") + "";


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        planName += time;

        try {
//            basePath = "E:/xuhui/marketing-control-api/cifimaster/visolink-sales-api/src";
            basePath = request.getServletContext().getRealPath("/");


            //模板文件路径。

            templatePath = File.separator + "TemplateExcel" + File.separator + "nodePlanAndWeek.xlsx";

            //导出临时文件文件夹。
            targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //设置content-disposition响应头控制浏览器以下载的形式打开文件
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));

            //验证模板文件是否存在
            // String realpath = this.getClass().getResource("/").getPath();


            // realpath = realpath.substring(0, realpath.indexOf("/target")) + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + templatePath;

            //  File templateFile = new File(realpath);
              templatePath = basePath + templatePath;
              File templateFile = new File(templatePath);

            if (!templateFile.exists()) {
                templateFile.mkdirs();
                throw new ServiceException("-15_1003", "认购确认导出失败。模板文件不存在");
            }
            //验证目标文件夹是否存在
            File targetFileDirFile = new File(targetFileDir);
            if (!targetFileDirFile.exists()) {
                targetFileDirFile.mkdirs();
            }
            //创建输出文档。
            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, mapFlow.get("name") + "");

            //清空原模板剩余数据
            /*标题名字*/
            Row row0 = targetSheet.getRow(0);

            Cell cell0 = row0.getCell(0);
            CellStyle style0 = cell0.getCellStyle();

            cell0.setCellStyle(style0);
            cell0.setCellValue(mapFlow.get("projectName") + " - 客储达成进度 - 填报导出数据");


            //起始行
            int startRows = 4;

            int maxCellNum = row0.getPhysicalNumberOfCells();


            //   targetSheet.protectSheet("edit");


            /**
             * 单元格样色共三种：
             * 1.白底 居中加粗 style1
             * 2.灰底 居中 加粗
             * 3.灰底 正常
             * 4.动态判断
             */
            XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
            Row row4 = targetSheet.getRow(4);
            Row row3 = targetSheet.getRow(2);
            Cell row3Ling = row3.getCell(0);
            CellStyle style3 = row3Ling.getCellStyle();
            Cell row4Cell = row4.getCell(0);
            CellStyle style1 = row4Cell.getCellStyle();
            CellStyle style2 = targetWorkBook.createCellStyle();
            style1.setAlignment(HorizontalAlignment.CENTER);


            style1.setBorderBottom(BorderStyle.THIN);
            style1.setBorderTop(BorderStyle.THIN);
            style1.setBorderLeft(BorderStyle.THIN);
            style1.setBorderRight(BorderStyle.THIN);

            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setAlignment(HorizontalAlignment.LEFT);


            List<MonthPlan> groupRowlist = new ArrayList<MonthPlan>();
            List<Map> mapList = (ArrayList) tempResult.get("NodePlan");
            List<Map> nodePlan = mapList;
            String big_way = mapList.get(0).get("big_way") + "";
            String little_way = mapList.get(0).get("little_way") + "";
            /*客储计划*/
            //循环遍历所有数据
            for (Map parentMap : mapList) {


                XSSFRow positionRow = targetSheet.createRow(startRows);
                Row row2 = targetSheet.getRow(startRows);
                positionRow.setHeightInPoints(20);
                Cell cell00 = row2.createCell(0);
                cell00.setCellStyle(style1);

                cell00.setCellValue(parentMap.get("nide_name") == null ? "" : parentMap.get("nide_name") + "");
                Cell cell1 = row2.createCell(1);
                cell1.setCellStyle(style1);
                cell1.setCellValue(parentMap.get("node_time") == null ? "" : parentMap.get("node_time") + "");
                Cell cell2 = row2.createCell(2);
                cell2.setCellStyle(style1);
                cell2.setCellValue(parentMap.get("line_name") == null ? "" : parentMap.get("line_name") + "");

                Cell cell3 = row2.createCell(3);
                cell3.setCellStyle(style2);
                cell3.setCellValue(parentMap.get("report_num") == null ? "" : parentMap.get("report_num") + "");
                Cell cell4 = row2.createCell(4);
                cell4.setCellStyle(style2);
                cell4.setCellValue(parentMap.get("visit_num") == null ? "" : parentMap.get("visit_num") + "");
                Cell cell5 = row2.createCell(5);
                cell5.setCellStyle(style2);
                cell5.setCellValue(parentMap.get("little_num") == null ? "" : parentMap.get("little_num") + "");
                Cell cell6 = row2.createCell(6);
                cell6.setCellStyle(style2);
                cell6.setCellValue(parentMap.get("little_per") == null ? "" : parentMap.get("little_per") + "");
                Cell cell7 = row2.createCell(7);
                cell7.setCellStyle(style2);
                cell7.setCellValue(parentMap.get("big_num") == null ? "" : parentMap.get("big_num") + "");
                Cell cell8 = row2.createCell(8);
                cell8.setCellStyle(style2);
                cell8.setCellValue(parentMap.get("big_per") == null ? "" : parentMap.get("big_per") + "");
                Cell cell9 = row2.createCell(9);
                cell9.setCellStyle(style2);
                cell9.setCellValue(parentMap.get("sub_num") == null ? "" : parentMap.get("sub_num") + "");
                Cell cell10 = row2.createCell(10);
                cell10.setCellStyle(style2);
                cell10.setCellValue(parentMap.get("make_per") == null ? "" : parentMap.get("make_per") + "");

                //此条数据类型为区域 type=
                startRows++;
            }

            mapList = (ArrayList) tempResult.get("Week");

            startRows = 22;
            //周拆分
            for (Map parentMap : mapList) {
                /*填充完整波段*/
                if (startRows == 22) {
                    Integer plan_total = 0;
                    Integer actual_add = 0;

                    for (Map total : mapList) {
                        plan_total += Integer.parseInt(total.get("plan_add") + "");
                        actual_add += Integer.parseInt(total.get("actual_add") + "");
                    }

                    XSSFRow positionRow = targetSheet.createRow(startRows);
                    Row row2 = targetSheet.getRow(startRows);
                    Cell cell00 = row2.createCell(0);
                    cell00.setCellStyle(style1);
                    cell00.setCellValue(parentMap.get("week") + "");
                    Cell cell1 = row2.createCell(1);
                    cell1.setCellStyle(style1);
                    cell1.setCellValue(parentMap.get("day_date") + "");
                    Cell cell2 = row2.createCell(2);
                    cell2.setCellStyle(style2);
                    cell2.setCellValue(plan_total);
                    Cell cell5 = row2.createCell(5);
                    cell5.setCellStyle(style2);
                    cell5.setCellValue(actual_add);
                    Cell cell6 = row2.createCell(6);
                    cell6.setCellStyle(style2);
                    Cell cell8 = row2.createCell(8);
                    cell8.setCellStyle(style2);

                    Cell cell9 = row2.createCell(9);
                    cell9.setCellStyle(style2);

                    Cell cell10 = row2.createCell(10);
                    cell10.setCellStyle(style2);


                } else {


                    XSSFRow positionRow = targetSheet.createRow(startRows);
                    Row row2 = targetSheet.getRow(startRows);
                    positionRow.setHeightInPoints(20);
                    Cell cell00 = row2.createCell(0);
                    cell00.setCellStyle(style1);
                    cell00.setCellValue(parentMap.get("week") + "");
                    Cell cell1 = row2.createCell(1);
                    cell1.setCellStyle(style1);
                    cell1.setCellValue(parentMap.get("day_date") + "");
                    Cell cell2 = row2.createCell(2);
                    cell2.setCellStyle(style2);
                    cell2.setCellValue(parentMap.get("plan_add") + "");
                    Cell cell3 = row2.createCell(3);
                    cell3.setCellStyle(style2);
                    cell3.setCellValue(parentMap.get("plan_total") + "");
                    Cell cell4 = row2.createCell(4);
                    cell4.setCellStyle(style2);
                    cell4.setCellValue(parentMap.get("plan_task_per") + "");
                    Cell cell5 = row2.createCell(5);
                    cell5.setCellStyle(style2);
                    cell5.setCellValue(parentMap.get("actual_add") + "");
                    Cell cell6 = row2.createCell(6);
                    cell6.setCellStyle(style2);
                    cell6.setCellValue(parentMap.get("actual_total") + "");
                    Cell cell7 = row2.createCell(7);
                    cell7.setCellStyle(style2);
                    cell7.setCellValue(parentMap.get("actual_task_per") + "");
                    Cell cell8 = row2.createCell(8);
                    cell8.setCellStyle(style2);
                    cell8.setCellValue(parentMap.get("week_bais_value") + "");
                    Cell cell9 = row2.createCell(9);
                    cell9.setCellStyle(style2);
                    cell9.setCellValue(parentMap.get("bias_per") + "");
                    Cell cell10 = row2.createCell(10);
                    cell10.setCellStyle(style2);
                    cell10.setCellValue(parentMap.get("bias_cause") == null ? "" : parentMap.get("bias_cause") + "");
                }

                //此条数据类型为区域 type=
                startRows++;
            }

            if ((tempResult.get("change") + "").equals("1")) {
                startRows = startRows + 2;
                XSSFRow positionRow = targetSheet.createRow(startRows);
                Row rowNode = targetSheet.getRow(startRows);
                positionRow.setHeightInPoints(20);
                Cell cellNode = rowNode.createCell(0);

                cellNode.setCellValue("变更节点客储计划");
                CellRangeAddress regionNodePlan = new CellRangeAddress(startRows, startRows, 0, 10);
                targetSheet.addMergedRegion(regionNodePlan);
                cellNode.setCellStyle(style3);
                startRows = startRows + 1;
                /*变更客储要复制一份*/
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                Row forNewHead = targetSheet.getRow(startRows);


                Cell cell0forNewHead = forNewHead.createCell(0);
                cell0forNewHead.setCellStyle(style1);

                cell0forNewHead.setCellValue("节点");
                Cell cell1forNewHead = forNewHead.createCell(1);
                cell1forNewHead.setCellStyle(style1);
                cell1forNewHead.setCellValue("日期");
                Cell cell2forNewHead = forNewHead.createCell(2);
                cell2forNewHead.setCellStyle(style1);
                cell2forNewHead.setCellValue("");

                Cell cell3forNewHead = forNewHead.createCell(3);
                cell3forNewHead.setCellStyle(style1);
                cell3forNewHead.setCellValue("报备(组)");
                Cell cell4forNewHead = forNewHead.createCell(4);
                cell4forNewHead.setCellStyle(style1);
                cell4forNewHead.setCellValue("来访(组)");
                Cell cell5forNewHead = forNewHead.createCell(5);
                cell5forNewHead.setCellStyle(style1);
                cell5forNewHead.setCellValue("小卡(组)");
                Cell cell6forNewHead = forNewHead.createCell(6);
                cell6forNewHead.setCellStyle(style1);
                cell6forNewHead.setCellValue("小卡率(%)");
                Cell cell7forNewHead = forNewHead.createCell(7);
                cell7forNewHead.setCellStyle(style1);
                cell7forNewHead.setCellValue("大卡(组)");
                Cell cell8forNewHead = forNewHead.createCell(8);
                cell8forNewHead.setCellStyle(style1);
                cell8forNewHead.setCellValue("大卡率(%)");
                Cell cell9forNewHead = forNewHead.createCell(9);
                cell9forNewHead.setCellStyle(style1);
                cell9forNewHead.setCellValue("认购(套)");
                Cell cell10forNewHead = forNewHead.createCell(10);
                cell10forNewHead.setCellStyle(style1);
                cell10forNewHead.setCellValue("成交率(%)");
                List<Map> onlyPlan = new ArrayList<>();
                for (Map parentMap : nodePlan) {
                    if ((parentMap.get("level")+"").equals("1")) {
                        onlyPlan.add(parentMap);
                    }
                }
                startRows=startRows+1;
                /*客储计划*/
                //循环遍历所有数据
                for (Map parentMap : onlyPlan) {


                    XSSFRow forNewHeadRow = targetSheet.createRow(startRows);
                    Row NodePlanRow2 = targetSheet.getRow(startRows);
                    forNewHeadRow.setHeightInPoints(20);
                    Cell cell00 = NodePlanRow2.createCell(0);
                    cell00.setCellStyle(style1);

                    cell00.setCellValue(parentMap.get("nide_name") == null ? "" : parentMap.get("nide_name") + "");
                    Cell cell1 = NodePlanRow2.createCell(1);
                    cell1.setCellStyle(style1);
                    cell1.setCellValue(parentMap.get("node_time") == null ? "" : parentMap.get("node_time") + "");
                    Cell cell2 = NodePlanRow2.createCell(2);
                    cell2.setCellStyle(style1);
                    cell2.setCellValue(parentMap.get("line_name") == null ? "" : parentMap.get("line_name") + "");

                    Cell cell3 = NodePlanRow2.createCell(3);
                    cell3.setCellStyle(style2);
                    cell3.setCellValue(parentMap.get("report_num") == null ? "" : parentMap.get("report_num") + "");
                    Cell cell4 = NodePlanRow2.createCell(4);
                    cell4.setCellStyle(style2);
                    cell4.setCellValue(parentMap.get("visit_num") == null ? "" : parentMap.get("visit_num") + "");
                    Cell cell5 = NodePlanRow2.createCell(5);
                    cell5.setCellStyle(style2);
                    cell5.setCellValue(parentMap.get("little_num") == null ? "" : parentMap.get("little_num") + "");
                    Cell cell6 = NodePlanRow2.createCell(6);
                    cell6.setCellStyle(style2);
                    cell6.setCellValue(parentMap.get("little_per") == null ? "" : parentMap.get("little_per") + "");
                    Cell cell7 = NodePlanRow2.createCell(7);
                    cell7.setCellStyle(style2);
                    cell7.setCellValue(parentMap.get("big_num") == null ? "" : parentMap.get("big_num") + "");
                    Cell cell8 = NodePlanRow2.createCell(8);
                    cell8.setCellStyle(style2);
                    cell8.setCellValue(parentMap.get("big_per") == null ? "" : parentMap.get("big_per") + "");
                    Cell cell9 = NodePlanRow2.createCell(9);
                    cell9.setCellStyle(style2);
                    cell9.setCellValue(parentMap.get("sub_num") == null ? "" : parentMap.get("sub_num") + "");
                    Cell cell10 = NodePlanRow2.createCell(10);
                    cell10.setCellStyle(style2);
                    cell10.setCellValue(parentMap.get("make_per") == null ? "" : parentMap.get("make_per") + "");

                    //此条数据类型为区域 type=
                    startRows++;
                }
                /*周拆分*/
                startRows = startRows + 2;
                XSSFRow positionRowForNewWeek = targetSheet.createRow(startRows);
                Row rowWeek = targetSheet.getRow(startRows);
                positionRowForNewWeek.setHeightInPoints(20);
                Cell cellWeek = rowWeek.createCell(0);

                cellWeek.setCellValue("变更来访周拆分");
                CellRangeAddress regionNewWeek = new CellRangeAddress(startRows, startRows, 0, 10);
                targetSheet.addMergedRegion(regionNewWeek);
                cellWeek.setCellStyle(style3);
                startRows = startRows + 1;
                /*变更来访问周要复制一份*/
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                Row forNewWeekHead = targetSheet.getRow(startRows);

                Cell cell0forNewWeek = forNewWeekHead.createCell(0);
                cell0forNewWeek.setCellStyle(style1);
                cell0forNewWeek.setCellValue("周期");
                Cell cell1forNewWeek = forNewWeekHead.createCell(1);
                cell1forNewWeek.setCellStyle(style1);
                cell1forNewWeek.setCellValue("日期");
                Cell cell2forNewWeek = forNewWeekHead.createCell(2);
                cell2forNewWeek.setCellStyle(style1);
                cell2forNewWeek.setCellValue("计划新增(组)");

                Cell ce3forNewWeek = forNewWeekHead.createCell(3);
                ce3forNewWeek.setCellStyle(style1);
                Cell ce4forNewWeek = forNewWeekHead.createCell(4);
                ce4forNewWeek.setCellStyle(style1);
                Cell cell3forNewWeek = forNewWeekHead.createCell(5);
                cell3forNewWeek.setCellStyle(style1);
                CellRangeAddress weekAddRange = new CellRangeAddress(startRows, startRows, 2, 4);
                targetSheet.addMergedRegion(weekAddRange);

                cell3forNewWeek.setCellValue("计划累计(组)");

                Cell ce6forNewWeek = forNewWeekHead.createCell(6);
                ce6forNewWeek.setCellStyle(style1);
                Cell ce7forNewWeek = forNewWeekHead.createCell(7);
                ce7forNewWeek.setCellStyle(style1);
                CellRangeAddress weekAddAll = new CellRangeAddress(startRows, startRows, 5, 7);
                targetSheet.addMergedRegion(weekAddAll);

                Cell cell4forNewWeek = forNewWeekHead.createCell(8);
                cell4forNewWeek.setCellStyle(style1);
                cell4forNewWeek.setCellValue("计划阶段任务占比(%)");
                CellRangeAddress weekAddRate = new CellRangeAddress(startRows, startRows, 8, 10);
                targetSheet.addMergedRegion(weekAddRate);
                Cell ce9forNewWeek = forNewWeekHead.createCell(9);
                ce9forNewWeek.setCellStyle(style1);
                Cell ce10forNewWeek = forNewWeekHead.createCell(10);
                ce10forNewWeek.setCellStyle(style1);
                startRows = startRows + 1;
                /*变更来访问周要复制一份*/
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                Row forWeekPerfect = targetSheet.getRow(startRows);

                Integer plan_total = 0;
                Integer actual_add = 0;

                for (Map total : mapList) {
                    plan_total += Integer.parseInt(total.get("plan_add") + "");

                }


                Cell perfectCell00 = forWeekPerfect.createCell(0);
                perfectCell00.setCellStyle(style1);
                perfectCell00.setCellValue(mapList.get(0).get("week") + "");
                Cell cell1 = forWeekPerfect.createCell(1);
                cell1.setCellStyle(style1);
                cell1.setCellValue(mapList.get(0).get("day_date") + "");
                Cell cell2 = forWeekPerfect.createCell(2);
                cell2.setCellStyle(style2);
                cell2.setCellValue(plan_total);

                Cell cell3 = forWeekPerfect.createCell(3);
                cell3.setCellStyle(style2);
                Cell cell4 = forWeekPerfect.createCell(4);
                cell4.setCellStyle(style2);
                Cell cell5 = forWeekPerfect.createCell(5);
                cell5.setCellStyle(style2);

                Cell cell6 = forWeekPerfect.createCell(6);
                cell6.setCellStyle(style2);
                Cell cell7 = forWeekPerfect.createCell(7);
                cell7.setCellStyle(style2);
                Cell cell8 = forWeekPerfect.createCell(8);
                cell8.setCellStyle(style2);

                Cell cell9 = forWeekPerfect.createCell(9);
                cell9.setCellStyle(style2);

                Cell cell10 = forWeekPerfect.createCell(10);
                cell10.setCellStyle(style2);
                CellRangeAddress weekPerRange = new CellRangeAddress(startRows, startRows, 2, 10);
                targetSheet.addMergedRegion(weekPerRange);


                startRows=startRows+1;
                for (Map parentMap : mapList) {




                    positionRow = targetSheet.createRow(startRows);
                    positionRow.setHeightInPoints(20);
                    Row weekRow = targetSheet.getRow(startRows);

                    Cell weekRow00 = weekRow.createCell(0);
                    weekRow00.setCellStyle(style1);
                    weekRow00.setCellValue(parentMap.get("week") + "");
                    Cell weekRow1 = weekRow.createCell(1);
                    weekRow1.setCellStyle(style1);
                    weekRow1.setCellValue(parentMap.get("day_date") + "");
                    Cell weekRow2 = weekRow.createCell(2);
                    weekRow2.setCellStyle(style2);
                    weekRow2.setCellValue(parentMap.get("plan_add") + "");
                    Cell weekRow3 = weekRow.createCell(3);
                    weekRow3.setCellStyle(style2);

                    Cell weekRow4 = weekRow.createCell(4);
                    weekRow4.setCellStyle(style2);

                    Cell weekRow5 = weekRow.createCell(5);
                    weekRow5.setCellStyle(style2);
                    weekRow5.setCellValue(parentMap.get("plan_total") + "");
                    Cell weekRow6 = weekRow.createCell(6);
                    weekRow6.setCellStyle(style2);
                    Cell weekRow7 = weekRow.createCell(7);
                    weekRow7.setCellStyle(style2);
                    Cell weekRow8 = weekRow.createCell(8);
                    weekRow8.setCellStyle(style2);
                    weekRow8.setCellValue(parentMap.get("plan_task_per") + "");
                    Cell weekRow9 = weekRow.createCell(9);
                    weekRow9.setCellStyle(style2);

                    Cell weekRow10 = weekRow.createCell(10);
                    weekRow10.setCellStyle(style2);

                    CellRangeAddress weekRowRange = new CellRangeAddress(startRows, startRows, 2, 4);
                    targetSheet.addMergedRegion(weekRowRange);
                    CellRangeAddress weekRowAdd = new CellRangeAddress(startRows, startRows, 5, 7);
                    targetSheet.addMergedRegion(weekRowAdd);
                    CellRangeAddress weekRowRate = new CellRangeAddress(startRows, startRows, 8, 10);
                    targetSheet.addMergedRegion(weekRowRate);
                    startRows++;  }

                    //此条数据类型为区域 type=


                    startRows = startRows + 2;
                    positionRow = targetSheet.createRow(startRows);
                    Row row2 = targetSheet.getRow(startRows);
                    positionRow.setHeightInPoints(20);
                    Cell cell00 = row2.createCell(0);

                    cell00.setCellValue("办卡方式");
                    CellRangeAddress region = new CellRangeAddress(startRows, startRows, 0, 10);
                    targetSheet.addMergedRegion(region);
                    cell00.setCellStyle(style3);
                    startRows = startRows + 2;
                    positionRow = targetSheet.createRow(startRows);
                    Row rowlitter = targetSheet.getRow(startRows);
                    Cell row3Zero = rowlitter.createCell(0);
                    row3Zero.setCellStyle(style1);
                    row3Zero.setCellValue("小卡");
                    Cell row3One = rowlitter.createCell(1);
                     row3One.setCellStyle(style2);
                    row3One.setCellValue(little_way);
                    for(int i=1;i<4;i++){
                            for(int j=i==1?2:1;j<11;j++){
                                Cell row3two = rowlitter.createCell(j);
                                row3two.setCellStyle(style2);
                            }
                        targetSheet.createRow(startRows+i);
                         rowlitter = targetSheet.getRow(startRows+i);
                    }


                CellRangeAddress  regionone = new CellRangeAddress(startRows, startRows + 2, 1, 10);
                    targetSheet.addMergedRegion(regionone);



                    startRows = startRows + 3;
                    positionRow = targetSheet.createRow(startRows);
                    Row rowBig = targetSheet.getRow(startRows);
                    Cell rowBigZero = rowBig.createCell(0);
                    rowBigZero.setCellStyle(style1);
                    rowBigZero.setCellValue("大卡");
                    Cell rowBigOne = rowBig.createCell(1);
                     rowBigOne.setCellStyle(style2);
                    rowBigOne.setCellValue(big_way);
                for(int i=1;i<4;i++){
                    for(int j=i==1?2:1;j<11;j++){
                        Cell row3two = rowBig.createCell(j);
                        row3two.setCellStyle(style2);
                    }
                    targetSheet.createRow(startRows+i);
                    rowBig = targetSheet.getRow(startRows+i);
                }
                CellRangeAddress   regiontwo = new CellRangeAddress(startRows, startRows + 2, 1, 10);
                    targetSheet.addMergedRegion(regiontwo);

        /*        RegionUtil.setBorderBottom(1, regionone, targetSheet); // 下边框
                RegionUtil.setBorderLeft(1, regionone, targetSheet); // 左边框
                RegionUtil.setBorderRight(1, regionone, targetSheet); // 有边框
                RegionUtil.setBorderTop(1, regionone, targetSheet); // 上边框
                RegionUtil.setBorderBottom(1, regiontwo, targetSheet); // 下边框
                RegionUtil.setBorderLeft(1, regiontwo, targetSheet); // 左边框
                RegionUtil.setBorderRight(1, regiontwo, targetSheet); // 有边框
                RegionUtil.setBorderTop(1, regiontwo, targetSheet); // 上边框*/
            }
                targetSheet.setRowSumsBelow(false);

                //  targetSheet.setForceFormulaRecalculation(true);
                fileOutputStream = new FileOutputStream(targetFilePath);
                //页面输出
                targetWorkBook.write(response.getOutputStream());

                //服务器硬盘输出
                // targetWorkBook.write(fileOutputStream);
             }catch(ServiceException se){
                se.printStackTrace();
                System.out.println(se.getResponseMsg());
            } catch(UnsupportedEncodingException e){
                System.out.print("中文字符转换异常");
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            } finally{
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
        }
    }





