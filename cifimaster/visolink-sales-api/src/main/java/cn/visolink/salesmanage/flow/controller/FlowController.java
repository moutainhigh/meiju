package cn.visolink.salesmanage.flow.controller;


import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.flow.service.FlowService;
import cn.visolink.utils.FlowUtil;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.flowpojo.FlowDefined;
import cn.visolink.utils.flowpojo.FlowOpinion;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wyg
 */
@RestController
@RequestMapping("/Workflow")
@Api(tags = "")
public class FlowController {

    @Autowired
    private FlowService flowService;
    @Autowired
    private FlowUtil flowUtil;

    @Value("${organization.url}")
    private String organizationUrl;
    @Value("${testMyApi.SendApproveCheck}")
    private String SendApproveCheck;

    @ApiOperation(value = "")
    @GetMapping("/MTStart2.aspx")
    public ResultBody IndicatorDataExport(String BSID,String BTID,String UserID,String BOID,String LoginKey) throws IOException {
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        ResultBody resultBody = new ResultBody();
        //接收参数map
        Map map = new HashMap();
        //返回路径
        String result = null;
        if(FlowDefined.FLOWKEY_MYSL_SALES_PRICEZD.equals(BTID)){
            response.sendRedirect(organizationUrl+"BOID="+BOID+"&LoginKey="+LoginKey+"&UserID="+UserID);
        }else if(FlowDefined.FLOWKEY_MYSL_SALES_PRICEZD_EDIT.equals(BTID)){
            String btId = FlowDefined.FLOWKEY_MYSL_SALES_PRICEZD;
            //定调价发起
            resultBody = flowService.startFlowNew(map,BSID,btId,UserID,BOID,LoginKey);
        }else {
            //其他流程发起--封装发起审批所需参数，跳转url
            resultBody = flowService.startFlowNew(map,BSID,BTID,UserID,BOID,LoginKey);
        }

        String s = JSON.toJSONString(resultBody);
        System.err.println(s);
        //如果发起成功,则跳转
        if(resultBody.getData()!=null && resultBody.getCode()==200){
            //如果不是定调价和周上报的话，直接转发打开
            if (FlowDefined.FLOWKEY_MYSL_SALES_PRICEZD.equals(BTID) ||
                    FlowDefined.FLOWKEY_MYSL_SALES_PRICEZD_EDIT.equals(BTID) ||
                    FlowDefined.FLOWKEY_WEEK_AREA_REPORT.equals(BTID) ||
                    FlowDefined.FLOWKEY_FP.equals(BSID) || FlowDefined.FLOWKEY_MY_PACKAGE_STAGE.equals(BTID)
                    || FlowDefined.FLOWKEY_Plicy.equals(BTID)
            ) {
                result = resultBody.getData() + "";
                resultBody.setMessages("成功");
                resultBody.setData(result);
            } else {
                response.sendRedirect(resultBody.getData() + "");
            }
        }else {
            System.out.println("流程发起失败!失败原因:" + resultBody.getMessages());
            resultBody.setMessages("流程发起失败!失败原因:"+resultBody.getMessages());
            resultBody.setCode(-1);
        }
        return resultBody;

    }


    @ApiOperation(value = "")
    @GetMapping("/toHistory")
    public void IndicatorDataExport(String procInstId,String UserID,String key,String LoginKey){
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try {
            String flowReviewUrl = flowUtil.getFlowReviewUrl(procInstId);
            response.sendRedirect(flowReviewUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "")
    @PostMapping("/selectUrl")
    public ResultBody selectUrl(@RequestBody Map map){
        String BOID = map.get("BOID")+"";
        Map resultMap = flowService.selectUrl(BOID);
        ResultBody resultBody = new ResultBody();
        resultBody.setData(resultMap);
        return resultBody;
    }


    @Log("定时任务")
    @ApiOperation(value = "定时任务")
   // @Scheduled(cron = "0 07 20 * * ?")
    public void weeklyForecast(){
        String params = "";
        flowService.weeklyForecast(params);
    }

    @Log("定时任务--手动")
    @ApiOperation(value = "定时任务")
    @PostMapping("/weeklyForecast")
    public void weeklyForecast(String params){
        flowService.weeklyForecast(params);
    }


    @Log("供内部自己调用的查看历史审批")
    @ApiOperation(value = "")
    @PostMapping("/toHistoryUrl")
    public ResultBody toHistoryUrl(@RequestBody Map mapRes){
        ResultBody resultBody = new ResultBody();
        try {
            String procInstId = mapRes.get("proInstId").toString();
            String flowReviewUrl = flowUtil.getFlowReviewUrl(procInstId);
            resultBody.setData(flowReviewUrl);
        }catch (Exception e){
            resultBody.setData("跳转失败");
            resultBody.setCode(-1);
            e.printStackTrace();
        }
        return resultBody;
    }


    @Log("验证明源预算接口")
    @ApiOperation(value = "")
    @PostMapping("/SendApproveCheck")
    public ResultBody SendApproveCheck(@RequestBody Map textMap){
        try {
            System.out.println("地址==>" + SendApproveCheck);
            System.err.println(JSON.toJSONString("参数==>:"+textMap));
            String result = HttpClientUtil.doPost(SendApproveCheck,textMap);
            System.out.println(result);
            ResultBody resultBody = new ResultBody();
            JSONObject jb = JSON.parseObject(result);
            resultBody.setData(jb.get("Result")+"");
            resultBody.setMessages(jb.get("Message")+"");

            return resultBody;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Log("合同审批校验是否是关联交易")
    @ApiOperation(value = "")
    @PostMapping("/transaction")
    public ResultBody transaction(@RequestBody Map tranMap){
        ResultBody resultBody = flowService.transaction(tranMap);
        return resultBody;
    }


    @Log("查询OA流程实例的审批记录")
    @ApiOperation(value = "查询OA流程实例的审批记录")
    @PostMapping("/queryFlowOpinions")
    public FlowOpinion flowOpinions(@RequestBody Map tranMap){
        String flow_id=tranMap.get("id")+"";
        String flow_code=tranMap.get("flow_code")+"";
        FlowOpinion resultBody = flowUtil.flowOpinions(flow_id);
        return  resultBody;
    }

    @Log("手动回调-MQ")
    @ApiOperation(value = "手动回调-MQ")
    @PostMapping("/oAcallback")
    public ResultBody oAcallback(@RequestBody Map mqMessage){
        try {
            flowService.tOAcallback(mqMessage);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1005,e.toString());
        }
        return ResultBody.success(null) ;
    }
}
