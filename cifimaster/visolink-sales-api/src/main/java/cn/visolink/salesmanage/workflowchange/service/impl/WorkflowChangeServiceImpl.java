package cn.visolink.salesmanage.workflowchange.service.impl;

import cn.hutool.json.XML;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.flow.service.impl.FlowOtherImpl;
import cn.visolink.salesmanage.pricing.dao.PricingMapper;
import cn.visolink.salesmanage.pricing.service.PricingService;
import cn.visolink.salesmanage.workflowchange.dao.WorkflowChangeDao;
import cn.visolink.salesmanage.workflowchange.service.WorkflowChangeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 *  * @author lihuan,wuyaoguang
 *  * @since 2019-11-12
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class WorkflowChangeServiceImpl implements WorkflowChangeService {

    @Autowired
    private WorkflowChangeDao business;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private PricingMapper pricingMapper;

    @Autowired
    private WorkflowDao workflowDao;
    @Autowired
    private FlowOtherImpl flowOther;

    @Value("${obtainJsonID.urlOne}")
    String urlOne;
    @Value("${obtainJsonID.urlTwo}")
    String urlTwo;
    @Value("${obtainJsonID.fileUrlOld}")
    String fileUrlOld;
    @Value("${obtainJsonID.fileUrlNew}")
    String fileUrlNew;

    /**
     * 签约后变更神批发起
     *
     * @param map
     * @return
     */
    @Override
    public Map workflowSend(Map<String, Object> map) {
        //记录数据推送日志
        business.workflowParamUpdate(JSON.toJSONString(map));
        Map mapData = changeToJson(map);
        //记录明源推送数据
        business.workflowLogUpdate(mapData);
        Map resultMap = new HashMap();
        //定调价审批流程--->sjl
        if("My_Sales_PriceZD".equals(mapData.get("flow_code")))
        {
            map = pricingService.analysisMyData(map);

        }
        //其他审批-数据解析
        else{
            String json = mapData.get("json_id").toString();
            map.put("BOID",json);
            List<Map> oldMap = business.workflowSelect(map);
            if(null == oldMap || oldMap.isEmpty()){
                business.workflowSend(mapData);
                resultMap.put("code","1");
                resultMap.put("msg","发起成功");
                map.put("Result",JSON.toJSONString(resultMap));
            }else{
                String flow_status=oldMap.get(0).get("flow_status")+"";
                if("7".equals(flow_status)){
                    Map oldFlowMap = business.queryFlowDateByBaseID(json, "", "","","");
                    if(oldFlowMap!=null){
                        String oldFlow_statuss=oldFlowMap.get("flow_status")+"";
                        if(!"7".equals(oldFlow_statuss)){
                            mapData.put("json_id",oldFlowMap.get("json_id")+"");
                            mapData.put("base_id",oldFlowMap.get("base_id")+"");
                            business.workflowUpdate(mapData);
                            resultMap.put("code","1");
                            resultMap.put("msg","发起成功");
                            map.put("Result",JSON.toJSONString(resultMap));
                        }
                    }else{
                        String newJsonid = UUID.randomUUID().toString();
                        mapData.put("json_id",newJsonid);
                        mapData.put("base_id",json);
                        business.workflowSend(mapData);
                        map.put("BOID",newJsonid);
                        resultMap.put("code","1");
                        resultMap.put("msg","驳回重发起成功");
                        map.put("Result",JSON.toJSONString(resultMap));
                    }
                }else{
                    business.workflowUpdate(mapData);
                    resultMap.put("code","1");
                    resultMap.put("msg","发起成功");
                    map.put("Result",JSON.toJSONString(resultMap));
                }

            }
        }

        return  map;

    }

    /**
     *签约后变更神批表单查询
     * @param jsonId
     * @return
     */
    @Override
    public List<Map> workflowSelect(String jsonId) {
        try {

            //获取jsonID
            Map<String, Object> map = new HashMap<String, Object>();
            /*  接口配置在配置文件中
            String param1="http://sales.cifi.com.cn:9060/PubPlatform/Nav/Login/SSO/Login.aspx?UserCode=admin&Password=4076f862096d1536b6cac6866e386685&PageUrl=http%3a%2f%2fsales.cifi.com.cn%3a9060%2fapi%2fMysoft.Map6.FileManager.AppServices.FileManagerAppService";
            String param2="https://sales.cifi.com.cn/PubPlatform/Nav/Login/SSO/Login.aspx?UserCode=admin&Password=4076f862096d1536b6cac6866e386685&PageUrl=https%3a%2f%2fsales.cifi.com.cn%2fapi%2fMysoft.Map6.FileManager.AppServices.FileManagerAppService"; */
            map.put("BOID", jsonId);
            map.put("param1", urlOne);
            map.put("param2", urlTwo);
            map.put("fileUrlOld", fileUrlOld);
            map.put("fileUrlNew", fileUrlNew);
            //获取jsonID
            List<Map> mapList = business.workflowSelect(map);
            Map flowmaps = mapList.get(0);
            String flow_status = flowmaps.get("flow_status") + "";
            if ("7".equals(flow_status)) {
                Map newFlowMap = business.queryFlowDateByBaseID(jsonId, urlOne, urlTwo, fileUrlOld, fileUrlNew);
                if (newFlowMap != null) {
                    jsonId = newFlowMap.get("json_id") + "";
                    flowmaps.clear();
                    flowmaps = newFlowMap;
                }
            }
            String flow_json=flowmaps.get("flow_json")+"";
            String ApplyDate=null;
            Map flowMap = JSON.parseObject(flow_json, Map.class);
            if(flowMap!=null){
                ApplyDate=flowmaps.get("create_time")+"";
                if("".equals(ApplyDate)||"null".equals(ApplyDate)){
                    ApplyDate="";
                }
            }

            Map<Object, Object> maps = new HashMap<>();
            flowmaps.put("ApplyDate",ApplyDate);
            mapList.remove(0);
            mapList.add(flowmaps);
            return mapList;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map workflowEnd(Map<String,Object> map) {
        String instanceId = "";
        Map resultMap = new HashMap();
        try {
            //获取明源主数据id
            List list= (List)map.get("Data");
            String json_id = list.get(2).toString().trim();
            //通过主数据id查询流程id
            Map flowIdMap = business.queryFlowCode(json_id);
            if(flowIdMap==null){
                map.put("Result","因未查到流程数据，作废失败");
                String messages = JSON.toJSONString(map);
                workflowDao.insertMessage(messages);
                throw new RuntimeException("");
            }else {
                instanceId = flowIdMap.get("flow_id")+"";
                if(!"".equals(instanceId) && !"null".equals(instanceId)){
                    flowOther.endFlow(instanceId);
                    resultMap.put("code","1");
                    resultMap.put("msg","作废成功");
                    map.put("Result",JSON.toJSONString(resultMap));
                }else {
                    map.put("Result","因未识别流程id，作废失败");
                    String messages = JSON.toJSONString(map);
                    workflowDao.insertMessage(messages);
                    throw new RuntimeException("");
                }
            }
        }catch (Exception e){
            resultMap.put("code","-1");
            resultMap.put("msg","作废失败");
            map.put("Result",JSON.toJSONString(resultMap));
        }
        return map;
    }

    @Override
    public ResultBody writeBusinessData(Map map) {
        String flow_code=map.get("flow_code")+"";
        //系统标识
        String flow_type=map.get("flow_type")+"";
        //业务数据id
        String business_id=map.get("business_id")+"";
        String project_id=map.get("project_id")+"";
        String title=map.get("title")+"";
        //创建人
        String creator=map.get("creator")+"";
        //区域集团id
        String regionalGroup_id=map.get("regionalGroup_id")+"";
        try {
            if("null".equals(flow_code)||"".equals(flow_code)){
                return ResultBody.error(-1104,"流程code(flow_code)不能为空!");
            }
            if("null".equals(flow_type)||"".equals(flow_type)){
                return ResultBody.error(-1104,"系统标识(flow_type)不能为空!");
            }
            if("null".equals(business_id)||"".equals(business_id)){
                return ResultBody.error(-1104,"业务数据ID(business_id)不能为空!");
            }
            if("null".equals(project_id)||"".equals(project_id)){
                return ResultBody.error(-1104,"主数据项目ID(project_id)不能为空!");
            }
            if("null".equals(creator)||"".equals(creator)){
                return ResultBody.error(-1104,"创建人账号(creator)不能为空!");
            }
            if("null".equals(regionalGroup_id)||"".equals(regionalGroup_id)){
                return ResultBody.error(-1104,"区域集团id(regionalGroup_id)不能为空!");
            }
            if("null".equals(title)||"".equals(title)){
                return ResultBody.error(-1104,"流程标题(title)不能为空!");
            }

            Map tproject = business.getTproject(project_id);
            if(tproject==null){
                return ResultBody.error(-1104,"未查询到该项目!");
            }
            //查询项目对应的区域集团id
            String buiness_id = business.getBuiness_id(project_id);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("project_id",project_id);
            paramMap.put("flow_type",flow_type);
            paramMap.put("json_id",business_id);
            paramMap.put("flow_code",flow_code);
            paramMap.put("TITLE",title);
            paramMap.put("BUCode",buiness_id);
            paramMap.put("creator",creator);
            Map buiness_data = (Map) map.get("buiness_data");
            if(buiness_data!=null){
                String toJSONString = JSON.toJSONString(buiness_data);
                paramMap.put("comcommon",toJSONString);
                paramMap.put("flow_json",toJSONString);
            }
            paramMap.put("stage_id",project_id);
            map.put("BOID",business_id);
            List<Map> oldMap = business.workflowSelect(map);
            if(oldMap!=null&&oldMap.size()>0){
                //修改
                business.workflowUpdate(paramMap);
            }else{
                //新增
                business.workflowSend(paramMap);
            }
            return ResultBody.success(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1107,"系统错误数据写入失败,请联系管理员!");
        }
    }
    /**
     * 明源json，xml数据转化
     * @param mingyuanData
     * @return
     */
    public Map<String,Object> changeToJson(Map<String,Object> mingyuanData){
        JSONObject json =  new JSONObject();
        List list= (List)mingyuanData.get("Data");
        String BSID = list.get(0).toString().trim();
        String BTID = list.get(1).toString().trim();
        String BOID = list.get(2).toString().trim();
        String BSXML  = list.get(3).toString().trim();
        String creator = list.get(5).toString().trim();
        cn.hutool.json.JSONObject xmlJSONObj = XML.toJSONObject(BSXML,true);
        String result = xmlJSONObj.getStr("DATA").trim();
        JSONObject jb = JSON.parseObject(result);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("flow_type",BSID);
        map.put("flow_code",BTID);
        map.put("json_id",BOID);
        map.put("flow_json",result);
        map.put("creator",creator);

        if(!"My_Sales_PriceZD".equals(BTID)){
            map.putAll(thirteenChanges(jb,BTID));
        }

        return map;
    }

    //13个变更产生的参数
    public Map thirteenChanges(JSONObject jb,String BTID){
        Map map = new HashMap();
        Map<Object, Object> parameterMap = new HashMap<>();
        //区域code
        String BUCode = "";
        if(jb.get("BUCode")!=null && jb.get("BUCode")+""!="" && jb.get("BUCode")+""!="null"){
            BUCode = jb.get("BUCode")+"";
        }else if(jb.get("BUCODE")!=null && jb.get("BUCODE")+""!="" && jb.get("BUCODE")+""!="null"){
            BUCode = jb.get("BUCODE")+"";
        }
        //流程标题
        String TITLE = "";
        if(jb.get("TITLE")!=null && jb.get("TITLE")+""!="" && jb.get("TITLE")+""!="null"){
            TITLE = jb.get("TITLE")+"";
        }else if(jb.get("Subject")!=null && jb.get("Subject")+""!="" && jb.get("Subject")+""!="null"){
            TITLE = jb.get("Subject")+"";
        }else if("MY_FYs_FDDZXHTSP".equals(BTID)){
            String jbr = jb.get("Jbr")+"";
            String ContractName = jb.get("ContractName")+"";
            TITLE = "【营销补充合同流程】-"+jbr+"-"+ContractName;
        }else if("My_Sales_Payment".equals(BTID)){
            TITLE = jb.get("x_PayTitle")+"";
        }
        //是否是直系亲属更名
        String AlterType = "";
        if(jb.get("AlterType")!=null && jb.get("AlterType")+""!="" && jb.get("AlterType")+""!="null"){
            AlterType = jb.get("AlterType")+"";
            parameterMap.put("AlterType", AlterType);
        }

        //项目id参数，用于查询项目id使用
        String project_id =  "";
        if(jb.get("KeyProjGUID")!=null && jb.get("KeyProjGUID")+""!="" && jb.get("KeyProjGUID")+""!="null"){
            String KeyProjGUID = jb.get("KeyProjGUID")+"";
            Map mapPro = new HashMap();
            mapPro.put("KeyProjGUID",KeyProjGUID);
            List<Map> projectData = workflowDao.getProjectData(mapPro);
            if (projectData.size()==1) {
                project_id = projectData.get(0).get("project_id") + "";
                map.put("project_id", project_id);
                map.put("stage_id", project_id);
            }
        }
        //如果项目id为空，同时ProjGUID存在的话。使用ProjGUID继续查询项目id
        boolean projGuid = ("".equals(project_id) || "null".equals(project_id)) && jb.get("ProjGUID")!=null
                && jb.get("ProjGUID")+""!="" && jb.get("ProjGUID")+""!="null";
        if(projGuid){
            String myProjectId = jb.get("ProjGUID")+"";
            Map mapPro = new HashMap();
            mapPro.put("myProjectId",myProjectId);
            List<Map> projectData = workflowDao.getProjectData(mapPro);
            if (projectData.size()==1) {
                project_id = projectData.get(0).get("project_id") + "";
                map.put("project_id", project_id);
                map.put("stage_id", project_id);
            }
        }
        //逻辑同上
        boolean projectGuid = ("".equals(project_id) || "null".equals(project_id)) && jb.get("ProjectGUID")!=null
                && jb.get("ProjectGUID")+""!="" && jb.get("ProjectGUID")+""!="null";
        if(projectGuid){
            String myProjectId = jb.get("ProjectGUID")+"";
            Map mapPro = new HashMap();
            mapPro.put("myProjectId",myProjectId);
            List<Map> projectData = workflowDao.getProjectData(mapPro);
            if (projectData.size()==1) {
                project_id = projectData.get(0).get("project_id") + "";
                map.put("project_id", project_id);
                map.put("stage_id", project_id);
            }
        }
        //逻辑同上
        boolean kdProjCode = ("".equals(project_id) || "null".equals(project_id)) && jb.get("KdProjCode")!=null
                && jb.get("KdProjCode")+""!="" && jb.get("KdProjCode")+""!="null";
        if(kdProjCode){
            String project_code = jb.get("KdProjCode")+"";
            Map mapPro = new HashMap();
            mapPro.put("project_code",project_code);
            List<Map> projectData = workflowDao.getProjectData(mapPro);
            if (projectData.size()==1) {
                project_id = projectData.get(0).get("project_id") + "";
                map.put("project_id", project_id);
                map.put("stage_id", project_id);
            }
        }
        //如以上操作未查到项目id，则将项目id相关参数置空
        if("".equals(project_id) || "null".equals(project_id)){
            map.put("project_id", null);
            map.put("stage_id", null);
        }

        //将所有的参数接收、解析并且集中放在 cocommon中
        if(jb.size()>0){
            Set<String> keys = jb.keySet();
            for(String key:keys){
                parameterMap.put(key,jb.get(key));
            }

        }

        map.put("BUCode",BUCode);
        map.put("TITLE",TITLE);
        if(parameterMap.size()>0 && parameterMap!=null){
            map.put("comcommon", JSON.toJSONString(parameterMap));
        }


        return map;
    }

}
