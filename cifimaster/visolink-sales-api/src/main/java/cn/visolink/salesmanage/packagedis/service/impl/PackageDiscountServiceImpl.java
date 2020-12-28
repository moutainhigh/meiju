package cn.visolink.salesmanage.packagedis.service.impl;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.flow.dao.WorkflowDao;
import cn.visolink.salesmanage.packagedis.dao.PackageDiscountDao;
import cn.visolink.salesmanage.packagedis.service.PackageDiscontService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @auther: sjl
 * @Date: 2019/10/11 0011 16:21
 * @Description:
 */
@Transactional(noRollbackForClassName = {"InstrumentNotFoundException"})
@Service
public class PackageDiscountServiceImpl implements PackageDiscontService {
    @Autowired
    private PackageDiscountDao packageDiscountDao;

    @Value("${resultUrl.url}")
    private String resultUrl;

    /**
     * 查询登录人的项目
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> getProjects(Map map) {
        List<Map> projects = packageDiscountDao.getProjects(map);
        return projects;

    }

    /**
     * 查询项目的业态
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> getFormats(Map map) {
        List<Map> formats = packageDiscountDao.getFormats(map);
        return formats;

    }

    /**
     * 查询项目的楼栋和项目数据
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> getBuilding(Map map) {
        List<Map> building = packageDiscountDao.getBuildData(map);
        return building;
    }

    @Override
    public ResultBody getBuildingAndFormatsData(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        Map<Object, Object> dataMap = new HashMap<>();
        String projecid = map.get("projectId") + "";
        dataMap.put("project_id", projecid);
        //查询项目名称
        Map projectMap = packageDiscountDao.getProjectName(dataMap);
        try {
            List<Map> formatsList = packageDiscountDao.getFormats(map);
            if (formatsList != null && formatsList.isEmpty()) {
                for (Map formatMap : formatsList) {
                    String productCode = formatMap.get("building_id") + "";
                    String groupId = formatMap.get("groupId") + "";
                    Map<Object, Object> paramMap = new HashMap<>();
                    paramMap.put("productCode", productCode);
                    paramMap.put("groupId", groupId);
                    List<Map> buildData = packageDiscountDao.getBuildData(paramMap);
                    formatMap.put("buildData", buildData);
                }
            }
            Map<Object, Object> datasMap = new HashMap<>();
            if (projectMap != null && projectMap.size() > 0) {
                datasMap.put("projectName", projectMap.get("project_name") + "");
            }
            datasMap.put("buildAndFormat", formatsList);
            resultBody.setData(datasMap);
            resultBody.setCode(200);
            resultBody.setMessages("查询业态楼栋信息成功");
            return resultBody;
        } catch (Exception e) {
            resultBody.setCode(-3089);
            resultBody.setMessages("查询业态楼栋信息失败");
            return resultBody;
        }
    }

    @Autowired
    private WorkflowDao workflowDao;


    /**
     * 添加一揽子折扣（详情）
     *
     * @return
     */
    @Override
    public ResultBody insertPackageDis(Map maps) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            //一揽子折扣主数据id
            String uuid = UUID.randomUUID().toString();
            Map map = filterMapData(maps);
            String id = null;
            String baseId = map.get("baseId") + "";
            //清空一揽子分期主数据
            packageDiscountDao.clearPackageDis(baseId);
            if (!"".equals(baseId) && !"null".equals(baseId)) {
                id = baseId;
                map.put("id", id);
                packageDiscountDao.updatePackageDis(map);
            } else {
                id = uuid;
                map.put("id", id);
                //添加一揽子折扣主数据
                packageDiscountDao.insertPackageDis(map);
            }
            //获取政策明细数据
            //获取明细数据
            List<Map<String, Object>> agingDataList = (List<Map<String, Object>>) map.get("policyInfo");
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("id", id);
            //清除已存在的明细信息及楼栋数据
            List<Map> packageDisImemApply = packageDiscountDao.getPackageDisImemApply(paramMap);
            packageDiscountDao.deletePackageDisItem(paramMap);
            if (packageDisImemApply != null && packageDisImemApply.size() > 0) {
                for (Map map1 : packageDisImemApply) {
                    packageDiscountDao.deleteBuildingData(map1);
                }
            }
            for (Map<String, Object> agingMap : agingDataList) {
                String aginid = UUID.randomUUID().toString();
                agingMap.put("id", aginid);
                agingMap.put("packageDisId", id);
                packageDiscountDao.insertPackageDiscountItem(agingMap);
                List<Map<String, Object>> buildList = (List<Map<String, Object>>) agingMap.get("building");
                if(buildList!=null&&buildList.size()>0){
                    for (Map<String, Object> stringObjectMap : buildList) {
                        stringObjectMap.put("id", UUID.randomUUID().toString());
                        stringObjectMap.put("package_id", aginid);
                        packageDiscountDao.insertPackageBuilding(stringObjectMap);
                    }
                }

            }


            Map<String, Object> flowMap = new HashMap<>();
            //删除流程数据
            //packageDiscountDao.deleteFlowData(id);
            //创建流程数据
            String orgName = map.get("departName") + "";
            if (orgName.contains("10030000")||orgName.contains("10050000")||orgName.contains("10070000")
                    ||orgName.contains("10100000")||orgName.contains("10110000")||orgName.contains("10120000")
                    ||orgName.contains("10130000")||orgName.contains("10150000")||orgName.contains("10170000")
                    ||orgName.contains("50006094")||orgName.contains("50007031")||orgName.contains("50007155")
                    ||orgName.contains("50007348")
            ) {
                flowMap.put("orgname", "事业部");
            } else if (orgName.contains("10020000")) {
                flowMap.put("orgname", "苏南");
            } else if (orgName.contains("10010000")) {
                flowMap.put("orgname", "上海");
            } else if (orgName.contains("10060000")) {
                flowMap.put("orgname", "皖赣");
            } else if (orgName.contains("10080000")) {
                flowMap.put("orgname", "西南");
            } else if (orgName.contains("10040000")) {
                flowMap.put("orgname", "浙江");
            }else if (orgName.contains("10270000")) {
                flowMap.put("orgname", "山东");
            }else{
                //回滚数据
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                resultBody.setMessages("当前申请人部门无法发起审批!");
                resultBody.setCode(-1);
                return  resultBody;
            }
            Map<Object, Object> maspp = new HashMap<>();

            flowMap.put("id", UUID.randomUUID().toString());
            maspp.put("basePolicy", map.get("basePolicy") + "");
            maspp.put("allLossPrice", map.get("allLossPrice") + "");
            maspp.put("profitNetprofit", map.get("profitNetprofit") + "");
            flowMap.put("comcommon", JSON.toJSONString(maspp));
            flowMap.put("flowType", "My_Sales");
            flowMap.put("baseId", id);
            flowMap.put("jsonId", id);
            flowMap.put("flowCode", "My_Package_Dis");
            flowMap.put("userName", map.get("userName") + "");
            flowMap.put("projectName",map.get("projectName") + "");
            //将添加的主数据转换为json字符串
            String jsonString = JSON.toJSONString(map);
            flowMap.put("flowJson", jsonString);
            //项目id
            flowMap.put("projectId", map.get("projectId") + "");
            flowMap.put("stageId", map.get("projectId") + "");
            flowMap.put("creator", map.get("userName") + "");
            flowMap.put("editor", map.get("userName") + "");
            flowMap.put("title",map.get("itemName")+"");
            String isTs = map.get("isTs") + "";
            HashMap<Object, Object> resultMap = new HashMap<>();
            resultMap.put("baseId", id);
            if(!"".equals(baseId)&&!"null".equalsIgnoreCase(baseId)){
                packageDiscountDao.clearFlowData(baseId);
                packageDiscountDao.updateFlowData(map);
            }else{
                packageDiscountDao.createFlowData(flowMap);
            }
            if ("1".equals(isTs)) {
                resultBody.setCode(200);
                resultBody.setMessages("暂存成功!");
                resultBody.setData(resultMap);
                return resultBody;
            }
            resultBody.setCode(200);
            resultBody.setData(resultMap);
            resultBody.setMessages("一揽子折扣审批发起成功！");
            return resultBody;
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setCode(-2034);
            resultBody.setMessages("一揽子折扣审批发起失败!");
            return resultBody;
        }
    }

    /**
     * 添加一揽子分期（详情）
     *
     * @return
     */

    @Override
    public ResultBody insertPackageStages(Map maps) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            String uuid = UUID.randomUUID().toString();
            Map map = filterMapData(maps);
            String id = null;
            String baseId = map.get("baseId") + "";
            //清空一揽子分期主数据
            packageDiscountDao.clearPackageStages(baseId);
            if (baseId != null && !"".equals(baseId) && !"null".equals(baseId)) {
                id = baseId;
                map.put("id", id);
                packageDiscountDao.updatePackageStages(map);
            } else {
                id = uuid;
                map.put("id", id);
                //添加一揽子分期主数据
                packageDiscountDao.insertPackageStages(map);
            }

            //获取分期数据和套数数据
            //获取楼栋数据
            List<Map<String, Object>> agingDataList = (List<Map<String, Object>>) map.get("policyInfo");
            //清空一揽子分期详情数据
            //获取一揽子分期详情数据
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("id", id);
            List<Map> packageStagesItemApply = packageDiscountDao.getPackageStagesItemApply(paramMap);
            packageDiscountDao.deletePackageItem(paramMap);
            if (packageStagesItemApply != null && packageStagesItemApply.size() > 0) {
                for (Map map1 : packageStagesItemApply) {
                    packageDiscountDao.deleteBuildingData(map1);
                }
            }
            if (agingDataList != null && agingDataList.size() > 0) {
                // 添加一揽子分期数据和楼栋数据
                for (Map agmap : agingDataList) {
                    String toString = UUID.randomUUID().toString();
                    agmap.put("id", toString);
                    agmap.put("packageStagesId", id);
                    //一揽子折扣政策审批-政策明细
                    packageDiscountDao.insertPackageStagesItem(agmap);
                    List<Map<String, Object>> buildList = (List<Map<String, Object>>) agmap.get("building");
                    if(buildList!=null&&buildList.size()>0){
                        for (Map mas : buildList) {
                            String uid = UUID.randomUUID().toString();
                            mas.put("id", uid);
                            mas.put("package_id", toString);
                            //添加楼栋
                            packageDiscountDao.insertPackageBuilding(mas);
                        }
                    }

                }
            }
            Map<String, Object> flowMap = new HashMap<>();
            //删除流程数据
            //packageDiscountDao.deleteFlowData(id);
            //创建流程数据
            String orgName = map.get("departName") + "";
            if (orgName.contains("10030000")||orgName.contains("10050000")||orgName.contains("10070000")
            ||orgName.contains("10100000")||orgName.contains("10110000")||orgName.contains("10120000")
                    ||orgName.contains("10130000")||orgName.contains("10150000")||orgName.contains("10170000")
                    ||orgName.contains("50006094")||orgName.contains("50007031")||orgName.contains("50007155")
                    ||orgName.contains("50007348")
            ) {
                flowMap.put("orgname", "事业部");
            } else if (orgName.contains("10020000")) {
                flowMap.put("orgname", "苏南");
            } else if (orgName.contains("10010000")) {
                flowMap.put("orgname", "上海");
            } else if (orgName.contains("10060000")) {
                flowMap.put("orgname", "皖赣");
            } else if (orgName.contains("10080000")) {
                flowMap.put("orgname", "西南");
            } else if (orgName.contains("10040000")) {
                flowMap.put("orgname", "浙江");
            }else if (orgName.contains("10270000")) {
                flowMap.put("orgname", "山东");
            }else{
                //回滚数据
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                resultBody.setMessages("当前申请人部门无法发起审批!");
                resultBody.setCode(-1);
                return  resultBody;
            }
            Map<Object, Object> maspp = new HashMap<>();
            maspp.put("stageDataType", map.get("stageDataType") + "");
            flowMap.put("comcommon", JSON.toJSONString(maspp));
            flowMap.put("id", UUID.randomUUID().toString());

            flowMap.put("flowType", "My_Sales");
            flowMap.put("baseId", id);
            flowMap.put("jsonId", id);
            flowMap.put("flowCode", "My_Package_Stage");
            flowMap.put("creator", map.get("userName") + "");
            flowMap.put("stageId", map.get("projectId") + "");
            flowMap.put("editor", map.get("userName") + "");
            flowMap.put("title",map.get("itemName")+"");
            //将添加的主数据转换为json字符串
            String jsonString = JSON.toJSONString(map);
            flowMap.put("flowJson", jsonString);
            //项目id
            flowMap.put("projectId", map.get("projectId") + "");
            String isTs = map.get("isTs") + "";
            HashMap<Object, Object> resultMap = new HashMap<>();
            resultMap.put("baseId", id);

            if (!"".equals(baseId) && !"null".equalsIgnoreCase(baseId)) {
                packageDiscountDao.clearFlowData(baseId);
                packageDiscountDao.updateFlowData(flowMap);
            }else{
                packageDiscountDao.createFlowData(flowMap);
            }
            if ("1".equals(isTs)) {
                resultBody.setCode(200);
                resultBody.setMessages("暂存成功!");
                resultBody.setData(resultMap);
                return resultBody;
            }
            resultBody.setData(resultMap);
            resultBody.setCode(200);
            resultBody.setMessages("一揽子分期审批发起成功!");
            return resultBody;
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setCode(-45450);
            resultBody.setMessages("一揽子分期审批发起失败!");
            return resultBody;
        }
    }

    @Override
    public ResultBody packageStagesApply(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            Map baseIdMap = packageDiscountDao.getBaseId(map);
            Map packageStagesData = packageDiscountDao.getPackageStagesApply(baseIdMap);
            List<Map> packageStagesItem = packageDiscountDao.getPackageStagesItemApply(baseIdMap);
            HashMap<Object, Object> resultMap = new HashMap<>();
            resultMap.put("basicInfo", packageStagesData);
            if (packageStagesItem != null && packageStagesItem.size() > 0) {
                Map<Object, Object> itemMasp = new HashMap<>();
                for (Map itemMap : packageStagesItem) {
                    String id = itemMap.get("id") + "";
                    itemMasp.put("package_id", id);
                    List<Map> stageBuildIng = packageDiscountDao.getPackageStageBuildIng(itemMasp);
                    itemMap.put("buildData", stageBuildIng);
                }
                resultMap.put("itemInfo", packageStagesItem);
            }
            resultMap.put("flowCode","My_Package_Stage");
            resultBody.setData(resultMap);
            resultBody.setCode(200);
            resultBody.setMessages("一揽子分期数据获取成功");
            return resultBody;
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.setCode(-3435);
            resultBody.setMessages("一揽子分期数据获取失败");
            return resultBody;
        }

    }

    @Override
    public ResultBody packageStagesDisApply(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {

            Map baseIdMap = packageDiscountDao.getBaseId(map);
            Map packageStagesData = packageDiscountDao.getPackageDisApply(baseIdMap);
            List<Map> packageStagesItem = packageDiscountDao.getPackageDisImemApply(baseIdMap);
            HashMap<Object, Object> resultMap = new HashMap<>();
            resultMap.put("basicInfo", packageStagesData);
            if (packageStagesItem != null && packageStagesItem.size() > 0) {
                Map<Object, Object> itemMasp = new HashMap<>();
                for (Map itemMap : packageStagesItem) {
                    String id = itemMap.get("id") + "";
                    itemMasp.put("package_id", id);
                    List<Map> stageBuildIng = packageDiscountDao.getPackageStageBuildIng(itemMasp);
                    itemMap.put("buildData", stageBuildIng);
                }
                resultMap.put("itemInfo", packageStagesItem);
            }
            resultMap.put("flowCode","My_Sales_Dis");
            resultBody.setData(resultMap);
            resultBody.setCode(200);
            resultBody.setMessages("一揽子折扣数据获取成功");
            return resultBody;
        } catch (Exception e) {
            resultBody.setCode(-3435);
            resultBody.setMessages("一揽子折扣数据获取失败");
            return resultBody;
        }


    }

    //过滤空数据
    public Map filterMapData(Map<String, Object> map) {
        Map<Object, Object> maps = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String values = entry.getValue() + "";
            if (!"".equals(values) && !"null".equalsIgnoreCase(values)) {
                maps.put(entry.getKey(), entry.getValue());
            }
        }
        return maps;
    }



    /**
     * 一揽子分期折扣列表
     *
     */
    @Override
    public ResultBody stagesSelect(Map map){
        PageHelper pageHelper=new PageHelper();
        pageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        ResultBody<Object> resultBody = new ResultBody<>();
        List<Map> list= packageDiscountDao.stagesSelect(map);
        PageInfo pageInfo = new PageInfo(list);
        resultBody.setData(pageInfo) ;
        resultBody.setCode(200);
        return   resultBody;
    }

    @Override
    public String getFlowCode(String josnid) {

        return packageDiscountDao.getFlowCode(josnid);
    }
    @Override
    public ResultBody getFlowDataInfo(String jsonId){
        ResultBody<Object> resultBody = new ResultBody<>();
        if(jsonId==null||"".equals(jsonId)||"null".equalsIgnoreCase(jsonId)){
            resultBody.setCode(-34356);
            resultBody.setMessages("流程主数据参数缺失!");
            return  resultBody;
        }
        Map dataInfo = packageDiscountDao.getFlowDataInfo(jsonId);
        resultBody.setData(dataInfo);
        resultBody.setCode(200);
        resultBody.setMessages("流程数据详情获取成功!");
        return  resultBody;
    }
    @Override
    public ResultBody windowPhase(){
        ResultBody<Object> resultBody = new ResultBody<>();
        try {

            Map<Object, Object> paramMap = new HashMap<>();

            String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //当前时间
            paramMap.put("nowDate",nowDate);
            List<Map> windowPashDate = packageDiscountDao.getWindowPashDate(paramMap);
            //时间集合
            Calendar calendar=Calendar.getInstance();
            List<Long> timelist = new ArrayList<>();
            Map<Object, Object> maps = new HashMap<>();
            if(windowPashDate!=null&&windowPashDate.size()>0){
                if(windowPashDate.size()==1){
                    Map map = windowPashDate.get(0);
                    resultBody.setData(map);
                    resultBody.setCode(200);
                    return resultBody;
                }else{
                    for (Map map : windowPashDate) {
                        //获取时间
                        String thisTime=map.get("thisTime")+"";
                        DateTime dateTime1 = DateUtil.parse(thisTime);
                        calendar.setTime(dateTime1);
                        long thisTimes = calendar.getTimeInMillis();
                        DateTime dateTime2 = DateUtil.parse(nowDate);
                        calendar.setTime(dateTime2);
                        long nowTimes = calendar.getTimeInMillis();
                        long times= (nowTimes-thisTimes);
                        timelist.add(times<0?-times:times);
                        maps.put(times<0?-times+"":times+"",map);
                    }
                    Long min = Collections.min(timelist);
                    String minJson=maps.get(min+"")+"";
                    if(!"".equals(minJson)&&!"null".equalsIgnoreCase(minJson)){
                        Map map= (Map) maps.get(min+"");
                        if(map!=null&&map.size()>0){
                            resultBody.setData(map);
                            resultBody.setCode(200);
                            return  resultBody;
                        }
                    }
                    resultBody.setMessages("无需设置窗口期!");
                    resultBody.setCode(200);
                    return  resultBody;
                }
            }else{
                resultBody.setCode(200);
                resultBody.setMessages("无需设置窗口期");
                return  resultBody;
            }

        }catch (Exception e){
            e.printStackTrace();
            resultBody.setCode(-3434);
            resultBody.setMessages("窗口期设置失败!");
            return  resultBody;
        }

    }
}

