package cn.visolink.firstplan.opening.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.opening.service.OpenThisDayService;
import cn.visolink.salesmanage.fileupload.service.UploadService;
import cn.visolink.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import cn.visolink.logs.aop.log.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * OpenThisDay前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2020-02-19
 */
@RestController
@Api(tags = "opening")
@RequestMapping("/opening")
public class OpeningController {

    @Autowired
    public OpenThisDayService openThisDayService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    private TakeLandService takeLandService;

    @Value("${oaflow.fpFlowCode}")
    private String fpFlowCode;

    @Log("当日播报新增OpenThisDay数据")
    @ApiOperation(value = "新增修改OpenThisDay数据")
    @PostMapping(value = "/insertOpenThisDay")
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody insertPanoramaProject(@RequestBody Map params,HttpServletRequest request) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        String username=request.getHeader("username");
        try {
            //计划节点ID都会带，创建新版本不会带
            String plan_node_id=params.get("plan_node_id")+"";
            String plan_id=params.get("plan_id")+"";
            if(plan_node_id.equals("null")||plan_node_id.equals("")||plan_node_id.length()==4){
                //计划节点ID
                String newplan_node_id=UUID.randomUUID()+"";
                //新增计划节点
                Map planNode=new HashMap();
                planNode.put("id",newplan_node_id);
                planNode.put("plan_id",plan_id);
                planNode.put("creator",username);
                planNode.put("node_level","8");
                openThisDayService.insertPlanNodeOpen(planNode);
                plan_node_id=newplan_node_id;
            }
                //计划节点ID
                Map paramData=(Map)params.get("paramData");
                paramData.put("plan_id",plan_id);
                paramData.put("plan_node_id",plan_node_id);
                String id=paramData.get("id")+"";
                if(id.equals("null") || id.equals("")|| id.length()==4){
                    id=UUID.randomUUID().toString();
                    paramData.put("id",id);
                    //写入开盘数据记录表
                    openThisDayService.insertOpenThisDay(params);
                }else{
                    //修改开盘数据记录表
                    openThisDayService.updateOpenThisDay(params);
                }
                String button=params.get("button")+"";
                //发起审批
                if(button.equals("submit") || button.equals("ks")) {
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                String jobName = null;
                if (jwtUser != null) {
                    jobName = jwtUser.getJob().get("JobName") + "";
                }
                Map flowParams = new HashMap();
                flowParams.put("json_id", plan_node_id);
                flowParams.put("project_id", params.get("project_id"));
                flowParams.put("creator", username);
                flowParams.put("flow_code",fpFlowCode);
                flowParams.put("TITLE", "首开当日播报审批表");
                flowParams.put("post_name", jobName);
                flowParams.put("orgName", "fp_open");
                Map comcommon = new HashMap();
                comcommon.put("open", "1122");
                comcommon.put("open1", "fff");
                flowParams.put("comcommon", JSONObject.toJSONString(comcommon));
                takeLandService.insertFlow(flowParams);
                    //回参
                    Map res=new HashMap();
                    res.put("BSID","FP");
                    res.put("BTID",fpFlowCode);
                    res.put("BOID",plan_node_id);
                    res.put("UserID",username);
                    res.put("LoginKey","");
                    resBoby.setResult(res);
            }
            //修改节点为当前节点
/*            Map thisNode=new HashMap();
            thisNode.put("plan_id",plan_id);
            thisNode.put("node_level","8");
            openThisDayService.updatePlanThisNodeById(thisNode);*/
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("保存出错");
        }
        return resBoby;
    }

    @Log("当日播报渲染页面接口")
    @ApiOperation(value = "当日播报渲染页面接口")
    @PostMapping(value = "/viewOpenThisDayInfo")
    public VisolinkResultBody viewOpenThisDay(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {

            String flow_status="";
            Map openThisDay=openThisDayService.selectOpenThisDayInfo(params);
            if(openThisDay!=null&&openThisDay.size()>0){
                flow_status=openThisDay.get("flow_status")+"";
            }
            if(openThisDay==null || openThisDay.size()==0){
                openThisDay=openThisDayService.getOpenPageInfo(params);
                openThisDay.put("actual_this_client",0);
                flow_status="s";
            } else if("null".equals(flow_status) ||"".equals(flow_status)|| flow_status.equals("10")||flow_status.equals("2")||flow_status.equals("5")){
                //如果是保存状态
                Map initMap=openThisDayService.getOpenPageInfo(params);
                initMap.remove("open_time");
                initMap.remove("actual_this_client");
                openThisDay.putAll(initMap);
            }
            //顶设2的产品类型
           // openThisDay.put("product_type",openThisDayService.selectProductTypeByPlanId(params.get("plan_id")+""));
            String create = params.get("create")+"";
            if(create.equals("new")){
                Map result=openThisDayService.selectLastOpeningService(params);
                openThisDay.putAll(result);
            }
            resBoby.setResult(openThisDay);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("查询出错");
        }
        return resBoby;
    }


    @Log("当日播报预览页面")
    @ApiOperation(value = "当日播报预览页面")
    @PostMapping(value = "/getOpenThisDayInfo")
    public VisolinkResultBody getOpenThisDayInfo(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            Map openThisDay=openThisDayService.selectOpenThisDayInfo(params);
            resBoby.setResult(openThisDay);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("查询出错");
        }
        return resBoby;
    }


    @Log("次日播报新增fp_open_morrow_broadcast数据")
    @ApiOperation(value = "次日播报新增fp_open_morrow_broadcast数据")
    @PostMapping(value = "/insertOpenMorrowBroadcast")
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody insertOpenMorrowBroadcast(@RequestBody Map params,HttpServletRequest request) {
        String username=request.getHeader("username");
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            Map paramData =(Map)params.get("paramData");
            String plan_node_id=params.get("plan_node_id")+"";
            String id=paramData.get("id")+"";
            if(id.equals("null") || paramData.get("id").equals("")||id.length()==4){
                paramData.put("plan_id",params.get("plan_id"));
                paramData.put("plan_node_id",plan_node_id);
                //写入开盘次日数据记录表
                //次日播报表
                id=UUID.randomUUID()+"";
                paramData.put("id",id);
                openThisDayService.insertOpenMorrowBroadcast(paramData);
            }else{
                //修改开盘数据记录表
                openThisDayService.updateOpenMorrowBroadcast(paramData);
            }
            String button=params.get("button")+"";

            //提交
            if(button.equals("submit") || button.equals("ks")){
                Map dayIsSubmit = openThisDayService.queryOpenThisDayIsSubmit(params);
                if(dayIsSubmit==null||params.size()<=0){
                    resBoby.setCode(-1002);
                    resBoby.setMessages("数据已保存!由于首开当日播报表未提报，暂无法发起次日播报表!");
                    return resBoby;
                }
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                String jobName = null;
                if (jwtUser != null) {
                    jobName = jwtUser.getJob().get("JobName") + "";
                }
                Map flowParams = new HashMap();
                flowParams.put("json_id", id);
                flowParams.put("project_id", params.get("project_id"));
                flowParams.put("creator", username);
                flowParams.put("flow_code", fpFlowCode);
                flowParams.put("TITLE", "首开次日播报审批表");
                flowParams.put("post_name", jobName);
                flowParams.put("orgName", "fp_open_morrow");
                Map comcommon = new HashMap();
                comcommon.put("open", "1122");
                comcommon.put("open1", "fff");
                flowParams.put("comcommon", JSONObject.toJSONString(comcommon));
                takeLandService.insertFlow(flowParams);
                //回参
                Map res=new HashMap();
                res.put("BSID","FP");
                res.put("BTID",fpFlowCode);
                res.put("BOID",id);
                res.put("UserID",username);
                res.put("LoginKey","");
                resBoby.setResult(res);
            }
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages(e.getMessage());
        }
        return resBoby;
    }


    @Log("次日播报初始化顶设2数据")
    @ApiOperation(value = "次日播报初始化顶设2数据")
    @PostMapping(value = "/querySevenDayIndex")
    public VisolinkResultBody querySevenDayIndex(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            //查询开盘前七天日数据记录表
            resBoby.setResult(openThisDayService.initOperMorrow(params.get("plan_id")+""));
        } catch (Exception e) {
        }
        return resBoby;
    }

    @Log("次日播报渲染页面接口")
    @ApiOperation(value = "次日播报渲染页面接口")
    @PostMapping(value = "/viewOpenMorrowDayInfo")
    public VisolinkResultBody viewOpenMorrowDayInfo(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            Map openMorrow=openThisDayService.selectOpenMorrowDayInfo(params);
            String create=params.get("create")+"";
            Map res=new HashMap();
            if(openMorrow==null || openMorrow.size()==0 || create.equals("new")){
                openMorrow=new HashMap();
                openMorrow.putAll(openThisDayService.initOperMorrow(params.get("plan_id")+""));
                resBoby.setResult(openMorrow);
            }else{
                res.put("info",openMorrow);
                res.put("version",openThisDayService.getOpenMorrowBroadcastByPlanId(params.get("plan_id")+""));
                resBoby.setResult(res);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }
/*    @Log("根据ID次日播报渲染页面接口")
    @ApiOperation(value = "根据ID次日播报渲染页面接口")
    @PostMapping(value = "/getOpenMorrowDayIByIdnfo")
    public VisolinkResultBody getOpenMorrowDayIByIdnfo(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            Map openMorrow=openThisDayService.selectOpenMorrowDayInfo(params);
            if(openMorrow==null || openMorrow.size()==0){
                openMorrow=openThisDayService.initOperMorrow(params.get("plan_id")+"");
            }else{
                openMorrow.put("version",openThisDayService.getOpenMorrowBroadcastByPlanId(params.get("plan_id")+""));
            }
            resBoby.setResult(openMorrow);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }*/


    @Log("初始化开盘客储达成进度")
    @ApiOperation(value = "初始化开盘客储达成进度")
    @PostMapping(value = "/initGuestStorage")
    public VisolinkResultBody initGuestStorage(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.initGuestStorage(params));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("选择版本开盘客储达成进度")
    @ApiOperation(value = "选择版本开盘客储达成进度")
    @PostMapping(value = "/queryGuestStorageList")
    public VisolinkResultBody queryGuestStorageList(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.selectGuestStorageFlow(params.get("id")+""));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("开盘客储达成进度暂存提交")
    @ApiOperation(value = "开盘客储达成进度暂存提交")
    @PostMapping(value = "/insertGuestStorage")
    public VisolinkResultBody insertGuestStorage(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.insertGuestStorage(params));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("开盘后一个月查询附件")
    @ApiOperation(value = "开盘后一个月查询附件")
    @PostMapping(value = "/queryOpenMonth")
    public VisolinkResultBody queryOpenMonth(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(uploadService.getFileLists(params.get("id")+""));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("开盘后一个月预览页面")
    @ApiOperation(value = "开盘后一个月预览页面")
    @PostMapping(value = "/getOpenMonth")
    public VisolinkResultBody getOpenMonth(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            String plan_node_id=params.get("plan_node_id")+"";
            Map res=new HashMap();
            res.put("info",openThisDayService.selectOpenFileFlow(plan_node_id));
            List fileList=uploadService.getFileLists(plan_node_id);
            res.put("fileList",fileList);
            resBoby.setResult(res);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("次日播报版本查询")
    @ApiOperation(value = "次日播报版本查询")
    @PostMapping(value = "/getOpenVersionById")
    public VisolinkResultBody getOpenVersionById(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.getOpenMorrowDayIByIdnfo(params.get("id")+""));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("当日播报版本")
    @ApiOperation(value = "当日播报版本")
    @PostMapping(value = "/getOpenVersionByPlanId")
    public VisolinkResultBody getOpenVersionByPlanId(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.getOpenVersionByPlanId(params.get("plan_id")+""));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    @Log("开盘后一个月查询版本")
    @ApiOperation(value = "开盘后一个月查询版本")
    @PostMapping(value = "/queryPlanNodeVersionByPlanId")
    public VisolinkResultBody queryPlanNodeVersionByPlanId(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.selectPlanNodeVersionByPlanId(params));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("开盘后一个月创建版本")
    @ApiOperation(value = "开盘后一个月创建版本")
    @PostMapping(value = "/insertOpenBack")
    public VisolinkResultBody insertOpenBack(@RequestBody Map params,HttpServletRequest request) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            String username=request.getHeader("username");
            //计划节点ID都会带，创建新版本不会带
            String plan_node_id = params.get("plan_node_id") + "";
            if (plan_node_id == null || plan_node_id.equals("") || plan_node_id.equals("null")) {
                //计划节点ID
                String id = UUID.randomUUID() + "";
                //新增计划节点
                Map planNode = new HashMap();
                planNode.put("id", id);
                planNode.put("plan_id", params.get("plan_id"));
                planNode.put("creator", username);
                planNode.put("node_level", params.get("node_level"));
                openThisDayService.insertPlanNodeOpen(planNode);
            }
        } catch(Exception e){
                resBoby.setCode(1);
                resBoby.setMessages("请求失败，请稍候再试");
        }
            return resBoby;

    }

    @Log("数据项目楼栋")
    @ApiOperation(value = "数据项目楼栋")
    @PostMapping(value = "/queryBuildingName")
    public VisolinkResultBody queryBuildingName(@RequestBody Map params) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.selectBuildingName(params.get("project_id")+""));
        } catch(Exception e){
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;

    }
    @Log("获取周拆分")
    @ApiOperation(value = "获取周拆分")
    @PostMapping(value = "/queryWeek")
    public VisolinkResultBody queryWeek(@RequestBody Map params) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            resBoby.setResult(openThisDayService.getWeekSplit(params));
        } catch(Exception e){
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("审批接口")
    @ApiOperation(value = "审批接口")
    @PostMapping(value = "/approveOpenNodeInfo")
    public VisolinkResultBody approveOpenNodeInfo(@RequestBody Map params) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            String businesskey=params.get("businesskey")+"";
            String flowKey=params.get("flowKey")+"";
            String orgName=params.get("orgName")+"";
            String plan_id=params.get("plan_id")+"";
            String obj = "{\"plan_id\":\""+plan_id+"\",\"orgName\":\""+orgName+"\",\"timestamp\":1581593325473,\"flowKey\":\""+flowKey+"\",\"instanceId\":\"" + businesskey + "\",\"eventType\":4,\"businesskey\":\"" + businesskey + "\",\"sysCode\":\"xsgl\",\"bnsParameters\":null,\"backHandMode\":null}";
            HashMap hashMap = JSON.parseObject(obj, HashMap.class);
            openThisDayService.approveOpenNodeInfo(hashMap);
            resBoby.setResult(1);
        } catch(Exception e){
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("三大件提交")
    @ApiOperation(value = "三大件提交")
    @PostMapping(value = "/subApprovePlanNode")
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody subApprovePlanNode(@RequestBody Map params,HttpServletRequest request) {
        String username=request.getHeader("username");
        VisolinkResultBody resBoby = new VisolinkResultBody();
        String plan_id=params.get("plan_id")+"";
        try {
            String plan_node_id=params.get("plan_node_id")+"";
            if(params.get("fileList")!=null){
                List fileList=(List)params.get("fileList");
                if(fileList.size()>0){
                    uploadService.updateFileBizId(params);
                }
            }
            //快速审批
            if(params.get("button")!=null && params.get("button").equals("submit") || params.get("button").equals("ks") ){
                if(params.get("node_level").equals(9)||params.get("node_level").equals("9")){
                    JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                    String jobName = null;
                    if (jwtUser != null) {
                        jobName = jwtUser.getJob().get("JobName") + "";
                    }
                    Map flowParams = new HashMap();
                    flowParams.put("json_id", plan_node_id);
                    flowParams.put("project_id", params.get("project_id"));
                    flowParams.put("creator", username);
                    flowParams.put("flow_code", fpFlowCode);
                    flowParams.put("TITLE", "首开后1月复盘审批表");
                    flowParams.put("post_name", jobName);
                    flowParams.put("orgName", "fp_open_one_month");
                    //获取分支走向
                    Map flowMove=openThisDayService.selectFlowMove(plan_id);
                    if(flowMove!=null){
                        Map comcommon = new HashMap();
                        comcommon.put("profit_all", flowMove.get("profit_all"));
                        comcommon.put("profit_value",flowMove.get("profit_value"));
                        flowParams.put("comcommon", JSONObject.toJSONString(comcommon));
                    }
                    takeLandService.insertFlow(flowParams);

                    //修改节点为当前节点
                    Map thisNode=new HashMap();
                    thisNode.put("plan_id",plan_id);
                    thisNode.put("node_level","10");
                    openThisDayService.updatePlanThisNodeById(thisNode);
                    //回参
                    Map res=new HashMap();
                    res.put("BSID","FP");
                    res.put("BTID",fpFlowCode);
                    res.put("BOID",plan_node_id);
                    res.put("UserID",username);
                    res.put("LoginKey","");
                    resBoby.setResult(res);
                }else{
                    openThisDayService.updatePlanNodeFlowApproval(plan_node_id);
                    //生成消息
                    HashMap<Object, Object> paramMap = new HashMap<>();
                    paramMap.put("plan_node_id",plan_node_id);
                    paramMap.put("plan_id",plan_id);
                    openThisDayService.generateMessage(paramMap);
                }
            }
/*            if(params.get("node_level").equals(9)||params.get("node_level").equals("9")){
                //修改节点为当前节点
                //修改节点为当前节点
                Map thisNode=new HashMap();
                thisNode.put("plan_id",plan_id);
                thisNode.put("node_level","9");
                openThisDayService.updatePlanThisNodeById(thisNode);
            }*/
        } catch(Exception e){
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }
}

