package cn.visolink.firstplan.TaskLand.service.impl;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.firstplan.TaskLand.dao.TakeLandDao;
import cn.visolink.firstplan.TaskLand.pojo.tasklandPublic;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.firstplan.preemptionopen.dao.PreemptionOpenDao;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.workflowchange.dao.WorkflowChangeDao;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  * @author wyg
 *  * @since 2020-02-17
 */
@Service
public class TakeLandServiceImpl implements TakeLandService {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);    //日志记录
    @Autowired
    private TakeLandDao takeLandDao;
    @Autowired
    private WorkflowDao workflowDao;
    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;
    @Autowired
    private DesignTwoIndexService designTwoIndexService;
    @Autowired
    private PreemptionOpenDao preemptionOpenDao;

    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;


    @Autowired
    private WorkflowChangeDao business;
    @Override
    public List<Map> queryRegion(Map map) {
        List<Map> map2 = takeLandDao.queryRegion(map);
        return map2;
    }
    @Override
    public List<Map> queryCMRegion(Map map) {
        List<Map> map2 = takeLandDao.queryCMRegion(map);
        return map2;
    }

    @Override
    public List<Map> queryOrgListByOrgId(String orgId) {
        return takeLandDao.queryOrgListByOrgId(orgId);
    }

    @Override
    public List<Map> queryCitys(Map map) {
        return takeLandDao.queryCitys(map);
    }


    @Override
    public Map queryPlan(Map map) {
        Map mapPro = new HashMap();

        //前台传过来的项目id
        String projectid = map.get("project_id")+"";
        List<Map> mapContent= null;

        //抢开编制按钮展示控制
        boolean isShowpreemptionOpen=true;
        //21天延期开盘按钮展示控制
        boolean isShow21Postpone=true;
        //7天延期开盘按钮展示控制
        boolean isShow7Postpone=true;
        try {
            if(!"".equals(projectid)) {
                Map mapPlan = takeLandDao.queryPlanValue(map);
                if(mapPlan!=null){
                    mapContent = takeLandDao.queryByProjectId(map);
                    String plan_id=mapPlan.get("id")+"";
                    //查询首开前2月是否已完成
                    Map selectTwoMonth = takeLandDao.selectTwoMonth(plan_id,"5");
                    if(selectTwoMonth!=null&&selectTwoMonth.size()>0){
                        //查询前7天是否已经开始审批/已经审批
                        Map sevenDay = takeLandDao.getSevenDay(plan_id);
                        if(sevenDay != null && sevenDay.size()>0){
                            isShowpreemptionOpen=false;
                            isShow21Postpone=false;
                        }
                    }else{
                        isShowpreemptionOpen=false;
                        isShow21Postpone=false;
                    }
                    //查询首开前21天是否已经完成
                    Map selectTwentyDay = takeLandDao.selectTwoMonth(plan_id,"6");
                    if(selectTwentyDay!=null&&selectTwentyDay.size()>0){
                        //查询首开是否已经提报
                        Map openDay = takeLandDao.getOpenDay(plan_id);
                        if(openDay!=null&&openDay.size()>0){
                            isShow7Postpone=false;
                        }
                    }else{
                        isShow7Postpone=false;
                    }
                    String plan_node_idtd="";
                    String plan_node_idsd="";
                    //获取首开前21天的节点id
                    for (Map planMap : mapContent) {
                       String node_level=planMap.get("node_level")+"";
                       if("6".equals(node_level)){
                           plan_node_idtd=planMap.get("plan_node_id")+"";
                           continue;
                       }
                        if("7".equals(node_level)){
                            plan_node_idsd=planMap.get("plan_node_id")+"";
                            continue;
                        }
                    }
                    Map<Object, Object> paramMap = new HashMap<>();
                    paramMap.put("plan_id",plan_id);

                    //查询该计划首开前21天是否发起过延期开盘
                    paramMap.put("plan_node_id",plan_node_idtd);
                    paramMap.put("node_level",6);
                    List<Map> list1 = openbeforetwentyoneDao.selectIsHaveDelayApplyData(paramMap);
                    if(list1!=null&&list1.size()>0){
                        isShow21Postpone=true;
                    }
                    //判断该项目是否发起过抢开
                    List<Map> preeOenVersionData = preemptionOpenDao.getPreemptionOpenVersionData(paramMap);
                    if(preeOenVersionData!=null&&preeOenVersionData.size()>0){
                        isShowpreemptionOpen=true;
                    }
                    //查询该项目首开前7天是否发起过延期开盘
                    paramMap.put("plan_node_id",plan_node_idsd);
                    paramMap.put("node_level",7);
                    List<Map> list2 = openbeforetwentyoneDao.selectIsHaveDelayApplyData(paramMap);
                    if(list2!=null&&list2.size()>0){
                        isShow7Postpone=true;
                    }

                }else {
                    mapContent= takeLandDao.queryContent();
                    if(mapContent!=null&&mapContent.size()>0){
                        for ( Map maps : mapContent) {
                            maps.put("this_node",1);
                        }
                    }
                    isShowpreemptionOpen=false;
                    isShow21Postpone=false;
                    isShow7Postpone=false;
                }
                //是否显示抢开编制按钮
                mapPro.put("isShowpreemptionOpen",isShowpreemptionOpen);
                //是否显示21天延期开盘按钮
                mapPro.put("isShow21Postpone",isShow21Postpone);
                //是否显示7天延期开盘按钮
                mapPro.put("isShow7Postpone",isShow7Postpone);
                mapPro.put("mapContent",mapContent);
            }else {
                throw new Exception("没有关键参数project_id！");
            }
        }catch (Exception  e){
            e.printStackTrace();
        }

        return mapPro;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody insertTakeLand(Map map, HttpServletRequest request) {
        VisolinkResultBody v = new VisolinkResultBody();
        //是否是新版（创建新版）
        String isNew = map.get("isNew")+"";
        //项目计划id
        String plan_id = map.get("plan_id")+"";
        //项目计划节点id
        String plan_node_id = map.get("plan_node_id")+"";
        //登录人
        String username = request.getHeader("username");
        //拿地后的当前状态
        String light_stuat = map.get("light_stuat")+"";
        //系统当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String createTime = df.format(new Date());
        //当前日期不加时分秒
        SimpleDateFormat dfm = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String dfmNowTime = dfm.format(new Date());
        //前台传过来的项目id
        String projectid = map.get("project_id")+"";
        //产品系
        String product_set = map.get("product_set")+"";
        //岗位名称
        String jobName = null;
        try {
            //如果当前人不为空
            if(!"".equals(username)&&!"null".equals(username)){
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                if (jwtUser != null) {
                    jobName = jwtUser.getJob().get("JobName") + "";
                }
            }

            //如果计划节点不为空，查询数据库有无数据，有则删除
            if(!"".equals(plan_node_id)){
                Map nodeMapDel = new HashMap();
                nodeMapDel.put("plan_node_id",plan_node_id);
                takeLandDao.deleteTakeLandValue(nodeMapDel);
                takeLandDao.deleteTakeLandRoom(nodeMapDel);
                takeLandDao.deleteTakeLandDate(nodeMapDel);
                takeLandDao.deleteTakeLandCost(nodeMapDel);
                takeLandDao.deleteTakeLandSales(nodeMapDel);
            }
            //查询模板节点信息
            List<Map> mapContent= takeLandDao.queryContent();
            if(!"".equals(projectid)){
                //查询区域项目
                Map mapProject = takeLandDao.queryPrpject(map);
                //查看是否该项目是否存在计划，不存在则创建计划与十二个节点
                List<Map> mapPlan= takeLandDao.queryPlan(map);
                if(mapPlan.size() == 0){
                    if(mapProject!=null){
                        map.put("project_name",mapProject.get("project_name")+"");
                        map.put("area_id",mapProject.get("business_unit_id")+"");
                        map.put("area_name",mapProject.get("business_unit_name")+"");
                    }else {
                        throw new Exception("未找到项目相关信息！");
                    }
                    String newPlan_id = UUID.randomUUID().toString();
                    map.put("template_id",mapContent.get(0).get("template_id")+"");
                    map.put("plan_id",newPlan_id);
                    map.put("this_node","1");
                    //当没有计划id时，新创建的计划id则为当前需要用的计划id
                    plan_id = newPlan_id;
                    //创建计划
                    takeLandDao.insertPlan(map);
                    //添加节点
                    List<Map> mapNodeList = new ArrayList<>();
                    for (int i = 0; i < mapContent.size(); i++) {
                        String planNodeId = UUID.randomUUID().toString();
                        String nodeLevel = mapContent.get(i).get("node_level")+"";
                        String node_name = mapContent.get(i).get("node_name")+"";
                        String node_order = mapContent.get(i).get("node_order")+"";
                        String warning_day = mapContent.get(i).get("warning_day")+"";
                        String send_post_code = mapContent.get(i).get("send_post_code")+"";
                        String send_post_name = mapContent.get(i).get("send_post_name")+"";
                        Integer warningDayNum =0;
                        if(warning_day!=null && !"null".equals(warning_day)){
                            warningDayNum = Integer.parseInt(warning_day);
                        }
                        Map mapNodePlNde = new HashMap();
                        if("1".equals(node_order)){
                            plan_node_id = planNodeId;
                            mapNodePlNde.put("product_set",product_set);
                        }
                        mapNodePlNde.put("is_effective","1");
                        mapNodePlNde.put("light_stuat","1");
                        mapNodePlNde.put("plan_approval","2");
                        mapNodePlNde.put("template_node_id",mapContent.get(i).get("template_node_id")+"");
                        mapNodePlNde.put("plan_id",newPlan_id);
                        mapNodePlNde.put("node_name",node_name);
                        mapNodePlNde.put("creator",username);
                        mapNodePlNde.put("create_time",createTime);
                        mapNodePlNde.put("editor",username);
                        mapNodePlNde.put("update_time",createTime);
                        mapNodePlNde.put("node_level",nodeLevel);
                        mapNodePlNde.put("planNodeId",planNodeId);
                        mapNodePlNde.put("node_order",node_order);
                        mapNodePlNde.put("version",node_name+"v"+dfmNowTime+"-1");
                        mapNodePlNde.put("send_post_code",send_post_code);
                        mapNodePlNde.put("send_post_name",send_post_name);
                        mapNodePlNde.put("warning_day",warningDayNum);
                        mapNodeList.add(mapNodePlNde);
                    }
                    //创建计划节点
                    takeLandDao.insertPlNde(mapNodeList);

                }else {
                    //如果是创建新版本，则需要创建新版本
                    if("创建新版".equals(isNew)){
                        Map numMap = new HashMap();
                        numMap.put("plan_id", plan_id);
                        numMap.put("node_order","1");
                        Map numMaptwo = takeLandDao.queryPlanNodeNum(numMap);
                        String num = numMaptwo.get("num")+"";
                        Integer numves = Integer.parseInt(num)+1;
                        //创建拿地后新版本节点
                        List<Map> mapNodeList = new ArrayList<>();
                        String planNodeId = UUID.randomUUID().toString();
                        plan_node_id = planNodeId;
                        String template_node_id = "";
                        String warning_day = "";
                        String send_post_code = "";
                        String send_post_name = "";
                        Integer warningDayNum = 0;
                        Map mapNodePlNde = new HashMap();
                        for (int i = 0; i < mapContent.size(); i++) {
                            String node_order = mapContent.get(i).get("node_order")+"";
                            if("1".equals(node_order)){
                                template_node_id = mapContent.get(i).get("template_node_id")+"";
                                warning_day = mapContent.get(i).get("warning_day")+"";
                                send_post_code = mapContent.get(i).get("send_post_code")+"";
                                send_post_name = mapContent.get(i).get("send_post_name")+"";
                                if(warning_day!=null && !"null".equals(warning_day)){
                                    warningDayNum = Integer.parseInt(warning_day);
                                }
                                break;
                            }
                        }
                        mapNodePlNde.put("light_stuat","1");
                        mapNodePlNde.put("template_node_id",template_node_id);
                        mapNodePlNde.put("plan_id",plan_id);
                        mapNodePlNde.put("node_name","拿地后");
                        mapNodePlNde.put("creator",username);
                        mapNodePlNde.put("create_time",createTime);
                        mapNodePlNde.put("editor",username);
                        mapNodePlNde.put("update_time",createTime);
                        mapNodePlNde.put("version","拿地后v"+dfmNowTime+"-"+numves);
                        mapNodePlNde.put("node_level","1");
                        mapNodePlNde.put("planNodeId",planNodeId);
                        mapNodePlNde.put("node_order","1");
                        mapNodePlNde.put("plan_approval","2");
                        mapNodePlNde.put("is_effective","1");
                        mapNodePlNde.put("warning_day",warningDayNum);
                        mapNodePlNde.put("send_post_code",send_post_code);
                        mapNodePlNde.put("send_post_name",send_post_name);
                        if(!"".equals(product_set) && !"null".equals(product_set)){
                            mapNodePlNde.put("product_set",product_set);
                        }
                        mapNodeList.add(mapNodePlNde);
                        //创建计划节点
                        takeLandDao.insertPlNde(mapNodeList);
                    }else {
                        Map map1 = new HashMap();
                        map1.put("update_time",createTime);
                        map1.put("editor",username);
                        map1.put("plan_node_id",plan_node_id);
                        map1.put("plan_id",plan_id);
                        map1.put("product_set",product_set);
                        takeLandDao.updateTakeLandStatus(map1);
                    }

                }
            }else {
                throw new Exception("没有关键参数project_id！");
            }

            //货值结构
            List<Map> mapValues = (List<Map>) map.get("tollerlist");
            tasklandPublic tasklandPublic = new tasklandPublic();
            if(mapValues!=null && mapValues.size()>0){
                List<Map> mapValuesResult = analyticalWarehous(mapValues,plan_node_id);
                //处理一下参数，将空和null的去掉
                List<Map> mapValuesResult2 = tasklandPublic.publicNull(mapValuesResult);
                takeLandDao.insertValueStructure(mapValuesResult2);
            }
            //户型
            List<Map> mapRoom = (List<Map>) map.get("roomlist");
            if(mapRoom!=null && mapRoom.size()>0){
                List<Map> mapValuesResult = analyticalWarehous(mapRoom,plan_node_id);
                //处理一下参数，将空和null的去掉
                List<Map> mapValuesResult2 = tasklandPublic.publicNull(mapValuesResult);
                takeLandDao.insertRoom(mapValuesResult2);
            }
            //时间节点
            Map mapTime = (Map) map.get("timeNode");
            if(mapTime!=null){
                String delisting_time = mapTime.get("delisting_time")+"";
                String sales_time = mapTime.get("sales_time")+"";
                String designone_time = mapTime.get("designone_time")+"";
                String sample_open_time = mapTime.get("sample_open_time")+"";
                String designtwo_time = mapTime.get("designtwo_time")+"";
                String model_open_time = mapTime.get("model_open_time")+"";
                String open_time = mapTime.get("open_time")+"";
                if("".equals(delisting_time)){
                    mapTime.put("delisting_time",null);
                }
                if("".equals(sales_time)){
                    mapTime.put("sales_time",null);
                }
                if("".equals(designone_time)){
                    mapTime.put("designone_time",null);
                }
                if("".equals(sample_open_time)){
                    mapTime.put("sample_open_time",null);
                }
                if("".equals(designtwo_time)){
                    mapTime.put("designtwo_time",null);
                }
                if("".equals(model_open_time)){
                    mapTime.put("model_open_time",null);
                }
                if("".equals(open_time)){
                    mapTime.put("open_time",null);
                }
                mapTime.put("plan_id",plan_id);
                mapTime.put("plan_node_id",plan_node_id);
                takeLandDao.insertTimeNode(mapTime);
            }
            //销售目标
            List<Map> mapSales = (List<Map>) map.get("sales");
            if(mapSales!=null && mapSales.size()>0){
                for (int i = 0; i < mapSales.size(); i++) {
                    mapSales.get(i).put("plan_id",plan_id);
                    mapSales.get(i).put("plan_node_id",plan_node_id);
                }
                //处理一下参数，将空和null的去掉
                List<Map> mapSalesNUll = tasklandPublic.publicNull(mapSales);
                takeLandDao.insertSales(mapSalesNUll);

            }
            //费用
            List<Map> mapCost = (List<Map>) map.get("cost");
            if(mapCost!=null && mapCost.size()>0){
                mapCost.get(0).put("plan_id",plan_id);
                mapCost.get(0).put("plan_node_id",plan_node_id);
                //处理一下参数，将空和null的去掉
                List<Map> mapCostNull = tasklandPublic.publicNull(mapCost);
                takeLandDao.insertCost(mapCostNull);
            }

            //给已经提交的文件做关联
            List<Map> fileList = (List<Map>) map.get("fileList");
            if(fileList.size()>0){
                takeLandDao.delFile(plan_node_id);
                for (int i = 0; i < fileList.size(); i++) {
                    String id = fileList.get(i).get("id")+"";
                    if(!"".equals(id)&&!"null".equals(id)){
                        Map fileMap = fileList.get(i);
                        fileMap.put("BizID",plan_node_id);
                        takeLandDao.updateSattach(fileMap);
                    }

                }

            }

            //如果会提交，则发起审批流程
            if("3".equals(light_stuat) || "4".equals(light_stuat)){
                Map<Object, Object> parameterMap = new HashMap<>();
                parameterMap.put("product_set",product_set);
                Map flowMap = new HashMap();
                flowMap.put("json_id",plan_node_id);
                flowMap.put("project_id",projectid);
                flowMap.put("creator",username);
                flowMap.put("orgName","fp_land_back");
                flowMap.put("comcommon", JSON.toJSONString(parameterMap));
                flowMap.put("TITLE","拿地后流程审批");
                flowMap.put("post_name",jobName);
                v = insertFlow(flowMap);
            }

        }catch (Exception e){
            v.setCode(-1);
            v.setMessages("提交失败，请检查数据格式是否正确！");
            logger.info("回滚事务");
            logger.error("失败：" + e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        }

        return v;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody insertTopOne(Map map,HttpServletRequest request) {
        VisolinkResultBody v = new VisolinkResultBody();
        //项目计划id
        String plan_id = map.get("plan_id")+"";
        //项目计划节点id
        String plan_node_id = map.get("plan_node_id")+"";
        //登录人
        String username = request.getHeader("username");
        //当前状态
        String light_stuat = map.get("light_stuat")+"";
        //是否创建新版
        String isNew = map.get("isNew")+"";
        //系统当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String createTime = df.format(new Date());
        //当前日期不加时分秒
        SimpleDateFormat dfm = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String dfmNowTime = dfm.format(new Date());
        //前台传过来的项目id
        String projectid = map.get("project_id")+"";
        //过会次数
        String browse_num = map.get("browse_num")+"";
        //产品系
        String product_set = map.get("product_set")+"";
        //岗位名称
        String jobName = null;
        try{
            //如果当前人不为空
            if(!"".equals(username)&&!"null".equals(username)){
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                if (jwtUser != null) {
                    jobName = jwtUser.getJob().get("JobName") + "";
                }
            }
            if(!"".equals(plan_node_id)){
                Map nodeMapDel = new HashMap();
                nodeMapDel.put("plan_node_id",plan_node_id);
                takeLandDao.deleteDesignoneValue(nodeMapDel);
                takeLandDao.deletedesignoneRoom(nodeMapDel);
                takeLandDao.deletedeSalesTarget(nodeMapDel);
                takeLandDao.deleteTakeLandDate(nodeMapDel);
            }

            //如果是创建新版本，则需要创建新版本
            //查询模板节点信息
            List<Map> mapContent= takeLandDao.queryContent();
            if("创建新版".equals(isNew)){
                Map numMap = new HashMap();
                numMap.put("plan_id", plan_id);
                numMap.put("node_order","2");
                Map numMaptwo = takeLandDao.queryPlanNodeNum(numMap);
                String num = numMaptwo.get("num")+"";
                Integer numves = Integer.parseInt(num)+1;
                //创建拿地后新版本节点
                List<Map> mapNodeList = new ArrayList<>();
                String planNodeId = UUID.randomUUID().toString();
                plan_node_id = planNodeId;
                String template_node_id = "";
                String warning_day = "";
                String send_post_code = "";
                String send_post_name = "";
                Map mapNodePlNde = new HashMap();
                //此循环目的是为了获取顶设1节点的数据
                for (int i = 0; i < mapContent.size(); i++) {
                    String node_order = mapContent.get(i).get("node_order")+"";
                    if("2".equals(node_order)){
                        template_node_id = mapContent.get(i).get("template_node_id")+"";
                        warning_day = mapContent.get(i).get("warning_day")+"";
                        send_post_code = mapContent.get(i).get("send_post_code")+"";
                        send_post_name = mapContent.get(i).get("send_post_name")+"";
                        break;
                    }
                }
                mapNodePlNde.put("light_stuat",light_stuat);
                mapNodePlNde.put("template_node_id",template_node_id);
                mapNodePlNde.put("plan_id",plan_id);
                mapNodePlNde.put("node_name","顶设1");
                mapNodePlNde.put("creator",username);
                mapNodePlNde.put("create_time",createTime);
                mapNodePlNde.put("editor",username);
                mapNodePlNde.put("update_time",createTime);
                mapNodePlNde.put("version","顶设1v"+dfmNowTime+"-"+numves);
                mapNodePlNde.put("node_level","2");
                mapNodePlNde.put("planNodeId",planNodeId);
                mapNodePlNde.put("node_order","2");
                mapNodePlNde.put("node_level_status","2");
                mapNodePlNde.put("warning_day",warning_day);
                mapNodePlNde.put("send_post_code",send_post_code);
                mapNodePlNde.put("send_post_name",send_post_name);
                if(!"".equals(browse_num)&&!"null".equals(browse_num)){
                    mapNodePlNde.put("browse_num",browse_num);
                }
                if(!"".equals(product_set)&&!"null".equals(product_set)){
                    mapNodePlNde.put("product_set",product_set);
                }

                //获取计划完成时间和计划开始时间
                Map mapPlanEnd = takeLandDao.querytakeLandNdId(mapNodePlNde);
                if(mapPlanEnd!=null){
                    String planEnd = mapPlanEnd.get("plan_end_time")+"";
                    String planStart = mapPlanEnd.get("plan_start_time")+"";
                    if(!"null".equals(planStart) && !"".equals(planStart)){
                        mapNodePlNde.put("plan_start_time",planStart);
                        if(planStart.compareTo(createTime)<0){
                            mapNodePlNde.put("light_stuat","6");
                        }
                    }
                    if(planEnd!=null && !"null".equals(planEnd)){
                        mapNodePlNde.put("plan_end_time",planEnd);
                        if(planEnd.compareTo(createTime)<0){
                            mapNodePlNde.put("light_stuat","5");
                        }
                    }
                }
                mapNodePlNde.put("plan_approval","2");
                mapNodePlNde.put("is_effective","1");
                mapNodeList.add(mapNodePlNde);
                //创建计划节点
                takeLandDao.insertPlNde(mapNodeList);
            }else {
                //主要目的是更新过会次数
                map.remove("light_stuat");
                if("".equals(browse_num)){
                    map.put("browse_num",null);
                }
                if("".equals(product_set)){
                    map.put("product_set",null);
                }
                takeLandDao.updateTakeLandStatus(map);

            }

            tasklandPublic tasklandPublic = new tasklandPublic();
            //货值结构
            List<Map> mapValues = (List<Map>) map.get("tollerlist");
            //户型
            List<Map> mapRoom = (List<Map>) map.get("roomlist");
            //时间节点
            Map mapTime = (Map) map.get("timeNode");

            if(mapValues!=null && mapValues.size()>0){
                List<Map> mapValuesResult = analyticalWarehous(mapValues,plan_node_id);
                //处理一下参数，将空和null的去掉
                List<Map> mapValuesResult2 = tasklandPublic.publicNull(mapValuesResult);
                takeLandDao.insertTopOneValues(mapValuesResult2);
            }

            //户型
            if(mapRoom!=null && mapRoom.size()>0){
                List<Map> mapValuesResult = analyticalWarehous(mapRoom,plan_node_id);
                //处理一下参数，将空和null的去掉
                List<Map> mapValuesResultNull = tasklandPublic.publicNull(mapValuesResult);
                takeLandDao.insertTopOneApartment(mapValuesResultNull);
            }

            if(mapTime!=null){
                String delisting_time = mapTime.get("delisting_time")+"";
                String sales_time = mapTime.get("sales_time")+"";
                String designone_time = mapTime.get("designone_time")+"";
                String sample_open_time = mapTime.get("sample_open_time")+"";
                String designtwo_time = mapTime.get("designtwo_time")+"";
                String model_open_time = mapTime.get("model_open_time")+"";
                String open_time = mapTime.get("open_time")+"";
                if("".equals(delisting_time)){
                    mapTime.put("delisting_time",null);
                }
                if("".equals(sales_time)){
                    mapTime.put("sales_time",null);
                }
                if("".equals(designone_time)){
                    mapTime.put("designone_time",null);
                }
                if("".equals(sample_open_time)){
                    mapTime.put("sample_open_time",null);
                }
                if("".equals(designtwo_time)){
                    mapTime.put("designtwo_time",null);
                }
                if("".equals(model_open_time)){
                    mapTime.put("model_open_time",null);
                }
                if("".equals(open_time)){
                    mapTime.put("open_time",null);
                }
                mapTime.put("plan_id",plan_id);
                mapTime.put("plan_node_id",plan_node_id);
                takeLandDao.insertTimeNode(mapTime);

            }

            //销售目标
            List<Map> mapSales = (List<Map>) map.get("sales");
            if(mapSales!=null && mapSales.size()>0){
                for (int i = 0; i < mapSales.size(); i++) {
                    mapSales.get(i).put("plan_id",plan_id);
                    mapSales.get(i).put("plan_node_id",plan_node_id);
                }
                //处理一下参数，将空和null的去掉
                List<Map> mapSalesNull = tasklandPublic.publicNull(mapSales);
                takeLandDao.insertTopOneSales(mapSalesNull);
            }

            //给已经提交的文件做关联
            List<Map> fileList = (List<Map>) map.get("fileList");
            if(fileList!=null && fileList.size()>0){
                takeLandDao.delFile(plan_node_id);
                for (int i = 0; i < fileList.size(); i++) {
                    String id = fileList.get(i).get("id")+"";
                    if(!"".equals(id)&&!"null".equals(id)){
                        Map fileMap = fileList.get(i);
                        fileMap.put("BizID",plan_node_id);
                        takeLandDao.updateSattach(fileMap);
                    }

                }

            }

            //流程发起（3正常发起，4快速审批）
            if("3".equals(light_stuat) || "4".equals(light_stuat)){
                Map<Object, Object> parameterMap = new HashMap<>();
                parameterMap.put("product_set",product_set);
                //发起流程保存实例
                Map flowMap = new HashMap();
                flowMap.put("json_id",plan_node_id);
                flowMap.put("project_id",projectid);
                flowMap.put("creator",username);
                flowMap.put("TITLE","顶设1流程审批");
                flowMap.put("orgName","fp_designone");
                flowMap.put("comcommon", JSON.toJSONString(parameterMap));
                flowMap.put("post_name",jobName);
                v = insertFlow(flowMap);
            }

        }catch (Exception e){
            v.setCode(-1);
            logger.info("回滚事务");
            logger.error("添加失败：" + e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return v;
    }


    @Override
    public List<Map> selectPlanNode(Map map) {
        String project_id = map.get("project_id")+"";
        List<Map> list = null;
        try {
            if(!"".equals(project_id) && project_id != null){
                list = takeLandDao.selectPlanNode(map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map queryTakeLands(Map map) {
        String id = map.get("plan_node_id")+"";
        List fileList = null;
        if("".equals(id)||"null".equals(id)){
            map.put("node_level_status","1");
            Map nodeIdMap = takeLandDao.querytakeLandNdId(map);
            if(nodeIdMap==null){
                return null;
            }else {
                String node_id = nodeIdMap.get("plan_node_id")+"";
                id = node_id;
                map.put("plan_node_id",node_id);
            }
        }else {
            fileList = takeLandDao.getFileLists(id);
        }
        List<Map> tollerlist = takeLandDao.queryValueStructure(map);
        List<Map> roomlist = takeLandDao.queryApartment(map);
        List<Map> timeNode = takeLandDao.queryTimeNode(map);
        List<Map> sales = takeLandDao.querySalesTarget(map);
        List<Map> cost = takeLandDao.queryCost(map);

        map.put("node_order","2");
        List numberList = takeLandDao.queryTopNumber(map);
        Map resultMap = new HashMap();
        //解析货值结构数据传给前端
        List<Map> tollerMap = new ArrayList<>();
        if(tollerlist!=null){
            for (int i = 0; i < tollerlist.size(); i++) {
                String takeLandId = tollerlist.get(i).get("plan_node_id")+"";
                tollerlist.get(i).put("takeLandId",takeLandId);
            }
            tollerMap = analyticalData(tollerlist);
        }
        //解析户型数据传给前端
        List<Map> roomMap = new ArrayList<>();
        if(roomlist!=null){
            roomMap = analyticalData(roomlist);
        }
        //查询产品系和过会次数
        Map browseNumMap = takeLandDao.queryTopOneOnly(map);
        String product_set = "";
        if(browseNumMap!=null){
            product_set = browseNumMap.get("product_set")+"";
        }
        if(!"".equals(product_set) && !"null".equals(product_set)){
            resultMap.put("product_set",product_set);
        }
        //查询用户个人信息
        Map peopleMap = takeLandDao.queryPeopleMessage(map);

        resultMap.put("tollerMap",tollerMap);
        resultMap.put("roomlist",roomMap);
        resultMap.put("timeNode",timeNode);
        resultMap.put("sales",sales);
        resultMap.put("cost",cost);
        resultMap.put("fileList",fileList);
        resultMap.put("numberList",numberList);
        resultMap.put("peopleMap",peopleMap);
        return resultMap;
    }

    @Override
    public List<Map> selectNodeVersion(Map map) {
        return takeLandDao.selectNodeVersion(map);
    }

    @Override
    public Map queryValueStructure(Map map) {
        List<Map> tollerlist = takeLandDao.queryValueStructure(map);
        //解析货值结构数据传给前端
        Map resultMap = new HashMap();
        List<Map> tollerMap = new ArrayList<>();
        if(tollerlist!=null){
            tollerMap = analyticalData(tollerlist);
        }
        resultMap.put("tollerMap",tollerMap);
        return resultMap;
    }

    @Override
    public Map queryApartment(Map map) {
        List<Map> roomlist = takeLandDao.queryByTopOne(map);
        //解析户型数据传给前端
        Map resultMap = new HashMap();
        List<Map> roomMap = new ArrayList<>();
        if(roomlist!=null){
            roomMap = analyticalData(roomlist);
        }
        resultMap.put("roomlist",roomMap);
        return resultMap;
    }

    @Override
    public List<Map> queryTimeNode(Map map) {
        return takeLandDao.queryTimeNode(map);
    }

    @Override
    public List<Map> querySalesTarget(Map map) {
        return takeLandDao.querySalesTarget(map);
    }

    @Override
    public List<Map> queryCost(Map map) {
        return takeLandDao.queryCost(map);
    }

    @Override
    public List<Map> queryPlanNodeId(Map map) {
        return takeLandDao.queryPlanNodeId(map);
    }

    @Override
    public List getFileLists(String id) {
        return takeLandDao.getFileLists(id);
    }

    @Override
    public Map queryTopOne(Map map) {

        //项目计划id
        String plan_id = map.get("plan_id")+"";
        //项目计划节点id
        String plan_node_id = map.get("plan_node_id")+"";
        //拿地后的当前状态
        String light_stuat = map.get("light_stuat")+"";
        //节点等级
        String node_level = map.get("node_level")+"";
        //是否创建新版
        String isNew = map.get("isNew")+"";
        //系统当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String createTime = df.format(new Date());
        //前台传过来的项目id
        String projectid = map.get("project_id")+"";
        try {
            Map valueMap = new HashMap();
            Map LandMap = new HashMap();
            if(!"".equals(plan_node_id) || "创建新版".equals(isNew)){

                List<Map> valueList = takeLandDao.queryTopOneValue(map);
                //如果有数据，显示顶设一的数据
                if(valueList.size()>0 && !"创建新版".equals(isNew)){
                    //查询顶设一数据
                    valueMap = queryTopOneMessages(map,valueList,plan_node_id,isNew);

                }else if("创建新版".equals(isNew)){
                    map.put("node_level_status","2");
                    Map nodeIdMap = takeLandDao.querytakeLandNdId(map);
                    if(nodeIdMap==null){
                        //如果没有数据，说明没有存过数据，则取查询拿地后的数据
                        valueMap = queryLand(node_level,map);
                        LandMap = queryLand(node_level,map);
                        valueMap.put("LandMap",LandMap);
                    }else {
                        String node_id = nodeIdMap.get("plan_node_id")+"";
                        //查询顶设一数据
                        valueMap = queryTopOneMessages(map,valueList,node_id,isNew);
                    }
                }else {
                    //如果没有数据，说明没有存过数据，则取查询拿地后的数据
                    valueMap = queryLand(node_level,map);
                    LandMap = queryLand(node_level,map);
                    valueMap.put("LandMap",LandMap);
                }
            }else {
                throw new Exception("没有关键参数plan_node_id！");
            }
            return valueMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //获取顶设一数据
    public Map queryTopOneMessages(Map map,List<Map> valueList,String plan_node_id,String isNew){
        Map valueMap = new HashMap();
        Map LandMap = new HashMap();
        //节点等级
        String node_level = map.get("node_level")+"";
        List fileList = null;

        if("创建新版".equals(isNew)){
            map.remove("plan_node_id");
            map.put("plan_node_id",plan_node_id);
            valueList = takeLandDao.queryTopOneValue(map);
        }else {
            //文件
            fileList = takeLandDao.getFileLists(plan_node_id);
        }

        List<Map> tollerMap = analyticalData(valueList);
        valueMap.put("tollerMap",tollerMap);
        //户型
        List<Map> roomlist = takeLandDao.queryTopOneRoom(map);
        List<Map> roomMap = analyticalData(roomlist);
        valueMap.put("roomlist",roomMap);
        //时间节点
        List<Map> timeNode = takeLandDao.queryTimeNode(map);
        valueMap.put("timeNode",timeNode);
        //销售目标
        List<Map> sales = takeLandDao.queryTopOneSales(map);
        valueMap.put("sales",sales);

        valueMap.put("fileList",fileList);
        //是否符合创建版本要求
        map.put("node_order","3");
        List numberList = takeLandDao.queryTopNumber(map);
        valueMap.put("numberList",numberList);
        //拿地后的数据
        LandMap = queryLand(node_level,map);
        valueMap.put("LandMap",LandMap);
        //查询产品系和过会次数
        Map browseNumMap = takeLandDao.queryTopOneOnly(map);
        String browse_num = browseNumMap.get("browse_num")+"";
        String product_set = browseNumMap.get("product_set")+"";
        if(!"".equals(browse_num)&&!"null".equals(browse_num)){
            valueMap.put("browse_num",browse_num);
        }
        if(!"".equals(product_set)&&!"null".equals(product_set)){
            valueMap.put("product_set",product_set);
        }
        //查询用户个人信息
        Map peopleMap = takeLandDao.queryPeopleMessage(map);
        valueMap.put("peopleMap",peopleMap);
        return valueMap;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody insertFlow(Map map) {
        takeLandDao.insertflowLog(map+"");

        VisolinkResultBody resBoby=new VisolinkResultBody();
        String json_id = map.get("json_id")+"";//必填
        String project_id = map.get("project_id")+"";//必填
        //获取项目名称
        Map projectInfo=takeLandDao.selectTmmProjectByProjectId(project_id);
        String creator = map.get("creator")+"";//必填
        String flow_code = map.get("flow_code")+"";//必填
        String project_name="";
        map.put("stage_id",project_id);
        //区域id
        String business_unit_id = "";
        if (projectInfo!=null) {
            project_name=projectInfo.get("project_name")+"";
            business_unit_id=projectInfo.get("business_unit_id")+"";
        }
        //判断是否是授权集团
        if(!"".equals(business_unit_id) && !"null".equals(business_unit_id)){
            if("10020000".equals(business_unit_id) || "10040000".equals(business_unit_id)
                    ||"10080000".equals(business_unit_id)||"10030000".equals(business_unit_id)||"10120000".equals(business_unit_id)){
                //授权小集团
                flow_code = "SkAuthorized";
                if("10030000".equals(business_unit_id)||"10040000".equals(business_unit_id)||"10120000".equals(business_unit_id)
                ){
                    flow_code = "SkAuthorized_2020";
                }
            }else if("10270000".equals(business_unit_id)){
                //山东首开流程
                flow_code = "SK_SD";
            }else {
                if("10130000".equals(business_unit_id) || "10070000".equals(business_unit_id)
                        || "50007031".equals(business_unit_id) || "10100000".equals(business_unit_id)
                        || "10150000".equals(business_unit_id) || "10110000".equals(business_unit_id)
                        || "10010000".equals(business_unit_id) || "10060000".equalsIgnoreCase(business_unit_id)
                ){
                    //非授权小集团
                    flow_code = "SkAuthorized_2020";
                }else{
                    //非授权小集团
                    flow_code = "SkUnAuthorized";
                }

            }
        }
        String TITLE ="";//非必填
        if(project_name.endsWith("项目")){
            TITLE ="首开计划"+project_name+map.get("TITLE")+"";//非必填
        }else {
            TITLE ="首开计划"+project_name+"项目"+map.get("TITLE")+"";//非必填
        }

        String comcommon = map.get("comcommon")+"";//非必填（有多个分支的必填）
        String post_name = map.get("post_name")+"";
        String orgName = map.get("orgName")+"";//必填

        try{
            if(json_id==null || "null".equals(json_id) || "".equals(json_id)){
                resBoby.setMessages("添加失败，未解析到json_id");
                throw new RuntimeException("");
            }
            if(project_id==null || "null".equals(project_id) || "".equals(project_id)){
                resBoby.setMessages("添加失败，未解析到project_id");
                throw new RuntimeException("");
            }
            if(flow_code==null || "null".equals(flow_code) || "".equals(flow_code)){
                resBoby.setMessages("添加失败，未解析到flow_code");
                throw new RuntimeException("");
            }else {
                map.put("flow_code",flow_code);
            }
            if(creator==null || "null".equals(creator) || "".equals(creator)){
                resBoby.setMessages("添加失败，未解析到creator");
                throw new RuntimeException("");
            }
            if(orgName==null || "null".equals(orgName) || "".equals(orgName)){
                resBoby.setMessages("添加失败，未解析到orgName");
                throw new RuntimeException("");
            }
            map.put("flow_status","1");
            map.put("flow_type","FP");
            map.put("TITLE",TITLE);
            map.put("comcommon",comcommon);
            map.put("post_name",post_name);
            Map flowMap = takeLandDao.selectFlowInfoByJsonId(json_id);
            if(flowMap!=null&&flowMap.size()>0){
                String flow_status=flowMap.get("flow_status")+"";
                String json_ids=flowMap.get("json_id")+"";
                //如果当前流程已经作废
                if("7".equals(flow_status)){
                    //查询该流程有没有被废弃重发起过
                    Map oldFlowMap = business.queryFlowDateByBaseID(json_ids, "", "", "", "");
                    if(oldFlowMap!=null){
                        String oldFlow_statuss=oldFlowMap.get("flow_status")+"";
                        //如果没有被作废过，就不生成新的流程
                        if(!"7".equals(oldFlow_statuss)){
                            map.put("json_id",oldFlowMap.get("json_id")+"");
                            map.put("base_id",json_id);
                            takeLandDao.updateFlowInfoByJsonId(map);
                        }

                    }else{
                        String newJsonid = UUID.randomUUID().toString();
                        map.put("json_id",newJsonid);
                        map.put("base_id",json_id);
                        takeLandDao.insertFlow(map);
                    }

                }else{
                    takeLandDao.updateFlowInfoByJsonId(map);
                }
            }else{
                takeLandDao.insertFlow(map);
            }
            resBoby.setResult(map);
            resBoby.setMessages("成功");
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.info("回滚事务");
            logger.error("添加失败：" + e.getMessage(), e);
        }
        return resBoby;
    }

    @Override
    public void ExportGetLand(HttpServletRequest request, HttpServletResponse response,  Map map) {
        String filePath;
        //配置本地模版路径
        String realpath = null;
//        realpath = "D:\\dmMarketing\\marketing-control-api\\marketing-control-api\\cifimaster\\visolink-sales-api\\src\\main\\webapp\\TemplateExcel\\getTopOne.xlsx";
        //配置服务器模版路径
        realpath = request.getServletContext().getRealPath("/");
        String templatePath = File.separator + "TemplateExcel" + File.separator;
//        filePath=realpath;
        filePath = realpath + templatePath;
        FileInputStream fileInputStream = null;
        String levelNodeName="";
        String levelNodeNameLand="拿地后-填报导出数据";
        String levelNodeNameTop="顶设1-填报导出数据";
        String fileName = "";

        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");

        try {

            //需要导出的数据
            String plan_node_id = request.getParameter("plan_node_id")+"";
            String plan_id = request.getParameter("plan_id")+"";
            String product_set = request.getParameter("product_set")+"";
            String projectName = request.getParameter("projectName")+"";
            String isType = request.getParameter("isType")+"";
            String browse_num = request.getParameter("browse_num")+"";
            map.put("plan_id",plan_id);
            map.put("plan_node_id",plan_node_id);

            //判断
            List<Map> tollerlist = null;
            List<Map> roomlist = null;
            List<Map> sales = null;
            //拿地后
            if(!"1".equals(isType)){
                //页脚名称
                fileName = levelNodeNameLand;
                //标题
                levelNodeName = projectName+"-"+levelNodeNameLand;
                //货值结构原始数据
                tollerlist = takeLandDao.queryValueStructure(map);
                //户型原始数据
                roomlist = takeLandDao.queryApartment(map);
                //导出销售目标
                sales = takeLandDao.querySalesTarget(map);
                filePath = filePath +"getLand.xlsx";
            }else {
                //顶设1
                //页脚名称
                fileName = levelNodeNameTop;
                //标题
                levelNodeName = projectName+"-"+levelNodeNameTop;
                //顶设一货值结构原始数据
                tollerlist = takeLandDao.queryTopOneValue(map);
                //户型原始数据
                roomlist = takeLandDao.queryTopOneRoom(map);
                //销售目标原始数据
                sales = takeLandDao.queryTopOneSales(map);
                filePath = filePath +"getTopOne.xlsx";
            }

            File templateFile = new File(filePath);
            if (!templateFile.exists()) {
                throw new BadRequestException(1004, "未读取到配置的导出模版，请先配置导出模版!");
            }
            //使用poi读取模版文件
            fileInputStream = new FileInputStream(templateFile);
            //创建工作簿对象，
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            //获取首开审批所需要的模版sheet页
            XSSFSheet sheetAt = workbook.getSheetAt(0);
            XSSFRow row = sheetAt.getRow(0);
            //标题
            row.getCell(0).setCellValue(levelNodeName);
            //填充产品系
            XSSFRow atRow = sheetAt.getRow(2);
            XSSFCell cell = atRow.getCell(1);
            XSSFCell cellGhNum = atRow.getCell(3);
            if(!"".equals(product_set)&&!"null".equals(product_set)&&!"undefined".equals(product_set)){
                cell.setCellValue(product_set);
            }else {
                cell.setCellValue("--");
            }
            CellStyle cellStyle = cell.getCellStyle();

            //导出货值结构
            //货值结构处理后数据
            List<Map> tollerMap = analyticalData(tollerlist);
            //Excel货值结构数据填充
            tasklandPublic pub = new tasklandPublic();
            Integer numberToller = pub.ExcelPublicToller(tollerlist,tollerMap,sheetAt,cellStyle,isType);

            //导出户型
            //户型处理后数据
            List<Map> roomMap = analyticalData(roomlist);
            //原始填充数据行数
            Integer startRoom = numberToller+5;
            Integer numberRoom = pub.ExcePublicRoom(roomlist,roomMap,sheetAt,cellStyle,startRoom,isType);

            //导出时间节点
            Map timeNode = takeLandDao.selectIndexTime(map);
            pub.ExcelPublicTime(timeNode,sheetAt,numberRoom);

            //导出销售目标
            Integer salesNumber = pub.ExcelPublicSales(sales,sheetAt,cellStyle,numberRoom,isType);

            if(!"1".equals(isType)){
                //费用导出
                List<Map> cost = takeLandDao.queryCost(map);
                pub.ExcelPublicCose(cost,sheetAt,salesNumber);
            }else {
                //填充过会次数
                if(!"".equals(browse_num)&&!"null".equals(browse_num)&&!"undefined".equals(browse_num)){
                    cellGhNum.setCellValue(browse_num);
                }else {
                    cellGhNum.setCellValue("");
                }
            }

            String formatDate = sdf.format(new Date());

            fileName = fileName+formatDate+".xlsx";
            response.setContentType("application/vnd.ms-excel;charset=utf-8");

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //公共方法
    public Map queryLand(String node_level,Map map){
        if(!"".equals(node_level) && null!=node_level && !"null".equals(node_level)){
            map.put("node_level_status",Integer.parseInt(node_level)-1);
        }
        Map nodeIdMap = takeLandDao.querytakeLandNdId(map);
        if(nodeIdMap==null){
            return null;
        }
        List<Map> listnoRo =  takeLandDao.queryByTopOne(nodeIdMap);
        for (int i = 0; i < listnoRo.size(); i++) {
            String takeLandId = listnoRo.get(i).get("plan_node_id")+"";
            listnoRo.get(i).put("takeLandId",takeLandId);
        }
        List<Map> listnoRoom = analyticalData(listnoRo);
        Map valueMap = queryTakeLands(nodeIdMap);
        valueMap.remove("roomlist");
        valueMap.remove("fileList");
        valueMap.put("roomlist",listnoRoom);
        return valueMap;
    }

    //公共方法解析货值结构和户型数据（页面展示）
    public List<Map> analyticalData(List<Map> list){
        List<Map> tollerMap = new ArrayList<>();
        if(list!=null){
            Set<Object> set =new HashSet<>();
            for(Map map1:list){
                set.add(map1.get("operation_type"));
            }
            for(Object o:set){
                Map resultvalue =new HashMap();
                resultvalue.put("operation_type",o);
                List listResult = new ArrayList();
                for(Map map2:list){
                    if(o.equals(map2.get("operation_type"))){
                        listResult.add(map2);
                    }
                }
                resultvalue.put("child",listResult);
                tollerMap.add(resultvalue);
            }
        }
        return tollerMap;
    }

    //公共方法解析获知结构和户型数据（用于数据入库）
    public List<Map> analyticalWarehous(List<Map> mapValues,String plan_node_id){
        List<Map> mapValuesResult = new ArrayList<>();
        for (int i = 0; i < mapValues.size(); i++) {

            String operation_type = mapValues.get(i).get("operation_type")+"";
            List<Map> listvalue  = (List<Map>) mapValues.get(i).get("child");
            for (int j = 0; j < listvalue.size()-1; j++) {
                String vs_case_info=listvalue.get(0).get("vs_case_info")+"";
                System.err.println(vs_case_info);
                Map value = (Map) listvalue.get(j).get("obj");
                String product_type = listvalue.get(j).get("product_type")+"";
                value.put("product_type",product_type);
                value.put("operation_type",operation_type);
                value.put("plan_node_id",plan_node_id);
                value.put("vs_case_info",vs_case_info);
                mapValuesResult.add(value);
            }
        }
        return mapValuesResult;
    }

    //公共方法--时间铺排 type代表从哪里触发的时间排布（1.拿地后，2顶设二）
    public void arrangeTime(Map mapTime,String username,String plan_id,String light_stuat,String type){
        try{
            mapTime.put("plan_id",plan_id);
            if("1".equals(type)){
                takeLandDao.updateTimeArrangementTakeLand(mapTime);
            }
            takeLandDao.updateTimeArrangement(mapTime);

        }catch (Exception e){
            System.out.println(e.getMessage());

        }
    }

    @Override
    //拿地后审批完成mq调用的方法
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody takeLandSuccess(Map map){
        VisolinkResultBody visolinkResultBody = new VisolinkResultBody();
        try{
            String eventType = map.get("eventType")+"";//状态
            String plan_node_id = map.get("businesskey")+"";//数据计划节点id
            String flowKey = map.get("flowKey")+"";//模板名称
            String instanceId = map.get("instanceId")+"";//流程id
            String taskId = map.get("taskId")+"";//任务id
            String orgName = map.get("orgName")+"";//节点名称
            //设置日期格式
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // new Date()为获取当前系统时间
            String nowTime = df.format(new Date());
            //全局需要的map
            Map mapstatus = new HashMap();
            mapstatus.put("plan_node_id",plan_node_id);
            mapstatus.put("update_time",nowTime);
            Map mapPlan = takeLandDao.queryTopOneOnly(mapstatus);
            //计划id
            String plan_id = mapPlan.get("plan_id")+"";
            //修改计划当前节点需要的map
            Map mapPlanUpdate = new HashMap();
            mapPlanUpdate.put("plan_id",plan_id);
            if("3".equals(eventType)){
                mapstatus.put("plan_approval",eventType);
                String browse_num=mapPlan.get("browse_num")+"";
                if(!"".equals(browse_num)&&!"null".equals(browse_num)){
                    mapstatus.put("browse_num",mapPlan.get("browse_num")+"");
                }
                takeLandDao.updateTakeLandStatus(mapstatus);
            }else if("4".equals(eventType)){
                //时间排布
                if("fp_land_back".equals(orgName)){
                    //修改计划相关数据
                    Map mapTime = takeLandDao.selectIndexTime(mapstatus);
                    //String plan_id = mapTime.get("plan_id")+"";
                    //修改计划表中的三个时间字段
                    takeLandDao.updatePlan(mapTime);
                    //调用时间排布
                    arrangeTime(mapTime,null,plan_id,null,"1");
                    //修改计划中的信息
                    mapPlanUpdate.put("this_node","2");
                    takeLandDao.updatePlan(mapPlanUpdate);
                    //删除顶设一多余数据（删除）
                    deleteTopOneMassage(mapPlanUpdate);
                }else {
                    //修改计划中的信息
                    mapPlanUpdate.put("this_node","3");
                    takeLandDao.updatePlan(mapPlanUpdate);
                    //调用顶设2接口
                    designTwoIndexService.backDesignTwo(plan_id);
                }
                //修改数据状态
                takeLandDao.updateNdostatus(mapstatus);
            }else if("5".equals(eventType) || "6".equals(eventType)){
                mapstatus.put("plan_approval",eventType);
                String browse_num=mapPlan.get("browse_num")+"";
                if(!"".equals(browse_num)&&!"null".equals(browse_num)){
                    mapstatus.put("browse_num",mapPlan.get("browse_num")+"");
                }
                takeLandDao.updateTakeLandStatus(mapstatus);
            }
            visolinkResultBody.setMessages("成功");
        }catch (Exception e){
            visolinkResultBody.setMessages("失败");
            map.put("results","执行失败");
            String messages = JSON.toJSONString(map);
            workflowDao.insertMessage(messages);
            logger.info("回滚事务");
            logger.error("添加失败：" + e.getMessage(), e);
        }

        return visolinkResultBody;
    }


    /**
     * 判断是否要删除顶设一数据
     * */
    public Map deleteTopOneMassage(Map map){
        Map resultMap = new HashMap();
        try{
            String plan_id = map.get("plan_id")+"";
            if("".equals(plan_id) || "null".equals(plan_id)){
                String messages = "未获取plan_id导致删除顶设一数据失败！";
                takeLandDao.insertBugLog(messages);
                return null;
            }
            map.put("is_effective",1);
            map.put("node_level",2);
            //通过计划id找到顶设一的节点id（正式数据不允许出现多条编辑中数据。这里来以防出现多条数据，以list接收。将数据同时处理掉）
            List<Map> listTopOne = takeLandDao.queryPlanNodeMessage(map);
            if(listTopOne.size()==1){
                String plan_node_id = listTopOne.get(0).get("id")+"";
                if(!"".equals(plan_node_id) && !"null".equals(plan_node_id)){
                    resultMap.put("plan_node_id",plan_node_id);
                    takeLandDao.deleteDesignoneValue(resultMap);
                    takeLandDao.deletedesignoneRoom(resultMap);
                    takeLandDao.deletedeSalesTarget(resultMap);
                }
            }

        }catch (Exception e){
            String messages = map+"";
            takeLandDao.insertBugLog(messages);
        }
        return null;
    }


}
