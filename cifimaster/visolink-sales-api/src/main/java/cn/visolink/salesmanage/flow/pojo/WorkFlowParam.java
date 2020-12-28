package cn.visolink.salesmanage.flow.pojo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.utils.StringUtil;
import cn.visolink.utils.flowpojo.FlowDefined;
import cn.visolink.utils.flowpojo.SaveFlowRequest;
import com.alibaba.fastjson.JSON;

import java.util.*;

public class WorkFlowParam {
    Map<String,Object> apflowInfoMap ;
    Map<String,Object> comcommonMap = new HashMap();
    String BTID;
    String BOID;
    String UserId;
    boolean isTransaction;

    /**
     * @param apflowInfoMap mm_ap_flow_info表中的记录
     * @param BTID 参数传递的BTID (流程模板ID)
     * @param BOID 参数传递的BOID (数据库中的JSON_ID,如果明源流程，就是明源id，如果是首开流程，就是首开计划节点ID)
     * @param UserID  参数传递的UserID
     */
    public WorkFlowParam(Map<String,Object> apflowInfoMap,String BTID,String BOID,String UserID){
        this.apflowInfoMap = apflowInfoMap;
        String comcommon = ObjectUtil.defaultIfNull(apflowInfoMap.get("comcommon"),"").toString();
        if(StrUtil.isNotBlank(comcommon)){
            comcommonMap  = JSON.parseObject(comcommon,Map.class);
        }
        this.BTID = BTID;
        this.BOID = BOID;
        this.UserId = UserID;
    }
    /**
     * @param apflowInfoMap mm_ap_flow_info表中的记录
     */
    public WorkFlowParam(Map<String,Object> apflowInfoMap,boolean isTransaction){
        this.apflowInfoMap = apflowInfoMap;
        String comcommon = ObjectUtil.defaultIfNull(apflowInfoMap.get("comcommon"),"").toString();
        if(StrUtil.isNotBlank(comcommon)){
            comcommonMap  = JSON.parseObject(comcommon,Map.class);
        }

        String creator = apflowInfoMap.get("creator")+"";
        String editor = apflowInfoMap.get("editor")+"";
        String UserID = "";
        if("".equals(editor) || "null".equals(editor)){
            UserID = creator;
        }else {
            UserID = editor;
        }
        this.BTID = apflowInfoMap.get("flow_code")+"";
        this.BOID = apflowInfoMap.get("json_id")+"";
        this.UserId = UserID;
        this.isTransaction = isTransaction;
    }

    /**
     * 根据apflowInfoMap等信息，获取用户提交流程的参数
     * @return
     */
    public SaveFlowRequest getStartFlowRequest(){
        SaveFlowRequest flowRequest = new SaveFlowRequest();
        flowRequest.setAccount(UserId);
        Map flowMap = getFlowKey();
        String flowKey=flowMap.get("flowKey")+"";
        String orgName=flowMap.get("orgName")+"";
        //流程Code
        flowRequest.setFlowKey(flowKey);
        //所属区域集团
       // flowRequest.setOrgCode(orgName);
        flowRequest.setOrgName(orgName);
        flowRequest.setBusinessKey(BOID);
        String startOrgCode=apflowInfoMap.get("stage_id")+"";
        if(!"".equals(startOrgCode)&&!"null".equals(startOrgCode)){
            flowRequest.setStartOrgCode(startOrgCode);
        }
        //流程Id
        flowRequest.setInstanceId(ObjectUtil.defaultIfNull(apflowInfoMap.get("flow_id"),"").toString());
        //流程标题
        flowRequest.setSubject(ObjectUtil.defaultIfNull(apflowInfoMap.get("title"),"").toString());
        Map<String, String> varsMap = this.getVarsMap();
        if(isTransaction){
            varsMap.put("isTransaction",apflowInfoMap.get("isTransaction")+"");
        }

        String BUCode=varsMap.get("BUCode")+"";
        flowRequest.setVars(varsMap);

        return  flowRequest;
    }

    private Map<String, String> getVarsMap(){
        Map<String, String> varsMap = new HashMap<>();
        varsMap.put("BTID",BTID);
        //统一指定项目ID为postOrgCode
        varsMap.put("postOrgCode",ObjectUtil.defaultIfNull(apflowInfoMap.get("stage_id"),"").toString());
        varsMap.put("startOrgCode",ObjectUtil.defaultIfNull(apflowInfoMap.get("stage_id"),"").toString());

        varsMap.put("businessDivision",ObjectUtil.defaultIfNull(apflowInfoMap.get("orgName"),"").toString());
        varsMap.put("instanceId",ObjectUtil.defaultIfNull(apflowInfoMap.get("flow_id"),"").toString());

        //定调价时，使用
        varsMap.put("markup", ObjectUtil.defaultIfNull(apflowInfoMap.get("zj"), "").toString());
        varsMap.put("wreckValue", ObjectUtil.defaultIfNull(apflowInfoMap.get("zphz_Zs"), "").toString());
        varsMap.put("singleBatch", ObjectUtil.defaultIfNull(apflowInfoMap.get("dpdj_Zs"), "").toString());
        varsMap.put("dbzg_Lr", ObjectUtil.defaultIfNull(apflowInfoMap.get("dbzg_Lr"), "").toString());
        varsMap.put("dbzg_ll", ObjectUtil.defaultIfNull(apflowInfoMap.get("dbzg_ll"), "").toString());
        varsMap.put("markup", ObjectUtil.defaultIfNull(apflowInfoMap.get("zj"), "").toString());
        if("MY_FYs_CONTRACTSP".equals(BTID) || "MY_FYs_FDDZXHTSP".equals(BTID)) {
            varsMap.put("isTransaction", "");
        }

        if(comcommonMap!=null && comcommonMap.size()>0){
            for(String varsKey : comcommonMap.keySet()){
                Object varsValue = comcommonMap.get(varsKey);
                String varsString = varsValue.toString();
                if(StrUtil.isEmpty(varsString)){
                    continue;
                }
                if(varsString.contains("{") || varsString.contains("//")  || varsString.length() > 30){
                    if(varsKey.equals("AdjustOutInfo")){
                        Map mapTiem = (Map) varsValue;
                        Set<String> key2 = mapTiem.keySet();
                        for(String ke:key2){
                            List<Map> mapke = new ArrayList<>();
                            try{
                                mapke = (List<Map>) mapTiem.get(ke);
                            }catch (Exception e){
                                Map mapT = (Map) mapTiem.get(ke);
                                mapke.add(mapT);
                            }

                            for (int i = 0; i < mapke.size(); i++) {
                                //物业管理费
                                String AdjustOutCost = mapke.get(i).get("AdjustOutCost")+"";
                                String IsYsWyProject = mapke.get(i).get("IsYsWyProject")+"";
                                if (AdjustOutCost.contains("物业服务费") && IsYsWyProject.equals("1")) {
                                    varsMap.put("businessDivision","专款专用");
                                    break;
                                }
                            }
                        }
                    }
                }else {
                    varsMap.put(varsKey,varsValue+"");
                }
            }
        }
        Map<String, String> filterMap = new HashMap<>();
        for (Map.Entry<String,String> entry: varsMap.entrySet()){
            String key = entry.getKey();
            if(key.equals("JfProviderName")){
                filterMap.put(entry.getKey(),entry.getValue());
            }
            if(!key.contains("name")&&!key.contains("Name")&&!key.contains("NAME")){
                filterMap.put(entry.getKey(),entry.getValue());
            }
        }
        return filterMap;
    }

    /**
     * 根据流程BTID和组织信息，获取流程对应的FlowKey
     * @return
     */
    private Map getFlowKey(){
        String BTID = ObjectUtil.defaultIfNull(apflowInfoMap.get("flow_code"),"").toString();
        String orgName = ObjectUtil.defaultIfNull(apflowInfoMap.get("orgName"),"").toString();

        String flowKey = BTID;


        //调整合同口径和调剂合同口径
    /*    if("MY_FYs_YsAdjustHt".equals(BTID)){
            //判断走的分支是调整还是调剂
            if(null!=comcommonMap && comcommonMap.size()!=0){
                String AdjustWay = comcommonMap.get("AdjustWay")+"";
                if("QDSYB".equals(orgName)){
                    flowKey = "MY_FYs_YsAdjustHt_SD";
                }else if("BJSYB".equals(orgName)){
                    flowKey="MY_FYs_YsAdjustPay_2020";
                }else{
                    if("单项目调整".equals(AdjustWay) || "专款专用".equals(orgName)){
                        flowKey = "MY_FYs_YsAdjustHt";
                    }else if("同科目同项目调整".equals(AdjustWay) || "同科目跨项目调整".equals(AdjustWay) || "跨科目调整".equals(AdjustWay)){
                        flowKey = "MY_FYs_YsAdjustPay";
                    }
                }
            }
        }*/
        Map<String, String> flowMap = new HashMap<>();
        flowMap.put("orgName",orgName);
        flowMap.put("flowKey",flowKey);
        return flowMap;
    }

}
