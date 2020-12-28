package cn.visolink.job.handle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.firstplan.dataAccess.controller.DataAccessController;
import cn.visolink.firstplan.dataAccess.service.DataAccessService;
import cn.visolink.firstplan.dingtalkmicroapp.controller.FirstBriefingController;
import cn.visolink.firstplan.message.controller.MessageSendController;
import cn.visolink.firstplan.message.controller.TemplateEngineController;
import cn.visolink.firstplan.planmonitoring.controller.PlanMontitorController;
import cn.visolink.firstplan.planmonitoring.service.PlanMontitorService;
import cn.visolink.firstplan.plannode.controller.CommonWeekListener;
import cn.visolink.firstplan.plannode.service.PlanNodeService;
import cn.visolink.salesmanage.datainterface.controller.DatainterfaceController;
import cn.visolink.salesmanage.datainterface.service.impl.DatainterfaceserviceImpl;
import cn.visolink.salesmanage.flow.controller.FlowController;
import cn.visolink.salesmanage.flow.service.FlowService;
import cn.visolink.salesmanage.gxcinterface.service.GXCInterfaceservice;
import cn.visolink.salesmanage.idmAll.controller.IdmSelectController;
import cn.visolink.salesmanage.plandatainterface.controller.PlanDataInterfaceController;
import cn.visolink.salesmanage.riskcontrolmanager.controller.RiskController;
import cn.visolink.salesmanage.riskcontrolmanager.service.RiskControlService;
import cn.visolink.system.projectmanager.service.projectmanagerService;
import cn.visolink.utils.StringUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TimeHandles {
    @Autowired
    private DatainterfaceController datainterfaceController;
    @Autowired
    private FirstBriefingController firstBriefingController;
    @Autowired
    private DataAccessController dataAccessController;
    @Autowired
    private CommonWeekListener commonWeekListener;
    @Autowired
    private PlanMontitorController planMontitorController;
    @Autowired
    private RiskController riskController;
    @Autowired
    private FlowController flowController;
    @Autowired
    private PlanDataInterfaceController planDataInterfaceController;
    @Autowired
    private IdmSelectController idmSelectController;
    @Autowired
    DatainterfaceserviceImpl datainterfaceservice;
    @Autowired
    RiskControlService riskControlService;
    @Autowired
    private TemplateEngineController templateEngineController;

    @Autowired
    private PlanMontitorService planMontitorService;

    @Autowired
    private MessageSendController messageSendController;
    /*
     * 接入xxl-job后的 定时任务,初始周数据
     * */
    @XxlJob("zhoujihua")
    public ReturnT<String> zhoujihua(String param) throws Exception {
        try {
            XxlJobLogger.log("初始周数据开始");
            XxlJobLogger.log("传递参数:" + param);
            XxlJobLogger.log("实际使用参数:" + "");
            datainterfaceController.zhoujihua();
            XxlJobLogger.log("初始周数据结束");
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /*
     * 接入xxl-job后的 定时任务,增量同步明源签约数据
     * */
    @XxlJob("addSign")
    public ReturnT<String> addSign(String param) throws Exception {
        try {
            XxlJobLogger.log("增量同步明源签约数据开始");
            XxlJobLogger.log("传递参数:" + param);
            Map map = new HashMap();
            map.put("startTime",param);
            String messages = datainterfaceController.addSignRequest(map);
            XxlJobLogger.log("增量同步明源签约数据--"+messages);
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }


    /*
     * 接入xxl-job后的 定时任务,增量同步明源认购数据
     * */
    @XxlJob("addOrder")
    public ReturnT<String> addOrder(String param) throws Exception {
        try {

            XxlJobLogger.log("增量同步明源认购数据开始");
            XxlJobLogger.log("传递参数:" + param);
            Map map = new HashMap();
            map.put("startTime",param);
            String messages = datainterfaceController.addOrderRequest(map);
            XxlJobLogger.log("增量同步明源认购数据--"+messages);
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /*
     * 接入xxl-job后的 定时任务,每月初始明源上年跟当前年认购签约数据
     * */
    @XxlJob("initMingYuanTwoYear")
    public ReturnT<String> initMingYuanTwoYear(String param) throws Exception {
        try {
            XxlJobLogger.log("每月初始明源上年跟当前年认购签约数据开始");
            XxlJobLogger.log("传递参数:" + param);
            XxlJobLogger.log("实际使用参数:" + "");
            datainterfaceController.initMingYuanTwoYear();
            XxlJobLogger.log("每月初始明源上年跟当前年认购签约数据结束");
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("initSupplyValue")
    public ReturnT<String> initSupplyValue(String param) throws Exception {
        try {
            XxlJobLogger.log("每月1号7点初始化所有供货数据开始");
            XxlJobLogger.log("传递参数:" + param);
            XxlJobLogger.log("实际使用参数:" + "");
            datainterfaceController.initSupplyValue();
            XxlJobLogger.log("每月1号7点初始化所有供货数据结束");
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }



    /*
     * 接入xxl-job后的 定时任务,每月1号12点初始月度计划报表数据
     * */
    @XxlJob("jituan")
    public ReturnT<String> jituan(String param) throws Exception {
        try {
            XxlJobLogger.log("每月1号12点初始月度计划报表数据开始");
            XxlJobLogger.log("传递参数:" + param);
            XxlJobLogger.log("实际使用参数:" + "");
            datainterfaceController.jituan();
            XxlJobLogger.log("每月1号12点初始月度计划报表数据结束");
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }


    /*
     * 接入xxl-job后的 定时任务,每天2点同步IDM岗位数据
     * */
    @XxlJob("initSysPostOrg")
    public ReturnT<String> initSysPostOrg(String param) throws Exception {
        try {
            XxlJobLogger.log("每天2点同步IDM岗位数据开始");
            XxlJobLogger.log("传递参数:" + param);
            XxlJobLogger.log("实际使用参数:" + "");
            idmSelectController.initSysPostOrg();
            XxlJobLogger.log("每天2点同步IDM岗位数据结束");
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /*
     * 接入xxl-job后的 定时任务,每天9点更新名称项目表
     * */
    @XxlJob("updataBasic")
    public ReturnT<String> updataBasic(String param) throws Exception {
        try {
            XxlJobLogger.log("每天9点更新名称项目表开始");
            XxlJobLogger.log("传递参数:" + param);
            XxlJobLogger.log("实际使用参数:" + "");
            planDataInterfaceController.updataBasic();
            XxlJobLogger.log("每天9点更新名称项目表结束");
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                XxlJobLogger.log("异常结束，错误信息:" + e);
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /*
     * 定时任务--对比周预估数据
     * */
    @XxlJob("weeklyForecast")

    public ReturnT<String> weeklyForecast(String params) throws Exception {
        try {
            Thread.sleep(new Random().nextInt(100));
            flowController.weeklyForecast();
        }catch (Exception e){
            if (e instanceof InterruptedException) {
                throw e;
            }else {
                ReturnT.FAIL.setMsg(e+"");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 定时更新首开计划节点各个节点亮灯状态
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob("updateNodeStatusTiming")
    public ReturnT<String> updateNodeStatusTiming (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("每天晚上12点更新首开计划节点亮灯状态");
            XxlJobLogger.log("传递参数:" + params);
            XxlJobLogger.log("实际使用参数:" + params);
            planMontitorController.updateNodeStatusTiming();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    @XxlJob("updateProjectRelationship")
    public ReturnT<String> updateProjectRelationship (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("每天晚上12点更新项目关系表-用于合并项目");
            XxlJobLogger.log("传递参数:" + params);
            XxlJobLogger.log("实际使用参数:" + params);
            planMontitorService.updateProjectRelationship();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("synMasterDataProject")
    public ReturnT<String> synMasterDataProject (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("同步主数据项目-");
            XxlJobLogger.log("传递参数:" + params);
            XxlJobLogger.log("实际使用参数:" + params);
            datainterfaceController.synMasterDataProject();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    @XxlJob("initProjectType")
    public ReturnT<String> initProjectType (String params) throws Exception{
        try {
            datainterfaceController.initProjectType();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("refGuestStorage")
    public ReturnT<String> refGuestStorage (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("定时更新储客数据报备，来访，小卡，小卡率，大卡，大卡率，认购，成交率");
            XxlJobLogger.log("传递参数:" + params);
            XxlJobLogger.log("实际使用参数:" + params);
            //写入储客记录表定时获取昨天到今天的报备跟来访
            dataAccessController.refGuestStorage();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    @XxlJob("sendNodeReport")
    public ReturnT<String> sendNodeReport (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("首开计划节点发送钉钉通知");
            XxlJobLogger.log("传递参数:" + params);
            XxlJobLogger.log("实际使用参数:" + params);
            dataAccessController.sendNodeReport();

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("initgetData")
    public ReturnT<String> initgetData (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("初始化风控数据");
            XxlJobLogger.log("传递参数:" + params);
            String startSyncTime = "";
            String endSyncTime = "";
            if (params.length() > 0 && params.contains("&")) {
                String[] paramsArray = params.split("&");
                for (String para : paramsArray) {
                    if (para.contains("=")) {
                        String[] kv = para.split("=");
                        String key = kv[0];
                        String value = kv[1];

                        if ("startSyncTime".equals(key)) {
                            startSyncTime = value;
                        }
                        if ("endSyncTime".equals(key)) {
                            endSyncTime = value;
                        }
                    }
                }
            }

            String yesterday = DateUtil.format(DateUtil.yesterday(), "yyyy-MM-dd");
            if (StringUtil.isEmpty(startSyncTime)) {
                startSyncTime = yesterday;
            }
            if (StringUtil.isEmpty(endSyncTime)) {
                endSyncTime = yesterday;
            }


            Map map = new HashMap();
            map.put("queryStartDate", "");
            map.put("queryEndDate", "");
            map.put("query", "全量");
            map.put("startSyncTime", startSyncTime);
            map.put("endSyncTime", endSyncTime);

            Boolean b = riskController.initgetData(map);
            if(b){
                return ReturnT.SUCCESS;
            }else{
                return ReturnT.FAIL;
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                e.printStackTrace();
                ReturnT.FAIL.setMsg(e + "");
            }
            return ReturnT.FAIL;
        }
    }
    @XxlJob("initCommonWeekPlan")
    public ReturnT<String> initCommonWeekPlan (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("周上报数据生成");
            XxlJobLogger.log("传递参数:" + params);
            XxlJobLogger.log("实际使用参数:" + params);


            commonWeekListener.initCommonWeekPlan();
            System.out.println("周上报数据生成，执行任务成功");

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    @XxlJob("updatePlanProjectName")
    public ReturnT<String> updatePlanProjectName (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("定时更新首开计划的项目名称");
            XxlJobLogger.log("执行开始");
            XxlJobLogger.log("实际使用参数:" + params);
            planMontitorController.updatePlanProjectName();
            XxlJobLogger.log("执行结束");
            System.out.println("更新首开计划的项目名称成功");
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            } else {
                ReturnT.FAIL.setMsg(e + "");
                e.printStackTrace();
            }
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("customerStorageDeviationTimedTasks")
    public ReturnT<String> customerStorageDeviationTimedTasks (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始生成首开计划客储预警消息");
            XxlJobLogger.log("执行开始");
            templateEngineController.customerStorageDeviationTimedTasks();
            XxlJobLogger.log("执行结束");
            XxlJobLogger.log("消息生成成功!");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    @XxlJob("threepiecesRemindTimedTasks")
    public ReturnT<String> threepiecesRemindTimedTasks (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始生成首开计划");
            XxlJobLogger.log("执行开始");
            XxlJobLogger.log("开始生成首开计划三大件延期提醒消息");
            templateEngineController.threepiecesRemindTimedTasks();
            XxlJobLogger.log("执行结束");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("sendMessageServer")
    public ReturnT<String> sendMessageServer (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始发送消息");
            XxlJobLogger.log("执行开始");
            XxlJobLogger.log("开始发送所有未发送消息列表");
            messageSendController.sendMessageServer();
            XxlJobLogger.log("执行结束");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 首开计划十二大节点逾期一天提醒
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob("sendNodeOverdueMesTimedTasks")
    public ReturnT<String> sendNodeOverdueMesTimedTasks (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始生成首开计划各个节点逾期消息");
            XxlJobLogger.log("执行开始");
            XxlJobLogger.log("开始生成首开计划各个节点逾期消息");
            templateEngineController.sendNodeOverdueMesTimedTasks();
            XxlJobLogger.log("执行结束");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    /**
     * 佣金管理增量获取佣金数据
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob("incrementCommission")
    public ReturnT<String> incrementCommission (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始同步结佣数据");
            XxlJobLogger.log("执行开始");
            if(Validator.isNumber(params)){
                datainterfaceController.incrementCommission(params);
            }else{
                XxlJobLogger.log("传入数据为非数字类型,默认执行前两天数据");
                datainterfaceController.incrementCommission(null);
            }
            XxlJobLogger.log("结束同步结佣数据");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("updateSoonNode")
    public ReturnT<String> updateSoonNode (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始更新this_node");
            XxlJobLogger.log("执行开始");
            planMontitorController.updateSoonNode();
            XxlJobLogger.log("结束更新this_node");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 同步周报数据
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob("updateZJHData")
    public ReturnT<String> updateZJHData (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始同步周报数据");
            XxlJobLogger.log("执行开始");
            datainterfaceController.updateZJHData();
            XxlJobLogger.log("结束同步周报数据");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 定时发送开盘播报钉钉消息
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob("firstBriefingMessage")
    public ReturnT<String> firstBriefingMessage (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始生成消息");
            XxlJobLogger.log("开始生成开盘播报消息");
            firstBriefingController.firstBriefingMessage();
            XxlJobLogger.log("消息生成完成，请前往消息列表查看");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    /**
     * 定时同步OA、销管及下游系统流程审批状态
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob("flowComparisonPush")
    public ReturnT<String> flowComparisonPush (String params) throws Exception{
        try {
            Thread.sleep(new Random().nextInt(100));
            XxlJobLogger.log("开始对比同步流程状态");
            firstBriefingController.flowComparisonPush();
            XxlJobLogger.log("流程状态同步成功");
        } catch (Exception e) {
            XxlJobLogger.log("错误信息:"+e.toString());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
}
