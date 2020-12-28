package cn.visolink.salesmanage.flow.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.flow.service.FlowOtherService;
import cn.visolink.salesmanage.flow.service.PriceAdjustmentService;
import cn.visolink.salesmanage.workflowchange.service.WorkflowChangeService;
import cn.visolink.utils.FlowUtil;
import cn.visolink.utils.flowpojo.FlowOpinion;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OA审批流程的
 */
@RestController
@RequestMapping("/WorkflowChoose")
public class FlowOtherController {

    @Autowired
    private FlowOtherService service;
    @Autowired
    private PriceAdjustmentService priceAdjustmentService;
    @Autowired
    private WorkflowChangeService workflowChangeService;
    @Autowired
    private FlowUtil flowUtil;

    /**
     * 废弃流程
     * state message 返回参数
     * @param instanceId 流程实例 ID（必填）
     */
    @GetMapping("/end")
    public ResultBody doOaEndFlow(String instanceId){

        ResultBody resultBody = service.endFlow(instanceId);
        return resultBody;
    }

    /**
     * 删除流程
     * state message 返回参数
     * @param instanceId 流程实例 ID（必填）
     */
    @GetMapping("/delete")
    public ResultBody doOaDeleteFlow(String instanceId){
        ResultBody resultBody = service.deleteFlow(instanceId);
        return resultBody;
    }

    /**
     * 获取定调价编制数据
     */
    @PostMapping("/adjustment")
    public ResultBody adjustment(String BOID){
        ResultBody resultBody = new ResultBody();
        Map resultMap = priceAdjustmentService.adjustment(BOID);
        resultBody.setCode(200);
        resultBody.setMessages("数据信息");
        resultBody.setData(resultMap);
        return resultBody;
    }

    /**
     * 获取废弃时候需要的流程id
     */
    @PostMapping("/queryFlowId")
    public ResultBody queryFlowId(String BOID){
        Map resultMap = service.queryFlowId(BOID);
        ResultBody resultBody = new ResultBody();
        resultBody.setCode(200);
        resultBody.setMessages("数据信息");
        resultBody.setData(resultMap);
        return resultBody;
    }
    @Log("获取流程实例的审批人员路径")
    @ApiOperation(value = "获取流程实例的审批人员路径")
    @RequestMapping(value = "/getFlowUsersPath",method = RequestMethod.POST)
    public ResultBody getFlowPath(@RequestBody String jsonId){
        try {
            String str = JSON.parseObject(jsonId).getString("json_id");
            List<Map> result = workflowChangeService.workflowSelect(str);
            Map info=result.get(0);
            Map res = new HashMap();
            String account=null;
            Map flowOpinions= flowUtil.flowPaths(info.get("flow_id")+"");
            if(flowOpinions!=null){
                List<Map> flowList=(List<Map>)flowOpinions.get("pathList");
                return ResultBody.success(flowList);
            }else{
                return ResultBody.error(-2006,"未查询到该流程的审批人员推演");
            }
        }catch (Exception e){
            return ResultBody.error(-1,"获取信息失败:"+e.toString());
        }
    };
    @Log("获取流程实例的处理路径")
    @ApiOperation(value = "获取流程实例的处理路径")
    @RequestMapping(value = "/getFlowApprovalPath",method = RequestMethod.POST)
    public ResultBody getFlowApprovalPath(@RequestBody String jsonId){
        try {
            String str = JSON.parseObject(jsonId).getString("json_id");
            List<Map> result = workflowChangeService.workflowSelect(str);
            Map info=result.get(0);
            FlowOpinion flowOpinion = flowUtil.flowOpinions(info.get("flow_id") + "");
            if(flowOpinion.getOpinions()!=null){
                return ResultBody.success(flowOpinion.getOpinions());
            }else{
                return ResultBody.error(-2006,"未查询到该流程的审批处理数据");
            }
        }catch (Exception e){
            return ResultBody.error(-1,"获取信息失败:"+e.toString());
        }
    };



}
