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
    //风控回调地址
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
    //消息生成服务
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

    //接收queue消息
    @JmsListener(destination = "${activemq.alignment}")
    public void handler(String message) {
        try {
            //往消息表里添加数据
            workflow.insertMessage(message);

            Map map = JSON.parseObject(message, Map.class);

            String eventType = map.get("eventType") + "";//状态
            String businessKey = map.get("businesskey")+"";//明源id
            String flowKey = map.get("flowKey")+"";//模板名称
            String instanceId = map.get("instanceId")+"";//流程id
            String taskId = map.get("taskId")+"";//任务id

            String operatorAccount = map.get("operatorAccount")+"";//操作人id
            List<Map> resultFlow = service.workflowSelect(businessKey);
            Map info=resultFlow.get(0);
            String base_id=info.get("base_id")+"";
            //转换code
         /* if(!flowKey.contains("MY_FYs_DSManageFee")&&!flowKey.contains("MY_FYs_CONTRACTSP")){
              Map oldFlowCode = workflowChangeDao.getOldFlowCode(flowKey);
              if(oldFlowCode!=null&&oldFlowCode.size()>0){
                  String DictCode=oldFlowCode.get("DictCode")+"";
                  flowKey=DictCode;
              }
          }*/
            //根据json_id查询转换以前的code,传递下游系统
            Map queryFlowCode = workflowChangeDao.queryFlowCode(businessKey);
            if(queryFlowCode!=null){
                flowKey=queryFlowCode.get("flow_code")+"";
            }
            //如果是定调价，不获取base_id
            if((!flowKey.contains("My_Sales_PriceZD")&&!(flowKey.contains("My_Sales_PriceZD_2020")))&&!flowKey.contains("week_area_report")){
                //如果当前流程已经被废弃过
                if(!"".equals(base_id)&&!"null".equals(base_id)){
                    //如果流程已经被废弃过，把base_id传递给下游系统/本系统
                    businessKey=base_id;
                }
            }
            //调用OA获取当前审批的审批路径
            Map flowOpinions= flowUtil.flowPaths(info.get("flow_id")+"");
            String account=null;
            if(flowOpinions!=null){
                List<Map> flowList=(List<Map>)flowOpinions.get("pathList");
                for (int i=flowList.size()-1;i>=0;i--){
                    Map userMap = flowList.get(i);
                    if(userMap!=null){
                        //获取最后一个用户任务
                        String nodeType=userMap.get("nodeType")+"";
                        if("userTask".equals(nodeType)){
                            //获取最后一个节点的审批人集合
                            List<Map> userlist= (List<Map>) userMap.get("excutor");
                            if(userlist!=null&&userlist.size()>0){
                                Map applyUsermap = userlist.get(0);
                                //获取最后一个审批人的账号
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
                }else if(flowKey.contains("MY_FYs_DSManageFeeOut")||flowKey.contains("MY_FYs_DSManageFee")||flowKey.contains("MY_FYs_YsAdjustOtherFee") || flowKey.contains("MY_FYs_CONTRACTSP")||flowKey.contains("MY_FYs_CONTRACTSP_2020")||flowKey.contains("MY_FYs_DSManageFeeOut_2020")||flowKey.contains("MY_FYs_OtherFee")){//三个电商或合同流程（包含补充合同）
                    Map fysMap = workflowChangeDao.queryFlowCode(businessKey);
                    String flow_code = fysMap.get("flow_code")+"";
                    flowKey = flow_code;
                    isType = "cost";
                    System.err.println(flowKey);
                }else if(flowKey.contains("MY_FYs_YsAdjustHt")||flowKey.contains("MY_FYs_YsAdjustHt_2020")){//调整调剂（合同口径）
                    flowKey = "MY_FYs_YsAdjustHt";
                    isType = "cost";
                }else if(flowKey.contains("MY_FYs_YsAdjustPayCaliber")||flowKey.contains("MY_FYs_YsAdjustPayCaliber_2020")||flowKey.contains("MY_FYs_YsAdjustPay")||flowKey.contains("MY_FYs_YsAdjustPay_2020")){//调整调剂（支付口径）
                    flowKey = "MY_FYs_YsAdjustPay";
                    isType = "cost";
                }else if(flowKey.contains("MY_FYs_HTFKSP")){//付款
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
                //异常结束需要保存任务id(taskId)
                if(!"".equals(taskId) && !"null".equals(taskId)){
                    resultMap.put("taskId",taskId);
                }else {
                    resultMap.put("taskId","必要参数未获取");
                }
            }
            resultMap.put("businessKey",businessKey);


            //设置日期格式
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // new Date()为获取当前系统时间
            String nowTime = df.format(new Date());

            //往消息表里添加数据
//        workflow.insertMessage(resultMap);
            //修改库里状态字段的方法
            workflow.updateStartworkflow(resultMap);
            Map jsonMap = new HashMap();

            //如果是定调价相关的数据，需要回调明源接口
            if("My_Sales_PriceZD".equals(flowKey)){
                if("3".equals(eventType)){
                    //发起成功，调用明源接口
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("DataSource","SaleMrg");
                    jsonMap.put("Strmessage","流程发起成功");
                    jsonMap.put("Bsuccess","1");
                    //jsonMap.put("Procurl","");

                    //回调明源
                    Object result = HttpRequestUtilTwo.httpPost(urlStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                    //生成消息
                    pricingMessageService.pricingMessageGen(businessKey);
                    jsonMap.put("eventType",eventType);
                    addFlowResultInfo(jsonMap,result.toString(),urlStart,"售楼系统");
                    log.info("<====================调用明源发起接口======"+result+"================>");
                    System.out.println(result);
                }else if("4".equals(eventType)){
                    //流程结束，调用明源接口
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Eprocessinstanceresult","1");
                    jsonMap.put("Strcomment","流程审批结束");
                    jsonMap.put("Dttime",nowTime);
                    operatorAccount=account;
                    jsonMap.put("currentDomainUid",operatorAccount);
                    Object result = HttpRequestUtilTwo.httpPost(urlEnd, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                    log.info("<====================调用明源流程结束接口======"+result+"================>");
                    System.err.println("传递给明源参数:"+jsonMap);
                    System.out.println(result);
                    jsonMap.put("eventType",eventType);
                    addFlowResultInfo(jsonMap,result.toString(),urlEnd,"售楼系统");
                }else if("5".equals(eventType) || "6".equals(eventType) || "9".equals(eventType)){
                    //流程异常结束，调用明源流程过程接口
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Strstepname","暂无");
                    jsonMap.put("Strapproverid",operatorAccount);
                    jsonMap.put("Eaction","2");
                    jsonMap.put("Dttime",nowTime);
                    Object result = HttpRequestUtilTwo.httpPost(urlHistory, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                    log.info("<====================调用明源异常结束接口======"+result+"================>");
                    System.out.println(result);
                    jsonMap.put("eventType",eventType);
                    addFlowResultInfo(jsonMap,result.toString(),urlHistory,"售楼系统");
                }

                //如果是周报，需要调用上报接口
            }else if("week_area_report".equals(flowKey)){
                if("4".equals(eventType)){
                    //周上报需要的base_id
                    Map baseidMap = workflow.selectBaseId(resultMap);
                    String id = "";
                    if(baseidMap==null){
                        id = "周上报未查到base_id，导致接口调用失败！";
                        workflow.insertMessage(id);
                        throw new Exception("周上报未查到base_id，导致接口调用失败！");
                    }else {
                        id = baseidMap.get("base_id")+"";
                    }

                    if(!"".equals(id) && !"null".equals(id)){

                        //周上报需要的参数
                        Map weekMap = workflow.queryMmWeekMarketingPlan(id);
                        weekMap.put("is_effective",1);
                        weekMap.put("plan_status",1);
                        //提报接口
                        weeklyMarketingService.weekMarketingPlanEffective(weekMap);
                    }else {
                        String messages = "周上报参数错误";
                        workflow.insertMessage(messages);
                        System.out.println("周上报参数错误");
                        throw new Exception("周上报参数错误！");
                    }

                }

                //首开计划需要回调的接口
            }else if(flowKey.equals(fpFlowCode) || MqDefined.MQ_FP_FLOWCODE_ONE.equals(flowKey)
                    || MqDefined.MQ_FP_FLOWCODE_TWO.equals(flowKey)||MqDefined.MQ_FP_FLOWCODE_SD.equals(flowKey)||flowKey.equals("SkAuthorized_2020")){
                //查询流程信息
                Map flowInfo=workflow.queryworkflow(businessKey);
                if(flowInfo!=null && flowInfo.size()>0){
                    if("3".equals(eventType)){
                        workflow.updateFlowzdDateByJsonId(businessKey);
                    }
                    //oa推送消息
                    if(map!=null){
                        //首开编码判断流程调用模块接口
                        String orgName=flowInfo.get("orgName")+"";
                        map.putAll(flowInfo);
                        if(!"".equals(orgName)){
                            //拿地后
                            if(MqDefined.MQ_FP_LAND_BACK.equals(orgName) || MqDefined.MQ_FP_DESIGNONE.equals(orgName)){
                                takeLandService.takeLandSuccess(map);
                                //开盘模块
                            }else if(MqDefined.MQ_FP_OPEN.equals(orgName)|| MqDefined.MQ_FP_open_morrow.equals(orgName)
                                    || MqDefined.MQ_FP_open_one_month.equals(orgName)){
                                //开盘
                                openThisDayService.approveOpenNodeInfo(map);
                                if(eventType.equals("4")){
                                    map.put("json_id",businessKey);
                                    templateEngineService.firstBroadcastMessageGen(map);
                                }

                            }else if(MqDefined.MQ_FP_open_three.equals(orgName)|| MqDefined.MQ_FP_open_two.equals(orgName)
                                    || MqDefined.MQ_FP_open_twentyone_node.equals(orgName )|| MqDefined.MQ_FP_designtwo.equals(orgName)){
                                designTwoIndexService.forUpdateNode(map);
                                //顶设2 前二月 前三月 前21储客
                                //审批通过以后，调用消息生成
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

                                //抢开
                                if("fp_preemptionOpen".equals(orgName)){
                                    map.put("businesskey",businessKey);
                                    map.put("eventType",eventType);
                                    preemptionOpenService.approvedCallback(map);
                                }else{
                                    openBeforeSevenDayService.applyAdoptTellInterface(map);
                                }
                                //前21延期，前七天首开 延期
                                //审批通过以后，调用消息生成
                                if("fp_open_seven_approve".equals(orgName)){
                                    if("4".equals(eventType)){
                                        map.put("flowKey",orgName);
                                        map.put("node_level",7);
                                        templateEngineService.firstPlanMessage(map);
                                    }
                                }

                            }else  if(orgName.equals("fp_Supplementary_record")){
                                //补录附件
                                skipNodeUploadFileService.applayCallback(map);
                            }
                        }else {
                            System.out.println("未获取orgName");
                        }

                    }
                }
                //一揽子分期折扣需要将数据回调明源
            }else if(FlowDefined.FLOWKEY_MY_PACKAGE_STAGE.equals(flowKey)){
                map.put("flow_id",instanceId);
                packageanddiscountService.approvalPushDataForMy(map);
            }//回调风控
            else if(flowKey.contains("Risk_Approval_risk")||"Risk_Approval_risk".equals(flowKey)){
                map.put("flowKey","Risk_Approval_risk");
                map.put("eventType",eventType);
                map.put("instanceId",instanceId);
                map.put("Businesskey",businessKey);
                String resultStr = HttpRequestUtilTwo.httpPost(faceSysCallbackUrl, JSONObject.parseObject(JSONObject.toJSONString(map)), false);
                System.err.println("地址:"+faceSysCallbackUrl);
                System.err.println("参数:"+JSON.toJSONString(map));
                System.err.println("返回:"+resultStr);
                //记录日志
                addFlowResultInfo(map,resultStr,faceSysCallbackUrl,"风控系统");
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
                    //发起成功，调用明源接口
                    jsonMap.put("Strboid", businessKey);
                    jsonMap.put("Strbtid", flowKey);
                    jsonMap.put("Iprocinstid", instanceId);
                    jsonMap.put("DataSource", "SaleMrg");
                    jsonMap.put("Strmessage", "流程发起成功");
                    jsonMap.put("Bsuccess", "1");
                    Object result = "";
                    if("cost".equals(isType)){
                        result = HttpRequestUtilTwo.httpPost(adjustmentStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("地址:"+adjustmentStart);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result+"",adjustmentStart,"费用系统");
                    }else {
                        result = HttpRequestUtilTwo.httpPost(salesOfficeStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("地址:"+salesOfficeStart);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeStart,"售楼系统");
                    }
                    log.info("<====================调用明源发起接口======"+result+"================>");
                    System.err.println(result);
                    System.err.println("传递给明源参数:"+jsonMap);

                }else if("4".equals(eventType)){
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Eprocessinstanceresult","1");
                    jsonMap.put("Strcomment","流程审批结束");
                    jsonMap.put("Dttime",nowTime);
                    operatorAccount=account;
                    jsonMap.put("currentDomainUid",operatorAccount);
                    Object result = "";
                    if("cost".equals(isType)){
                        System.err.println(JSON.toJSONString(jsonMap));
                        System.err.println("接口地址:"+adjustmentEnd);
                        result = HttpRequestUtilTwo.httpPost(adjustmentEnd, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),adjustmentEnd,"费用系统");
                    }else {
                        System.err.println("接口地址:"+salesOfficeEnd);
                        result = HttpRequestUtilTwo.httpPost(salesOfficeEnd, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeEnd,"售楼系统");
                    }
                    log.info("<====================调用明源流程结束接口======"+result+"================>");

                    System.err.println(result);
                    System.err.println("传递给明源参数:"+jsonMap);

                }else if("5".equals(eventType) || "6".equals(eventType) || "9".equals(eventType)){
                    //流程异常结束，调用明源流程过程接口
                    jsonMap.put("Strboid",businessKey);
                    jsonMap.put("Strbtid",flowKey);
                    jsonMap.put("Iprocinstid",instanceId);
                    jsonMap.put("Strstepname","暂无");
                    jsonMap.put("Strapproverid",operatorAccount);
                    jsonMap.put("Eaction","2");
                    jsonMap.put("Dttime",nowTime);
                    Object result = "";
                    if("cost".equals(isType)){
                        result = HttpRequestUtilTwo.httpPost(adjustmentHistory, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("地址:"+adjustmentHistory);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),adjustmentHistory,"费用系统");

                    }else {
                        result = HttpRequestUtilTwo.httpPost(salesOfficeHistory, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("地址:"+salesOfficeHistory);
                        jsonMap.put("eventType",eventType);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeHistory,"售楼系统");
                    }
                    log.info("<====================调用明源异常结束接口======"+result+"================>");
                    System.err.println(result);
                    System.err.println("传递给明源参数:"+jsonMap);

                }
            }
        }catch (Exception e){
            e.getMessage();
        }

    }


    //接收topic消息
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
