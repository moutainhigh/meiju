package cn.visolink.firstplan.openbeforeseven.service.impl;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignSevenDao;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.fpdesigntwo.service.DesignSevenSevice;
import cn.visolink.firstplan.fpdesigntwo.service.impl.toInteger;
import cn.visolink.firstplan.openbeforeseven.dao.OpenBeforeSevenDayDao;
import cn.visolink.firstplan.openbeforeseven.service.OpenBeforeSevenDayService;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.firstplan.openbeforetwentyone.service.OpenBeforeTwentyoneService;
import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sjl
 * @Created date 2020/3/9 12:26 下午
 */
@Service
@Transactional
public class OpenBeforeSevenDayServiceImpl implements OpenBeforeSevenDayService {
    @Autowired
    private OpenBeforeSevenDayDao openBeforeSevenDayDao;
    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;
    @Autowired
    private FileDao fileDao;
    @Value("${oaflow.fpFlowCode}")
    private String fpFlowCode;
    @Autowired
    private OpenBeforeTwentyoneService openBeforeTwentyoneService;
    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;
    @Autowired
    private TakeLandService takeLandService;
    @Autowired
    private toInteger toInteger;
    @Autowired
    private DesignTwoIndexDao designTwoIndexDao;
    @Autowired
    private DesignSevenDao designSevenDao;
    @Autowired
    DesignSevenSevice designSevenSevice;

    @Override
    public VisolinkResultBody viewOpenBeforeSevenDayOpenApplay(Map map) {
        Map<Object, Object> resultMap = new HashMap<>();
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();
        String create = map.get("create") + "";
        String plan_id = map.get("plan_id") + "";
        String plan_node_id = map.get("plan_node_id") + "";
        String project_id = map.get("project_id") + "";
        //查询是否有首开审批数据
        Map openApplyData = openBeforeSevenDayDao.selectOpenApplyData(map);
        if (openApplyData != null && !"new".equals(create)) {
            /**
             * 渲染数据
             */
            //查询当计划节点下的版本
            List<Map> versionData = openBeforeSevenDayDao.getVersionData(map);
            resultMap.put("versionData", versionData);
            //默认展示最新的版本数据
            Map versionMap = versionData.get(0);
            String versionId = versionMap.get("id") + "";


            //根据id查询数据
            Map openApplayMainData = openBeforeSevenDayDao.getOpenApplayMainData(versionId);
            //查询首开均价数据
            List<Map> firstOpenAvgData = openBeforeSevenDayDao.getFirstOpenAvgData(versionId);
            resultMap.put("firstOpenAvgData", firstOpenAvgData);
            //查询竞品情况数据
            List<Map> competingpPoducts = openBeforeSevenDayDao.getCompetingpPoducts(versionId);
            resultMap.put("competingpPoducts", competingpPoducts);
            map.put("id", versionId);
            //查询节点客储节点数据
            List<Map> customerStorageNodeData = openBeforeSevenDayDao.getCustomerStorageNodeData(map);
            resultMap.put("customerStorageNode", customerStorageNodeData);
            //查询周拆分
            List<Map> weekData = openBeforeSevenDayDao.getWeekData(map);

            resultMap.put("weekStroageData", weekData);
            String approval_stuat=openApplayMainData.get("approval_stuat")+"";
            if ("2".equals(approval_stuat)){
                updateData(openApplayMainData,map,resultMap);
            }
            resultMap.put("openApplayMainData", openApplayMainData);
            //获取附件信息
            List fileLists = fileDao.getFileLists(versionId);
            resultMap.put("fileList", fileLists);
        } else {
            /**
             * 初始化基础数据
             */
            //查询顶设2核心指标数据
            Map designtwoCoreData = openBeforeSevenDayDao.getDesigntwoCoreData(plan_id);
            //查询有没有当前版本已经审批通过的最新版本数据
            Map applayadopt = openBeforeSevenDayDao.getApplayadopt(plan_node_id);
            if (designtwoCoreData == null) {
                designtwoCoreData = new HashMap();
            }
            //查询顶设2首开均价
            List<Map> designtwoAvgPrice = openBeforeSevenDayDao.getDesigntwoAvgPrice(plan_id);
            String str1 = "";
            String str2 = "";
            String str3 = "";
            if (designtwoAvgPrice != null && designtwoAvgPrice.size() > 0) {
                for (Map avgMap : designtwoAvgPrice) {
                    String invest_avg = avgMap.get("invest_avg") + "";
                    String rules_avg = avgMap.get("rules_avg") + "";
                    String designtwo_avg = avgMap.get("designtwo_avg") + "";
                    if (!"null".equals(invest_avg) && !"0.00".equals(invest_avg)) {
                        str1 += avgMap.get("product_type") + "、";
                    }
                    if (!"null".equals(rules_avg) && !"0.00".equals(rules_avg)) {
                        str2 += avgMap.get("product_type") + "、";
                    }
                    if (!"null".equals(designtwo_avg) && !"0.00".equals(designtwo_avg)) {
                        str3 += avgMap.get("product_type") + "、";
                    }
                }
            }
            if (!"".equals(str1)) {
                str1 = str1.substring(0, str1.length() - 1);
            }
            if (!"".equals(str2)) {
                str2 = str2.substring(0, str2.length() - 1);
            }
            if (!"".equals(str3)) {
                str3 = str3.substring(0, str3.length() - 1);
            }
            designtwoCoreData.put("estimate_product_type", str3);
            designtwoCoreData.put("estimate_operation_name", str3);
            designtwoCoreData.put("invest_product_type", str1);
            designtwoCoreData.put("rules_product_type", str2);
            designtwoCoreData.put("designtwo_product_type", str3);
            designtwoCoreData.put("customer_cause", "");

            //
           if(applayadopt!=null){
               applayadopt.put("applay_status",2);
               //将上一版本最新数据返回
                designtwoCoreData.putAll(applayadopt);
            }
           //数据更新
            updateData(designtwoCoreData,map,resultMap);
            designtwoCoreData.remove("id");
           //查询有没有进行过延期开盘

            String delayOpenData = openBeforeSevenDayDao.getDelayOpenData(plan_id);
            if(delayOpenData!=null&&!"".equals(delayOpenData)){
                designtwoCoreData.put("estimate_open_node",delayOpenData);
            }
            //designtwoCoreData.putAll(applayadopt);
            resultMap.put("openApplayMainData", designtwoCoreData);
            resultMap.put("firstOpenAvgData", designtwoAvgPrice);

            //查询竞品情况数据
          List<Map> competingpPoducts = openBeforeSevenDayDao.getApplayadotCompetingpPoducts(plan_node_id);
            if(competingpPoducts!=null&&competingpPoducts.size()>0){
                resultMap.put("competingpPoducts", competingpPoducts);
            }
        }

        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("plan_id",plan_id);
        paramMap.put("node_level",10);
        //获取三大件时间
        Map salesMap = designSevenDao.getThreepiecesForSalesTimes(paramMap);
        if(salesMap!=null&&salesMap.size()>0){
            paramMap.putAll(salesMap);{
                paramMap.putAll(salesMap);
            }
        }
        paramMap.put("node_level",11);
        Map sampleMap = designSevenDao.getThreepiecesForSampleTimes(paramMap);
        if(sampleMap!=null&&sampleMap.size()>0){
            paramMap.putAll(sampleMap);
        }
        resultMap.put("threepiecesTimes",paramMap);
        resultBody.setResult(resultMap);
        resultBody.setCode(200);
        return resultBody;
    }

    /**
     *
     * @param designtwoCoreData
     * @param map
     * @param resultMap
     */
    public void updateData(Map designtwoCoreData,Map map,Map resultMap){
        String plan_id=map.get("plan_id")+"";
        String plan_node_id=map.get("plan_node_id")+"";
        String project_id=map.get("project_id")+"";
        //投资版数据集合
        List<Map> customerStorageNode = null;
        try {
            List<Map> project_idList = openbeforetwentyoneDao.getSubsidiaryProject(map.get("project_id") + "");
            designtwoCoreData.put("projectidList", project_idList);
            //查询最新的客户情况数据
            Map xukeactCustomer = openBeforeSevenDayDao.getXukeactCustomer(plan_id);
            Map customerInfoForDesigntwo = openBeforeSevenDayDao.getCustomerInfoForDesigntwo(plan_id);
            if (xukeactCustomer != null) {
                //计算达成率
                //来访目标
                int come_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("come_customer_target") + "");
                //来访达成
                int come_customer_actual = Integer.parseInt(xukeactCustomer.get("come_customer_actual") + "");
                //小卡目标
                int lesser_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("lesser_customer_target") + "");
                //小卡实际
                int lesser_customer_actual = Integer.parseInt(xukeactCustomer.get("lesser_customer_actual") + "");
                //小卡目标
                int big_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("big_customer_target") + "");
                //大卡实际
                int big_customer_actual = Integer.parseInt(xukeactCustomer.get("big_customer_actual") + "");
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                df.setMinimumFractionDigits(2);
                df.setGroupingUsed(false);
                if (0 != come_customer_target) {
                    designtwoCoreData.put("come_customer_per", df.format(come_customer_actual * 100 / come_customer_target));
                } else {
                    designtwoCoreData.put("come_customer_per", 0);
                }
                if (0 != lesser_customer_target) {
                    designtwoCoreData.put("lesser_customer_per", df.format(lesser_customer_actual * 100 / lesser_customer_target));
                } else {
                    designtwoCoreData.put("lesser_customer_per", 0);
                }
                if (0 != big_customer_target) {
                    designtwoCoreData.put("big_customer_per", df.format(big_customer_actual * 100 / big_customer_target));
                } else {
                    designtwoCoreData.put("big_customer_per", 0);
                }
            } else {
                designtwoCoreData.put("big_customer_per", 0);
                designtwoCoreData.put("lesser_customer_per", 0);
                designtwoCoreData.put("come_customer_per", 0);
            }
            List<Map> customerStorageNodeold = (List<Map>) resultMap.get("customerStorageNode");
            if(customerStorageNodeold==null||customerStorageNodeold.size()<=0){
                //获取客储节点数据获取最新节点的数据
                customerStorageNode = openBeforeSevenDayDao.getCustomerStorageNodeSeven(plan_id, "7");
                if (customerStorageNode == null || customerStorageNode.size() < 1) {
                    //查询首开前21天的客储数据
                    customerStorageNode = openBeforeSevenDayDao.getCustomerStorageNode(plan_id);
                }
                /*走到这一步说明直接略到开盘前7填*/
                Map mapForDesignTwo = new HashMap();
                if (customerStorageNode == null || customerStorageNode.size() < 1) {
                    map.put("node_level",6);
                    mapForDesignTwo = designSevenSevice.selectStorageNodePlan(map);
                    customerStorageNode = (ArrayList) mapForDesignTwo.get("NodePlan");
                    for (Map map1 : customerStorageNode) {
                        map1.put("node_name", map1.get("nide_name"));
                    }
                }
                map.put("node_level",7);
                if (customerStorageNode != null) {
                    for (Map map1 : customerStorageNode) {
                        String level = map1.get("level") + "";
                        String node_level = map1.get("node_level") + "";
                        //如果当前节点为首开 并且为实际数据//取旭客数据
                        if ("2".equals(level) && "8".equals(node_level)) {
                            map1.put("plan_id", plan_id);
                            Map xukeCostomerStorageNode = openBeforeSevenDayDao.getXukeCostomerStorageNode(map1);
                            if (xukeCostomerStorageNode != null) {
                                xukeCostomerStorageNode.put("line_name", "实际");
                                xukeCostomerStorageNode.put("level", "2");
                                xukeCostomerStorageNode.put("node_level", "8");
                                //计算偏差率
                                map1.putAll(xukeCostomerStorageNode);
                            }
                        }
                    }

                }
                resultMap.put("customerStorageNode", customerStorageNode);

                List<Map> weekStroageDataold = (List<Map>) resultMap.get("weekStroageData");

                if(weekStroageDataold== null||weekStroageDataold.size()<1){
                    //查询开盘前7天的周拆分数据
                    List<Map> weekStroageData = openBeforeSevenDayDao.getWeekStroageDataSeven(plan_id, "7");
                    //如果开盘前7天数据没有查到
                    if (weekStroageData == null || weekStroageData.size() < 1) {
                        //查询开盘前21天的周拆分数据
                        weekStroageData = openBeforeSevenDayDao.getWeekStroageData(plan_id);
                        //如果还没有查询到周拆分数据
                        /*走到这一步说明省略到了开盘前7填*/
                        if (weekStroageData == null || weekStroageData.size() < 1) {
                            weekStroageData = (ArrayList) mapForDesignTwo.get("Week");
                            List<Map> forCustomerInfo = (ArrayList) mapForDesignTwo.get("NodePlan");
                            if(forCustomerInfo!=null){
                                for (Map map1 : forCustomerInfo) {
                                    String node_name=map1.get("nide_name")+"";
                                    String level = map1.get("level") + "";
                                    if ("首开".equals(node_name) && ("1").equals(level)) {
                                        customerInfoForDesigntwo.put("come_customer_target", map1.get("visit_num"));
                                        customerInfoForDesigntwo.put("lesser_customer_target", map1.get("little_num"));
                                        customerInfoForDesigntwo.put("big_customer_target", map1.get("big_num"));
                                    }
                                }
                            }

                        }
                    }
                    //designtwoCoreData.remove("id");
                    //查询开盘前7天的计划完成时间
                    String planEndTime = openBeforeSevenDayDao.getPlanEndTime(plan_node_id);
                    if (planEndTime != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        long time = sdf.parse(planEndTime).getTime();
                        for (Map weekStroageDatum : weekStroageData) {
                            String week = weekStroageDatum.get("week") + "";
                            if (!"完整波段".equals(week)) {
                                String end_time = weekStroageDatum.get("end_time") + "";
                                long endtime2 = sdf.parse(end_time).getTime();
                                if (endtime2 > time) {
                                    weekStroageDatum.put("is_edit", 1);
                                }
                            }
                        }
                    }
                }
            }



            if (xukeactCustomer != null) {
                designtwoCoreData.putAll(xukeactCustomer);
            }
            if (customerInfoForDesigntwo != null) {
                designtwoCoreData.putAll(customerInfoForDesigntwo);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public VisolinkResultBody saveOpenBeforeSevenDayOpenApplay(Map map) {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        String button = map.get("button") + "";
        String plan_id = map.get("plan_id") + "";
        String plan_node_id = map.get("plan_node_id") + "";
        String project_id = map.get("project_id") + "";
        String flowDataId = null;
        String approvalStuat = "2";
        String str = "提交成功";
        Map filterMap=null;
        String username = map.get("username") + "";
        Map openApplyData = (Map) map.get("openApplayMainData");
        String version = openApplyData.get("version") + "";
        String id = openApplyData.get("id") + "";
        String dataid = UUID.randomUUID().toString();
        boolean flag = false;
        //快速审批
        String buttonKs = map.get("buttonKs") + "";
        if (!"".equals(buttonKs) && !"null".equals(buttonKs)) {
            flag = true;
        }


        if ("save".equals(button)) {
            str = "保存成功";
        }
        ;
        if ("submit".equals(button) || flag) {
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("id", map.get("plan_id"));
            paramMap.put("node_level", 7);
            paramMap.put("estimate_open_node", openApplyData.get("estimate_open_node"));
            JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
            String jobName = null;
            if (jwtUser != null) {
                jobName = jwtUser.getJob().get("JobName") + "";
            }
            Map flowParams = new HashMap();
            if (!"".equals(id) && !"null".equals(id)) {
                flowDataId = id;
                flowParams.put("json_id", id);
            } else {
                flowParams.put("json_id", dataid);
                flowDataId = dataid;
            }
            flowParams.put("project_id", project_id);
            flowParams.put("creator", username);
            flowParams.put("flow_code", fpFlowCode);
            flowParams.put("TITLE", "首开前7天开盘申请");
            flowParams.put("post_name", jobName);
            flowParams.put("orgName", "fp_open_seven_approve");
            Map comcommon = new HashMap();
            /*flowParams.put("comcommon", comcommon+"");*/
            takeLandService.insertFlow(flowParams);
        }
        if (!"".equals(id) && !"null".equals(id)) {
            openApplyData.put("approval_stuat", approvalStuat);
            filterMap= filterMap(openApplyData);
            //修改数据
            openBeforeSevenDayDao.updateOpenApplayMainData(filterMap);
            //修改竞品数据
            //清空竞品数据
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("id", id);
            paramMap.put("plan_id", plan_id);
            paramMap.put("plan_node_id", plan_node_id);
            paramMap.put("version", version);
            //清楚附属数据
            openBeforeSevenDayDao.clearSunData(paramMap);
            List<Map> competingpPoducts = (List<Map>) map.get("competingpPoducts");
            if (competingpPoducts != null && competingpPoducts.size() > 0) {
                //添加竞品情况数据
                for (Map competingpPoduct : competingpPoducts) {
                    competingpPoduct.put("id", UUID.randomUUID().toString());
                    competingpPoduct.put("delay_id", id);
                    competingpPoduct.put("plan_id", plan_id);
                    competingpPoduct.put("plan_node_id", plan_node_id);
                    openBeforeSevenDayDao.insertCompetingpPoducts(competingpPoduct);
                }
            }
            List<Map> firstOpenAvgData = (List<Map>) map.get("firstOpenAvgData");
            if (firstOpenAvgData != null && firstOpenAvgData.size() > 0) {
                for (Map firstOpenAvgDatum : firstOpenAvgData) {
                    firstOpenAvgDatum.put("id", UUID.randomUUID().toString());
                    firstOpenAvgDatum.put("day_id", id);
                    firstOpenAvgDatum.put("plan_id", plan_id);
                    firstOpenAvgDatum.put("plan_node_id", plan_node_id);
                    filterMap=filterMap(firstOpenAvgDatum);
                    openBeforeSevenDayDao.insertPriceAvg(filterMap);
                }
            }
            Map<Object, Object> storageNodeMap = new HashMap<>();
            String ids = UUID.randomUUID().toString();

            storageNodeMap.put("change", 0);
            storageNodeMap.put("approval_stuat", approvalStuat);
            storageNodeMap.put("node_level", 7);
            storageNodeMap.put("plan_node_id", id);
            storageNodeMap.put("plan_id", plan_id);
            storageNodeMap.put("id", ids);
            storageNodeMap.put("version", version);
            openBeforeSevenDayDao.insertCustomerStoreFlow(storageNodeMap);
            //获取节点客储计划数据
            List<Map> customerStorageNode = (List<Map>) map.get("customerStorageNode");
            if (customerStorageNode != null && customerStorageNode.size() > 0) {
                //添加节点客储计划数据
                for (Map nodeMap : customerStorageNode) {
                    nodeMap.put("id", UUID.randomUUID().toString());
                    nodeMap.put("plan_id", plan_id);
                    nodeMap.put("plan_node_id", plan_node_id);
                    nodeMap.put("version", version);
                    nodeMap.put("flow_id", ids);
                    openBeforeSevenDayDao.insertCustomerStore(nodeMap);
                }
            }
            List<Map> weekData = (List<Map>) map.get("weekStroageData");
            if (weekData != null && weekData.size() > 0) {
                for (Map weekDatum : weekData) {
                    weekDatum.put("id", UUID.randomUUID().toString());
                    weekDatum.put("flow_id", ids);
                    weekDatum.put("version", version);
                    weekDatum.put("node_level", "7");
                    openBeforeSevenDayDao.insertWeekData(weekDatum);
                }
            }
            //获取附件数据
            List<Map> fileList = (List<Map>) map.get("fileList");
            if (fileList != null && fileList.size() > 0) {
                //删除附件
                fileDao.delFileByBizId(id);
                for (Map fileMap : fileList) {
                    fileMap.put("bizID", id);
                    fileDao.updateFileBizID(fileMap);
                }
                //添加新附件
            }
        } else {
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("plan_id", map.get("plan_id"));
            paramMap.put("plan_node_id", map.get("plan_node_id"));
            Map versionMap = openBeforeSevenDayDao.createVserion(paramMap);
            Integer vserion;
            String create = map.get("thisVserion") + "";
            if (versionMap != null) {
                vserion = Integer.parseInt(versionMap.get("version") + "");
            } else {
                vserion = 0;
            }
            if ("".equals(create) || "null".equals(create)) {
                openApplyData.put("version", vserion + 1);
                vserion = vserion + 1;
            }
            //添加数据

            openApplyData.put("id", dataid);
            openApplyData.put("approval_stuat", approvalStuat);
            openApplyData.put("plan_id", plan_id);
            openApplyData.put("plan_node_id", plan_node_id);
            filterMap=filterMap(openApplyData);
            openBeforeSevenDayDao.insertOpenApplayMainData(filterMap);
            //获取竞品情况数据
            List<Map> competingpPoducts = (List<Map>) map.get("competingpPoducts");
            if (competingpPoducts != null && competingpPoducts.size() > 0) {
                //添加竞品情况数据
                for (Map competingpPoduct : competingpPoducts) {
                    competingpPoduct.put("id", UUID.randomUUID().toString());
                    competingpPoduct.put("delay_id", dataid);
                    competingpPoduct.put("plan_id", plan_id);
                    competingpPoduct.put("plan_node_id", plan_node_id);
                    openBeforeSevenDayDao.insertCompetingpPoducts(competingpPoduct);
                }
            }
            //添加均价数据
            List<Map> firstOpenAvgData = (List<Map>) map.get("firstOpenAvgData");
            for (Map firstOpenAvgDatum : firstOpenAvgData) {
                firstOpenAvgDatum.put("id", UUID.randomUUID().toString());
                firstOpenAvgDatum.put("day_id", dataid);
                firstOpenAvgDatum.put("plan_id", plan_id);
                firstOpenAvgDatum.put("plan_node_id", plan_node_id);
                filterMap=filterMap(firstOpenAvgDatum);
                openBeforeSevenDayDao.insertPriceAvg(filterMap);
            }
            String ids = UUID.randomUUID().toString();

            //添加客储审批流程数据
            Map<Object, Object> storageNodeMap = new HashMap<>();
            storageNodeMap.put("change", 0);
            storageNodeMap.put("approval_stuat", approvalStuat);
            storageNodeMap.put("node_level", 7);
            storageNodeMap.put("plan_node_id", dataid);
            storageNodeMap.put("plan_id", plan_id);
            storageNodeMap.put("id", ids);
            storageNodeMap.put("version", vserion);
            openBeforeSevenDayDao.insertCustomerStoreFlow(storageNodeMap);
            //获取节点客储计划数据
            List<Map> customerStorageNode = (List<Map>) map.get("customerStorageNode");
            if (customerStorageNode != null && customerStorageNode.size() > 0) {
                //添加节点客储计划数据
                for (Map nodeMap : customerStorageNode) {
                    nodeMap.put("plan_id", plan_id);
                    nodeMap.put("id", UUID.randomUUID().toString());
                    nodeMap.put("plan_node_id", plan_node_id);
                    nodeMap.put("version", vserion);
                    nodeMap.put("flow_id", ids);
                    openBeforeSevenDayDao.insertCustomerStore(nodeMap);
                }
            }
            //获取周拆分数据
            List<Map> weekData = (List<Map>) map.get("weekStroageData");
            if (weekData != null && weekData.size() > 0) {
                for (Map weekDatum : weekData) {
                    weekDatum.put("id", UUID.randomUUID().toString());
                    weekDatum.put("flow_id", ids);
                    weekDatum.put("version", vserion);
                    openBeforeSevenDayDao.insertWeekData(weekDatum);
                }
            }
            //获取附件数据
            List<Map> fileList = (List<Map>) map.get("fileList");
            if (fileList != null && fileList.size() > 0) {
                //删除附件
                fileDao.delFileByBizId(dataid);
                for (Map fileMap : fileList) {
                    fileMap.put("bizID", dataid);
                    fileDao.updateFileBizID(fileMap);
                }
                //添加新附件
            }
        }

        //发起流程所需参数
        Map<Object, Object> resultMapFlow = new HashMap<>();
        resultMapFlow.put("BSID", "FP");
        resultMapFlow.put("BTID", "skcslc");
        resultMapFlow.put("codeBOID", flowDataId);
        resultMapFlow.put("bkUserID", username);
        resultMapFlow.put("loginKey", "");
        response.setMessages(str);
        response.setResult(resultMapFlow);
        response.setCode(200);
        return response;
    }

    @Override
    public VisolinkResultBody switchVersion(Map map) {

        Map<Object, Object> resultMap = new HashMap<>();
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();
        String versionId = map.get("id") + "";

       String operation= map.get("operation")+"";
        try {
            //查询审批数据
            Map applayDatas = openBeforeSevenDayDao.getApplayDatas(versionId);
            resultMap.put("getApplayDatas", applayDatas);
            //根据id查询数据
            Map openApplayMainData = openBeforeSevenDayDao.getOpenApplayMainData(versionId);

            resultMap.put("openApplayMainData", openApplayMainData);
            //查询首开均价数据
            List<Map> firstOpenAvgData = openBeforeSevenDayDao.getFirstOpenAvgData(versionId);
            resultMap.put("firstOpenAvgData", firstOpenAvgData);
            //查询竞品情况数据
            List<Map> arrayList = new ArrayList<>();
            List<Map> competingpPoducts = openBeforeSevenDayDao.getCompetingpPoducts(versionId);
            if (competingpPoducts == null) {
                competingpPoducts = arrayList;
            }
            resultMap.put("competingpPoducts", competingpPoducts);
            map.put("version", openApplayMainData.get("version") + "");
            //查询节点客储节点数据
            List<Map> customerStorageNodeData = openBeforeSevenDayDao.getCustomerStorageNodeData(map);
            resultMap.put("customerStorageNode", customerStorageNodeData);

            //查询周拆分
            List<Map> weekData = openBeforeSevenDayDao.getWeekData(map);
            resultMap.put("weekStroageData", weekData);
            //如果是预览页面，不更新数据
            if(!"view".equals(operation)){
                if(openApplayMainData!=null){
                    String approval_stuat=openApplayMainData.get("approval_stuat")+"";
                    if("2".equals(approval_stuat)){
                        updateData(openApplayMainData,map,resultMap);
                    }
                }
            }
            //获取附件信息
            List fileLists = fileDao.getFileLists(versionId);
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
            resultMap.put("threepiecesTimes",paramMap);
            resultMap.put("fileList", fileLists);
        } catch (Exception e) {
            e.printStackTrace();
        }

        resultBody.setResult(resultMap);
        return resultBody;
    }

    @Override
    public ResultBody exportOpenApplayData(Map map, HttpServletRequest request, HttpServletResponse response) {
        String filePath;
        //配置本地模版路径
        String realpath = null;

    /*   realpath="/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/openBeforeSevenday.xlsx";
        filePath=realpath;*/
        //配置服务器模版路径
        realpath = request.getServletContext().getRealPath("/");
        String templatePath = File.separator + "TemplateExcel" + File.separator + "openBeforeSevenday.xlsx";
        filePath = realpath + templatePath;
        FileInputStream fileInputStream = null;
        String levelNode="首开前7天";
        String tabNode="首开申请-填报导出数据";

        String id=map.get("id")+"";
        String projectName=map.get("projectName")+"";
        String node_level=map.get("node_level")+"";
        try {
            File templateFile = new File(filePath);
            if (!templateFile.exists()) {
                throw new BadRequestException(1004, "未读取到配置的导出模版，请先配置导出模版!");
            }
            //使用poi读取模版文件
            fileInputStream = new FileInputStream(templateFile);
            if (fileInputStream == null) {
                throw new BadRequestException(1004, "未读取到模版文件!");
            }
            //创建工作簿对象，
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            //获取首开审批所需要的模版sheet页
            XSSFSheet sheetAt = workbook.getSheetAt(0);
            XSSFRow row = sheetAt.getRow(0);
            //获取首开指标样式
            row.getCell(0).setCellValue(projectName + "-" + levelNode + "-" + tabNode);
            XSSFRow row2 = sheetAt.getRow(2);
            XSSFCell row2Cell = row2.getCell(0);
            XSSFCellStyle cellStyleFirstOpen = row2Cell.getCellStyle();
            //查询主数据
            Map openApplayMainData = openBeforeSevenDayDao.getOpenApplayMainData(id);
            //首开指标-首开节点 字段定义
            String[] openNodeArray = {"invest_open_node", "rules_open_node", "designtwo_open_node", "estimate_open_node", "deviation_open_node", "deviationper_open_node", "cause_open_node"};
            //首开指标-产品类型 字段定义
            String[] productTypeArray = {"invest_product_type", "rules_product_type", "designtwo_product_type", "estimate_product_type", "deviation_product_type", "deviationper_product_type", "cause_product_type"};
            //首开指标-首开取证货值（万元） 字段定义
            String[] takeCardArray = {"invest_take_card_value", "rules_take_card_value", "designtwo_take_card_value", "estimate_take_card_value", "deviation_take_card_value", "deviationper_take_card_value", "cause_take_card_value"};
            //首开指标-首开推售货值（万元） 字段定义
            String[] pushValueArray = {"invest_push_value", "rules_push_value", "designtwo_push_value", "estimate_push_value", "deviation_push_value", "deviationper_push_value", "cause_push_value"};
            //首开指标-首开去化货值（万元） 字段定义
            String[] sellingValueArray = {"invest_selling_value", "rules_selling_value", "designtwo_selling_value", "estimate_selling_value", "deviation_selling_value", "deviationper_selling_value", "cause_selling_value"};

            if (openApplayMainData != null) {
                //填充首开均价以上数据
                for (int i = 4; i < 9; i++) {
                    XSSFRow atRow = sheetAt.getRow(i);
                    for (int j = 2; j < 9; j++) {
                        XSSFCell cell = atRow.getCell(j);
                        if (i == 4) {
                            String value = openApplayMainData.get(openNodeArray[j-2]) + "";
                            if (!"".equals(value) && !"null".equals(value)) {
                                cell.setCellValue(value);
                            }
                        } else if (i == 5) {
                            String value = openApplayMainData.get(productTypeArray[j-2]) + "";
                            if (!"".equals(value) && !"null".equals(value)) {
                                cell.setCellValue(value);
                            }
                        } else if (i == 6) {
                            String value = openApplayMainData.get(takeCardArray[j-2]) + "";
                            if (!"".equals(value) && !"null".equals(value)) {
                                cell.setCellValue(value);
                            }
                        } else if (i == 7) {
                            String value = openApplayMainData.get(pushValueArray[j-2]) + "";
                            if (!"".equals(value) && !"null".equals(value)) {
                                cell.setCellValue(value);
                            }
                        } else if (i == 8) {
                            String value = openApplayMainData.get(sellingValueArray[j-2]) + "";
                            if (!"".equals(value) && !"null".equals(value)) {
                                cell.setCellValue(value);
                            }
                        }
                    }
                }
                int rownum = 9;
                //填充首开均价数据
                //定义首开均价字段数组
                String[] avgPriceArray = {"product_type", "invest_avg", "rules_avg", "designtwo_avg", "open_estimate", "bias_price", "bias_per", "bias_cause"};
                XSSFRow row9 = sheetAt.getRow(4);
                XSSFCell cell9_0 = row9.getCell(2);
                XSSFCell cell2_0 = row9.getCell(0);
                XSSFCellStyle cell2_0_cellStyle = cell2_0.getCellStyle();
                XSSFCellStyle cell9_0_cellStyle = cell9_0.getCellStyle();
                //查询首开均价数据
               List<Map> firstOpenAvgData = openBeforeSevenDayDao.getFirstOpenAvgData(id);

                if(firstOpenAvgData!=null&&firstOpenAvgData.size()>0) {
                    rownum += firstOpenAvgData.size();
                    sheetAt.shiftRows(9,sheetAt.getLastRowNum(),firstOpenAvgData.size() ,true,false);
                    for (int i = 9; i <rownum; i++) {
                        XSSFRow row1 = sheetAt.createRow(i);
                            if(i==9){
                                XSSFCell cell1 = row1.createCell(0);
                                cell1.setCellValue("首开均价");
                                cell1.setCellStyle(cell9_0_cellStyle);
                            }
                            for (int j = 1; j < 9; j++) {
                                XSSFCell cell = row1.createCell(j);
                                    Map openAvgMap = firstOpenAvgData.get(i - 9);
                                    String value = openAvgMap.get(avgPriceArray[j - 1]) + "";
                                    if (!"".equals(value) && !"null".equals(value)) {
                                        cell.setCellValue(value);
                                        cell.setCellStyle(cell9_0_cellStyle);
                                    }
                            }
                    }
                    if(firstOpenAvgData.size()>1){
                        CellRangeAddress region = new CellRangeAddress(9, rownum-1, 0, 0);
                        sheetAt.addMergedRegion(region);
                    }

                }
                //不同业态价差数据填充
                String[] operationValueArray = {"invest_operation_name", "rules_operation_name", "designtwo_operation_name", "estimate_operation_name"};
                String[] operationbfbArray = {"invest_operation_bfb", "rules_operation_bfb", "designtwo_operation_bfb", "estimate_operation_bfb"};
                for (int i=rownum;i<rownum+2;i++){
                    XSSFRow row1 = sheetAt.getRow(i);
                    for (int j=2;j<6;j++){
                        XSSFCell cell = row1.getCell(j);
                        cell.setCellStyle(cell9_0_cellStyle);
                        if(i==rownum){
                            cell.setCellValue(openApplayMainData.get(operationValueArray[j-2])+"");
                        }
                        if(i==rownum+1){
                            cell.setCellValue(openApplayMainData.get(operationbfbArray[j-2])+"");
                        }

                    }
                }
                rownum=rownum+2;
                //取证数据
                String[] takePerArray={"invest_take_card_per","rules_take_card_per","designtwo_take_card_per","estimate_take_card_per","deviation_take_card_per","deviationper_take_card_per","cause_take_card_per"};
                //档期利润率
                String[] thisPerArray={"invest_this_per","rules_this_per","designtwo_this_per","estimate_this_per","deviation_this_per","deviationper_this_per","cause_this_per"};
                //整盘利润率
                String[] allArray={"invest_all_per","rules_all_per","designtwo_all_per","estimate_all_per","deviation_all_per","deviationper_all_per","cause_all_per"};
                //非融irr
                String[] irrArray={"invest_irr","rules_irr","designtwo_irr","estimate_irr","deviation_irr","deviationper_irr","cause_irr"};
                //静态回收
                String[] staticArray={"invest_payback","rules_payback","designtwo_payback","estimate_payback","deviation_payback","deviationper_payback","cause_payback"};

                for(int i=rownum;i<rownum+5;i++){
                    XSSFRow row1 = sheetAt.getRow(i);
                    for (int j=2;j<9;j++){
                            XSSFCell cell = row1.getCell(j);
                            if(i==rownum){
                                String value=openApplayMainData.get(takePerArray[j-2])+"";
                                if(!"".equals(value)&&!"null".equals(value)){
                                    cell.setCellValue(value);
                                }
                            }else if(i==rownum+1){
                                String value=openApplayMainData.get(thisPerArray[j-2])+"";
                                if(!"".equals(value)&&!"null".equals(value)){
                                    cell.setCellValue(value);
                                }
                            }else if(i==rownum+2){
                                String value=openApplayMainData.get(allArray[j-2])+"";
                                if(!"".equals(value)&&!"null".equals(value)){
                                    cell.setCellValue(value);
                                }
                            }else if(i==rownum+3){
                                String value=openApplayMainData.get(irrArray[j-2])+"";
                                if(!"".equals(value)&&!"null".equals(value)){
                                    cell.setCellValue(value);
                                }
                            }else if(i==rownum+4){
                                String value=openApplayMainData.get(staticArray[j-2])+"";
                                if(!"".equals(value)&&!"null".equals(value)){
                                    cell.setCellValue(value);
                                }
                            }
                    }
                    if(firstOpenAvgData!=null&&firstOpenAvgData.size()>0){
                        if(firstOpenAvgData.size()==1||firstOpenAvgData.size()==2){
                            CellRangeAddress region = new CellRangeAddress(i, i, 0, 1);
                            sheetAt.addMergedRegion(region);
                        }else if(firstOpenAvgData.size()==3){
                            if(i!=rownum){
                                CellRangeAddress region = new CellRangeAddress(i, i, 0, 1);
                                sheetAt.addMergedRegion(region);
                            }
                        }else if(firstOpenAvgData.size()==4){
                            if(i!=rownum&&i!=rownum+1){
                                CellRangeAddress region = new CellRangeAddress(i, i, 0, 1);
                                sheetAt.addMergedRegion(region);
                            }
                        }
                    }

                }
                System.err.println(rownum+4);

                int count=rownum+8;
                //竞品情况数据
                String[] competingArry={"product_project","first_opentime","take_money","month_avg_flow","avg_price_product"};
                //填充竞品情况
                List<Map> competingpPoducts = openBeforeSevenDayDao.getCompetingpPoducts(id);
                if(competingpPoducts!=null&&competingpPoducts.size()>0){
                    sheetAt.shiftRows(count,sheetAt.getLastRowNum(),competingpPoducts.size() ,true,false);
                    for (int i=count;i<count+competingpPoducts.size();i++){
                        XSSFRow row1 = sheetAt.createRow(i);
                        Map competingMap = competingpPoducts.get(i - count);
                        for (int j=0;j<5;j++){
                            String value=competingMap.get(competingArry[j])+"";
                            if(!"".equals(value)&&!"null".equals(value)){
                                XSSFCell cell = row1.createCell(j);
                                cell.setCellValue(value);
                                cell.setCellStyle(cell9_0_cellStyle);
                            }
                        }
                    }
                    count=count+competingpPoducts.size();
                }
                //填充客户情况数据
                String[] come_customerArray={"come_customer_target","come_customer_actual","come_customer_per"};
                String[] lesser_customerArray={"lesser_customer_target","lesser_customer_actual","lesser_customer_per"};
                String[] big_customerArray={"big_customer_target","big_customer_actual","big_customer_per"};
                count=count+3;
                for(int i=count;i<count+3;i++){
                    String value="";
                    XSSFRow rows = sheetAt.getRow(i);
                    for (int j=1;j<5;j++){
                        XSSFCell cell = rows.getCell(j);
                        if(i==count&&j==4){
                            cell.setCellValue(openApplayMainData.get("customer_cause")+"");
                        }else if(j!=4){
                            if(i==count){
                                value =openApplayMainData.get(come_customerArray[j-1])+"";
                            }else if(i==count+1){
                                value=openApplayMainData.get(lesser_customerArray[j-1])+"";
                            }else if(i==count+2){
                                value=openApplayMainData.get(big_customerArray[j-1])+"";
                            }
                            if(!"".equals(value)&&!"null".equals(value)){
                                cell.setCellValue(value);
                            }
                        }
                    }
                }
                CellRangeAddress region = new CellRangeAddress(count, count+2, 4, 4);
                sheetAt.addMergedRegion(region);
                //到开盘当前预估数据第一行
                count=count+5;
                String[] nowday={"this_customer_visit","this_customer_conversionper"};
                String[] subdata={"sub_customer_actual","sub_customer_per"};


                //设置开盘当前预估数据样式
                sheetAt.getRow(count-1).getCell(0).setCellStyle(cellStyleFirstOpen);
                //填充开盘当前预估数据
                for (int i=count;i<count+2;i++){
                    XSSFRow row1 = sheetAt.getRow(i);
                    for (int j=1;j<4;j++){
                            XSSFCell cell = row1.getCell(j);
                            if(i==count&&(j==1||j==3)){
                                if(j==1){
                                    cell.setCellValue(openApplayMainData.get(nowday[0])+"");
                                }else  if(j==3){
                                    cell.setCellValue(openApplayMainData.get(nowday[1])+"");
                                }
                            }else if(i==count+1&&(j==1||j==3)){
                                if(j==1){
                                    cell.setCellValue(openApplayMainData.get(subdata[0])+"");
                            }else if(j==3){
                                    cell.setCellValue(openApplayMainData.get(subdata[1])+"");
                                }
                        }
                    }
                }
                //行数：事业部营销负责人开盘预判
                count=count+4;
                XSSFRow row1 = sheetAt.getRow(count);
                XSSFCell cell = row1.getCell(0);
                //设置事业部营销负责人开盘预判标题样式
                 sheetAt.getRow(count-1).getCell(0).setCellStyle(cellStyleFirstOpen);

                cell.setCellValue(openApplayMainData.get("trader_open_prediction")+"");
                //合并单元格（事业部营销负责人开盘预判数据）
                CellRangeAddress region2 = new CellRangeAddress(count, count+2, 0, 3);
                sheetAt.addMergedRegion(region2);
                //count+4=节点客储计划数据行
                count=count+6;
                System.err.println(count);
                //填充节点客储计划数据
                //查询客储数据
                List<Map> customerStorageNodeData = openBeforeSevenDayDao.getCustomerStorageNodeData(map);
                //字段定义
                String[] customerStorage={"node_name","node_time","line_name","report_num","visit_num","little_num","little_per","big_num","big_per","sub_num","make_per"};
                //合并单元格定义
                List<String> sb=new ArrayList<>();



                if(customerStorageNodeData!=null&&customerStorageNodeData.size()>0){
                    sheetAt.shiftRows(count,sheetAt.getLastRowNum(),customerStorageNodeData.size() ,true,false);
                    for (int i = count; i <count+customerStorageNodeData.size() ; i++) {
                        XSSFRow row3 = sheetAt.createRow(i);
                        for (int j=0;j<11;j++){
                            Map storageMap = customerStorageNodeData.get(i - count);
                            String node_name = storageMap.get("node_name") + "";
                            String value=storageMap.get(customerStorage[j])+"";
                            if(j==0&&sb.contains(node_name)){
                                value="";
                            }
                            sb.add(node_name);
                            XSSFCell cell1 = row3.createCell(j);
                            cell1.setCellStyle(cell9_0_cellStyle);
                            cell1.setCellValue("null".equals(value)?"":value);
                        }
                    }
                    count=count+customerStorageNodeData.size();
                    //合并单元格
                    for (int i=count-customerStorageNodeData.size();i<count;i+=3){
                        CellRangeAddress region3 = new CellRangeAddress(i, i+2, 0, 0);
                        sheetAt.addMergedRegion(region3);
                    }
                }
                //周拆分行
                count=count+3;
                //查询周拆分数据
                Map<Object, Object> paramMap = new HashMap<>();
                paramMap.put("id",id);
                List<Map> weekData = openBeforeSevenDayDao.getWeekData(paramMap);
                String total = openbeforetwentyoneDao.selectWeekDataTotal(id);
                String[] weekArray={"week","day_date","plan_add","plan_total","plan_task_per","actual_add","actual_total","actual_task_per","bias_value","bias_per","bias_cause"};
               if(weekData!=null&&weekData.size()>0){
                    for (int i=count;i<count+weekData.size();i++){
                        XSSFRow row3 = sheetAt.createRow(i);
                        for (int j=0;j<11;j++){
                            XSSFCell row3Cell = row3.createCell(j);
                            String value=weekData.get(i-count).get(weekArray[j])+"";
                            row3Cell.setCellStyle(cell9_0_cellStyle);
                            if(i==count&&j==2){
                                row3Cell.setCellValue(total);
                            }else{
                                row3Cell.setCellValue("null".equals(value)?"":value);
                            }
                        }
                    }
                }
            }else{
                return ResultBody.error(-1005,"导出失败,没有查询到当前节点的有效版本数据,请填报!");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String formatDate = sdf.format(new Date());
            String fileName = projectName+"-"+levelNode+tabNode+"-"+formatDate+".xlsx";
            response.setContentType("application/vnd.ms-excel;charset=utf-8");

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }catch (Exception e){
            e.printStackTrace();
           return ResultBody.error(-1005,"导出失败:"+e.toString());
        }
        return ResultBody.success(null);
    }

    public void updatePlanStartTime(Map map) {
        Map<Object, Object> paramMap = new HashMap<>();
        //计划
        String plan_id = map.get("plan_id") + "" + "";
        try {
            paramMap.put("plan_id", plan_id);
            paramMap.put("node_level", 8);
            //新申请开盘前21天日期
            String new_first_time = map.get("estimate_open_node") + "" + "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//要转换的日期格式，根据实际调整""里面内容
            Map waringDay3 = openbeforetwentyoneDao.getWaringDay(paramMap);
            if (waringDay3 != null) {
                String warning_day = waringDay3.get("warning_day") + "";
                int day1 = Integer.parseInt(warning_day);
                long new_first_time2 = simpleDateFormat.parse(new_first_time).getTime();
                long countTime = new_first_time2 - (day1 * 86400000);
                String format = simpleDateFormat.format(countTime);
                waringDay3.put("plan_start_time", format);
                openbeforetwentyoneDao.updateNodesPlanStartTime(waringDay3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public VisolinkResultBody applyAdoptTellInterface(Map map) {
        VisolinkResultBody<Object> visolink = new VisolinkResultBody<>();
        String id = map.get("businesskey") + "";
        String eventType = map.get("eventType") + "";
        //获取明源推送的审批单类型
        String orgName = map.get("orgName") + "";
        Map<Object, Object> paramMap = new HashMap<>();
        int node_level=6;
        //开盘前7天审批回调
        try {
            if ("fp_open_seven_approve".equals(orgName)) {
                node_level=7;
                if ("3".equals(eventType)) {
                    paramMap.put("status", eventType);
                    paramMap.put("id", id);
                    //更改数据状态
                    openBeforeSevenDayDao.updateApplayStatus(paramMap);
                }
                //如果是驳回或者撤回。将本版本数据重置为编制状态
                else if ("5".equals(eventType) || "6".equals(eventType)) {
                    paramMap.put("status", 2);
                    paramMap.put("id", id);
                    //更改数据状态
                    openBeforeSevenDayDao.updateApplayStatus(paramMap);
                } else if ("4".equals(eventType)) {
                    //查询时间铺排所需数据
                    Map applayData = openBeforeSevenDayDao.getApplayData(id);
                    //查询节点实际完成时间是否为空
                    String planNodeFinshTime = openBeforeSevenDayDao.getPlanNodeFinshTime(id);
                    if (planNodeFinshTime == null || "".equals(planNodeFinshTime)) {
                        //更改个实际完成时间
                        paramMap.put("id", id);
                        openBeforeSevenDayDao.updatePlanNodeFinshTime(paramMap);
                        //更改节点亮灯
                        openbeforetwentyoneDao.updatePlanTime(applayData);
                    }
                    paramMap.put("id", id);
                    paramMap.put("status", eventType);
                    //不更改实际完成时间，只更改状态
                    openBeforeSevenDayDao.updateApplayStatus(paramMap);
                    //时间铺排
                    this.updatePlanStartTime(applayData);

                    String plan_id = applayData.get("plan_id") + "";
                    //审批通过更改当前计划的节点为当前节点
                    openBeforeSevenDayDao.updateThisNodeforSevenDay(plan_id);

                }
                //开盘前21天/开盘前7天 ---延期开盘申请
            } else {
                Map applayDateTime = openbeforetwentyoneDao.getApplayDateTime(id);
                //时间铺排
                //获取时间铺排所需参数
                if ("4".equals(eventType)) {
                    paramMap.put("status", eventType);
                    paramMap.put("id", id);
                    //更改数据状态
                    openbeforetwentyoneDao.updateApplayStatus(paramMap);
                    String finshTimeTwentyDay = openbeforetwentyoneDao.getFinshTimeTwentyDay(id);
                    if (finshTimeTwentyDay == null) {
                        //   openbeforetwentyoneDao.updateFinishDate(id);
                        //更改节点完成时间
                        //      openbeforetwentyoneDao.updatePlanTime(applayDateTime);
                    }
                    openBeforeTwentyoneService.updateOpen_timeData(applayDateTime);
                    String plan_id = applayDateTime.get("plan_id") + "";
                    String level = applayDateTime.get("level") + "";
                    Map<Object, Object> thisNode = new HashMap<>();
                    thisNode.put("plan_id", plan_id);
                    thisNode.put("node_level", level);
                    //审批通过更改当前计划的节点为当前节点
                    openbeforetwentyoneDao.updateThisNodeforTwenDay(thisNode);

                    /*更改开盘前21天和开盘前7天里的客储计划*/
                    /*更改开盘时间*/
                    Map opentime = new HashMap();
                    opentime.put("id", id);
                    opentime.put("plan_id", plan_id);
                    //    openbeforetwentyoneDao.updateOpenTime(opentime);
                    /*先查找对应的延期开盘的flowID*/

                    String flowId = openbeforetwentyoneDao.selectDelayID(id);
                    /*判断哪些节点要新增延期开盘的数据*/
                    Integer one = 7;
                    Integer two = 7;
                    Map testOne = new HashMap();
                    testOne.put("id", id);
                    testOne.put("plan_id", plan_id);
                    Map mapTestAll = openbeforetwentyoneDao.selectTestOneTwo(testOne);
                    int one2 = Integer.parseInt(mapTestAll.get("one") + "");
                    String two2=mapTestAll.get("two")+"";
                    if (one2>= 21 || "".equals(two2)||"null".equals(two2)) {
                        one = 6;
                        /* 现在有一种情况，如果21被掠过，这时候开启了延期开盘，那么就要往21里填数据并且给它赋予初始值*/



                        //将首开前21天设置为可创建版本状态
                        openbeforetwentyoneDao.updateSixLight(plan_id);
                        Map NodeMap = new HashMap();
                        NodeMap.put("plan_id", plan_id);
                        NodeMap.put("node_level", 6);
                        List<Map> result = designTwoIndexDao.selectStorageFlow(NodeMap);
                        if (result != null && result.size() > 0) {
                            Map realResult = new HashMap();
                            realResult.putAll(result.get(0));
                            realResult.put("plan_id", plan_id);
                            realResult.put("plan_node_id", mapTestAll.get("plan_node_id"));

                            List<Map> NodePlan = designSevenDao.selectStoragePlan(realResult);
                            if (NodePlan == null || NodePlan.size() < 1) {
                                /*走到这一步说明21没有数据*/

                                designSevenDao.insertNewStorageFake(realResult);
                                designSevenDao.insertNewStorageWeekFake(realResult);
                            }
                        }
                    }

                    Map<Object, Object> paramMaps = new HashMap<>();
                    paramMaps.put("plan_id",plan_id);
                    paramMaps.put("node_level",7);

                    //将节点初始化
                    openbeforetwentyoneDao.updateSevenLight(paramMaps);
                    paramMap.put("status", 4);
                    paramMap.put("id", id);
                    //将数据重置为填报状态
                    openbeforetwentyoneDao.updateApplayStatus(paramMap);
                    List<Map> allNode = openbeforetwentyoneDao.selectCustomerStorageNodeData(id);
                    for (Map mapNode : allNode) {
                        mapNode.put("delay_id", id);
                        mapNode.put("flow_Id", flowId);
                        mapNode.put("plan_id", plan_id);
                        mapNode.put("one", one);
                        mapNode.put("two", two);
                        openbeforetwentyoneDao.updateNewNodePlan(mapNode);
                    }
                    /*此事数据库里关于储客计划部分数据不正确，所以需要走一遍先删再初始化*/
                    /*找到所有需要修改的FLOWID*/
                    Map mapThisNode = new HashMap();
                    mapThisNode.putAll(thisNode);
                    mapThisNode.put("one", one);
                    mapThisNode.put("two", two);
                    mapThisNode.put("delay_id", id);
                    List<Map> allNeed = openbeforetwentyoneDao.selectAllNeedUpdate(mapThisNode);
                    if (allNeed != null && allNeed.size() > 0) {
                        for (Map map1 : allNeed) {
                            openbeforetwentyoneDao.deleteNewStorage(map1);
                            openbeforetwentyoneDao.insertNewStorage(map1);
                        }
                    }


                    List<Map> flowList = openbeforetwentyoneDao.selectAllFlowId(mapThisNode);
                    String startTime = openbeforetwentyoneDao.selectStartTime(id);
                    if (flowList != null && flowList.size() > 0) {
                        /*先删除多余的周*/
                        for (Map mapFlow : flowList) {
                            Map deleteWeek = new HashMap();
                            deleteWeek.put("id", id);
                            deleteWeek.put("plan_node_id", mapFlow.get("plan_node_id"));
                            deleteWeek.put("one", one);
                            deleteWeek.put("two", two);
                            deleteWeek.put("start_time", startTime);
                            deleteWeek.putAll(mapFlow);
                            openbeforetwentyoneDao.deletePlusWeek(deleteWeek);
                        }
                        /*得到所有需要加上新的来访周拆分的数据，并得到最大的周，并累加*/
                        List<Map> listMaxWeek = openbeforetwentyoneDao.selectMaxWeek(flowList);
                        if (listMaxWeek != null && listMaxWeek.size() > 0) {
                            for (Map map1 : listMaxWeek) {

                                /*得到最大的周，然后往下累加*/
                                Integer intWeek = toInteger.ComputeResult(map1.get("WEEK") + "");
                                /*获取项目ID*/
                                Map week = openbeforetwentyoneDao.selectWeekFlowData(plan_id);
                                /*思路：找到所有需要填充的周拆分，然后把延期开盘的数据附上原有的周拆分的FlowID等信息再填进去*/


                                week.put("delay_id", flowId);

                                week.put("flow_id", map1.get("flow_id"));
                                week.put("plan_node_id", map1.get("plan_node_id"));
                                week.put("plan_id", map1.get("plan_id"));
                                /*此时初始化的数据里面还缺少了WEEK*/
                                openbeforetwentyoneDao.insertDelayStorageWeek(week);
                                /*更改Week*/
                                List<String> noWeek = openbeforetwentyoneDao.selectAllNoWeek(week);
                                for (String weekname : noWeek) {
                                    intWeek = intWeek + 1;
                                    week.put("uuid", weekname);
                                    week.put("week", "第" + int2chineseNum(intWeek) + "周");
                                    openbeforetwentyoneDao.insertWeekForDelay(week);
                                }
                            }
                        }

                    }
                    /*算THISNODE的状态*/
                    Map map1 = new HashMap();
                    map1.put("plan_id", plan_id);
                    designTwoIndexDao.updateNodeName(map1);

                    //审批中
                } else if ("3".equals(eventType)) {
                    paramMap.put("status", eventType);
                    paramMap.put("id", id);
                    //更改数据状态
                    openbeforetwentyoneDao.updateApplayStatus(paramMap);
                }
                //如果当前流程撤回或驳回，将本版本数据重置为编制中
                else if ("5".equals(eventType) || "6".equals(eventType)) {
                    paramMap.put("status", 2);
                    paramMap.put("id", id);
                    //更改数据状态
                    openbeforetwentyoneDao.updateApplayStatus(paramMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        visolink.setCode(200);
        visolink.setMessages("快速审批成功!");
        return visolink;
    }

    /*将数字转文字*/
    public static String int2chineseNum(int src) {
        final String num[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final String unit[] = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String dst = "";
        int count = 0;
        while (src > 0) {
            dst = (num[src % 10] + unit[count]) + dst;
            src = src / 10;
            count++;
        }
        return dst.replaceAll("零[千百十]", "零").replaceAll("一十", "十").replaceAll("零+万", "万")
                .replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
                .replaceAll("零+", "零").replaceAll("零$", "");

    }
    //过滤即将入库的参数数据
    public Map filterMap(Map<String,Object> map){
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map.Entry<String,Object> entry:map.entrySet()){
            String value=entry.getValue()+"";
            if(!"".equals(value)&&!"null".equalsIgnoreCase(value)){
                resultMap.put(entry.getKey(),entry.getValue());
            }
        }
        return resultMap;
    }
}
