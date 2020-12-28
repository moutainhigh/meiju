package cn.visolink.salesmanage.packageanddiscount.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.exception.BadRequestException;
import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.packageanddiscount.service.PackageanddiscountService;
import com.alibaba.fastjson.JSON;
import cn.visolink.salesmanage.packageanddiscount.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author sjl
 * @Created date 2020/3/25 11:20 上午
 */
@Service
@Transactional
public class PackageanddiscountServiceImpl implements PackageanddiscountService {

    @Autowired
    private PackageanddiscountDao packageanddiscountDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    private WorkflowDao workflowDao;
    @Value("${syncStageData.url}")
    private String syncStageDataUrl;
    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    /*
    初始化一揽子分期/折扣数据
     */
    @Override
    public VisolinkResultBody viewPackageDiscount(Map map, HttpServletRequest request) {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        Map<Object, Object> resultMap = new HashMap<>();
        String json_id = map.get("json_id") + "";
        String project_id = map.get("project_id") + "";
        //查询当前登陆人id
        String useId = request.getHeader("userid");

        String username = request.getHeader("username");
      /*  if ("".equals(useId) || "null".equals(useId) || useId == null) {
            response.setCode(400);
            response.setMessages("未获取到当前登陆人ID");
            return response;
        }
*/


        try {
            //如果本系统已存在本次审批
            if (!"".equals(json_id) && !"null".equals(json_id)) {
                Map flowStatusMap = packageanddiscountDao.getFlowStatus(json_id);
                String flow_status = flowStatusMap.get("flow_status") + "";
                String comcommon = flowStatusMap.get("comcommon") + "";
                Map comcommonMap = new HashMap();
                if (!"".equals(comcommon) && !"null".equals(comcommon)) {
                    Map parseObject = JSON.parseObject(comcommon, Map.class);
                    String isApplyDis = parseObject.get("isApplyDis") + "";
                    String isApplyStage = parseObject.get("isApplyStage") + "";
                    if ("1".equals(isApplyDis)) {
                        comcommonMap.put("isShowDis", 1);
                    } else {
                        comcommonMap.put("isShowDis", 0);
                    }
                    if ("1".equals(isApplyStage)) {
                        comcommonMap.put("isShowStage", 1);
                    } else {
                        comcommonMap.put("isShowStage", 0);
                    }
                } else {
                    comcommonMap = new HashMap();
                    comcommonMap.put("isShowStage", 1);
                    comcommonMap.put("isShowDis", 1);
                }
                boolean findFlag = false;
                // || "3".equals(flow_status)
                if ("1".equals(flow_status) || "3".equals(flow_status) || "2".equals(flow_status) || "5".equals(flow_status) || "6".equals(flow_status)) {
                    findFlag = true;
                    //编织页面
                }
                //渲染旧数据
                //获取一揽子分期/折扣主数据
                Map packageStageMainData = packageanddiscountDao.getPackageStageMainData(json_id);
                Map<Object, Object> projectMap = new HashMap<>();

                if (packageStageMainData != null) {
                    //获取楼栋数据
                    List<Map> buildData = packageanddiscountDao.getBuildDataByProject(project_id);
                    resultMap.put("buildData", buildData);
                }else{
                    packageStageMainData = new HashMap();
                }
                packageStageMainData.putAll(comcommonMap);
                resultMap.put("stageMainData", packageStageMainData);
                List<Map> stageOldDataApplay = packageanddiscountDao.getStageOldAndNewData(json_id, "3");
                if (stageOldDataApplay != null && stageOldDataApplay.size() > 0) {
                    //查询一揽子分期原有明细数据
                    resultMap.put("stageItemDataOld", stageOldDataApplay);
                } else {
                    if (findFlag) {
                        //查询一揽子分期原有明细数据
                        List<Map> stageDataOld = packageanddiscountDao.getStageOriginalItemData(project_id);
                        List<Map> stageDataOlds = countSoldNum(stageDataOld, findFlag);
                        resultMap.put("stageItemDataOld", stageDataOlds);
                    }

                }

                //查询一揽子分期新增明细数据
                List<Map> stageDataNew = packageanddiscountDao.getStageOldAndNewData(json_id, "2");
                resultMap.put("stageItemDataNew", stageDataNew);

                List<Map> disOldDataAppay = packageanddiscountDao.getDisOldAndNewData(json_id, "3");
                if (disOldDataAppay != null && disOldDataAppay.size() > 0) {
                    resultMap.put("disItemDataOld", disOldDataAppay);
                } else {
                    if (findFlag) {
                        //查询一揽子折扣原有明细数据
                        List<Map> disItemDataOld = packageanddiscountDao.getDisOriginalItemData(project_id);
                        List<Map> disItemDataOlds = countSoldNum(disItemDataOld, findFlag);
                        resultMap.put("disItemDataOld", disItemDataOlds);
                    }
                }
                //查询一揽子折扣新增明细数据
                List<Map> disNewData = packageanddiscountDao.getDisOldAndNewData(json_id, "2");
                resultMap.put("disItemDataNew", disNewData);
                //查询附件列表数据
                List fileLists = fileDao.getFileLists(json_id);
                if("4".equals(flow_status)||"3".equals(flow_status)){
                    String flow_json = flowStatusMap.get("flow_json")+"";
                    Map parseMap = JSON.parseObject(flow_json, Map.class);
                    if(parseMap!=null&&parseMap.size()>0){
                        List<Map> stageItemDataNew = (List<Map>) parseMap.get("stageItemDataNew");
                        List<Map> disItemDataNew = (List<Map>) parseMap.get("disItemDataNew");
                        List<Map> stageItemDataOld = (List<Map>) parseMap.get("stageItemDataOld");
                        List<Map> disItemDataOld = (List<Map>) parseMap.get("disItemDataOld");
                        //新增一揽子分期数据
                        resultMap.put("stageItemDataNew", stageItemDataNew);
                        //原有一揽子分期数据
                        resultMap.put("stageItemDataOld", stageItemDataOld);
                        //新增一揽子折扣数据
                        resultMap.put("disItemDataNew", disItemDataNew);
                        //原有一揽子折扣数据
                        resultMap.put("disItemDataOld", disItemDataOld);
                    }
                }
                resultMap.put("fileList", fileLists);
            }
            /**
             * 初始化发起审批数据
             */
            else {
                Map userInfo = packageanddiscountDao.getUserInfo(useId);
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                if (jwtUser != null) {
                    String jobName = jwtUser.getJob().get("JobName") + "";
                    userInfo.put("department_name", jobName);
                }
                userInfo.put("rules_value", 0);
                userInfo.put("rules_margin", 0);
                userInfo.put("rules_per", 0);
                userInfo.put("dynamic_value", 0);
                userInfo.put("dynamic_margin", 0);
                userInfo.put("dynamic_per", 0);
                userInfo.put("realization_value", 0);
                userInfo.put("realization_margin", 0);
                userInfo.put("realization_per", 0);
                userInfo.put("isShowStage", 0);
                userInfo.put("isShowDis", 0);
                resultMap.put("stageMainData", userInfo);
                //获取原有分期政策明细数据
                List<Map> stageDataOld = packageanddiscountDao.getStageOriginalItemData(project_id);
                List<Map> stageDataOlds = countSoldNum(stageDataOld, false);
                //原有分期政策明细
                resultMap.put("stageItemDataOld", stageDataOlds);
                //获取原有折扣政策明细数据
                List<Map> disItemDataOld = packageanddiscountDao.getDisOriginalItemData(project_id);

                //计算已使用套数和剩余套数
                List<Map> disItemDataOlds = countSoldNum(disItemDataOld, false);
                //原有折扣政策明细
                resultMap.put("disItemDataOld", disItemDataOlds);
                //获取楼栋数据
                List<Map> buildData = packageanddiscountDao.getBuildDataByProject(project_id);
                resultMap.put("buildData", buildData);

            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessages("数据渲染失败!");
            response.setCode(400);
            return response;
        }
        response.setCode(200);
        response.setResult(resultMap);
        return response;
    }

    @Override
    public VisolinkResultBody getBuildDataByProjectId(Map map) {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        Map<Object, Object> resultMap = new HashMap<>();
        Map<Object, Object> flowMap = new HashMap<>();
        String project_id = map.get("project_id") + "";
        String jsonid = map.get("json_id") + "";
        if ("".equals(jsonid) || "null".equals(jsonid)) {
            jsonid = "";
        }
        //查询当前项目下有没有已经存在的流程
        Map isHaveApplay = packageanddiscountDao.getIsHaveApplay(project_id);
        if (isHaveApplay != null) {
            String ishave = isHaveApplay.get("json_id") + "";
            if (!jsonid.equals(ishave)) {
                response.setCode(0);
                response.setMessages("当前项目已存在审批中/编制中的申请!");
                return response;
            }
        }
        //查询项目是否已通过
       /* Map shenpiTongGuo = packageanddiscountDao.getShenpiTongGuo(jsonid);
        if(shenpiTongGuo!=null&&shenpiTongGuo.size()>0){
            response.setCode(0);
            response.setMessages("当前流程已经启用流程,无法获取楼栋数据!");
            return response;
        }*/
        flowMap.put("project_id", project_id);
        if ("".equals(project_id) || "null".equals(project_id)) {
            response.setCode(400);
            response.setMessages("未获取到项目ID,查询项目相关数据失败,请选择项目层级发起政策申请!");
            return response;
        }
        List<Map> buildData = packageanddiscountDao.getBuildDataByProject(project_id);
        //获取原有分期政策明细数据
        List<Map> stageDataOld = packageanddiscountDao.getStageOriginalItemData(project_id);
        List<Map> stageDataOlds = countSoldNum(stageDataOld, false);
        //原有分期政策明细
        resultMap.put("stageItemDataOld", stageDataOlds);
        //获取原有折扣政策明细数据
        List<Map> disItemDataOld = packageanddiscountDao.getDisOriginalItemData(project_id);

        //计算已使用套数和剩余套数
        List<Map> disItemDataOlds = countSoldNum(disItemDataOld, false);
        //原有折扣政策明细
        resultMap.put("disItemDataOld", disItemDataOlds);
        resultMap.put("buildData", buildData);
        response.setResult(resultMap);
        response.setMessages("楼栋数据获取成功");
        return response;
    }

    @Override
    public VisolinkResultBody savePackageDiscount(Map map, HttpServletRequest request) {
        //记录请求日志
        packageanddiscountDao.insertParamLog("一揽子分期折扣数据保存/提交:" + JSON.toJSONString(map));
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        Map<Object, Object> resultMap = new HashMap<>();
        //查询登陆人userId
        Map<Object, Object> flowMap = new HashMap<>();
        String userid = request.getHeader("userid");
        String button = map.get("button") + "";
        String messageStr = "保存";
        String dataid = UUID.randomUUID().toString();
        Map<Object, Object> stagePolicyMap = new HashMap<>();
        Map<Object, Object> stageDayMap = new HashMap<>();
        List<Integer> stagePolicyList = new ArrayList<>();
        List<String> oldList = new ArrayList<>();
        //获取主数据
        Map stageMainData = (Map) map.get("stageMainData");
        stageMainData.put("job_id",request.getHeader("jobid"));
        stageMainData.put("job_org_id",request.getHeader("joborgid"));
        stageMainData.put("org_id",request.getHeader("orgid"));
        stageMainData.put("org_level",request.getHeader("orglevel"));
        String project_id = stageMainData.get("project_id") + "";

        boolean saveFlag = false;
        String id = stageMainData.get("id") + "";
        String json_id;
        try {
            if (!"".equals(id) && !"null".equals(id)) {
                json_id = id;
            } else {
                json_id = dataid;
            }
            //查询当前项目下是否存在审批中的申请
            Map isHaveApplay = packageanddiscountDao.getIsHaveApplay(project_id);
            if (isHaveApplay != null && isHaveApplay.size() > 0) {
                String ishave = isHaveApplay.get("json_id") + "";
                if (!ishave.equals(json_id)) {
                    response.setMessages("当前项目下已存在申请,请切换其他项目发起申请!");
                    response.setCode(0);
                    return response;
                }
            }
            if ("submit".equals(button)) {
                saveFlag = true;
                messageStr = "提交";
                //发起流程--todo对接oA
                flowMap.put("flow_status", "1");
            } else {
                //发起流程--todo对接oA
                flowMap.put("flow_status", "2");
            }
            if (!"".equals(id) && !"null".equals(id)) {
                //修改主数据
                stageMainData.put("editor", userid);
                stageMainData.put("id", json_id);
                /*修改政策主表 (bql 2020.07.20)*/
                packageanddiscountDao.updateCmPlicySales(stageMainData);
                //修改主数据
                packageanddiscountDao.updatePackageStageMainData(stageMainData);
            } else {
                stageMainData.put("creator", userid);
                stageMainData.put("id", json_id);
                /*将销售政策数据添加到政策主表 (bql 2020.07.17)*/
                packageanddiscountDao.insertCmPolicySales(stageMainData);
                /*添加政策权限中间表 (bql 2020.07.20)*/
                packageanddiscountDao.insertPolicyOrgRel(stageMainData);
                //新增主数据
                packageanddiscountDao.insertPackageStageMainData(stageMainData);
            }


            //删除附属数据--分期明细数据和折扣明细数据--删除流程
            packageanddiscountDao.clearSubsidiaryData(json_id);
            //更新附属数据--分期原明细数据
            List<Map> stageItemDataOld = (List<Map>) map.get("stageItemDataOld");
            if (stageItemDataOld != null && stageItemDataOld.size() > 0) {
                for (Map stageOldMap : stageItemDataOld) {
                    String ids = stageOldMap.get("id") + "";
                    String append_num = stageOldMap.get("append_num") + "";
                    //如果追加了套数，追加分支条件
                    if (!"0".equals(append_num) && !"".equals(append_num) && !"null".equals(append_num)) {
                        getStagePolicy(stageOldMap, stagePolicyList);
                    }
                    /*if(!saveFlag){
                        stageOldMap.put("append_num",0);
                    }*/
                    stageOldMap.put("type", 1);
                    stageOldMap.put("project_id", project_id);
                    //stageOldMap.put("package_stages_id", json_id);
                    stageOldMap.put("policy_effective_time", stageOldMap.get("policy_effective_time") + "-01");
                    if (!"".equals(ids) && !"null".equals(ids)) {
                        oldList.add(ids);
                        packageanddiscountDao.updateStageItem(stageOldMap);
                    } else {
                        String toString = UUID.randomUUID().toString();
                        stageOldMap.put("id", toString);
                        stageOldMap.put("package_stages_id", json_id);
                        Map filterMap = filterMap(stageOldMap);
                        oldList.add(toString);
                        packageanddiscountDao.insertStageItem(filterMap);
                    }
                }
            }

            //清空当前流程的新增分期明细数据
            packageanddiscountDao.clearAddStageItemData(json_id);
            //添加新分期明细数据
            List<Map> stageItemDataNew = (List<Map>) map.get("stageItemDataNew");
            if (stageItemDataNew != null && stageItemDataNew.size() > 0) {
                for (Map stageNewMap : stageItemDataNew) {
                    String ids = stageNewMap.get("id") + "";
                    String policy_num = stageNewMap.get("policy_num") + "";
                    if (!"null".equals(policy_num) && !"".equals(policy_num)) {
                        int parseInt = Integer.parseInt(policy_num);
                        stageNewMap.put("policy_sum_num", parseInt);
                    }
                    //追加分支条件
                    getStagePolicy(stageNewMap, stagePolicyList);
                   /* if(!flagAdd){
                        if("全款分期".equals(stage_policy_type)&&"全款分期>6个月".equals(stage_time_type)){
                            flagAdd=true;
                            stageDayMap.put("StageRange","1");
                            stagePolicyMap.put("5",stageDayMap);
                            stagePolicyList.add(5);
                        }
                    }*/

                    stageNewMap.put("type", 2);
                    stageNewMap.put("project_id", project_id);
                    stageNewMap.put("package_stages_id", json_id);

                    stageNewMap.put("policy_effective_time", stageNewMap.get("policy_effective_time") + "-01");
                    stageNewMap.put("id", UUID.randomUUID().toString());

                    Map filterMap = filterMap(stageNewMap);
                    packageanddiscountDao.insertStageItem(filterMap);
                }
            }

            //添加原折扣数据
            List<Map> disItemDataOld = (List<Map>) map.get("disItemDataOld");
            if (disItemDataOld != null && disItemDataOld.size() > 0) {
                for (Map disItemOldMap : disItemDataOld) {
                    String ids = disItemOldMap.get("id") + "";
                   /* if(!saveFlag){
                        disItemOldMap.put("append_num",0);
                    }*/
                    disItemOldMap.put("type", 1);
                    disItemOldMap.put("project_id", project_id);
                    //
                    disItemOldMap.put("policy_effective_time", disItemOldMap.get("policy_effective_time") + "-01");
                    if (!"".equals(ids) && !"null".equals(ids)) {
                        oldList.add(ids);
                        packageanddiscountDao.updateStageDisItem(disItemOldMap);
                    } else {
                        String toString = UUID.randomUUID().toString();
                        disItemOldMap.put("id", toString);
                        oldList.add(toString);
                        disItemOldMap.put("package_stages_id", json_id);
                        Map filterMap = filterMap(disItemOldMap);
                        packageanddiscountDao.insertStageDisItem(filterMap);
                    }
                }
            }
            //清空当前流程的新增折扣数据
            packageanddiscountDao.clearAddDisItemData(json_id);
            //添加新增折扣数据
            List<Map> disItemDataNew = (List<Map>) map.get("disItemDataNew");
            if (disItemDataNew != null && disItemDataNew.size() > 0) {
                for (Map disItemNewMap : disItemDataNew) {
                    String ids = disItemNewMap.get("id") + "";
                    String policy_num = disItemNewMap.get("policy_num") + "";
                    if (!"null".equals(policy_num) && !"".equals(policy_num)) {
                        int parseInt = Integer.parseInt(policy_num);
                        disItemNewMap.put("policy_sum_num", parseInt);
                    }
                    disItemNewMap.put("type", 2);
                    disItemNewMap.put("project_id", project_id);
                    disItemNewMap.put("package_stage_id", json_id);
                    disItemNewMap.put("policy_effective_time", disItemNewMap.get("policy_effective_time") + "-01");
                    disItemNewMap.put("id", UUID.randomUUID().toString());
                    Map filterMap = filterMap(disItemNewMap);
                    packageanddiscountDao.insertStageDisItem(filterMap);
                }
            }

            //更新附件数据
            List<Map> fileList = (List<Map>) map.get("fileList");
            if (fileList != null && fileList.size() > 0) {
                for (Map fileMap : fileList) {
                    fileMap.put("bizID", json_id);
                    fileDao.updateFileBizID(fileMap);
                }
            }

            //如果为提交审批，对接工作流
            //build_id
            flowMap.put("id", UUID.randomUUID().toString());
            flowMap.put("json_id", json_id);
            flowMap.put("zddate", new Date());
            flowMap.put("post_name", stageMainData.get("department_name"));
            flowMap.put("creator", request.getHeader("username"));
            flowMap.put("isdel", 0);
            flowMap.put("flow_code", "My_Package_Stage");
            flowMap.put("flow_type", "My_Sales");
            flowMap.put("title", stageMainData.get("item_name"));
            map.put("oldList", oldList);
            flowMap.put("flow_json", JSON.toJSONString(map));
            flowMap.put("project_id", project_id);
            flowMap.put("stage_id", project_id);

            Map<Object, Object> flowMaps = new HashMap<>();
            //如果没有发起审批-就不计算流程分支
            if ("submit".equals(button)) {
                List<Integer> orderIndexList = new ArrayList<>();
                List<Map> flowWhereList = new ArrayList<>();
                if (disItemDataNew != null && disItemDataNew.size() > 0) {
                    for (Map disItemMap : disItemDataNew) {
                        //判断流程分支
                        this.getFlowWhere(disItemMap.get("policy_type") + "", orderIndexList, flowWhereList);
                    }
                }


                String flowwhere = "";
                if (orderIndexList != null && orderIndexList.size() > 0) {
                    //获取折扣%足最大的
                    Integer max = Collections.max(orderIndexList);
                    for (Map flowWhere : flowWhereList) {
                        if (flowWhere.containsKey(max + "")) {
                            flowwhere = flowWhere.get(max + "") + "";
                        }
                    }
                }
                //查询事业部
                Map buinessData = packageanddiscountDao.getBuinessData(project_id);
                String business_unit_id = buinessData.get("business_unit_id") + "";
                //获取对应事业部名称
                getOrgname(business_unit_id, flowMap);
                flowMaps.put("policy_type", flowwhere);
                flowMaps.put("policy_type_info", stageMainData.get("policy_type"));

            }
            String isApplyDiss = map.get("isApplyDis") + "";
            String isApplyStages = map.get("isApplyStage") + "";
            if ("1".equals(isApplyDiss)) {
                flowMaps.put("isApplyDis", 1);
            } else {
                flowMaps.put("isApplyDis", 0);
            }
            if ("1".equals(isApplyStages)) {
                flowMaps.put("isApplyStage", 1);
            } else {
                flowMaps.put("isApplyStage", 0);
            }
            if (stagePolicyList != null && stagePolicyList.size() > 0) {
                Integer maxNumber = Collections.max(stagePolicyList);
                if (maxNumber != null) {
                    if (maxNumber == 5) {
                        flowMaps.put("StageRange", "1");
                    } else {
                        flowMaps.put("StageRange", "0");
                    }
                }
            }
            flowMap.put("comcommon", JSON.toJSONString(flowMaps));
                //查询是否已存在流程
                Map flowData = packageanddiscountDao.getFlowData(json_id);
                if (flowData != null && flowData.size() > 0) {
                    flowMap.put("id", flowData.get("id") + "");
                    packageanddiscountDao.updateFlowData(flowMap);
                } else {
                    //添加流程数据
                    packageanddiscountDao.insertFlowData(flowMap);
                }
            resultMap.put("BTID", "My_Package_Stage");
            resultMap.put("BOID", json_id);
            resultMap.put("UserID", request.getHeader("username"));
            resultMap.put("LoginKey", "");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            response.setCode(400);
            response.setMessages(messageStr + "失败");
            return response;
        }
        response.setMessages(messageStr + "成功");
        response.setResult(resultMap);
        return response;
    }

    @Override
    public VisolinkResultBody getApplayList(Map map, HttpServletRequest request) {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        try {
            Map<Object, Object> resultMap = new HashMap<>();

            String username = request.getHeader("username");
            map.put("username", username);
            //获取分页数据
            int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
            int pageSize = Integer.parseInt(map.get("pageSize").toString());
            String time = map.get("startTime") + "";
            if (!"".equals(time) && !"null".equals(time)) {
                String[] replace = time.replace("[", "").replace("]", "").split(",");
                map.put("startTime", replace[0]);
                map.put("endTime", replace[1]);
            }
            int i = (pageIndex - 1) * pageSize;
            map.put("pageIndex", i);
            /*List<Map> applayList = packageanddiscountDao.getApplayList(map);*/
            /*Integer applayListCount = packageanddiscountDao.getApplayListCount(map);*/
            /*销售政策查询调用新方法加权限 bql 2020.07.21*/
            List<Map> applayList = packageanddiscountDao.getApplayPolicyList(map);
            Integer applayListCount = packageanddiscountDao.getApplayPolicyListCount(map);
            resultMap.put("applayList", applayList);
            resultMap.put("applayCount", applayListCount);
            response.setMessages("查询成功");
            response.setResult(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessages("查询失败");
            response.setCode(400);
        }
        return response;
    }


    public List<Map> countSoldNum(List<Map> oldList, boolean findFlag) {
        int policy_num = 0;
        int choose_num = 0;

        if (oldList != null && oldList.size() > 0) {

            String ids = oldList.stream().map(map -> "'"+map.get("id")+"'").collect(Collectors.joining(","));
            String sql = "select *from VS_XSGL_POLICY where SaleChgPolicySetGUID in (" + ids + ") and x_isEnable=1";
            System.err.println(ids);
            List<Map<String, Object>> mapList = jdbcTemplatemy.queryForList(sql);

            for (Map buildDatum : oldList) {
                String id = buildDatum.get("id") + "";
                policy_num = Integer.parseInt(buildDatum.get("policy_num") + "");
                choose_num = Integer.parseInt(buildDatum.get("choose_num") + "");
                buildDatum.put("surplus_num", policy_num - choose_num);
                if (!findFlag) {
                    buildDatum.remove("append_num");
                }
                Map<String, Object> numberMap = null;
                for (int i = 0; i < mapList.size(); i++) {
                    String salechgpolicysetguid = mapList.get(i).get("salechgpolicysetguid")+"";
                    System.err.println(id);
                    System.err.println(salechgpolicysetguid);
                    if(salechgpolicysetguid.equalsIgnoreCase(id)){
                        numberMap = mapList.get(i);
                    }
                }
                if (mapList != null && mapList.size() > 0) {
                    if (numberMap != null) {
                        //获取已使用套数
                        int hasApplyCount = Integer.parseInt(numberMap.get("HasApplyCount") + "");
                        int x_applynumber = Integer.parseInt(numberMap.get("x_applynumber") + "");
                        buildDatum.put("policy_num", x_applynumber);
                        String policy_nums = buildDatum.get("policy_num") + "";
                        //破底政策套数
                        if (!"".equals(policy_nums) && !"null".equals(policy_nums)) {
                            policy_num = Integer.parseInt(buildDatum.get("policy_num") + "");
                        } else {
                            policy_num = 0;
                        }
                        //剩余套数
                        buildDatum.put("surplus_num", policy_num - hasApplyCount);
                        buildDatum.put("choose_num", hasApplyCount);
                    } else {
                        buildDatum.put("surplus_num", policy_num);
                        buildDatum.put("choose_num", choose_num);
                    }
                } else {
                    buildDatum.put("surplus_num", policy_num - choose_num);
                    buildDatum.put("choose_num", choose_num);
                }
            }
        }
        return oldList;
    }

    void getFlowWhere(String policy_type, List<Integer> orderIndexList, List<Map> whereMapList) {
        Map<Object, Object> map = new HashMap<>();
        switch (policy_type) {
            case "0-3%":
                orderIndexList.add(0);
                map.put("0", "0-3%");
                whereMapList.add(map);
                break;
            case "3%-5%":
                orderIndexList.add(1);
                map.put("1", "3%-5%");
                whereMapList.add(map);
                break;
            case "5%-10%":
                orderIndexList.add(2);
                map.put("2", "5%-10%");
                whereMapList.add(map);
                break;
            case "10%-20%":
                orderIndexList.add(3);
                map.put("3", "10%-20%");
                whereMapList.add(map);
                break;
            case "20%以上":
                orderIndexList.add(4);
                map.put("4", "20%以上");
                whereMapList.add(map);
                break;
        }
    }


    void getOrgname(String buinsID, Map map) {

        //
        String buinsData[] = {"10010000", "10060000", "10080000", "10020000", "10040000", "10270000", "10030000","10170000","10120000"};
        //
        String orgNameData[] = {"上海", "皖赣", "西南", "苏南", "浙江", "山东", "华北","西北","广桂"};
        List<String> asList = Arrays.asList(buinsData);
        int indexOf = asList.indexOf(buinsID);
        if (indexOf == -1) {
            map.put("orgName", "事业部");
        } else {
            map.put("orgName", orgNameData[indexOf]);
        }
    }

    @Override
    //审批通过以后向明源推送一揽子分期/折扣数据
    public VisolinkResultBody approvalPushDataForMy(Map map) {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        try {
            //获取此流程id
            String flow_id = map.get("flow_id") + "";
            String eventType = map.get("eventType") + "";
            boolean pushFlag = true;
            if ("5".equals(eventType) || "6".equals(eventType) || "3".equals(eventType)) {
                pushFlag = false;
            }
            //根据flow_id查询主数据id
            Map flowJsonData = packageanddiscountDao.getFlowJsonData(flow_id);
            if (flowJsonData == null) {
                response.setCode(400);
                response.setMessages("没有查询到此流程的数据");
                return response;
            }
            String json_id = flowJsonData.get("json_id") + "";
            String creator = flowJsonData.get("creator") + "";
            //将此审批政策数据的外键改为当前的json_id;

            if (pushFlag) {
                String flow_json = flowJsonData.get("flow_json") + "";
                Map flowMaps = JSON.parseObject(flow_json, Map.class);
                List<String> oldList = (List<String>) flowMaps.get("oldList");
                if (oldList != null && oldList.size() > 0) {
                    for (String id : oldList) {
                        Map<Object, Object> paramMap = new HashMap<>();
                        paramMap.put("id", id);
                        paramMap.put("json_id", json_id);
                        packageanddiscountDao.updatePackageStageId(paramMap);
                    }
                }
            }
            //固定审批单数据
            //查询一揽子分期原有明细数据
            List<Map> stageOldData = packageanddiscountDao.getStageOldAndNewData(json_id, "1");
            if (stageOldData != null && stageOldData.size() > 0) {
                for (Map stageOldDatum : stageOldData) {
                    stageOldDatum.put("id", UUID.randomUUID().toString());
                    stageOldDatum.put("type", 3);
                    stageOldDatum.put("policy_effective_time", stageOldDatum.get("policy_effective_time") + "-01");
                    Map filterMap = filterMap(stageOldDatum);
                    packageanddiscountDao.insertStageItem(filterMap);
                }
            }

            List<Map> disOldAndNewData = packageanddiscountDao.getDisOldAndNewData(json_id, "1");
            if (disOldAndNewData != null && disOldAndNewData.size() > 0) {
                for (Map disOldAndNewDatum : disOldAndNewData) {
                    disOldAndNewDatum.put("id", UUID.randomUUID().toString());
                    disOldAndNewDatum.put("type", 3);
                    disOldAndNewDatum.put("policy_effective_time", disOldAndNewDatum.get("policy_effective_time") + "-01");
                    Map filterMap = filterMap(disOldAndNewDatum);
                    packageanddiscountDao.insertStageDisItem(filterMap);
                }
            }


            if (pushFlag) {
                //获取分期政策数据
                List<Map> stageItemPushData = packageanddiscountDao.getStageItemPushData(json_id);
                if (stageItemPushData != null && stageItemPushData.size() > 0) {
                    for (Map stageItemPushDatum : stageItemPushData) {
                        Map policStope = getPolicStope(stageItemPushDatum);
                        policStope.put("SetUserCode", creator);
                        callinginterface(policStope);
                    }
                }
            }

            if (pushFlag) {
                List<Map> disItemPushData = packageanddiscountDao.getDisItemPushData(json_id);
                if (disItemPushData != null && disItemPushData.size() > 0) {
                    for (Map disItemPushDatum : disItemPushData) {
                        Map policNumber = getPolicNumber(disItemPushDatum);
                        policNumber.put("SetUserCode", creator);
                        callinginterface(policNumber);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(400);
            response.setMessages("同步明源数据失败");
            response.setResult(e.getMessage());
            return response;
        }
        return response;
    }


    public Map getPolicStope(Map map) {
        //获取分期天数
        String stage_days = map.get("stage_days") + "";
        switch (stage_days) {
            case "90天以上":
                map.put("PolicyScopeMax", "9999");
                map.put("PolicyScopeMin", "90");
                break;
            case "180天以上":
                map.put("PolicyScopeMax", "9999");
                map.put("PolicyScopeMin", "180");
                break;
            case "0～180天":
                map.put("PolicyScopeMax", "180");
                map.put("PolicyScopeMin", "0");
                break;
            case "0～90天":
                map.put("PolicyScopeMax", "90");
                map.put("PolicyScopeMin", "0");
                break;
        }
        return map;
    }

    public Map getPolicNumber(Map map) {
        String policy_type = map.get("policy_type") + "";
        switch (policy_type) {
            case "0-3%":
                map.put("PolicyScopeMax", "3");
                map.put("PolicyScopeMin", "0");
                break;
            case "3%-5%":
                map.put("PolicyScopeMax", "5");
                map.put("PolicyScopeMin", "3");
                break;
            case "5%-10%":
                map.put("PolicyScopeMax", "10");
                map.put("PolicyScopeMin", "5");
                break;
            case "10%-20%":
                map.put("PolicyScopeMax", "20");
                map.put("PolicyScopeMin", "10");
                break;
            case "20%以上":
                map.put("PolicyScopeMax", "99");
                map.put("PolicyScopeMin", "20");
                break;
        }
        return map;
    }

    public void getStagePolicy(Map stageMap, List<Integer> stagePolicyList) {
        //分期政策类别
        String stage_policy_type = stageMap.get("stage_policy_type") + "";
        //分期时间类别
        String stage_time_type = stageMap.get("stage_time_type") + "";

        if (("全款分期".equals(stage_policy_type) && "全款分期>6个月".equals(stage_time_type)) || ("首付分期".equals(stage_policy_type) && "首付分期>3个月".equals(stage_time_type))) {
            stagePolicyList.add(5);
        } else if (("全款分期".equals(stage_policy_type) && "全款分期≤6个月".equals(stage_time_type)) || ("首付分期".equals(stage_policy_type) && "首付分期≤3个月".equals(stage_time_type))) {
            stagePolicyList.add(4);
        }

    }

    //调用明源同步政策数据接口
    @Override
    public void callinginterface(Map map) {
        Map<Object, Object> paramMap = new HashMap<>();
        Map<Object, Object> clineMap = new HashMap<>();
        clineMap.put("instId", UUID.randomUUID().toString());
        clineMap.put("requestTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        //查询金蝶项目id
        String jdProjID = packageanddiscountDao.getJDProjectID(map.get("MdmProjID") + "");
        map.put("MdmProjID", jdProjID);
        map.remove("policy_type");
        map.remove("stage_days");
        map.put("isEnable", "1");
        Map<Object, Object> keymap = new HashMap<>();
        String id = map.get("SaleChgPolicySetGUID") + "";
        keymap.put("id", id);
        String sql = "select *from VS_XSGL_POLICY where SaleChgPolicySetGUID='" + id + "'";

        List<Map<String, Object>> mapList = jdbcTemplatemy.queryForList(sql);
        if (mapList == null || mapList.size() == 0) {
            map.remove("SaleChgPolicySetGUID");
        }
        map.remove("edit_time");
        paramMap.put("esbInfo", clineMap);
        paramMap.put("requestInfo", map);
        String jsonParam = JSON.toJSONString(paramMap);
        System.out.println("=====开始向明源同步政策数据=====");

        System.out.println("接口地址:" + syncStageDataUrl);
        System.out.println("传递参数:" + jsonParam);
        System.out.println("记录日志:" + jsonParam);
        //记录日志
        packageanddiscountDao.insertParamLog(jsonParam);

        String returnMessage = HttpUtil.post(syncStageDataUrl, jsonParam);
        Map returnMap = JSON.parseObject(returnMessage, Map.class);
        if (returnMap != null) {
            Map esbInfo = (Map) returnMap.get("esbInfo");
            String returnStatus = esbInfo.get("returnStatus") + "";
            if (!"S".equals(returnStatus)) {
                Object returnMsg = esbInfo.get("returnMsg");
                //记录日志
                packageanddiscountDao.insertParamLog("明源返回同步结果:" + JSON.toJSONString(returnMessage));
                throw new BadRequestException(returnMsg.toString());
            }
            packageanddiscountDao.insertParamLog("明源返回同步结果:" + JSON.toJSONString(returnMessage));
            Map resultMap = (Map) returnMap.get("resultInfo");
            if (resultMap != null) {
                String SaleChgPolicySetGUID = resultMap.get("SaleChgPolicySetGUID") + "";
                //将本系统id修改为明源返回的id
                keymap.put("SaleChgPolicySetGUID", SaleChgPolicySetGUID);
                packageanddiscountDao.updateStageItemPrimaryKey(keymap);
                packageanddiscountDao.updateDisItemPrimaryKey(keymap);
            }

        } else {
            throw new BadRequestException("没有获取到同步结果");
        }
    }

    //过滤前端传递的参数map
    public Map filterMap(Map<String, Object> map) {
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String value = entry.getValue() + "";
            if (!"".equals(value) && !"null".equalsIgnoreCase(value)) {
                resultMap.put(entry.getKey(), entry.getValue());
            }
        }
        return resultMap;
    }
}

