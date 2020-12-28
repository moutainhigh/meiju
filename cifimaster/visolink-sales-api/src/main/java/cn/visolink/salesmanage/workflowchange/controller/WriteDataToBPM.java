package cn.visolink.salesmanage.workflowchange.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.workflowchange.dao.WorkflowChangeDao;
import cn.visolink.salesmanage.workflowchange.service.WorkflowChangeService;
import cn.visolink.utils.FlowUtil;
import cn.visolink.utils.flowpojo.FlowOpinion;
import cn.visolink.utils.flowpojo.FlowOpinionRes;
import com.alibaba.fastjson.JSON;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *  * @author lihuan
 *  * @since 2019-11-12
 */
@RestController
@Api(tags = "流程表单页面")
@RequestMapping("/api")
public class WriteDataToBPM {

    @Autowired
    private WorkflowChangeService service;

    @Autowired
    private WorkflowChangeDao dao;

    @Autowired
    private FlowUtil flowUtil;

    @Log("流程表单页面发起")
//    @CessBody
    @ApiOperation(value = "流程表单页面发起")
//    @PostMapping("/public/writedatatobpm")
    //提供给明源的推送数据接口，解析记录明源推送数据--->明源发起审批
    @RequestMapping(value = "/public/writedatatobpm",method = RequestMethod.POST)
    public Map workflowSend(@RequestBody Map mingyuanData){
        Map<String,Object> resultBody = new HashMap<String,Object>();
        try{
            resultBody=service.workflowSend(mingyuanData);
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.put("code","-1");
            resultBody.put("msg","发起失败");
            return resultBody;
        }
        return resultBody;
    };
    @Log("外部系统流程表单发起写入数据")
    @ApiOperation(value = "外部系统流程表单发起写入数据")
    @RequestMapping(value = "/public/writeBusinessData",method = RequestMethod.POST)
    public ResultBody writeBusinessData(@RequestBody Map paramData){
        dao.workflowParamUpdate(JSON.toJSONString(paramData));
        return service.writeBusinessData(paramData);
    };


    @Log("流程表单页面查询")
    @CessBody
    @ApiOperation(value = "流程表单页面查询")
//    @PostMapping("/public/selectdatatobpm")
    @RequestMapping(value = "/public/selectdatatobpm",method = RequestMethod.POST)
    public List<Map> workflowSelect(@RequestBody String jsonId){
        String str =  JSON.parseObject(jsonId).getString("json_id");
        List<Map> result=service.workflowSelect(str);
        return result;
    };





    @Log("交给明源的作废接口")
    @ApiOperation(value = "交给明源的作废接口")
    @RequestMapping(value = "/public/workflowEnd",method = RequestMethod.POST)
    public Map workflowEnd(@RequestBody Map mingyuanData){
        //明源原始数据留痕
        dao.workflowParamUpdate(JSON.toJSONString(mingyuanData));
        Map<String,Object> resultBody = new HashMap<String,Object>();
        try{
            resultBody=service.workflowEnd(mingyuanData);
        } catch (Exception e) {
            resultBody.put("code","-1");
            resultBody.put("msg","作废失败");
            return resultBody;
        }
        return resultBody;
    };


    @Log("合同费用打印接口")
    @ApiOperation(value = "合同费用打印接口")
    @RequestMapping(value = "/contractPrint",method = RequestMethod.POST)
    public ResultBody contractPrint(@RequestBody String jsonId){
        ResultBody boby=new ResultBody();
        try {
            String str = JSON.parseObject(jsonId).getString("json_id");
            List<Map> result = service.workflowSelect(str);
            Map info=result.get(0);
            Map res = new HashMap();
            res.put("info",info);
            FlowOpinion flowOpinions= flowUtil.flowOpinions(info.get("flow_id")+"");
            res.put("flowInfo",flowOpinions);
            boby.setData(res);
        }catch (Exception e){
            return ResultBody.error(-1,"获取信息失败！");
        }
        return boby;
    };
    @Log("获取流程实例的审批路径")
    @ApiOperation(value = "获取流程实例的审批路径")
    @RequestMapping(value = "/getFlowPath",method = RequestMethod.POST)
    public ResultBody getFlowPath(@RequestBody String jsonId){
        ResultBody boby=new ResultBody();
        try {
            String str = JSON.parseObject(jsonId).getString("json_id");
            List<Map> result = service.workflowSelect(str);
            Map info=result.get(0);
            Map res = new HashMap();
            String account=null;
            Map flowOpinions= flowUtil.flowPaths(info.get("flow_id")+"");
            if(flowOpinions!=null){
                List<Map> flowList=(List<Map>)flowOpinions.get("pathList");
                for (int i=flowList.size()-1;i>=0;i--){
                    Map userMap = flowList.get(i);
                    if(userMap!=null){
                        String nodeType=userMap.get("nodeType")+"";
                        if("userTask".equals(nodeType)){
                           List<Map> userlist= (List<Map>) userMap.get("excutor");
                           if(userlist!=null&&userlist.size()>0){
                               Map applyUsermap = userlist.get(0);
                                account=applyUsermap.get("acount")+"";
                               break;
                           }
                        }
                    }
                }
            }
            res.put("flowInfo",flowOpinions);
            res.put("account",account);
            boby.setData(res);
        }catch (Exception e){
            return ResultBody.error(-1,"获取信息失败！");
        }
        return boby;
    };


}
