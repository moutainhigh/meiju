package cn.visolink.firstplan.preemptionopen.service.impl;

import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignSevenDao;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.openbeforeseven.dao.OpenBeforeSevenDayDao;
import cn.visolink.firstplan.openbeforeseven.service.impl.OpenBeforeSevenDayServiceImpl;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.firstplan.preemptionopen.dao.PreemptionOpenDao;
import cn.visolink.firstplan.preemptionopen.service.PreemptionOpenService;
import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import cn.visolink.utils.FilterMapUtils;
import cn.visolink.utils.UUID;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.visolink.utils.WeekSplitUtil.getWeekSplitList;

/**
 * @author sjl 抢开申请
 * @Created date 2020/7/15 11:14 上午
 */
@Service
@Transactional
public class PreemptionOpenServiceImpl  implements PreemptionOpenService {
    @Autowired
    private PreemptionOpenDao preemptionOpenDao;
    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;
    @Autowired
    DesignTwoIndexDao designTwoIndexDao;
    @Autowired
    private DesignSevenDao designSevenDao;
    @Autowired
    private FileDao fileDao;

    //借用首开前7天节点数据库访问接口
    @Autowired
    private OpenBeforeSevenDayDao openBeforeSevenDayDao;
    //借用首开前21天节点数据库访问接口
    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;

    @Autowired
    private OpenBeforeSevenDayServiceImpl openBeforeSevenDayService;

    @Autowired
    private TakeLandService takeLandService;
    /**
     * 抢开申请渲染数据（初始化/创建版本/审批预览）
     * @param map
     * @return
     */
    @Override
    public ResultBody viewPreemptionOpenData(Map map) {
        //返回数据封装
        Map<Object, Object> resultMap = new HashMap<>();
        //节点id
        String plan_node_id=map.get("plan_node_id")+"";
        //操作方式
        String create=map.get("create")+"";
        //存放主数据
        Map<Object, Object> dataMap = new HashMap<>();
        try {
            //查询当前节点的抢开数据
            List<Map> preeOenVersionData = preemptionOpenDao.getPreemptionOpenVersionData(map);
            //如果当前计划存在抢开记录，并且当前操作不是创建版本，查询历史数据，渲染页面
            if(preeOenVersionData!=null&&preeOenVersionData.size()>0&&!"new".equals(create)){
                //默认获取第一个版本/最新
                Map<String,String> versionMap = preeOenVersionData.get(0);
                String versionId = versionMap.get("id");
                resultMap.put("preeOpenVersion",preeOenVersionData);
                //查询抢先开盘预估指标+开盘当天客户到访预估+当前实际达成进度
                Map preeOpenMainData = openBeforeSevenDayDao.getOpenApplayMainData(versionId);
                if(preeOpenMainData!=null){
                    dataMap.putAll(preeOpenMainData);
                }
                //查询抢先开盘时间调整+抢先开盘客储计划调整+办卡方式
                Map preeOpenDelayData = preemptionOpenDao.getPreemptionOpenDelayData(versionMap);
                if(preeOpenDelayData!=null){
                    dataMap.putAll(preeOpenDelayData);
                }
                resultMap.put("preeOpenMainData",dataMap);
                //查询均价数据
                List<Map> preeOpenAvgData = openBeforeSevenDayDao.getFirstOpenAvgData(versionId);
                resultMap.put("preeOpenAvgData",preeOpenAvgData);
                //查询竞品情况数据
                List<Map> preeOpenPoducts = openBeforeSevenDayDao.getCompetingpPoducts(versionId);
                resultMap.put("preeOpenPoducts",preeOpenPoducts);
                //查询客储节点数据
                List<Map> preOpencustomerStorage = openBeforeSevenDayDao.getCustomerStorageNodeDataChange(versionMap);
                resultMap.put("preOpencustomerStorage",preOpencustomerStorage);
                //查询周拆分数据
                List<Map> preOpenweekData = openBeforeSevenDayDao.getWeekData(versionMap);
                resultMap.put("preOpenweekData",preOpenweekData);
                //查询附件数据
                List fileLists = fileDao.getFileLists(versionId);
                resultMap.put("preeOpenFileList",fileLists);
                resultMap.put("preeOpenMainData",dataMap);
                String approval_stuat = String.valueOf(versionMap.get("approval_stuat"));

                if("2".equals(approval_stuat)){
                    updatePreeOpenData(dataMap,resultMap,map);
                }
                //主数据

                return ResultBody.success(resultMap);
            }
            //创建版本或初始化数据
            else{
                String versionId="";
                //查询是否有已经审批通过的数据版本
                Map preeOpenapplayadopt = preemptionOpenDao.getApplayadoptPreeOpen(plan_node_id);
                if(preeOpenapplayadopt!=null&&preeOpenapplayadopt.size()>0){
                    preeOpenapplayadopt.remove("version");
                    versionId=preeOpenapplayadopt.get("id")+"";
                }
                Map openDelayData = preemptionOpenDao.getPreemptionOpenDelayData(preeOpenapplayadopt);
                if(openDelayData!=null){
                    openDelayData.remove("delayOpenId");
                    dataMap.putAll(openDelayData);
                }
                if(preeOpenapplayadopt!=null){
                    preeOpenapplayadopt.remove("id");
                    dataMap.putAll(preeOpenapplayadopt);
                }
                //查询竞品情况，审批通过的
                //查询竞品情况数据
                List<Map> preeOpenPoducts = openBeforeSevenDayDao.getCompetingpPoducts(versionId);
                resultMap.put("preeOpenPoducts",preeOpenPoducts);
                //查询客储节点数据
               Map<Object, Object> versionMap = new HashMap<>();
                versionMap.put("id",versionId);

                List<Map> preOpencustomerStorage = openBeforeSevenDayDao.getCustomerStorageNodeDataChange(versionMap);
                resultMap.put("preOpencustomerStorage",preOpencustomerStorage);
                //查询周拆分数据
                List<Map> preOpenweekData = openBeforeSevenDayDao.getWeekData(versionMap);
                resultMap.put("preOpenweekData",preOpenweekData);
                updatePreeOpenData(dataMap,resultMap,map);
                return ResultBody.success(resultMap);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-2006,"抢开数据渲染失败:"+e.toString());
        }
    }

    /**
     * 获取周拆分数据
     * @param map
     * @return
     */
    @Override
    public ResultBody getWeekSpiltData(Map map) {
        Map<Object, Object> resultMap = new HashMap<>();
        try {
            ResultBody resultBody = getWeekSplitList((map.get("start_time") + ""), map.get("end_time") + "");
            if(resultBody.getCode()==200){
                List<Map> data = (List<Map>) resultBody.getData();
                //临时生成一条周拆分流程
                String flow_id = UUID.randomUUID().toString();
                if(data!=null&&data.size()>0){
                    map.put("approval_stuat",4);
                    map.put("node_level",6);
                    map.put("version",1);
                    map.put("id",flow_id);
                    preemptionOpenDao.insertGuestFlow(map);
                    for (Map weekDatum : data) {
                        weekDatum.put("plan_add",0);
                        weekDatum.put("plan_total",0);
                        weekDatum.put("plan_task_per",0);
                        weekDatum.put("node_level",6);
                        weekDatum.put("version",1);
                        weekDatum.put("plan_id",map.get("plan_id"));
                        weekDatum.put("plan_node_id",map.get("plan_node_id"));
                        weekDatum.put("flow_id",flow_id);
                        weekDatum.put("id",UUID.randomUUID().toString());
                        openBeforeSevenDayDao.insertWeekData(weekDatum);
                    }
                    map.put("node_level",6);
                    map.put("flow_id",flow_id);
                    List<Map> guestWeekSplitData = preemptionOpenDao.getGuestWeekSplitData(map);
                    //删除原数据
                    map.put("flow_id",flow_id);
                    preemptionOpenDao.deleteOldWeekData(map);

                    //查询首开前21天是否已完成
                    //map.put("plan_id")
                    map.put("node_level",6);
                    Map twentydays = preemptionOpenDao.openThefirstTwentydays(map);
                    boolean flag=true;
                    if(twentydays!=null&&twentydays.size()>0){
                        flag=false;
                    }
                    //封装周拆分数据
                    resultMap.put("preOpenweekData",guestWeekSplitData);
                    //21天是否完成
                    resultMap.put("isComplete",flag);
                    return ResultBody.success(resultMap);
                }
            }else{
                return  resultBody;
            }
        }catch (Exception e){
            e.printStackTrace();
            return  ResultBody.error(-1005,"数据生成失败:"+e.toString());
        }
        return null;
    }

    @Override
    public ResultBody savePreemptionOpenData(Map map) {
        String username=map.get("username")+"";
        if("null".equals(username)||"".equals(username)){
            username="shenyl02";
        }
        String project_id=map.get("project_id")+"";
        String plan_id=map.get("plan_id")+"";
        String button=map.get("button")+"";
        String plan_node_id=map.get("plan_node_id")+"";
        try {
            Map preeOpenMainData = (Map) map.get("preeOpenMainData");
            if(preeOpenMainData!=null){
                String preeOpenId=preeOpenMainData.get("preeOpenId")+"";
                String delayOpenId=preeOpenMainData.get("delayOpenId")+"";
                String version=preeOpenMainData.get("version")+"";
                //生成版本号
                Integer versionNumber = preemptionOpenDao.getVersionNumber(map);
                int newVersion=1;
                if(!"".equals(version)&&!"null".equals(version)){
                    newVersion=Integer.parseInt(version);
                }else{
                    if(versionNumber!=null){
                        newVersion= versionNumber;
                    }
                }
                String mainId = UUID.randomUUID().toString();
                preeOpenMainData.put("plan_id",plan_id);
                preeOpenMainData.put("plan_node_id",plan_node_id);
                if(!"null".equals(preeOpenId)&&!"".equals(preeOpenId)){
                    //修改数据
                    mainId=preeOpenId;
                    preeOpenMainData.put("id",mainId);
                    Map filterMap = FilterMapUtils.filterMap(preeOpenMainData);
                    openBeforeSevenDayDao.updateOpenApplayMainData(filterMap);
                }else{
                    preeOpenMainData.put("id",mainId);
                    preeOpenMainData.put("version",newVersion);
                    preeOpenMainData.put("approval_stuat",2);
                    Map filterMap = FilterMapUtils.filterMap(preeOpenMainData);
                    openBeforeSevenDayDao.insertOpenApplayMainData(filterMap);
                }
                if(!"null".equals(delayOpenId)&&!"".equals(delayOpenId)){
                    //修改数据
                    preeOpenMainData.put("id",delayOpenId);
                    preeOpenMainData.put("plan_node_id",mainId);
                    preeOpenMainData.put("plan_id",plan_id);
                    Map filterMap = FilterMapUtils.filterMap(preeOpenMainData);
                    openbeforetwentyoneDao.updateDelayApplyData(filterMap);
                }else{
                    preeOpenMainData.put("id",UUID.randomUUID().toString());
                    preeOpenMainData.put("plan_node_id",mainId);
                    preeOpenMainData.put("plan_id",plan_id);
                    Map filterMap = FilterMapUtils.filterMap(preeOpenMainData);
                    openbeforetwentyoneDao.insertDelayApplyData(filterMap);
                }
                //清楚附属数据
                preemptionOpenDao.clearFsData(mainId);
                Map<String, Object> storageNodeMap = new HashMap<>();
                String flow_id = UUID.randomUUID().toString();
                storageNodeMap.put("change", 2);
                storageNodeMap.put("approval_stuat", 2);
                storageNodeMap.put("node_level", 6);
                storageNodeMap.put("plan_node_id", mainId);
                storageNodeMap.put("plan_id", plan_id);
                storageNodeMap.put("id", flow_id);
                storageNodeMap.put("version", newVersion);
                openBeforeSevenDayDao.insertCustomerStoreFlow(storageNodeMap);
                List<Map> preOpenweekData = (List<Map>) map.get("preOpenweekData");
                if(preOpenweekData!=null&&preOpenweekData.size()>0){
                    for (Map preOpenweekDatum : preOpenweekData) {
                        preOpenweekDatum.put("plan_node_id",plan_node_id);
                        preOpenweekDatum.put("plan_id",plan_id);
                        preOpenweekDatum.put("flow_id",flow_id);
                        preOpenweekDatum.put("id",UUID.randomUUID().toString());
                        preOpenweekDatum.put("version", newVersion);
                        Map filterMap = FilterMapUtils.filterMap(preOpenweekDatum);
                        openBeforeSevenDayDao.insertWeekData(filterMap);
                    }
                }
                List<Map> preOpencustomerStorage = (List<Map>) map.get("preOpencustomerStorage");
                if(preOpencustomerStorage!=null&&preOpencustomerStorage.size()>0){
                    //公共参数
                    HashMap<Object, Object> cMap = new HashMap<>();
                    cMap.put("plan_node_id",mainId);
                    cMap.put("plan_id",plan_id);
                    cMap.put("flow_id",flow_id);
                    cMap.put("version", newVersion);
                    if(preOpencustomerStorage.size()==2){
                        //查询顶设2的部分客储数据
                        List<Map> maps = preemptionOpenDao.designTwoCustomerStorageData(plan_id, "3");
                        for (Map preOpencustomeMap : maps) {
                            preOpencustomeMap.put("id",UUID.randomUUID().toString());
                            preOpencustomeMap.putAll(cMap);
                            openBeforeSevenDayDao.insertCustomerStore(preOpencustomeMap);
                        }
                    }
                    if(preOpencustomerStorage.size()==3){
                        //查询顶设2的部分客储数据
                        List<Map> maps = preemptionOpenDao.designTwoCustomerStorageData(plan_id, "2");
                        for (Map preOpencustomeMap : maps) {
                            preOpencustomeMap.put("id",UUID.randomUUID().toString());
                            preOpencustomeMap.putAll(cMap);
                            openBeforeSevenDayDao.insertCustomerStore(preOpencustomeMap);
                        }
                    }
                    for (Map preOpencustomeMap : preOpencustomerStorage) {
                        preOpencustomeMap.put("id",UUID.randomUUID().toString());
                        preOpencustomeMap.putAll(cMap);
                        preOpencustomeMap.put("changes", "1");
                        Map filterMap = FilterMapUtils.filterMap(preOpencustomeMap);
                        openBeforeSevenDayDao.insertCustomerStore(filterMap);
                    }
                }

                List<Map> preeOpenAvgData = (List<Map>) map.get("preeOpenAvgData");
                if (preeOpenAvgData != null && preeOpenAvgData.size() > 0) {
                    for (Map firstOpenAvgDatum : preeOpenAvgData) {
                        firstOpenAvgDatum.put("id", UUID.randomUUID().toString());
                        firstOpenAvgDatum.put("day_id", mainId);
                        firstOpenAvgDatum.put("plan_id", plan_id);
                        firstOpenAvgDatum.put("plan_node_id", plan_node_id);
                        Map filterMap = FilterMapUtils.filterMap(firstOpenAvgDatum);
                        openBeforeSevenDayDao.insertPriceAvg(filterMap);
                    }
                }
                List<Map> preeOpenPoducts = (List<Map>) map.get("preeOpenPoducts");
                if (preeOpenPoducts != null && preeOpenPoducts.size() > 0) {
                    //添加竞品情况数据
                    for (Map competingpPoduct : preeOpenPoducts) {
                        competingpPoduct.put("id",UUID.randomUUID().toString());
                        competingpPoduct.put("delay_id", mainId);
                        competingpPoduct.put("plan_id", plan_id);
                        competingpPoduct.put("plan_node_id", plan_node_id);
                        openBeforeSevenDayDao.insertCompetingpPoducts(competingpPoduct);
                    }
                }
                //获取附件数据
                List<Map> preeOpenFileList = (List<Map>) map.get("preeOpenFileList");
                if (preeOpenFileList != null && preeOpenFileList.size() > 0) {
                    //删除附件
                    fileDao.delFileByBizId(mainId);
                    for (Map fileMap : preeOpenFileList) {
                        fileMap.put("bizID", mainId);
                        fileDao.updateFileBizID(fileMap);
                    }
                    //添加新附件
                }
                //发起流程所需参数
                Map<Object, Object> resultMapFlow = new HashMap<>();

                JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
                String jobName = null;
                if (jwtUser != null) {
                    jobName = jwtUser.getJob().get("JobName") + "";
                }
                Map<Object, Object> flowParams = new HashMap<>();
                if("submit".equals(button)){
                    flowParams.put("project_id", project_id);
                    flowParams.put("creator", username);
                    flowParams.put("flow_code", "fp_preemptionOpen");
                    flowParams.put("json_id", mainId);
                    flowParams.put("TITLE", "抢开申请");
                    flowParams.put("post_name", jobName);
                    flowParams.put("orgName", "fp_preemptionOpen");
                    Map comcommon = new HashMap();
                    /*flowParams.put("comcommon", comcommon+"");*/
                    takeLandService.insertFlow(flowParams);
                }
                resultMapFlow.put("BSID", "FP");
                resultMapFlow.put("BTID", "fp_preemptionOpen");
                resultMapFlow.put("codeBOID", mainId);
                resultMapFlow.put("bkUserID", username);
                resultMapFlow.put("loginKey", "");
                return ResultBody.success(resultMapFlow);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-3075,"数据保存失败");
        }
        return null;
    }

    @Override
    public ResultBody swicthVersion(Map map) {
        String versionId=map.get("id")+"";
        String plan_id=map.get("plan_id")+"";

        Map<Object, Object> dataMap = new HashMap<>();
        Map<Object, Object> resultMap = new HashMap<>();
        //查询审批人信息
        Map appllayDataInfo = preemptionOpenDao.getAppllayData(versionId);
        resultMap.put("appllayDataInfo",appllayDataInfo);
        //查询抢先开盘预估指标+开盘当天客户到访预估+当前实际达成进度
        Map preeOpenMainData = openBeforeSevenDayDao.getOpenApplayMainData(versionId);
        if(preeOpenMainData!=null){
            map.put("plan_id", preeOpenMainData.get("plan_id"));
            //获取项目
            String project_info = preemptionOpenDao.getProjectInfo(preeOpenMainData.get("plan_id") + "");
            map.put("project_id", project_info);
            map.put("plan_node_id", preeOpenMainData.get("plan_node_id"));
            dataMap.putAll(preeOpenMainData);
        }
        //查询抢先开盘时间调整+抢先开盘客储计划调整+办卡方式
        Map preeOpenDelayData = preemptionOpenDao.getPreemptionOpenDelayData(map);
        if(preeOpenDelayData!=null){

            dataMap.putAll(preeOpenDelayData);
        }
        resultMap.put("preeOpenMainData",dataMap);
        //查询均价数据
        List<Map> preeOpenAvgData = openBeforeSevenDayDao.getFirstOpenAvgData(versionId);
        resultMap.put("preeOpenAvgData",preeOpenAvgData);
        //查询竞品情况数据
        List<Map> preeOpenPoducts = openBeforeSevenDayDao.getCompetingpPoducts(versionId);
        resultMap.put("preeOpenPoducts",preeOpenPoducts);
        //查询客储节点数据
        List<Map> preOpencustomerStorage = openBeforeSevenDayDao.getCustomerStorageNodeDataChange(map);
        resultMap.put("preOpencustomerStorage",preOpencustomerStorage);
        //查询周拆分数据
        List<Map> preOpenweekData = openBeforeSevenDayDao.getWeekData(map);
        resultMap.put("preOpenweekData",preOpenweekData);
        //查询附件数据
        List fileLists = fileDao.getFileLists(versionId);
        resultMap.put("preeOpenFileList",fileLists);
        resultMap.put("preeOpenMainData",dataMap);
        String approval_stuat=dataMap.get("approval_stuat")+"";
        if("2".equals(approval_stuat)){
            String toJSONString = JSON.toJSONString(dataMap);
            System.err.println(toJSONString);
            updatePreeOpenData(dataMap,resultMap,map);
        }
        return ResultBody.success(resultMap);
    }

    /**
     * 审批通过回调
     * @param map
     * @return
     */
    @Override
    public ResultBody approvedCallback(Map map) {
        String json_id = map.get("businesskey") + "";
        String eventType = map.get("eventType") + "";
        //获取明源推送的审批单类型
        String orgName = map.get("orgName") + "";
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("id", json_id);
        //审批中
        if("3".equals(eventType)){
            paramMap.put("status", eventType);
            //更改数据状态
            preemptionOpenDao.updatePreemptionOpen(paramMap);
        }
        //如果是驳回或者撤回。将本版本数据重置为编制状态
         else if ("5".equals(eventType) || "6".equals(eventType)||"7".equals(eventType)) {
            paramMap.put("status", 2);
            //更改数据状态
            preemptionOpenDao.updatePreemptionOpen(paramMap);
        }
         //审批通过
         else if("4".equals(eventType)){
            paramMap.put("status", 4);
            //更改数据状态
            preemptionOpenDao.updatePreemptionOpen(paramMap);
            //查询时间铺排所需的必要参数
            Map openDelayData = preemptionOpenDao.getPreemptionOpenDelayData(paramMap);
            Map preeOpenMainData = openBeforeSevenDayDao.getOpenApplayMainData(json_id);
            if(openDelayData!=null&&openDelayData.size()>0){
                //时间铺排-更改节点的动态完成时间
                timeArrangement(openDelayData);
                String plan_id=openDelayData.get("plan_id")+"";
                //处理21天客储数据
                map.put("plan_id",plan_id);
                map.put("node_level",6);

                //获取抢先的天数
                String delay_daynum=openDelayData.get("delay_daynum")+"";
                map.put("plan_node_id",preeOpenMainData.get("plan_node_id"));
                //查询首开前21天是否已完成
                Map twentydays = preemptionOpenDao.openThefirstTwentydays(map);
                boolean flag=false;
                //如果前21天已经完成
                if(twentydays!=null&&twentydays.size()>0){
                    flag=true;
                }
                //当前时间与首开前21天动态完成时间的时间差
                int now_diff_21time = Integer.parseInt(openDelayData.get("now_diff_21time") + "");
                //当前时间与首开前7天动态完成时间的时间差
                int now_diff_7time = Integer.parseInt(openDelayData.get("now_diff_7time") + "");
                //如果当前时间>=21天动态完成时间
                map.put("json_id",json_id);
                if(now_diff_21time>=0){
                    //处理21天节点
                    if(flag){
                        //如果21天已经完成-原数据不变
                    }else{
                        //若首开前21天节点未完成，给21天节点生成一套最新的并且审批通过的客储数据
                        map.put("status",4);
                        //审批通过更改当前计划的节点为当前节点
                        map.put("node_level",6);
                        openbeforetwentyoneDao.updateThisNodeforTwenDay(map);
                        //更改实际完成时间
                        paramMap.put("id",json_id);
                        openBeforeSevenDayDao.updatePlanNodeFinshTime(paramMap);

                        Map<Object, Object> paramMaps = new HashMap<>();
                        //查询前21天的节点id
                        String  plan_node_id= preemptionOpenDao.getPlanNodeIdByPlan(plan_id, "6");
                        paramMaps.put("plan_node_id",plan_node_id);
                        //将节点设置为完成状态
                        openbeforetwentyoneDao.updatePlanTime(paramMaps);
                        initCustomerStroageData(map);
                    }
                }else{
                    if(flag){
                        //如果21天已经完成-数据不变
                    }else{
                        //若首开前21天节点未完成，给21天节点生成一套最新的处于编制状态的客储数据
                        map.put("status",10);
                        initCustomerStroageData(map);
                    }
                }
                //如果当前时间>=七天动态完成时间
                Map<Object, Object> dataMap = new HashMap<>();
                //查询前7天的节点id
                String  plan_node_id= preemptionOpenDao.getPlanNodeIdByPlan(plan_id, "7");
                dataMap.put("plan_node_id",plan_node_id);
                dataMap.put("plan_id",plan_id);
                dataMap.put("json_id",json_id);
                if(now_diff_7time>=0){
                    //给前7天生成一版数据
                    //生成审批通过数据
                    dataMap.put("approval_stuat",4);
                    createSevenData(dataMap,flag);

                    //审批通过更改当前计划的节点为当前节点
                    map.put("node_level",7);
                    openbeforetwentyoneDao.updateThisNodeforTwenDay(map);
                    //更改实际完成时间
                    paramMap.put("id",json_id);
                    openBeforeSevenDayDao.updatePlanNodeFinshTime(paramMap);
                    //将节点设置为完成状态
                    openbeforetwentyoneDao.updatePlanTime(dataMap);
                }else {
                    //生成编制状态数据
                    dataMap.put("approval_stuat",2);
                    createSevenData(dataMap,flag);
                }
            }

        }

        return ResultBody.success(null);
    }

    /**
     *
     * @param dataMap 参数集
     * @param flag 21天是否完成
     */
    public void  createSevenData(Map dataMap,boolean flag){
        String json_id=dataMap.get("json_id")+"";
        String plan_id=dataMap.get("plan_id")+"";
        String approval_stuat=dataMap.get("approval_stuat")+"";
        String id = UUID.randomUUID().toString();
        List<Map> versionData = openBeforeSevenDayDao.getVersionData(dataMap);
        Integer version=1;
        if(versionData!=null&&versionData.size()>0){
            Map versionMap = versionData.get(0);
            dataMap.put("version",versionMap.get("version"));
            version=Integer.parseInt(versionMap.get("version")+"");
            dataMap.put("sevenDayId",versionMap.get("id"));
        }else{
            dataMap.put("version",0);
            dataMap.put("json_id",json_id);
        }
        dataMap.put("id",id);
        dataMap.put("approval_stuat",approval_stuat);
        //生成首开前7天表单数据
        preemptionOpenDao.createSevenDayData(dataMap);

        //生成客储数据
        Map<Object, Object> storageNodeMap = new HashMap<>();
        String flow_id = java.util.UUID.randomUUID().toString();

        storageNodeMap.put("change", 0);
        storageNodeMap.put("approval_stuat", approval_stuat);
        storageNodeMap.put("node_level", 7);
        storageNodeMap.put("plan_node_id", id);
        storageNodeMap.put("plan_id", plan_id);
        storageNodeMap.put("id", flow_id);
        storageNodeMap.put("version", version);
        openBeforeSevenDayDao.insertCustomerStoreFlow(storageNodeMap);
        //查询出流程id
        dataMap.put("flow_id",flow_id);
        //前7天的节点id
        String  plan_node_id= preemptionOpenDao.getPlanNodeIdByPlan(plan_id, "7");
        //前7天节点id
        dataMap.put("plan_node_id",plan_node_id);
        if(flag){
            //前21天取数-说明21天的数据没有更新，不再使用21天的数据
            List<Map> nodeTime = preemptionOpenDao.getNodeTime(dataMap);
            String threeTime="";
            String twoTime="";
            String twenTime="";
            if(nodeTime!=null&&nodeTime.size()>0){
                threeTime  = nodeTime.get(0).get("node_time")+"";
                twoTime  = nodeTime.get(1).get("node_time")+"";
                twenTime  = nodeTime.get(2).get("node_time")+"";

            }
            dataMap.put("threeTime",threeTime);
            dataMap.put("twoTime",twoTime);
            dataMap.put("twenTime",twenTime);
            preemptionOpenDao.initCustomerStorageDataTwenty(dataMap);
            //生成周拆分数据
            dataMap.put("version",version);
            preemptionOpenDao.initWeekSpiltStorageData(dataMap);

           // preemptionOpenDao.createCustomerAndWeekData(dataMap);
        }else{
            //前21天取数
            //生成客储数据
            preemptionOpenDao.initCustomerStorageData(dataMap);
            String  plan_node_ids= preemptionOpenDao.getPlanNodeIdByPlan(plan_id, "6");
            dataMap.put("flow_id",flow_id);
            dataMap.put("twenyday_plan_node_id",plan_node_ids);
            preemptionOpenDao.createCustomerAndWeekData(dataMap);
        }

    }


    @Override
    public void initCustomerStroageData(Map map) {
        String plan_node_id=map.get("plan_node_id")+"";
        String plan_id=map.get("plan_id")+"";
        String status=map.get("status")+"";
        Integer maxVersion = preemptionOpenDao.getFlowMaxVersion(plan_node_id);
        Integer version=1;
        if(maxVersion!=null){
            version=maxVersion;
        }
        if("10".equals(status)){
            //删除原来的数据
            preemptionOpenDao.deleteOladData(map);
        }
        //添加客储流程数据
        Map<Object, Object> storageNodeMap = new HashMap<>();
        String flow_id = UUID.randomUUID().toString();
        storageNodeMap.put("change", 0);
        storageNodeMap.put("approval_stuat", status);
        storageNodeMap.put("node_level", 6);
        storageNodeMap.put("plan_node_id", plan_node_id);
        storageNodeMap.put("plan_id", plan_id);
        storageNodeMap.put("id", flow_id);
        storageNodeMap.put("version", version);
        openBeforeSevenDayDao.insertCustomerStoreFlow(storageNodeMap);
        List<Map> nodeTime = preemptionOpenDao.getNodeTime(map);
        String threeTime="";
        String twoTime="";
        if(nodeTime!=null&&nodeTime.size()>0){
            threeTime  = nodeTime.get(0).get("node_time")+"";
            twoTime  = nodeTime.get(1).get("node_time")+"";
        }
        map.put("threeTime",threeTime);
        map.put("twoTime",twoTime);
        //查询出流程id
        map.put("flow_id",flow_id);
        //生成客储数据
        preemptionOpenDao.initCustomerStorageData(map);
        //生成周拆分数据
        map.put("version",version);
        preemptionOpenDao.initWeekSpiltStorageData(map);
    }

    /**
     * 数据更新
     * @param dataMap 数据集
     * @param resultMap 返回数据
     * @param paramMap 参数集
     */
    public void updatePreeOpenData(Map dataMap,Map resultMap,Map<String,String> paramMap){
        String plan_id = paramMap.get("plan_id");
        String plan_node_id = paramMap.get("plan_node_id");
        String project_id = paramMap.get("project_id");
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        //查询顶设2数据-更新数据
        Map designtwoCoreData = openBeforeSevenDayDao.getDesigntwoCoreData(plan_id);
        if(designtwoCoreData==null){
            designtwoCoreData=new HashMap();
        }
        //查询顶设2首开均价
        List<Map> designtwoAvgPrice = openBeforeSevenDayDao.getDesigntwoAvgPrice(plan_id);

        String str1 = "";
        String str2 = "";
        String str3 = "";
        if (designtwoAvgPrice != null && designtwoAvgPrice.size() > 0) {
            for (Map avgMap : designtwoAvgPrice) {
                String invest_avg = avgMap.get("invest_avg") + "";
                String rules_avg = avgMap.get("rules_avg") + "";
                String designtwo_avg = avgMap.get("designtwo_avg") + "";
                if (!"null".equals(invest_avg) && !"0.00".equals(invest_avg)) {
                    str1 += avgMap.get("product_type") + "、";
                }
                if (!"null".equals(rules_avg) && !"0.00".equals(rules_avg)) {
                    str2 += avgMap.get("product_type") + "、";
                }
                if (!"null".equals(designtwo_avg) && !"0.00".equals(designtwo_avg)) {
                    str3 += avgMap.get("product_type") + "、";
                }
            }
        }
        if (!"".equals(str1)) {
            str1 = str1.substring(0, str1.length() - 1);
        }
        if (!"".equals(str2)) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        if (!"".equals(str3)) {
            str3 = str3.substring(0, str3.length() - 1);
        }
        designtwoCoreData.put("estimate_product_type", str3);
        designtwoCoreData.put("estimate_operation_name", str3);
        designtwoCoreData.put("invest_product_type", str1);
        designtwoCoreData.put("rules_product_type", str2);
        designtwoCoreData.put("designtwo_product_type", str3);
        //designtwoCoreData.put("customer_cause", "");



        //客户情况数据
        Map xukeactCustomer = openBeforeSevenDayDao.getXukeactCustomer(plan_id);
        Map customerInfoForDesigntwo = openBeforeSevenDayDao.getCustomerInfoForDesigntwo(plan_id);
        if (xukeactCustomer != null) {
            //计算达成率
            //来访目标
            int come_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("come_customer_target") + "");
            //来访达成
            int come_customer_actual = Integer.parseInt(xukeactCustomer.get("come_customer_actual") + "");
            //小卡目标
            int lesser_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("lesser_customer_target") + "");
            //小卡实际
            int lesser_customer_actual = Integer.parseInt(xukeactCustomer.get("lesser_customer_actual") + "");
            //小卡目标
            int big_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("big_customer_target") + "");
            //大卡实际
            int big_customer_actual = Integer.parseInt(xukeactCustomer.get("big_customer_actual") + "");

            if (0 != come_customer_target) {
                designtwoCoreData.put("come_customer_per", df.format(come_customer_actual * 100 / come_customer_target));
            } else {
                designtwoCoreData.put("come_customer_per", 0);
            }
            if (0 != lesser_customer_target) {
                designtwoCoreData.put("lesser_customer_per", df.format(lesser_customer_actual * 100 / lesser_customer_target));
            } else {
                designtwoCoreData.put("lesser_customer_per", 0);
            }
            if (0 != big_customer_target) {
                designtwoCoreData.put("big_customer_per", df.format(big_customer_actual * 100 / big_customer_target));
            } else {
                designtwoCoreData.put("big_customer_per", 0);
            }
        } else {
            designtwoCoreData.put("big_customer_per", 0);
            designtwoCoreData.put("lesser_customer_per", 0);
            designtwoCoreData.put("come_customer_per", 0);
        }
        //计算达成率
        //来访目标
        int come_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("come_customer_target") + "");
        //来访达成
        int come_customer_actual = Integer.parseInt(xukeactCustomer.get("come_customer_actual") + "");
        //小卡目标
        int lesser_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("lesser_customer_target") + "");
        //小卡实际
        int lesser_customer_actual = Integer.parseInt(xukeactCustomer.get("lesser_customer_actual") + "");
        //小卡目标
        int big_customer_target = Integer.parseInt(customerInfoForDesigntwo.get("big_customer_target") + "");
        //大卡实际
        int big_customer_actual = Integer.parseInt(xukeactCustomer.get("big_customer_actual") + "");

        if (0 != come_customer_target) {
            designtwoCoreData.put("come_customer_per", df.format(come_customer_actual * 100 / come_customer_target));
        } else {
            designtwoCoreData.put("come_customer_per", 0);
        }
        if (0 != lesser_customer_target) {
            designtwoCoreData.put("lesser_customer_per", df.format(lesser_customer_actual * 100 / lesser_customer_target));
        } else {
            designtwoCoreData.put("lesser_customer_per", 0);
        }
        if (0 != big_customer_target) {
            designtwoCoreData.put("big_customer_per", df.format(big_customer_actual * 100 / big_customer_target));
        } else {
            designtwoCoreData.put("big_customer_per", 0);
        }
        String estimate_open_node=dataMap.get("estimate_open_node")+"";
        if(!"".equals(estimate_open_node)&&!"null".equals(estimate_open_node)){
            designtwoCoreData.remove("estimate_open_node");
        }
        //更新部分数据
        dataMap.putAll(designtwoCoreData);
        //抢先开盘时间调整数据更新
        //取顶设2核心指标战规版开盘日期
        Map dingsheTwoDateData = openbeforetwentyoneDao.getDingsheTwoDateData(paramMap);
        Map openTime = openbeforetwentyoneDao.getDingsheTwoOpenTime(paramMap);
        if(openTime==null){
            openTime=new HashMap<>();
        }
        openTime.put("plan_id", plan_id);
        Map planCustomerData = openbeforetwentyoneDao.getPlanCustomerData(openTime);
        openTime.put("project_id", project_id);
        //查询当前项目的附属项目
       /* List<Map> project_idList = openbeforetwentyoneDao.getSubsidiaryProject(project_id);
        openTime.put("projectidList",project_idList);*/
        Map xukeCustomerData = openbeforetwentyoneDao.getXukeCustomerData(openTime);
        if (dingsheTwoDateData == null) {
            dingsheTwoDateData = new HashMap();
        }
        if (planCustomerData != null) {
            //设置延期开盘后新增客储预估时间段
            dingsheTwoDateData.putAll(planCustomerData);
        }
        if(customerInfoForDesigntwo!=null){
            dingsheTwoDateData.putAll(customerInfoForDesigntwo);
        }
        if (openTime != null) {
            dingsheTwoDateData.putAll(openTime);
        }
        if(xukeactCustomer!=null){
            dingsheTwoDateData.putAll(xukeactCustomer);
        }
        if (xukeCustomerData != null) {
            dingsheTwoDateData.putAll(xukeCustomerData);
        }
        dataMap.putAll(dingsheTwoDateData);
        //预计客储达成数据初始化
        /*dingsheTwoDateData.put("estimate_plan_visit_num",0);
        dingsheTwoDateData.put("estimate_plan_little_num",0);
        dingsheTwoDateData.put("estimate_plan_little_per",0);
        dingsheTwoDateData.put("estimate_plan_big_num",0);
        dingsheTwoDateData.put("estimate_plan_big_per",0);
        dingsheTwoDateData.put("estimate_plan_take_per",0);*/

        List<Map> preeOpenAvgData = (List<Map>) resultMap.get("preeOpenAvgData");
        if(preeOpenAvgData!=null&&preeOpenAvgData.size()>0){
        }else{
            //均价
            resultMap.put("preeOpenAvgData",designtwoAvgPrice);
        }
        //查看21天是否已完成
        paramMap.put("node_level","6");
        Map twentydays = preemptionOpenDao.openThefirstTwentydays(paramMap);
        if(twentydays!=null&&twentydays.size()>0){
            dataMap.putAll(twentydays);
        }
        //主数据
        resultMap.put("preeOpenMainData",dataMap);
    }
    /**
     * 时间铺排
     */
    public void timeArrangement(Map map){
        {
            Map<Object, Object> paramMap = new HashMap<>();
            //计划
            String plan_id=map.get("plan_id")+""+"";
            //新申请开盘前21天日期
            String new_applay_21time=map.get("new_applay_21time")+""+"";
            String new_applay_7time=map.get("new_applay_7time")+""+"";
            String new_first_time=map.get("new_first_time")+""+"";
            try {
                //获取前21天的预警天数
                paramMap.put("node_level",6);
                paramMap.put("plan_id",plan_id);
                Map waringDay = openbeforetwentyoneDao.getWaringDay(paramMap);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//要转换的日期格式，根据实际调整""里面内容
                if(waringDay!=null){
                    String warning_day=waringDay.get("warning_day")+"";
                    int day1 = Integer.parseInt(warning_day);
                    long new_applay_21time2=simpleDateFormat.parse(new_applay_21time).getTime();
                    long countTime=new_applay_21time2-(day1*86400000);
                    String format = simpleDateFormat.format(countTime);
                    waringDay.put("plan_start_time",format);
                   // waringDay.put("plan_end_time",new_applay_21time);
                    waringDay.put("delay_open_time",new_applay_21time);
                    openbeforetwentyoneDao.updateNodesPlanStartTime(waringDay);
                }
                //前7天
                paramMap.put("node_level",7);
                Map waringDay2 = openbeforetwentyoneDao.getWaringDay(paramMap);
                if(waringDay2!=null){
                    String warning_day=waringDay2.get("warning_day")+"";
                    int day1 = Integer.parseInt(warning_day);
                    long new_applay_7time2=simpleDateFormat.parse(new_applay_7time).getTime();
                    long countTime=new_applay_7time2-(day1*86400000);
                    String format = simpleDateFormat.format(countTime);
                    waringDay2.put("plan_start_time",format);
                    //waringDay2.put("plan_end_time",new_applay_7time);
                    waringDay2.put("delay_open_time",new_applay_7time);
                    openbeforetwentyoneDao.updateNodesPlanStartTime(waringDay2);
                }
                //开盘
                paramMap.put("node_level",8);
                Map waringDay3 = openbeforetwentyoneDao.getWaringDay(paramMap);
                if(waringDay3!=null){
                    String warning_day=waringDay3.get("warning_day")+"";
                    int day1 = Integer.parseInt(warning_day);
                    long new_first_time2=simpleDateFormat.parse(new_first_time).getTime();
                    long countTime=new_first_time2-(day1*86400000);
                    String format = simpleDateFormat.format(countTime);
                    waringDay3.put("plan_start_time",format);
                   // waringDay3.put("plan_end_time",new_first_time);
                    waringDay3.put("delay_open_time",new_first_time);
                    openbeforetwentyoneDao.updateNodesPlanStartTime(waringDay3);

                    //更改开盘时间
                    waringDay3.put("plan_id",paramMap.get("plan_id"));
                    waringDay3.put("new_first_time",new_first_time);
                    openbeforetwentyoneDao.updateOpenTimeForPlan(waringDay3);
                }
                //开盘后一个月
                paramMap.put("node_level",9);
                Map waringDay4 = openbeforetwentyoneDao.getWaringDay(paramMap);
                if(waringDay4!=null){
                    String warning_day=waringDay4.get("warning_day")+"";
                    int day1 = Integer.parseInt(warning_day);
                    long new_first_time2=simpleDateFormat.parse(new_first_time).getTime();
                    long countTime=new_first_time2-(day1*86400000);
                    String format = simpleDateFormat.format(countTime);
                    /**
                     * 计算延期后一个月
                     */
                    Calendar rightNow = Calendar.getInstance();
                    Date parse = simpleDateFormat.parse(new_first_time);
                    rightNow.setTime(parse);
                    rightNow.add(Calendar.MONTH, 1);
                    Date monthAddtime = rightNow.getTime();
                    String formats = simpleDateFormat.format(monthAddtime);
                    waringDay4.put("plan_start_time",format);
                    waringDay4.put("delay_open_time",formats);
                    openbeforetwentyoneDao.updateNodesPlanStartTime(waringDay4);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}
