package cn.visolink.salesmanage.onlineretailersuse.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import cn.visolink.salesmanage.flow.dao.FlowOtherDao;
import cn.visolink.salesmanage.onlineretailersuse.dao.OnlineretailersUseDao;
import cn.visolink.salesmanage.onlineretailersuse.service.OnlineretailersUseService;
import cn.visolink.salesmanage.packageanddiscount.dao.PackageanddiscountDao;
import cn.visolink.salesmanage.workflowchange.dao.WorkflowChangeDao;
import cn.visolink.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author sjl
 * @Created date 2020/11/26 9:02 下午
 */
@Service
@Transactional
public class OnlineretailersUseServiceImpl implements OnlineretailersUseService {
    @Autowired
    private OnlineretailersUseDao onlineretailersUseDao;

    @Autowired
    private PackageanddiscountDao packageanddiscountDao;

    @Autowired
    private WorkflowChangeDao workflowChangeDao;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    @Value("${syncStageData.dsPolicyUrl}")
    private String dsPolicyUrl;

    @Value("${syncStageData.userId}")
    private String dsPolicyUserId;

    @Value("${syncStageData.password}")
    private String dsPolicyPassword;

    @Autowired
    private FileDao fileDao;
    @Autowired
    private FlowOtherDao flowOtherDao;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 获取电商使用政策申请列表
     * @param paramMap
     * @return
     */
    @Override
    public ResultBody getOnlineretailersUseApplayList(Map paramMap) {
        Map<Object, Object> resultMap = new HashMap<>();
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            //获取分页数据
            int pageIndex = Integer.parseInt(paramMap.get("pageIndex").toString());
            int pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
            int i = (pageIndex - 1) * pageSize;
            paramMap.put("pageIndex", i);
            //分页查询列表
            List<Map> applayList = onlineretailersUseDao.getOnlineretailersUseApplayList(paramMap);
            //查询总条数
            List<Map> applayCount = onlineretailersUseDao.getOnlineretailersUseApplayCount(paramMap);
            resultMap.put("applayList",applayList);
            resultMap.put("applayCount",applayCount.size());
            resultBody.setMessages("查询成功!");
            resultBody.setData(resultMap);
            resultBody.setCode(200);
            return resultBody;
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-194,"申请列表查询失败!");
        }
    }

    @Override
    public ResultBody saveOnlineretailersUseApplay(Map paramMap, HttpServletRequest request) {
        Map<Object, Object> resultMap = new HashMap<>();
        try {
            String username = request.getHeader("username");
            String jobid = request.getHeader("jobid");
            String joborgid = request.getHeader("joborgid");
            String orgid = request.getHeader("orgid");
            String orglevel = request.getHeader("orglevel");
            String userid = request.getHeader("userid");
            String message="保存成功!";
            //获取数据标识
            String BOID = String.valueOf(paramMap.get("BOID"));
            String project_id = String.valueOf(paramMap.get("project_id"));
            //查询事业部
            Map buinessData = packageanddiscountDao.getBuinessData(project_id);
            String business_unit_id = buinessData.get("business_unit_id") + "";
            //获取业务主数据
            Map mainData = (Map) paramMap.get("MainData");
            String button = String.valueOf(paramMap.get("button"));
            //如果业务数据ID已存在
            if(!"".equals(BOID)&&!"null".equals(BOID)){
                mainData.put("project_id", project_id);
                mainData.put("BOID", BOID);
                // todo 修改业务数据
                onlineretailersUseDao.updateOnlineretailersUseApplay(mainData);
                //清空附属数据
                onlineretailersUseDao.clearItemData(BOID);
                //新增附属数据
                List<Map> mainDateItem = (List<Map>) paramMap.get("MainDataItem");
                int i = 0;

                if(mainDateItem!=null&&mainDateItem.size()>0){
                    for (Map itemMap : mainDateItem) {
                        //获取分期id
                        String stage_id = String.valueOf(itemMap.get("stage_id"));
                        //获取分期名称
                        String stage_name = String.valueOf(itemMap.get("stage_name"));
                        List<Map> policyItem = (List<Map>) itemMap.get("policyItem");
                        for (Map policyMap : policyItem) {
                            i++;
                            policyMap.put("stage_id", stage_id);
                            policyMap.put("stage_name", stage_name);
                            policyMap.put("id", UUID.randomUUID().toString());
                            policyMap.put("base_id", BOID);
                            policyMap.put("order_num", i);
                            List<String> cycle = (List<String>) policyMap.get("cycle");
                            if (cycle != null && cycle.size() > 0) {
                                policyMap.put("cycle_start", cycle.get(0));
                                policyMap.put("cycle_end", cycle.get(1));
                            }
                            onlineretailersUseDao.saveOnlineretailersUseItem(policyMap);
                        }

                    }
                }
                //修改主数据
                mainData.put("editor", userid);
                mainData.put("item_name",mainData.get("title"));
                mainData.put("id", BOID);
                /*修改政策主表 (bql 2020.07.20)*/
                packageanddiscountDao.updateCmPlicySales(mainData);
            }else {
                //todo 新增业务数据
                String dataId = UUID.randomUUID().toString();
                BOID=dataId;
                mainData.put("id",dataId);
                mainData.put("creator",userid);
                mainData.put("project_id",project_id);
                //保存主数据
                onlineretailersUseDao.saveOnlineretailersUseApplay(mainData);
                //保存附属数据
                List<Map> mainDateItem = (List<Map>) paramMap.get("MainDataItem");
                int i = 0;
                if(mainDateItem!=null&&mainDateItem.size()>0){
                    for (Map itemMap : mainDateItem) {
                        i++;
                        itemMap.put("id", UUID.randomUUID().toString());
                        itemMap.put("base_id", dataId);
                        itemMap.put("order_num", i);
                        List<String> cycle = (List<String>)itemMap.get("cycle");
                        if(cycle!=null&&cycle.size()>0){
                            itemMap.put("cycle_start",cycle.get(0));
                            itemMap.put("cycle_end",cycle.get(1));
                        }
                        onlineretailersUseDao.saveOnlineretailersUseItem(itemMap);
                    }
                }
                mainData.put("id", dataId);
                mainData.put("item_name",mainData.get("title"));
                mainData.put("applicant_name",username);
                mainData.put("job_id",jobid);
                mainData.put("job_org_id",joborgid);
                mainData.put("org_id",orgid);
                mainData.put("org_level",orglevel);
                /*将销售政策数据添加到政策主表 (bql 2020.07.17)*/
                onlineretailersUseDao.insertCmPolicySalesForOnlineretailers(mainData);
                /*添加政策权限中间表 (bql 2020.07.20)*/
                packageanddiscountDao.insertPolicyOrgRel(mainData);
            }
            String jobName="";
            //如果当前人不为空
            if(!"".equals(username)&&!"null".equals(username)){
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                if (jwtUser != null) {
                    jobName = jwtUser.getJob().get("JobName") + "";
                }
            }
            //查询流程是否已存在
            Map flowMap = workflowChangeDao.queryFlowCode(BOID);
            HashMap<Object, Object> flowData = new HashMap<>();
           // if("submit".equals(button)){
                message="保存成功";
                flowData.put("creator",userid);
                flowData.put("editor",userid);
                flowData.put("flow_json",JSON.toJSONString(paramMap));
                flowData.put("flow_status",2);
                flowData.put("post_name",jobName);
                flowData.put("comcommon",JSON.toJSONString(mainData));
                flowData.put("stage_id",paramMap.get("project_id"));
                flowData.put("project_id",paramMap.get("project_id"));
                getOrgname(business_unit_id,flowData);
                if(flowMap!=null&&flowMap.size()>0){
                    flowData.put("id",flowMap.get("id")+"");
                    //修改流程
                    packageanddiscountDao.updateFlowData(flowData);
                }else{
                    flowData.put("id", UUID.randomUUID().toString());
                    flowData.put("flow_status", 2);
                    flowData.put("flow_type", "My_Sales");
                    flowData.put("flow_code", "My_Sales_policy");
                    flowData.put("isdel", "0");
                    flowData.put("json_id", BOID);
                    //新增流程
                    packageanddiscountDao.insertFlowData(flowData);
                    //  }
                }
            //删除附件
            fileDao.delFileByBizId(BOID);
            //更新附件数据
            List<Map> fileList = (List<Map>) paramMap.get("fileList");
            if (fileList != null && fileList.size() > 0) {

                for (Map fileMap : fileList) {
                    //更新最新附件
                    fileMap.put("bizID", BOID);
                    fileDao.updateFileBizID(fileMap);
                }
            }
            resultMap.put("json_id",BOID);
            resultMap.put("flow_code","My_Sales_policy");
            resultMap.put("flow_type","My_Sales");
            resultMap.put("userid",username);
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setCode(200);
            resultBody.setData(resultMap);
            resultBody.setMessages(message);
            return resultBody;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResultBody.error(-295,"操作失败!");
        }
    }


    @Override
    public ResultBody queryOnOnlineretailersUseInfo(Map paramMap,HttpServletRequest request) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            Map<Object, Object> resultMap = new HashMap<>();
            //获取业务主数据ID
            String BOID = MapUtils.getString(paramMap, "BOID");

            //获取项目ID
            String project_id = MapUtils.getString(paramMap, "project_id");
            //获取项目名称
            String project_name = MapUtils.getString(paramMap, "project_name");

            //获取当前登录人ID
            String userid = request.getHeader("userid");
            Map<Object, Object> dataMap = new HashMap<>();
            //获取当前登录人账号
            String username = request.getHeader("username");

            if(BOID!=null&&!"".equals(BOID)&&!"null".equals(BOID)){

                //查询数据库中保存的数据
                Map mainData = onlineretailersUseDao.getOnlineretailersMainData(paramMap);
                Map flowMap = flowOtherDao.queryFlowId(BOID);
                if(flowMap!=null&&flowMap.size()>0){
                    String flow_status = String.valueOf(flowMap.get("flow_status"));
                    if ("3".equals(flow_status) || "4".equals(flow_status)) {
                        if (mainData != null && mainData.size() > 0) {
                            dataMap.putAll(mainData);
                        }
                    } else {
                        if (project_id != null && !"".equals(project_id) && !"null".equals(project_id)) {
                            JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                            dataMap.putAll(mainData);
                            dataMap.put("create_time", DateUtil.format(new Date(), "yyyy-MM-dd"));
                            dataMap.put("applicant", jwtUser.getEmployeeName());
                            dataMap.put("post_name", jwtUser.getJob().get("JobName"));
                        } else {
                            if (mainData != null && mainData.size() > 0) {
                                dataMap.putAll(mainData);
                            }
                        }

                    }
                }
                //查询附属数据
                List<Map<String, Object>> itemData = onlineretailersUseDao.getOnlineretailersItemData(paramMap);
                List<Object> mainDataItem = new ArrayList<>();
                if (itemData != null && itemData.size() > 0) {
                    List<String> stageList = itemData.stream().map(p -> MapUtils.getString(p, "stage_id")).distinct().collect(Collectors.toList());
                    if (stageList != null && stageList.size() > 0) {
                        for (String stage_id : stageList) {
                            String stage_name = "";
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("stage_id", stage_id);
                            List<Map> itemList = null;
                            if (stage_id != null) {
                                itemList = itemData.stream().filter(p -> (MapUtils.getString(p, "stage_id").equals(stage_id))).collect(Collectors.toList());
                                if (itemList != null && itemList.size() > 0) {
                                    stage_name = String.valueOf(itemList.get(0).get("stage_name"));
                                }
                                itemMap.put("policItem", itemList);
                                itemMap.put("stage_name", stage_name);
                                mainDataItem.add(itemMap);
                            } else {
                                itemMap.put("policItem", itemList);
                            }
                        }
                    }
                }
                resultMap.put("MainDataItem", mainDataItem);
            }else{
                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                dataMap.put("create_time", DateUtil.format(new Date(),"yyyy-MM-dd"));
                dataMap.put("applicant",jwtUser.getEmployeeName());
                dataMap.put("post_name", jwtUser.getJob().get("JobName"));
                dataMap.put("project_name", project_name);
            }
            if(project_id==null||"".equals(project_id)||"null".equals(project_id)){
                project_id= String.valueOf(dataMap.get("project_id"));
            }
            if(project_id!=null&&!"".equals(project_id)){
                //dataMap.put("project_name",project_name);
                //查询页面上的各种下拉列表
                //1.查询明源系统的产品、楼栋、户型数据
                String sql = "SELECT \n" +
                        "kingdeeProjectFID as stage_id,\n" +
                        "projfname as stage_name\n" +
                        "FROM VS_XSGL_Ecommerce WHERE projectID='" + project_id + "'" +
                        "GROUP BY kingdeeProjectFID,projfname";
                List<Map<String, Object>> forList = jdbcTemplatemy.queryForList(sql);
                resultMap.put("stageData", forList);
                List<Map> companyList = onlineretailersUseDao.getOnlineretailersCompanyList();
                dataMap.put("onlCompanyList", companyList);
            }

            //查询附件列表数据
            List fileLists = fileDao.getFileLists(BOID);
            resultMap.put("fileList", fileLists);
            resultMap.put("MainData", dataMap);
            resultBody.setMessages("查询成功!");
            resultBody.setData(resultMap);
            resultBody.setCode(200);
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setMessages("查询失败!");
            resultBody.setCode(-1);
        }
        return resultBody;
    }

    /**
     * 同步明源电商使用政策数据
     *
     * @param paramMap
     * @return
     */
    @Override
    public ResultBody synOnOnlineretailersUseData(Map<String, String> paramMap) {
        Map<Object, Object> logsMap = new HashMap<>();
        String response = "";
        try {
            Map mainData = onlineretailersUseDao.getOnlineretailersMainData(paramMap);
            Map flowData = flowOtherDao.queryFlowId(paramMap.get("BOID"));
            List<Map<String, Object>> itemData = onlineretailersUseDao.getOnlineretailersItemDataForMy(paramMap);
            Map<Object, Object> esbMap = new HashMap<>();
            Map<Object, Object> requestMap = new HashMap<>();
            requestMap.put("instId", paramMap.get("BOID"));
            requestMap.put("requestTime", DateUtil.format(new Date(), "yyyy-MM-dd"));
            Map<Object, Object> dataMap = new HashMap<>();
            if (itemData != null && itemData.size() > 0) {
                List<String> stageList = itemData.stream().map(p -> MapUtils.getString(p, "projGuid")).distinct().collect(Collectors.toList());
                for (String stage_id : stageList) {
                    List<Map> itemList = itemData.stream().filter(p -> (MapUtils.getString(p, "projGuid").equals(stage_id))).collect(Collectors.toList());
                    dataMap.put("companyName", mainData.get("commerce_company"));
                    dataMap.put("companyId", mainData.get("commerce_id"));
                    String userName = onlineretailersUseDao.getUserName(String.valueOf(flowData.get("creator")));
                    dataMap.put("projGuid", stage_id);
                    dataMap.put("userCode", userName);
                    dataMap.put("editTime", flowData.get("ApplyDate"));
                    dataMap.put("remark", mainData.get("app_explan"));
                    dataMap.put("detail", itemList);
                    esbMap.put("requestInfo", dataMap);
                    esbMap.put("esbInfo", requestMap);
                    logsMap.put("params", JSON.toJSONString(esbMap));
                    logsMap.put("description", "向明源同步电商使用政策数据");
                    //记录推送日志
                    onlineretailersUseDao.insertParamLogForPushSystem(logsMap);
                    String message = "";
                    response = HttpUtil.post(dsPolicyUrl, JSON.toJSONString(esbMap));
                    Map responseMap = JSON.parseObject(response, Map.class);
                    if (responseMap != null && responseMap.size() > 0) {
                        Map<String, Object> esbInfo = (Map) responseMap.get("esbInfo");
                        String status = String.valueOf(esbInfo.get("returnStatus"));
                        if (status.equalsIgnoreCase("S")) {
                            message = "同步成功";
                        } else {
                            message = "同步失败";
                        }
                    }
                    logsMap.put("params", JSON.toJSONString(responseMap));
                    logsMap.put("description", "向明源同步电商使用政策数据同步结果==>" + message);
                }
            }
            //查询账号
      /*      if (mainData != null && mainData.size() > 0) {
                dataMap.put("companyName", mainData.get("commerce_company"));
                dataMap.put("companyId", mainData.get("commerce_id"));
                String userName = onlineretailersUseDao.getUserName(String.valueOf(flowData.get("creator")));
                dataMap.put("projGuid", mainData.get("stage_id"));
                dataMap.put("userCode", userName);
                dataMap.put("editTime", flowData.get("ApplyDate"));
                dataMap.put("remark", mainData.get("app_explan"));
                dataMap.put("detail", itemData);
            }*/


            //推送
           /* JSONObject createTimeriskresult = HttpRequestUtil.httpPost(dsPolicyUrl, dsPolicyUserId, dsPolicyPassword, JSONObject.parseObject(JSONObject.toJSONString(esbMap)), false);  //发送数据
            response = JSON.toJSONString(createTimeriskresult);*/

        } catch (Exception e) {
            e.printStackTrace();
            logsMap.put("params", e.getMessage());
            logsMap.put("description", "向明源同步电商使用政策数据出现异常");
        } finally {
            //记录推送结果日志
            onlineretailersUseDao.insertParamLogForPushSystem(logsMap);
        }
        ResultBody<Object> resultBody = new ResultBody<>();
        resultBody.setData(response);
        return resultBody;
    }

    @Override
    public ResultBody getProductData(Map<String, String> paramMap) {
        Map<Object, Object> resultMap = new HashMap<>();
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            String stage_id = paramMap.get("stage_id");
            //dataMap.put("project_name",project_name);
            //查询页面上的各种下拉列表
            //1.查询明源系统的产品、楼栋、户型数据
            String sql = "SELECT \n" +
                    "productTypeId,\n" +
                    "productTypeName,\n" +
                    "bldguid,\n" +
                    "bldname,\n" +
                    "hxGUID,\n" +
                    "hxname\n" +
                    "FROM VS_XSGL_Ecommerce WHERE kingdeeProjectFID='" + stage_id + "'";
            List<Map<String, Object>> forList = jdbcTemplatemy.queryForList(sql);
            List<Map<String, Object>> cloneBuildList = ObjectUtil.cloneByStream(forList);
            List<Map<String, Object>> cloneHxList = ObjectUtil.cloneByStream(forList);
            if (forList != null && forList.size() > 0) {
                //将产品类型分组
                List<Map<String, Object>> productList = forList.stream().filter(distinctByKey(n -> n.get("productTypeId")))
                        .collect(Collectors.toList());
                //将楼栋分组
                List<Map<String, Object>> buildList = cloneBuildList.stream().filter(distinctByKey(n -> n.get("bldguid")))
                        .collect(Collectors.toList());
                //用来筛选户型

                List<Object> productDataList = new ArrayList<>();
                for (Map<String, Object> productMap : productList) {
                    productMap.remove("bldguid");
                    productMap.remove("bldname");
                    productMap.remove("hxname");
                    String productTypeId = MapUtils.getString(productMap, "productTypeId");
                    //根据产品类型过滤出对应的楼栋
                    List<Map<String, Object>> buildFilterList = buildList.stream().filter(c -> (productTypeId.equals(MapUtils.getString(c, "productTypeId"))))
                            .collect(Collectors.toList());

                    if (buildFilterList != null && buildFilterList.size() > 0) {
                        for (Map<String, Object> buildMap : buildFilterList) {
                            buildMap.remove("hxname");
                            buildMap.remove("productTypeName");
                            buildMap.remove("productTypeId");
                            String bldguid = MapUtils.getString(buildMap, "bldguid");
                            List<Map<String, Object>> hxList = cloneHxList.stream().filter(c -> (MapUtils.getString(c, "bldguid").equals(bldguid))).collect(Collectors.toList());
                            buildMap.put("hxList", hxList);
                        }
                    }
                    productMap.put("buildList", buildFilterList);
                    productDataList.add(productMap);
                }
                resultMap.put("selectData", productDataList);
            } else {
                resultMap.put("selectData", null);
            }
            resultBody.setCode(200);
            resultBody.setMessages("查询成功!");
            resultBody.setData(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setCode(-10056);
            resultBody.setMessages("查询产品等数据失败!");
        }
        return resultBody;
    }

    @Override
    public ResultBody deleteOnOnlineretailersUseData(Map<String, String> paramMap) {
        String boid = paramMap.get("BOID");
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            Map map = flowOtherDao.queryFlowId(boid);
            if (map != null && map.size() > 0) {
                String value = String.valueOf(map.get("flow_status"));
                if ("3".equals(value) || "4".equals(value)) {
                    resultBody.setMessages("审批中/审批通过状态下，无法删除!");
                    resultBody.setCode(-4945);
                    return resultBody;
                } else {
                    onlineretailersUseDao.deleteMainData(boid);
                    //清除附属数
                    onlineretailersUseDao.clearItemData(boid);
                }
            }
            resultBody.setCode(200);
            resultBody.setMessages("删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setMessages("删除失败");
            resultBody.setCode(-1045);
        }
        return resultBody;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    void getOrgname(String buinsID, Map map) {

        //
        String buinsData[] = {"10010000", "10060000", "10080000", "10020000", "10040000", "10270000", "10030000", "10170000", "10120000"};
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


}
