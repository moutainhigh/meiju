package cn.visolink.activeMQ;

import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.firstplan.message.service.PricingMessageService;
import cn.visolink.firstplan.message.service.TemplateEngineService;
import cn.visolink.firstplan.openbeforeseven.service.OpenBeforeSevenDayService;
import cn.visolink.firstplan.opening.service.OpenThisDayService;
import cn.visolink.firstplan.preemptionopen.service.PreemptionOpenService;
import cn.visolink.firstplan.receipt.dao.ReceiptDao;
import cn.visolink.firstplan.receipt.service.impl.ReceiptServiceImpl;
import cn.visolink.firstplan.skipnodeupload.service.SkipNodeUploadFileService;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.onlineretailersuse.service.OnlineretailersUseService;
import cn.visolink.salesmanage.packageanddiscount.service.PackageanddiscountService;
import cn.visolink.salesmanage.weeklymarketingplan.service.WeeklyMarketingService;
import cn.visolink.salesmanage.workflowchange.dao.WorkflowChangeDao;
import cn.visolink.salesmanage.workflowchange.service.WorkflowChangeService;
import cn.visolink.utils.FlowUtil;
import cn.visolink.utils.HttpRequestUtilTwo;
import cn.visolink.utils.flowpojo.FlowDefined;
import cn.visolink.utils.flowpojo.MqDefined;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ActiveMQConsumer {
    @Autowired
    private WorkflowDao workflow;
    @Value("${testMyApi.urlStart}")
    private String urlStart;
    @Value("${testMyApi.urlHistory}")
    private String urlHistory;
    @Value("${testMyApi.urlEnd}")
    private String urlEnd;
    @Value("${testMyApi.adjustmentStart}")
    private String adjustmentStart;
    @Value("${testMyApi.adjustmentEnd}")
    private String adjustmentEnd;
    @Value("${testMyApi.adjustmentHistory}")
    private String adjustmentHistory;
    @Value("${oaflow.fpFlowCode}")
    private String fpFlowCode;
    @Value("${testMyApi.salesOfficeStart}")
    private String salesOfficeStart;
    @Value("${testMyApi.salesOfficeHistory}")
    private String salesOfficeHistory;
    @Value("${testMyApi.salesOfficeEnd}")
    private String salesOfficeEnd;
    @Value("${activemq.alignment}")
    private String alignment;
    //??????????????????
    @Value("${faceSysCallback.url}")
    private String faceSysCallbackUrl;
    @Autowired
    private WeeklyMarketingService weeklyMarketingService;
    @Autowired
    private WorkflowChangeDao workflowChangeDao;

    @Autowired
    private OpenThisDayService openThisDayService;
    @Autowired
    private DesignTwoIndexService designTwoIndexService;
    @Autowired
    private OpenBeforeSevenDayService openBeforeSevenDayService;
    @Autowired
    private TakeLandService takeLandService;
    @Autowired
    private PackageanddiscountService packageanddiscountService;
    @Autowired
    private WorkflowChangeService service;
    @Autowired
    private FlowUtil flowUtil;
    @Autowired
    private PricingMessageService pricingMessageService;
    //??????????????????
    @Autowired
    private TemplateEngineService templateEngineService;
    @Autowired
    private SkipNodeUploadFileService skipNodeUploadFileService;
    @Autowired
    private PreemptionOpenService preemptionOpenService;
    @Autowired
    private ReceiptDao receiptDao;
    @Autowired
    private OnlineretailersUseService onlineretailersUseService;

    //??????queue??????
    @JmsListener(destination = "${activemq.alignment}")
    public void handler(String message) {
        try {
            //???????????????????????????
            workflow.insertMessage(message);

            Map map = JSON.parseObject(message, Map.class);

            String eventType = map.get("eventType") + "";//??????
            String businessKey = map.get("businesskey")+"";//??????id
            String flowKey = map.get("flowKey")+"";//????????????
            String instanceId = map.get("instanceId")+"";//??????id
            String taskId = map.get("taskId")+"";//??????id

            String operatorAccount = map.get("operatorAccount")+"";//?????????id
            List<Map> resultFlow = service.workflowSelect(businessKey);
            Map info=resultFlow.get(0);
            String base_id=info.get("base_id")+"";
            //??????code
         /* if(!flowKey.contains("MY_FYs_DSManageFee")&&!flowKey.contains("MY_FYs_CONTRACTSP")){
              Map oldFlowCode = workflowChangeDao.getOldFlowCode(flowKey);
              if(oldFlowCode!=null&&oldFlowCode.size()>0){
                  String DictCode=oldFlowCode.get("DictCode")+"";
                  flowKey=DictCode;
              }
          }*/
            //??????json_id?????????????????????code,??????????????????
            Map queryFlowCode = workflowChangeDao.queryFlowCode(businessKey);
            if(queryFlowCode!=null){
                flowKey=queryFlowCode.get("flow_code")+"";
            }
            //??????????????????????????????base_id
            if((!flowKey.contains("My_Sales_PriceZD")&&!(flowKey.contains("My_Sales_PriceZD_2020")))&&!flowKey.contains("week_area_report")){
                //????????????????????????????????????
                if(!"".equals(base_id)&&!"null".equals(base_id)){
                    //????????????????????????????????????base_id?????????????????????/?????????
                    businessKey=base_id;
                }
            }
            //??????OA?????????????????????????????????
            Map flowOpinions= flowUtil.flowPaths(info.get("flow_id")+"");
            String account=null;
            if(flowOpinions!=null){
                List<Map> flowList=(List<Map>)flowOpinions.get("pathList");
                for (int i=flowList.size()-1;i>=0;i--){
                    Map userMap = flowList.get(i);
                    if(userMap!=null){
                        //??????????????????????????????
                        String nodeType=userMap.get("nodeType")+"";
                        if("userTask".equals(nodeType)){
                            //??????????????????????????????????????????
                            List<Map> userlist= (List<Map>) userMap.get("excutor");
                            if(userlist!=null&&userlist.size()>0){
                                Map applyUsermap = userlist.get(0);
                                //????????????????????????????????????
                                account=applyUsermap.get("acount")+"";
                                break;
                            }
                        }
                    }
                }
            }

            String isType = "";

            String My_Sales_PriceZD = "My_Sales_PriceZD";

            Map resultMap = new HashMap();
            if(!"".equals(eventType) && !"null".equals(eventType)){
                resultMap.put("flowStatus",eventType);
            }
            if(!"".equals(flowKey) && !"null".equals(flowKey)){
                if(flowKey.indexOf(My_Sales_PriceZD)!=-1||flowKey.indexOf("My_Sales_PriceZD_2020")!=-1){
                    flowKey = "My_Sales_PriceZD";
                }else if(flowKey.contains("week_area_report")){
                    flowKey = "week_area_report";
                }else if(flowKey.contains("MY_FYs_DSManageFeeOut")||flowKey.contains("MY_FYs_DSManageFee")||flowKey.contains("MY_FYs_YsAdjustOtherFee") || flowKey.contains("MY_FYs_CONTRACTSP")||flowKey.contains("MY_FYs_CONTRACTSP_2020")||flowKey.contains("MY_FYs_DSManageFeeOut_2020")||flowKey.contains("MY_FYs_OtherFee")){//???????????????????????????????????????????????????
                    Map fysMap = workflowChangeDao.queryFlowCode(businessKey);
                    String flow_code = fysMap.get("flow_code")+"";
                    flowKey = flow_code;
                    isType = "cost";
                    System.err.println(flowKey);
                }else if(flowKey.contains("MY_FYs_YsAdjustHt")||flowKey.contains("MY_FYs_YsAdjustHt_2020")){//??????????????????????????????
                    flowKey = "MY_FYs_YsAdjustHt";
                    isType = "cost";
                }else if(flowKey.contains("MY_FYs_YsAdjustPayCaliber")||flowKey.contains("MY_FYs_YsAdjustPayCaliber_2020")||flowKey.contains("MY_FYs_YsAdjustPay")||flowKey.contains("MY_FYs_YsAdjustPay_2020")){//??????????????????????????????
                    flowKey = "MY_FYs_YsAdjustPay";
                    isType = "cost";
                }else if(flowKey.contains("MY_FYs_HTFKSP")){//??????
                    flowKey = "MY_FYs_HTFKSP";
                    isType = "cost";
                } else if(flowKey.contains("MY_FYs_FDDZXHTSP")) {
                    flowKey = "MY_FYs_FDDZXHTSP";
                    isType = "cost";
                }else{
                    Map fysMap = workflowChangeDao.queryFlowCode(businessKey);
                    String flow_code = fysMap.get("flow_code")+"";
                    flowKey = flow_code;
                }

            }
            if(!"".equals(message) && !"null".equals(message)){
                resultMap.put("message",message);
            }
            if("5".equals(eventType) || "6".equals(eventType) || "9".equals(eventType)){
                //??????????????????????????????id(taskId)
                if(!"".equals(taskId) && !"null".equals(taskId)){
                    resultMap.put("taskId",taskId);
                }else {
                    resultMap.put("taskId","?????????????????????");
                }
            }
            resultMap.put("businessKey",businessKey);


            //??????????????????
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // new Date()???????????????????????????
            String nowTime = df.format(new Date());

            //???????????????????????????
//        workflow.insertMessage(resultMap);
            //?????????????????????????????????
            workflow.updateStartworkflow(resultMap);
            Map jsonMap = new HashMap();

            //????????????????????????????????????????????????????????????
            if("My_Sales_PriceZD".equals(flowKey)){
                if("3".equals(eventType)){
                    //?????????????????????????????????
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("DataSource","SaleMrg");
                    jsonMap.put("Strmessage","??????????????????");
                    jsonMap.put("Bsuccess","1");
                    //jsonMap.put("Procurl","");

                    //????????????
                    Object result = HttpRequestUtilTwo.httpPost(urlStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                    //????????????
                    pricingMessageService.pricingMessageGen(businessKey);
                    jsonMap.put("eventType",eventType);
                    addFlowResultInfo(jsonMap,result.toString(),urlStart,"????????????");
                    log.info("<====================????????????????????????======"+result+"================>");
                    System.out.println(result);
                }else if("4".equals(eventType)){
                    //?????????????????????????????????
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Eprocessinstanceresult","1");
                    jsonMap.put("Strcomment","??????????????????");
                    jsonMap.put("Dttime",nowTime);
                    operatorAccount=account;
                    jsonMap.put("currentDomainUid",operatorAccount);
                    Object result = HttpRequestUtilTwo.httpPost(urlEnd, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                    log.info("<====================??????????????????????????????======"+result+"================>");
                    System.err.println("?????????????????????:"+jsonMap);
                    System.out.println(result);
                    jsonMap.put("eventType",eventType);
                    addFlowResultInfo(jsonMap,result.toString(),urlEnd,"????????????");
                }else if("5".equals(eventType) || "6".equals(eventType) || "9".equals(eventType)){
                    //???????????????????????????????????????????????????
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Strstepname","??????");
                    jsonMap.put("Strapproverid",operatorAccount);
                    jsonMap.put("Eaction","2");
                    jsonMap.put("Dttime",nowTime);
                    Object result = HttpRequestUtilTwo.httpPost(urlHistory, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                    log.info("<====================??????????????????????????????======"+result+"================>");
                    System.out.println(result);
                    jsonMap.put("eventType",eventType);
                    addFlowResultInfo(jsonMap,result.toString(),urlHistory,"????????????");
                }

                //??????????????????????????????????????????
            }else if("week_area_report".equals(flowKey)){
                if("4".equals(eventType)){
                    //??????????????????base_id
                    Map baseidMap = workflow.selectBaseId(resultMap);
                    String id = "";
                    if(baseidMap==null){
                        id = "??????????????????base_id??????????????????????????????";
                        workflow.insertMessage(id);
                        throw new Exception("??????????????????base_id??????????????????????????????");
                    }else {
                        id = baseidMap.get("base_id")+"";
                    }

                    if(!"".equals(id) && !"null".equals(id)){

                        //????????????????????????
                        Map weekMap = workflow.queryMmWeekMarketingPlan(id);
                        weekMap.put("is_effective",1);
                        weekMap.put("plan_status",1);
                        //????????????
                        weeklyMarketingService.weekMarketingPlanEffective(weekMap);
                    }else {
                        String messages = "?????????????????????";
                        workflow.insertMessage(messages);
                        System.out.println("?????????????????????");
                        throw new Exception("????????????????????????");
                    }

                }

                //?????????????????????????????????
            }else if(flowKey.equals(fpFlowCode) || MqDefined.MQ_FP_FLOWCODE_ONE.equals(flowKey)
                    || MqDefined.MQ_FP_FLOWCODE_TWO.equals(flowKey)||MqDefined.MQ_FP_FLOWCODE_SD.equals(flowKey)||flowKey.equals("SkAuthorized_2020")){
                //??????????????????
                Map flowInfo=workflow.queryworkflow(businessKey);
                if(flowInfo!=null && flowInfo.size()>0){
                    if("3".equals(eventType)){
                        workflow.updateFlowzdDateByJsonId(businessKey);
                    }
                    //oa????????????
                    if(map!=null){
                        //??????????????????????????????????????????
                        String orgName=flowInfo.get("orgName")+"";
                        map.putAll(flowInfo);
                        if(!"".equals(orgName)){
                            //?????????
                            if(MqDefined.MQ_FP_LAND_BACK.equals(orgName) || MqDefined.MQ_FP_DESIGNONE.equals(orgName)){
                                takeLandService.takeLandSuccess(map);
                                //????????????
                            }else if(MqDefined.MQ_FP_OPEN.equals(orgName)|| MqDefined.MQ_FP_open_morrow.equals(orgName)
                                    || MqDefined.MQ_FP_open_one_month.equals(orgName)){
                                //??????
                                openThisDayService.approveOpenNodeInfo(map);
                                if(eventType.equals("4")){
                                    map.put("json_id",businessKey);
                                    templateEngineService.firstBroadcastMessageGen(map);
                                }

                            }else if(MqDefined.MQ_FP_open_three.equals(orgName)|| MqDefined.MQ_FP_open_two.equals(orgName)
                                    || MqDefined.MQ_FP_open_twentyone_node.equals(orgName )|| MqDefined.MQ_FP_designtwo.equals(orgName)){
                                designTwoIndexService.forUpdateNode(map);
                                //??????2 ????????? ????????? ???21??????
                                //???????????????????????????????????????
                                map.put("flowKey",orgName);
                                if("4".equals(eventType)){
                                    if(MqDefined.MQ_FP_open_three.equals(orgName)){
                                        map.put("node_level",4);
                                    }else if(MqDefined.MQ_FP_open_two.equals(orgName)){
                                        map.put("node_level",5);
                                    }else if( MqDefined.MQ_FP_open_twentyone_node.equals(orgName )){
                                        map.put("node_level",6);
                                    }
                                    templateEngineService.firstPlanMessage(map);
                                }
                            }else if(MqDefined.MQ_FP_open_twentyone_off.equals(orgName)|| MqDefined.MQ_FP_open_seven_approve.equals(orgName)
                                    || MqDefined.MQ_FP_open_seven_off.equals(orgName)||"fp_preemptionOpen".equals(orgName)){

                                //??????
                                if("fp_preemptionOpen".equals(orgName)){
                                    map.put("businesskey",businessKey);
                                    map.put("eventType",eventType);
                                    preemptionOpenService.approvedCallback(map);
                                }else{
                                    openBeforeSevenDayService.applyAdoptTellInterface(map);
                                }
                                //???21???????????????????????? ??????
                                //???????????????????????????????????????
                                if("fp_open_seven_approve".equals(orgName)){
                                    if("4".equals(eventType)){
                                        map.put("flowKey",orgName);
                                        map.put("node_level",7);
                                        templateEngineService.firstPlanMessage(map);
                                    }
                                }

                            }else  if(orgName.equals("fp_Supplementary_record")){
                                //????????????
                                skipNodeUploadFileService.applayCallback(map);
                            }
                        }else {
                            System.out.println("?????????orgName");
                        }

                    }
                }
                //????????????????????????????????????????????????
            }else if(FlowDefined.FLOWKEY_MY_PACKAGE_STAGE.equals(flowKey)){
                map.put("flow_id",instanceId);
                packageanddiscountService.approvalPushDataForMy(map);
            }//????????????
            else if(flowKey.contains("Risk_Approval_risk")||"Risk_Approval_risk".equals(flowKey)){
                map.put("flowKey","Risk_Approval_risk");
                map.put("eventType",eventType);
                map.put("instanceId",instanceId);
                map.put("Businesskey",businessKey);
                String resultStr = HttpRequestUtilTwo.httpPost(faceSysCallbackUrl, JSONObject.parseObject(JSONObject.toJSONString(map)), false);
                System.err.println("??????:"+faceSysCallbackUrl);
                System.err.println("??????:"+JSON.toJSONString(map));
                System.err.println("??????:"+resultStr);
                //????????????
                addFlowResultInfo(map,resultStr,faceSysCallbackUrl,"????????????");
            } else if(flowKey.equals("Commission_Receipt") ){
                if("3".equals(eventType)){
                    jsonMap.put("id",businessKey);
                    jsonMap.put("payment_status",6);
                    receiptDao.updatePaymentStatus3(jsonMap);
                } else if ("4".equals(eventType)) {
                    jsonMap.put("id", businessKey);
                    jsonMap.put("payment_status", 2);
                    receiptDao.updatePaymentStatus3(jsonMap);
                } else {
                    jsonMap.put("id", businessKey);
                    jsonMap.put("payment_status", 1);
                    receiptDao.updatePaymentStatus3(jsonMap);
                }
            } else if ("My_Sales_policy".equalsIgnoreCase(flowKey)) {
                if ("4".equalsIgnoreCase(eventType)) {
                    map.put("BOID", businessKey);
                    onlineretailersUseService.synOnOnlineretailersUseData(map);
                }
            } else {
                if ("3".equals(eventType)) {
                    //?????????????????????????????????
                    jsonMap.put("Strboid", businessKey);
                    jsonMap.put("Strbtid", flowKey);
                    jsonMap.put("Iprocinstid", instanceId);
                    jsonMap.put("DataSource", "SaleMrg");
                    jsonMap.put("Strmessage", "??????????????????");
                    jsonMap.put("Bsuccess", "1");
                    Object result = "";
                    if("cost".equals(isType)){
                        result = HttpRequestUtilTwo.httpPost(adjustmentStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("??????:"+adjustmentStart);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result+"",adjustmentStart,"????????????");
                    }else {
                        result = HttpRequestUtilTwo.httpPost(salesOfficeStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("??????:"+salesOfficeStart);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeStart,"????????????");
                    }
                    log.info("<====================????????????????????????======"+result+"================>");
                    System.err.println(result);
                    System.err.println("?????????????????????:"+jsonMap);

                }else if("4".equals(eventType)){
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Eprocessinstanceresult","1");
                    jsonMap.put("Strcomment","??????????????????");
                    jsonMap.put("Dttime",nowTime);
                    operatorAccount=account;
                    jsonMap.put("currentDomainUid",operatorAccount);
                    Object result = "";
                    if("cost".equals(isType)){
                        System.err.println(JSON.toJSONString(jsonMap));
                        System.err.println("????????????:"+adjustmentEnd);
                        result = HttpRequestUtilTwo.httpPost(adjustmentEnd, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),adjustmentEnd,"????????????");
                    }else {
                        System.err.println("????????????:"+salesOfficeEnd);
                        result = HttpRequestUtilTwo.httpPost(salesOfficeEnd, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeEnd,"????????????");
                    }
                    log.info("<====================??????????????????????????????======"+result+"================>");

                    System.err.println(result);
                    System.err.println("?????????????????????:"+jsonMap);

                }else if("5".equals(eventType) || "6".equals(eventType) || "9".equals(eventType)){
                    //???????????????????????????????????????????????????
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Strstepname","??????");
                    jsonMap.put("Strapproverid",operatorAccount);
                    jsonMap.put("Eaction","2");
                    jsonMap.put("Dttime",nowTime);
                    Object result = "";
                    if("cost".equals(isType)){
                        result = HttpRequestUtilTwo.httpPost(adjustmentHistory, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("??????:"+adjustmentHistory);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),adjustmentHistory,"????????????");

                    }else {
                        result = HttpRequestUtilTwo.httpPost(salesOfficeHistory, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("??????:"+salesOfficeHistory);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeHistory,"????????????");
                    }
                    log.info("<====================??????????????????????????????======"+result+"================>");
                    System.err.println(result);
                    System.err.println("?????????????????????:"+jsonMap);

                }
            }
        }catch (Exception e){
            e.getMessage();
        }

    }


    //??????topic??????
//    @JmsListener(destination = "topic_test")
//    public void handlerTopic(String msessage){
//        System.out.println(msessage);
//    }

    public void addFlowResultInfo(Map paramMap,String result,String url,String point){
        Map<Object, Object> paramMaps = new HashMap<>();
        paramMaps.put("param",JSON.toJSONString(paramMap));
        paramMaps.put("result",result);
        paramMaps.put("url",url);
        paramMaps.put("point",point);
        workflowChangeDao.addFlowResultInfo(paramMaps);
    };
}
