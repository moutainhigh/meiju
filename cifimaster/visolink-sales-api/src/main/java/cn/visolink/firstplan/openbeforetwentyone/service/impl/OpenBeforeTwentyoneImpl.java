package cn.visolink.firstplan.openbeforetwentyone.service.impl;
import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignSevenDao;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.firstplan.openbeforetwentyone.service.OpenBeforeTwentyoneService;
import cn.visolink.salesmanage.flieUtils.dao.FileDao;
import cn.visolink.utils.WeekUtil;
import com.alibaba.fastjson.JSON;
import org.apache.catalina.startup.Catalina;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sjl
 * @Created date 2020/3/6 2:03 下午
 */
@Transactional
@Service
public class OpenBeforeTwentyoneImpl implements OpenBeforeTwentyoneService {

    @Autowired
    DesignSevenDao designSevenDao;
    @Value("${oaflow.fpFlowCode}")
    private String fpFlowCode;
    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;
    @Autowired
    private FileDao fileDao;

    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;
    @Autowired
    private TakeLandService takeLandService;

    @Override
    public VisolinkResultBody viewdelayOpenApplay(Map map) {
        Map<Object, Object> resultMap = new HashMap<>();
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();
        String create = map.get("create") + "";
        String node_level = map.get("node_level") + "";
        //查询数据库中是否有
        List<Map> mapList = openbeforetwentyoneDao.selectIsHaveDelayApplyData(map);
        List<Map> versionData = null;
        if (mapList != null && mapList.size() > 0 && !"new".equals(create)) {
            if ("6".equals(node_level)) {
                versionData = openbeforetwentyoneDao.getVsersionData2(map);
            } else {
                versionData = openbeforetwentyoneDao.selectVersionData(map);
            }

            Map versionMap = versionData.get(0);
            String versionId = versionMap.get("id") + "";
            String version = versionMap.get("version") + "";
            //查询渲染：延期开盘申请表数据
            Map OpenApplyData = openbeforetwentyoneDao.selectExtensionOpenApplyData(versionId);
            resultMap.put("openApplyData", OpenApplyData);
            //查询客储计划调整数据
            List<Map> customerNodeStorage = openbeforetwentyoneDao.selectCustomerStorageNodeData(versionId);
            resultMap.put("customerNodeStorage", customerNodeStorage);
            resultMap.put("versionData", versionData);
            //查询周拆分数据
            map.put("versionId", versionId);
            List<Map> selectWeekData = openbeforetwentyoneDao.selectWeekData(versionId);
            resultMap.put("weekData", selectWeekData);
            //查询附件信息
            List fileLists = fileDao.getFileLists(versionId);
            resultMap.put("fileList", fileLists);
            resultBody.setResult(resultMap);
            resultBody.setMessages("数据获取成功!");
            VisolinkResultBody<Object> body = new VisolinkResultBody<>();
            return resultBody;
        } else {
            //取顶设2核心指标战规版开盘日期
            Map dingsheTwoDateData = openbeforetwentyoneDao.getDingsheTwoDateData(map);
            //查询当前节点有没有已经审批通过的数据
            Map applayDepotTwentyData = openbeforetwentyoneDao.getApplayDepotTwentyData(map);

            Map openTime = openbeforetwentyoneDao.getDingsheTwoOpenTime(map);
            if(openTime==null){
                openTime=new HashMap<>();
            }
            openTime.put("plan_id", map.get("plan_id"));
            openTime.put("node_level",node_level);
            Map planCustomerData = openbeforetwentyoneDao.getPlanCustomerData(openTime);
            openTime.put("project_id", map.get("project_id") + "");
            //查询当前项目的附属项目
       /*     List<Map> project_idList = openbeforetwentyoneDao.getSubsidiaryProject(map.get("project_id") + "");
            openTime.put("projectidList",project_idList);*/

            Map xukeCustomerData = openbeforetwentyoneDao.getXukeCustomerData(openTime);
            if (dingsheTwoDateData == null) {
                dingsheTwoDateData = new HashMap();
            }
            if (planCustomerData != null) {
                //设置延期开盘后新增客储预估时间段
                dingsheTwoDateData.putAll(planCustomerData);
            }
            if (openTime != null) {
                dingsheTwoDateData.putAll(openTime);
            }
            if (xukeCustomerData != null) {
                dingsheTwoDateData.putAll(xukeCustomerData);
            }


            //预计客储达成数据初始化
            dingsheTwoDateData.put("estimate_plan_visit_num",0);
            dingsheTwoDateData.put("estimate_plan_little_num",0);
            dingsheTwoDateData.put("estimate_plan_little_per",0);
            dingsheTwoDateData.put("estimate_plan_big_num",0);
            dingsheTwoDateData.put("estimate_plan_big_per",0);
            dingsheTwoDateData.put("estimate_plan_take_per",0);

            if(applayDepotTwentyData!=null){
                dingsheTwoDateData.putAll(applayDepotTwentyData);
                String id=applayDepotTwentyData.get("id")+"";
                //查询当前节点已经审批通过的最新客储数据
                List<Map> customerNodeStorage = openbeforetwentyoneDao.selectCustomerStorageNodeData(id);
                resultMap.put("customerNodeStorage",customerNodeStorage);
                dingsheTwoDateData.remove("id");
            }
            //取顶设2客储计划调整数据
            resultMap.put("openApplyData", dingsheTwoDateData);

            resultBody.setResult(resultMap);
            resultBody.setMessages("数据初始化成功!");
            return resultBody;
        }
    }

    @Override
    public VisolinkResultBody switchVersion(Map map) {
        Map<Object, Object> resultMap = new HashMap<>();
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();
        String versionId = map.get("id") + "";
        //查询审批人数据
        Map appllayDataInfo = openbeforetwentyoneDao.getAppllayDataInfo(versionId);
        resultMap.put("getAppllayDataInfo",appllayDataInfo);
        //查询渲染：延期开盘申请表数据
        Map OpenApplyData = openbeforetwentyoneDao.selectExtensionOpenApplyData(versionId);
        resultMap.put("openApplyData", OpenApplyData);
        //查询客储计划调整数据
        List<Map> customerNodeStorage = openbeforetwentyoneDao.selectCustomerStorageNodeData(versionId);
        resultMap.put("customerNodeStorage", customerNodeStorage);

        List<Map> selectWeekData = openbeforetwentyoneDao.selectWeekData(versionId);
        resultMap.put("weekData", selectWeekData);
        //查询附件信息
        List fileLists = fileDao.getFileLists(versionId);
        resultMap.put("fileList", fileLists);
        resultBody.setResult(resultMap);
        resultBody.setMessages("数据获取成功!");
        return resultBody;
    }

    @Override
    public VisolinkResultBody saveelayOpenApplay(Map map) {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        String button = map.get("button") + "";
        String approvalStuat = "2";
        String str = "提交成功";
        Map openApplyData = (Map) map.get("openApplyData");
        openApplyData.put("level", map.get("node_level"));
        String flowDataId=null;
        String id = openApplyData.get("id") + "";
        String version=openApplyData.get("version")+"";
        String node_level=map.get("node_level")+"";
        String project_id=map.get("project_id")+"";
        String username=map.get("username")+"";
        String dataId = UUID.randomUUID().toString();
        boolean flag=false;
        //快速审批
        String buttonKs=map.get("buttonKs")+"";

        if(!"".equals(buttonKs)&&!"null".equals(buttonKs)){
            flag=true;
        }

        if ("save".equals(button)) {
            //保存数据
            str = "保存成功";
        }

        ;
        if ("submit".equals(button)||flag) {
            JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(username);
            String jobName = null;
            if (jwtUser != null) {
                jobName = jwtUser.getJob().get("JobName") + "";
            }
            Map flowParams = new HashMap();
            if(!"".equals(id) && !"null".equals(id)){
                flowParams.put("json_id", id);
                flowDataId=id;
            }else{
                flowParams.put("json_id", dataId);
                flowDataId=dataId;
            }
            flowParams.put("project_id", project_id);
            flowParams.put("creator", username);
            if("6".equals(node_level)){
                flowParams.put("TITLE", "首开前21天延期开盘申请");
                flowParams.put("orgName", "fp_open_twentyone_off");
            }else{
                flowParams.put("TITLE", "首开前7天延期开盘申请");
                flowParams.put("orgName", "fp_open_seven_off");
            }
            flowParams.put("flow_code", fpFlowCode);
            flowParams.put("post_name", jobName);
            Map comcommon = new HashMap();
            String designtwo_time=openApplyData.get("designtwo_time")+"";

            String MM=designtwo_time.substring(5, 7);//截取系统月份
            String yy=designtwo_time.substring(0, 4);//截取系统年份

            String new_first_time=openApplyData.get("new_first_time")+"";
            String mm="";
            String year="";

            if(!"".equals(new_first_time)&&!"null".equals(new_first_time )){
                mm=new_first_time.substring(5, 7);
                year=new_first_time.substring(0, 4);
            }else {
                year=yy;
            }
            if(MM.equals(mm)){
                if(year.equals(yy)){
                    comcommon.put("isNextMonth",0);
                }else{
                    comcommon.put("isNextMonth",1);
                }
            }else{
                comcommon.put("isNextMonth",1);
                System.out.println("不在本月份");
            }

            comcommon.put("delay_daynum",openApplyData.get("delay_daynum"));

            flowParams.put("comcommon", JSON.toJSONString(comcommon));
            takeLandService.insertFlow(flowParams);
/*
            updateOpen_timeData(openApplyData);*/
            //更改当前节点

        }
        if (!"".equals(id) && !"null".equals(id)) {
            //修改
            openApplyData.put("approval_stuat", approvalStuat);

            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("id",id);
            openbeforetwentyoneDao.delteWeekData(paramMap);
            openbeforetwentyoneDao.delteWeekDataNodePlan(paramMap);
            openbeforetwentyoneDao.updateDelayApplyData(openApplyData);
          //查询flowID
            String flowId = openbeforetwentyoneDao.getFlowId(id);

            //修改节点客储计划调整数据
            List<Map> listNodeStorage = (List<Map>) map.get("customerNodeStorage");
            if (listNodeStorage != null && listNodeStorage.size() > 0) {
                for (Map nodeStorageMap : listNodeStorage) {
                    nodeStorageMap.put("id", UUID.randomUUID().toString());
                    nodeStorageMap.put("flow_id",flowId);
                    nodeStorageMap.put("plan_node_id",map.get("plan_node_id"));
                    nodeStorageMap.put("plan_id",map.get("plan_id"));
                    nodeStorageMap.put("version",version);
                    openbeforetwentyoneDao.insertCustomerNodeStorage(nodeStorageMap);
                }
            }
            List<Map> weekData = (List<Map>) map.get("weekData");
            if (weekData != null && weekData.size() > 0) {
                for (Map weekDatum : weekData) {
                    weekDatum.put("id", UUID.randomUUID().toString());
                    weekDatum.put("plan_id", map.get("plan_id"));
                    weekDatum.put("plan_node_id", map.get("plan_node_id"));
                    weekDatum.put("flow_id",flowId);
                    weekDatum.put("version",version);
                    weekDatum.put("node_level",node_level);
                    openbeforetwentyoneDao.insertWeekData(weekDatum);
                }
            }
            //获取附件数据
            List<Map>  fileList = (List<Map>) map.get("fileList");
            if(fileList!=null&&fileList.size()>0){
                //删除附件
                fileDao.delFileByBizId(id);
                for (Map fileMap : fileList) {
                    fileMap.put("bizID",id);
                    fileDao.updateFileBizID(fileMap);
                }
                //添加新附件
            }

        } else {
            Map<Object, Object> paramMap = new HashMap<>();
            paramMap.put("plan_id", map.get("plan_id"));
            paramMap.put("plan_node_id", map.get("plan_node_id"));
            paramMap.put("level", map.get("node_level"));
            Map versionMap = openbeforetwentyoneDao.createVserion(paramMap);
            Integer vserion;
            String create = map.get("thisVserion") + "";
            if (versionMap != null) {
                vserion = Integer.parseInt(versionMap.get("version") + "");
            } else {
                vserion = 0;
            }
            if ("".equals(create) || "null".equals(create)) {
                openApplyData.put("version", vserion + 1);
                vserion=vserion+1;
            }


            //创建版本
            openApplyData.put("approval_stuat", approvalStuat);
            openApplyData.put("id", dataId);
            openApplyData.put("plan_id", map.get("plan_id"));
            openApplyData.put("plan_node_id", map.get("plan_node_id"));
            openApplyData.put("level", map.get("node_level"));
            //保存延期申请数据
            openbeforetwentyoneDao.insertDelayApplyData(openApplyData);
            Map<Object, Object> paramMaps = new HashMap<>();
            String flow_id=UUID.randomUUID().toString();
            paramMaps.put("id",flow_id);
            paramMaps.put("node_level",map.get("node_level"));
            paramMaps.put("plan_node_id",dataId);
            paramMaps.put("version",vserion);
            paramMaps.put("approval_stuat",approvalStuat);
            //创建新版本
            openbeforetwentyoneDao.createFLowVersion(paramMaps);
            //保存客储计划调整数据
            List<Map> customerPlanList = (List<Map>) map.get("customerNodeStorage");
            if (customerPlanList != null && customerPlanList.size() > 0) {
                for (Map customerPlanMap : customerPlanList) {
                    //listIndex
                    customerPlanMap.put("id", UUID.randomUUID().toString());
                    customerPlanMap.put("delay_id", dataId);
                    customerPlanMap.put("flow_id",flow_id);
                    customerPlanMap.put("plan_node_id",map.get("plan_node_id"));
                    customerPlanMap.put("plan_id",map.get("plan_id"));
                    customerPlanMap.put("version",vserion);
                    openbeforetwentyoneDao.insertCustomerNodeStorage(customerPlanMap);
                }
            }

            List<Map> weekData = (List<Map>) map.get("weekData");
            if (weekData != null && weekData.size() > 0) {
                for (Map weekDatum : weekData) {
                    weekDatum.put("id", UUID.randomUUID().toString());
                    weekDatum.put("delay_id", dataId);
                    weekDatum.put("plan_id", map.get("plan_id"));
                    weekDatum.put("plan_node_id", map.get("plan_node_id"));
                    weekDatum.put("flow_id",flow_id);
                    weekDatum.put("version",vserion);
                    weekDatum.put("node_level",node_level);
                    openbeforetwentyoneDao.insertWeekData(weekDatum);
                }
            }
            //获取附件数据
            List<Map>  fileList = (List<Map>) map.get("fileList");
            if(fileList!=null&&fileList.size()>0){
                //删除附件
                fileDao.delFileByBizId(dataId);
                for (Map fileMap : fileList) {
                    fileMap.put("bizID",dataId);
                    fileDao.updateFileBizID(fileMap);
                }
                //添加新附件
            }
        }
        //发起流程所需参数
        Map<Object, Object> resultMapFlow = new HashMap<>();
        resultMapFlow.put("BSID","FP");
        resultMapFlow.put("BTID","skcslc");
        resultMapFlow.put("codeBOID",flowDataId);
        resultMapFlow.put("bkUserID",username);
        resultMapFlow.put("loginKey","");
        response.setMessages(str);
        response.setResult(resultMapFlow);
        response.setCode(200);
        return response;
    }

    @Override
    public VisolinkResultBody getWeeklyResolution(Map map) throws ParseException {
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        // 1.开始时间 2019-06-09 13:16:04
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");//要转换的日期格式，根据实际调整""里面内容
        String time = map.get("start_time") + "";
        if ("".equals(time) || "null".equals(time)) {
            return response;
        }
        long startTime = sdf2.parse(map.get("start_time") + "").getTime();
        long endTime = sdf2.parse(map.get("end_time") + "").getTime();

        if (endTime <= startTime) {
            response.setCode(400);
            response.setMessages("延期开盘日期不能小于原开盘日期");
            return response;
        }
        // 3.开始时间段区间集合
        List<Long> beginDateList = new ArrayList<Long>();
        // 4.结束时间段区间集合
        List<Long> endDateList = new ArrayList<Long>();
        // 5.调用工具类
        WeekUtil.getIntervalTimeByWeek(startTime, endTime, beginDateList, endDateList);
        // 6.打印输出
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        List<Map> weekList = new ArrayList<>();
        Map<Object, Object> maps = new HashMap<>();
        maps.put("week", "完整波段");
        weekList.add(maps);
        for (int i = 0; i < endDateList.size(); i++) {
            Long beginStr = beginDateList.get(i);
            Long endStr = endDateList.get(i);
            String begin1 = sdf.format(new Date(beginStr));
            String end1 = sdf.format(new Date(endStr));
            Map<Object, Object> weekMap = new HashMap<>();
            weekMap.put("week", "第" + (i + 1) + "周");
            weekMap.put("start_time", begin1);
            weekMap.put("end_time", end1);
            weekMap.put("day_date", begin1 + "-" + end1);
            weekList.add(weekMap);
        }
        response.setResult(weekList);
        return response;
    }

    //更改开盘、开盘前7天、开盘前21天的开盘节点

    @Override
    public void updateOpen_timeData(Map map){
        Map<Object, Object> paramMap = new HashMap<>();
        //计划
        String plan_id=map.get("plan_id")+""+"";
        String plan_node_id=map.get("plan_node_id")+"";
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

    @Override
    public ResultBody exportDelayOpenData(Map<Object, Object> map, HttpServletRequest request, HttpServletResponse response) {
        String filePath;
        //配置本地模版路径
        String realpath = null;
    /*  realpath="/Users/WorkSapce/Java/旭辉集团/Java/marketing-control-api/cifimaster/visolink-sales-api/src/main/webapp/TemplateExcel/twentyDayDelayOpen.xlsx";
        filePath=realpath;*/
        //配置服务器模版路径
       realpath = request.getServletContext().getRealPath("/");
        //导出模版路径
        String templatePath = File.separator + "TemplateExcel" + File.separator + "twentyDayDelayOpen.xlsx";
        filePath = realpath + templatePath;
        //配置服务器本地模版路径
        FileInputStream fileInputStream = null;
        String levelNode="首开前21天";
        String tabNode="延期开盘申请-填报导出数据";
        //获取当前有效数据
        String id=map.get("id")+"";
        String projectName=map.get("projectName")+"";
        String node_level=map.get("node_level")+"";

        try {
            File templateFile = new File(filePath);
            if(!templateFile.exists()){
                throw new BadRequestException(1004, "未读取到配置的导出模版，请先配置导出模版!");
            }
            //使用poi读取模版文件
            fileInputStream = new FileInputStream(templateFile);
            if (fileInputStream == null) {
                throw new BadRequestException(1004, "未读取到模版文件!");
            }
            //创建工作簿对象，
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            //获取延期开盘申请所需要的模版sheet页
            XSSFSheet sheetAt = workbook.getSheetAt(0);
            //查询数据
            Map delayData = openbeforetwentyoneDao.selectExtensionOpenApplyData(id);
            if("7".equals(node_level)){
                levelNode="首开前7天";
            }
            //获取第一行第一列
            XSSFRow oldRow = sheetAt.getRow(0);
            XSSFCell cell0 = oldRow.getCell(0);
            //标题
            cell0.setCellValue(projectName+"-"+levelNode+tabNode);

            String[] dateArray={"rules_time","designtwo_time","new_applay_21time","new_applay_7time","new_first_time","delay_daynum","delay_reasons"};
            String[] hstrArray={"战规版开盘日期","顶设2开盘日期","新申请开盘前21天","新申请开盘前7天","新申请开盘日期","延期天数","延期原因"};

            if(delayData!=null){
                //填写延期开盘申请表数据
                for (int i=3;i<10;i++){
                    //样式
                    XSSFRow row= sheetAt.getRow(i);
                    //第一列
                    XSSFCell cellt = row.getCell(0);
                    //第二列
                    XSSFCell cell1 = row.getCell(1);
                    //第三列
                    XSSFCell cell2 = row.getCell(2);
                    if(i!=9){
                        cellt.setCellValue("日期调整");
                    }
                    if(i==9){
                        cellt.setCellValue("延期原因");
                        String value = delayData.get(dateArray[i - 3]) + "";
                        if(!"".equals(value)&&!"null".equals(value)){
                            cell1.setCellValue(delayData.get(dateArray[i-3])+"");
                        }

                    }else{
                        cell1.setCellValue(hstrArray[i-3]);
                        cell2.setCellValue(delayData.get(dateArray[i-3])+"");
                    }

                }
                //填写客储达成进度数据
                //顶设2客储计划与达成计划数据
                String[] storePlanArray={"reach_plan_time","reach_plan_visit_num","reach_plan_little_num","reach_plan_little_per","reach_plan_big_num","reach_plan_big_per","reach_plan_sub_num","reach_plan_take_per"};
                //顶设2客储计划与达成实际数据
                String[] storeActualArray={"reach_actual_time","reach_actual_visit_num","reach_actual_littel_num","reach_actual_littel_per","reach_actual_big_num","reach_actual_big_per","reach_actual_sub_num","reach_actual_take_per"};
                //延期开盘后新增客储预估-计划数据
                String[] storeEstimateArray={"estimate_plan_time","estimate_plan_visit_num","estimate_plan_little_num","estimate_plan_little_per","estimate_plan_big_num","estimate_plan_big_per","estimate_plan_sub_num","estimate_plan_take_per"};
                //合计客储数据
                String[] storeSumteArray={"sum_time","sum_visit_num","sum_little_num","sum_little_per","sum_big_num","sum_big_per","sum_sub_num","sum_take_per"};
                for (int i=13;i<=16;i++){
                    //获取原样式
                    XSSFRow row = sheetAt.getRow(i);
                    XSSFCell cell1 = row.getCell(0);
                    XSSFCell cell2 = row.getCell(1);
                    if(i==13){
                        cell1.setCellValue("顶设2客储计划与达成");
                        cell2.setCellValue("计划");
                        for (int j=0;j<=7;j++){
                            XSSFCell cell = row.getCell(j+2);
                            cell.setCellValue(delayData.get(storePlanArray[j])+"");
                        }
                    }else if(i==14){
                        cell1.setCellValue("顶设2客储计划与达成");
                        cell2.setCellValue("实际");
                        for (int j=0;j<=7;j++){
                            XSSFCell cell = row.getCell(j+2);
                            cell.setCellValue(delayData.get(storeActualArray[j])+"");
                        }
                    }else if(i==15){
                        cell1.setCellValue("延期开盘后新增客储预估");
                        cell2.setCellValue("计划");
                        for (int j=0;j<=7;j++){
                            XSSFCell cell = row.getCell(j+2);
                            cell.setCellValue(delayData.get(storeEstimateArray[j])+"");
                        }
                    }else if(i==16){
                        cell1.setCellValue("延期开盘后合计客储");
                        for (int j=0;j<=7;j++){
                            XSSFCell cell = row.getCell(j+2);
                            cell.setCellValue(delayData.get(storeSumteArray[j])+"");
                        }
                    }

                }
                //节点客储计划字段数组
                String[] nodeArray={"node_name","node_time","line_name","visit_num","little_num","little_per","big_num","big_per","sub_num","make_per"};
                //查询变更节点客储计划
                List<Map> customerNodeStorage = openbeforetwentyoneDao.selectCustomerStorageNodeData(id);

                if(customerNodeStorage!=null&&customerNodeStorage.size()>0){
                    int a=20+customerNodeStorage.size();
                    //填充变更节点客储计划数据
                    for (int i=20;i<a;i++){
                        XSSFRow row = sheetAt.getRow(i);
                            for (int j=0;j<10;j++){
                                Map nodeMap = customerNodeStorage.get(i - 20);
                                XSSFCell cell = row.getCell(j);
                                cell.setCellValue(nodeMap.get(nodeArray[j])+"");
                        }
                    }
                }

                //周拆分字段数组

                String[] weekArray={"week","day_date","plan_add","plan_total","plan_task_per"};
                int b=0;
                int count=0;
                XSSFRow row20 = sheetAt.getRow(20);
                XSSFCell cell20_3= row20.getCell(3);
                XSSFCellStyle cell20_3CellStyle = cell20_3.getCellStyle();
                //填充周拆分数据
                List<Map> selectWeekData = openbeforetwentyoneDao.selectWeekData(id);
                String total = openbeforetwentyoneDao.selectWeekDataTotal(id);
                if(selectWeekData!=null&&selectWeekData.size()>0) {
                    sheetAt.shiftRows(26,sheetAt.getLastRowNum(),selectWeekData.size() ,true,false);
                    b=26+selectWeekData.size();
                    for (int i=26;i<b;i++){
                        XSSFRow row = sheetAt.createRow(i);
                        for (int j=0;j<5;j++){
                            Map weekMap = selectWeekData.get(i - 26);
                            XSSFCell cell = row.createCell(j);
                            cell.setCellStyle(cell20_3CellStyle);
                            String s = weekMap.get(weekArray[j]) + "";
                            if(i==26){
                                weekMap.remove("plan_task_per");
                            }
                            if(i==26&&j==2){
                                cell.setCellValue(Integer.parseInt(total));
                            }else{
                                if(!"".equals(s)&&!"null".equals(s)){
                                    cell.setCellValue(weekMap.get(weekArray[j])+"");
                                }
                            }
                        }
                    }
                }
               //填充办卡方式数据

                //办卡方式
                b=b+2;
                for (int i=b;i<b+4;i++){
                    System.out.println(i);
                    XSSFRow row = sheetAt.getRow(i);
                    XSSFCell cell = row.getCell(1);
                    if(i==b){
                        //填充小卡数据
                        cell.setCellValue(delayData.get("little_model")+"");
                    } else if(i==(b+2)){
                        cell.setCellValue(delayData.get("big_model")+"");
                    }
                }
                System.err.println(b);
                //合并小卡
              /*  CellRangeAddress region3 = new CellRangeAddress(b, b+1, 0, 0);
                sheetAt.addMergedRegion(region3);*/
             /*   CellRangeAddress region4 = new CellRangeAddress(b+2, b+3, 1, 5);
                sheetAt.addMergedRegion(region4);*/

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String formatDate = sdf.format(new Date());
                String fileName = projectName+"-"+levelNode+tabNode+"-"+formatDate+".xlsx";
                response.setContentType("application/vnd.ms-excel;charset=utf-8");

                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
                workbook.write(response.getOutputStream());
                response.getOutputStream().flush();
            }else{
                return  ResultBody.error(-1008,"未查询到当前节点的有效数据,导出失败!");
            }
        }catch (Exception e){
            e.printStackTrace();
            return  ResultBody.error(-1009,"导出数据失败:"+e.getMessage());
        }
        return ResultBody.success(null);
    }


}
