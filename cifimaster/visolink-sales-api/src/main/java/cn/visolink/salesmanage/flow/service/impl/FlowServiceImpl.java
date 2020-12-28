package cn.visolink.salesmanage.flow.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.firstplan.message.service.PricingMessageService;
import cn.visolink.firstplan.message.service.TemplateEngineService;
import cn.visolink.firstplan.openbeforeseven.service.OpenBeforeSevenDayService;
import cn.visolink.firstplan.opening.service.OpenThisDayService;
import cn.visolink.firstplan.preemptionopen.service.PreemptionOpenService;
import cn.visolink.firstplan.receipt.dao.ReceiptDao;
import cn.visolink.firstplan.skipnodeupload.service.SkipNodeUploadFileService;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.flow.pojo.WorkFlowParam;
import cn.visolink.salesmanage.flow.service.FlowService;
import cn.visolink.salesmanage.onlineretailersuse.service.OnlineretailersUseService;
import cn.visolink.salesmanage.packageanddiscount.service.PackageanddiscountService;
import cn.visolink.salesmanage.weeklymarketingplan.service.WeeklyMarketingService;
import cn.visolink.salesmanage.workflowchange.dao.WorkflowChangeDao;
import cn.visolink.salesmanage.workflowchange.service.WorkflowChangeService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.EncryptUtils;
import cn.visolink.utils.FlowUtil;
import cn.visolink.utils.HttpRequestUtilTwo;
import cn.visolink.utils.flowpojo.FlowDefined;
import cn.visolink.utils.flowpojo.FlowStateResult;
import cn.visolink.utils.flowpojo.MqDefined;
import cn.visolink.utils.flowpojo.SaveFlowRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class FlowServiceImpl implements FlowService {

    @Autowired
    private FlowUtil flowUtil;

    @Autowired
    private WorkflowDao workflow;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;
    @Autowired
    private ReceiptDao receiptDao;
    @Autowired
    private TimeLogsDao timeLogsDao;
    @Autowired
    private WorkflowChangeDao workflowChangeDao;
    @Value("${testMyApi.urlStart}")
    private String urlStart;
    @Value("${testMyApi.urlHistory}")
    private String urlHistory;
    @Value("${testMyApi.urlEnd}")
    private String urlEnd;
    //风控回调地址
    @Value("${faceSysCallback.url}")
    private String faceSysCallbackUrl;
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

    @Autowired
    private SkipNodeUploadFileService skipNodeUploadFileService;
    @Autowired
    private WeeklyMarketingService weeklyMarketingService;
    @Autowired
    private PreemptionOpenService preemptionOpenService;
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
    //消息生成服务
    @Autowired
    private TemplateEngineService templateEngineService;
    @Autowired
    private PricingMessageService pricingMessageService;
    @Autowired
    private OnlineretailersUseService onlineretailersUseService;

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody startFlowNew(Map params, String BSID, String BTID, String UserID, String BOID, String LoginKey) {

        ResultBody bobys = new ResultBody();

        //查询定调价需要的参数
        Map needMap = workflow.queryworkflow(BOID);

        if (null == needMap) {
            bobys.setCode(-1);
            bobys.setMessages("未查到流程数据");
            return bobys;
        }

        //调用参数处理的方法
        String taskId = ObjectUtil.defaultIfNull(needMap.get("taskId"),"").toString();
        WorkFlowParam flowParam = new WorkFlowParam(needMap,BTID,BOID,UserID);
        SaveFlowRequest saveFlowRequest = flowParam.getStartFlowRequest();
        //获取当前流程的flowkey
        String flowKey = saveFlowRequest.getFlowKey();
        //获取当前流程的orgCode
        boolean flag=true;
        //获取发起人所属事业部名称
        String orgName = saveFlowRequest.getOrgName();
  /*      if("MY_FYs_YsAdjustHt_SD".equals(flowKey)
                ||"MY_FYs_YsAdjustHt".equals(flowKey)){
            flag=false;
        }*/
    /*    if("MY_FYs_YsAdjustPayCaliber".equals(flowKey)){
            if("QDSYB".equals(orgName)){
                flowKey="MY_FYs_YsAdjustPayCaliber_SD";
            }
            flag=false;
        }*/
        String flow_id=needMap.get("flow_id")+"";
        List<Map> flowId = workflow.getFlowId(null);
        if(flowId!=null&&flowId.size()>0){
            for (Map map1 : flowId) {
                String DictName=map1.get("DictName")+"";
                if(DictName.contains(flow_id)){
                    orgName= DictName.substring(DictName.indexOf("-") + 1);
                    break;
                }
            }
        }


        Map flowCodeData = workflow.getFlowCodeData(flowKey);
        if(flag){
            if(flowCodeData!=null){
                //获取当前流程的code类型
                String dictType=flowCodeData.get("DictType")+"";
                String id=flowCodeData.get("id")+"";
                //获取名称
                String dictName=flowCodeData.get("DictName")+"";
                //获取code
                String dictCode=flowCodeData.get("DictCode")+"";
                System.out.println("流程名称=========="+dictName+"");
                System.out.println("流程code=========="+dictCode+"");
                //五大区域集团及山东事业部--提供明源使用
               //
                String[] codeArray={"hfsyb","SHSYB","CQSYB","SNSYB","ZJSYB","QDSYB","BJSYB","XASYB","GZSYB","zb","上海","皖赣","苏南","西南","山东","浙江","华北","西北","总部","广桂","原有事业部","10010000","10020000","10030000","10060000","10080000","10270000","10040000","10170000","10120000"};
                List<String> asList = Arrays.asList(codeArray);
                //如果明源推送的推演集团没有包含，默认为事业部
                if (!asList.contains(orgName)){
                    orgName="事业部";
                }
                //如果当前code类型为值类型
                if("1".equals(dictType)){
                    //直接发起
                    flowKey=dictCode;
                }//如果当前code类型为列表类型，查询子集
                else{
                    Map flowChildCodeData = workflow.getFlowChildCodeData(id, orgName);
                    if(flowChildCodeData!=null&&flowChildCodeData.size()>0){
                        flowKey=flowChildCodeData.get("DictCode")+"";
                    }
                }
            }

        }
        saveFlowRequest.setFlowKey(flowKey);
        return startWorkFlow(saveFlowRequest, taskId);
    }

    @Override
    public Map selectUrl(String BOID) {
        return workflow.selectUrl(BOID);
    }


    @Override
    public ResultBody transaction(Map map) {
        String isTransaction = map.get("isTransaction")+"";
        if("".equals(isTransaction) || "null".equals(isTransaction)){
            return ResultBody.error(-1,"未通知业务系统是否是关联交易");
        }else {
            workflow.updateStartworkflow(map);
        }
        Map needMap = workflow.selectBaseId(map);
        if(null==needMap){
            return ResultBody.error(-1,"未查到流程数据");
        }
        String flow_code=needMap.get("flow_code")+"";
        String orgName=needMap.get("orgName")+"";
        String flow_id=needMap.get("flow_id")+"";
        List<Map> flowId = workflow.getFlowId(null);
        if(flowId!=null&&flowId.size()>0){
            for (Map map1 : flowId) {
                String DictName=map1.get("DictName")+"";
                if(DictName.contains(flow_id)){
                    orgName= DictName.substring(DictName.indexOf("-") + 1);
                    break;
                }
            }
        }
      //
        String[] codeArray={"hfsyb","SHSYB","CQSYB","SNSYB","ZJSYB","QDSYB","BJSYB","原有事业部","XASYB","GZSYB","NNSYB","上海","皖赣","苏南","西南","山东","浙江","华北","山东","西北","广桂"};
        List<String> asList = Arrays.asList(codeArray);
        //如果明源推送的推演集团没有包含，默认为事业部
        if (!asList.contains(orgName)){
            orgName="事业部";
        }
        Map flowCodeData = workflow.getFlowCodeData(flow_code);
        Map flowChildCodeData = workflow.getFlowChildCodeData(flowCodeData.get("id") + "", orgName);
        if(flowChildCodeData!=null&&flowChildCodeData.size()>0){
            flow_code=flowChildCodeData.get("DictCode")+"";
        }
        //调用参数处理的方法
        String taskId = ObjectUtil.defaultIfNull(needMap.get("taskId"),"").toString();
        WorkFlowParam flowParam = new WorkFlowParam(needMap,true);
        SaveFlowRequest saveFlowRequest = flowParam.getStartFlowRequest();
        String comcommon=needMap.get("comcommon")+"";
        JSONObject jsonObject = JSON.parseObject(comcommon);
        Map object = JSON.toJavaObject(jsonObject, Map.class);
        System.err.println(object);
        saveFlowRequest.setFlowKey(flow_code);
        return startWorkFlow(saveFlowRequest, taskId);
    }

    private ResultBody startWorkFlow(SaveFlowRequest flowRequest, String taskId) {
        ResultBody bobys = new ResultBody();
        try {
            //获取oa的请求token
            String token = flowUtil.getToken();

            //token不为空
            if (StrUtil.isNotEmpty(token)) {

                //发起流程
                FlowStateResult result = flowUtil.saveFlow(flowRequest);
                //记录推送给OA的参数记录
                workflow.insertParamLog(JSON.toJSONString(flowRequest));
                flowRequest.setInstanceId(result.getInstId());

                //发起成功！
                if (result.isState()) {
                    //修改工作流状态
                    Map params = new HashMap();
                    params.put("instId",flowRequest.getInstanceId());
                    params.put("flowStatus",FlowDefined.OA_EVENT_TYPE_EDIT);
                    params.put("taskId",taskId);
                    params.put("businessKey",flowRequest.getBusinessKey());
                    workflow.updateStartworkflow(params);
                    String priviewUrl = flowUtil.getProviewUrl(result.getInstId(), taskId);
                    bobys.setData(priviewUrl);
                    bobys.setMessages("发起成功");
                    return bobys;
                } else {
                    System.err.println(JSON.toJSONString(flowRequest));
                    System.err.println(result.toString());
                    bobys.setMessages(result.getMessage());
                    bobys.setData(null);
                    bobys.setCode(-1);
                    return bobys;
                }
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
            bobys.setCode(-1);
            bobys.setMessages(e.getMessage());
            bobys.setData("发起失败");
        }
        return bobys;
    }


    /*
     * //定时任务--对比周预估数据
     * */
    //@Transactional(rollbackFor = Exception.class)
    @Override
    public String weeklyForecast(String params) {
        SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df  = new SimpleDateFormat("yyyy-MM-dd");
        String todayTime = dateFormat.format(new Date());
        //保存日志表
        SysLog sysLog1 = new SysLog();
        sysLog1.setStartTime(todayTime);
        sysLog1.setTaskName("对比数据");
        sysLog1.setNote("明源对比数据同步开始！！！");
        timeLogsDao.insertLogs(sysLog1);
        String tT = df.format(new Date());
        if(params==null || "".equals(params) || "null".equals(params)){
            for (int i = 0; i < 2; i++) {
                if(params!=null){
                    params += ","+tT;
                }else {
                    params = tT;
                }
            }
        }
        if(params==null){
            params="";
        }
        String[] start = params.split(",");
        //查询明源数据库中当天的全口径日报数据
        String weekSql = " SET NOCOUNT ON \n" +
                "DECLARE   @ProjGUID VARCHAR(MAX)\n" +
                "SELECT  @ProjGUID    =  ( SELECT stuff(\n" +
                "(" +
                "select  +','+   CAST(p_projectId AS NVARCHAR(50))   from p_Project where\n" +
                "p_projectId in (\n" +
                "SELECT  [p_projectId] FROM [dotnet_erp60].[dbo].[p_Project] p\n" +
                "where CAST([ParentGUID] AS NVARCHAR(50)) is not null\n" +
                // " and p.ParentCode  = 'A20180730075HZ'\n" +
                ")" +
                "for xml path('')\n" +
                "),1,1,'') as sss  ) \n" +
                "EXEC [Rpt_s_CW01SaleCollectDaily_Qkj_test2] @ProjGUID, '"+start[0]+"','"+start[1]+"' ";

        //查询明源数据库中当天的营销周计划数据
        String weekPlan = "SET NOCOUNT ON \n" +
                "DECLARE @ProjGUID VARCHAR(MAX)\n" +
                "SELECT  @ProjGUID    =  ( SELECT stuff(\n" +
                "(" +
                "select  +','+   CAST(p_projectId AS NVARCHAR(50))   from p_Project where\n" +
                "p_projectId in (\n" +
                "" +
                "SELECT  [p_projectId] FROM [dotnet_erp60].[dbo].[p_Project] p where 0=0\n" +
                "  and CAST([ParentGUID] AS NVARCHAR(50)) is   null  and p.ParentCode  = ''\n" +
                ")" +
                "for xml path('')\n" +
                "),1,1,'') as sss  ) \n" +
                "EXEC Rpt_YxWeekPlan01_Test @ProjGUID, '"+start[0]+"' ";
        //数据集合
        List<Map<String, Object>> flowOrderLists = jdbcTemplatemy.queryForList(weekSql);
        List<Map<String, Object>> weekPlanLists = jdbcTemplatemy.queryForList(weekPlan);
        //数据入库
        if(flowOrderLists!= null && flowOrderLists.size() > 0){
            for (int i = 0; i < flowOrderLists.size(); i++) {
                flowOrderLists.get(i).put("todayTime",todayTime);
            }
            workflow.insertCheckSale(flowOrderLists);
        }
        if(weekPlanLists!= null && weekPlanLists.size() > 0){
            for (int i = 0; i < weekPlanLists.size(); i++) {
                weekPlanLists.get(i).put("todayTime",todayTime);
            }
            workflow.insertWeelPlan(weekPlanLists);
        }
        //保存日志表
        sysLog1.setStartTime(todayTime);
        sysLog1.setTaskName("对比数据");
        sysLog1.setNote("明源对比数据同步结束！！！");
        timeLogsDao.insertLogs(sysLog1);
        return "成功";
    }
    @Override
    public void tOAcallback(Map map){
        try {
            String message = JSON.toJSONString(map);
            //往消息表里添加数据
            workflow.insertMessage(message);
            String eventType = map.get("eventType")+"";//状态
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
                addFlowResultInfo(map,resultStr,faceSysCallbackUrl,"风控系统-二次推送");
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
                    if ("cost".equals(isType)) {
                        result = HttpRequestUtilTwo.httpPost(adjustmentStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("地址:"+adjustmentStart);
                        addFlowResultInfo(jsonMap,result+"",adjustmentStart,"费用系统-二次推送");
                    }else {
                        result = HttpRequestUtilTwo.httpPost(salesOfficeStart, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("地址:"+salesOfficeStart);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeStart,"售楼系统-二次推送");
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
                        addFlowResultInfo(jsonMap,result.toString(),adjustmentEnd,"费用系统-二次推送");
                    }else {
                        System.err.println("接口地址:"+salesOfficeEnd);
                        result = HttpRequestUtilTwo.httpPost(salesOfficeEnd, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeEnd,"售楼系统-二次推送");
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
                        addFlowResultInfo(jsonMap,result.toString(),adjustmentHistory,"费用系统-二次推送");
                    }else {
                        result = HttpRequestUtilTwo.httpPost(salesOfficeHistory, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
                        System.err.println("地址:"+salesOfficeHistory);
                        addFlowResultInfo(jsonMap,result.toString(),salesOfficeHistory,"售楼系统-二次推送");
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
    public void addFlowResultInfo(Map paramMap,String result,String url,String point){
        Map<Object, Object> paramMaps = new HashMap<>();
        paramMaps.put("param",JSON.toJSONString(paramMap));
        paramMaps.put("result",result);
        paramMaps.put("url",url);
        paramMaps.put("point",point);
        workflowChangeDao.addFlowResultInfo(paramMaps);
    };

}
