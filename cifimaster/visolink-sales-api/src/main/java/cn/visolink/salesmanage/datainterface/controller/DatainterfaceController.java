package cn.visolink.salesmanage.datainterface.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.commission.service.CommissionService;
import cn.visolink.firstplan.dataAccess.dao.DataAccessDao;
import cn.visolink.firstplan.receipt.service.ReceiptService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.caopandata.service.CaoPanDataService;
import cn.visolink.salesmanage.commissionpolicy.dao.CmPolicyApplyMapper;
import cn.visolink.salesmanage.datainterface.dao.DatainterfaceDao;
import cn.visolink.salesmanage.datainterface.service.impl.DatainterfaceserviceImpl;
import cn.visolink.salesmanage.flow.service.FlowService;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManageDao;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageService;
import cn.visolink.salesmanage.gxcinterface.service.GXCInterfaceservice;
import cn.visolink.salesmanage.gxcinterface.service.NewGxcByProjectService;
import cn.visolink.system.org.service.OrganizationService;
import cn.visolink.system.projectmanager.service.projectmanagerService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>
 * 数据接口
 * </p>
 *
 * @author 刘昶 SALEMAN-711
 * @since 2019-10-1
 */
@RestController
@Api(tags = "数据接口")
@Slf4j
@RequestMapping("/datainterface")
public class DatainterfaceController {
    @Autowired
    DatainterfaceserviceImpl datainterfaceservice;
    @Autowired
    GroupManageService groupManageService;

    @Autowired
    private CmPolicyApplyMapper cmPolicyApplyMapper;
    @Autowired
    cn.visolink.salesmanage.weeklymarketingplan.service.WeeklyMarketingService service;
    //操盘数据
    @Autowired
    private CaoPanDataService caoPanDataService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private GroupManageDao groupManageDao;

    @Autowired
    public projectmanagerService managerservice;

    @Autowired
    private NewGxcByProjectService newGxcByProjectService;

    @Autowired
    public OrganizationService organizationService;

    //销管发送供销存月度签约计划esb接口地址，账号，密码
    @Value("${testgongxiaocun.url}")
    private String url;
    @Value("${testgongxiaocun.userId}")
    private String userId;
    @Value("${testgongxiaocun.password}")
    private String password;

    @Autowired
    private TimeLogsDao timeLogsDao;

    @Autowired
    private DatainterfaceDao datainterfaceDao;

    @Autowired
    private GXCInterfaceservice gXCInterfaceservice;

    @Autowired
    private CommissionService commissionServiceImpl;

    @Autowired
    private ReceiptService receiptServiceImpl;

    private static String CACHE_LOCK3 = "MESSAGE_PUSH_LOCK3";
    private static String CACHE_LOCK5 = "MESSAGE_PUSH_LOCK5";
    private static String CACHE_LOCK6 = "MESSAGE_PUSH_LOCK6";
    private static String CACHE_LOCK1 = "MESSAGE_PUSH_LOCK1";
    private static String CACHE_LOCK2 = "MESSAGE_PUSH_LOCK2";
    private static String CACHE_LOCK7 = "MESSAGE_PUSH_LOCK12";
    private static String CACHE_LOCK8 = "MESSAGE_PUSH_LOCK13";
    private static String CACHE_LOCK9 = "MESSAGE_PUSH_LOCK14";
    private static String CACHE_LOCK10 = "MESSAGE_PUSH_LOCK15";
    private static String CACHE_LOCK11 = "MESSAGE_PUSH_LOCK16";
    private static String CACHE_LOCK12 = "MESSAGE_PUSH_LOCK17";
    private static int EXPIRE_PERIOD = (int) DateUtils.MILLIS_PER_MINUTE * 5 / 1000;
    //每天1点增量执行签约
    private static String CACHE_LOCK_ADDSIGN = "MESSAGE_PUSH_ADDSIGN";
    //每天1点30增量执行认购
    private static String CACHE_LOCK_ADDSIGNORDER = "MESSAGE_PUSH_ORDER";
    //初始化签约到月度数据
    //  private static String CACHE_LOCK_SIGN = "MESSAGE_PUSH_SIGN";
    //初始化两年明源数据
    private static String CACHE_LOCK_MINGYUANTWOYEAR = "MESSAGE_PUSH_MINGYUANTWOYEAR";

    //更新佣金政策状态
    private static String CM_STATUS = "CM_STATUS";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    DataAccessDao dataAccessDao;

    @Log("更新t_mm_designBuild数据")
    @ApiOperation(value = "更新t_mm_designBuild数据")
    @PostMapping(value = "/updataDesignBuild")
    public Map updataDesignBuild(@RequestBody Map datas) {
        Map maplist = new HashMap<>();
        Map maplistt = new HashMap<>();
        try {
            datainterfaceservice.insertDesignBuild(datas);
        } catch (Exception e) {

            maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
            maplist.put("returnStatus", "E");
            maplist.put("returnCode", "A0001-SMS");
            maplist.put("returnMsg", "调用失败");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            maplist.put("requstTime", df.format(new Date()));
            maplist.put("reponsTime", df.format(new Date()));
            maplist.put("attr1", null);
            maplist.put("attr2", null);
            maplist.put("attr3", null);

            maplistt.put("esbInfo", maplist);
            return maplistt;
        }
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }

    @Log("更新t_mm_Project数据")
    @ApiOperation(value = "更新t_mm_Project数据")
    @PostMapping(value = "/updataProject")
    public Map updataProject(@RequestBody Map datas) {
        Map maplist = new HashMap<>();
        Map maplistt = new HashMap<>();
        try {
            datainterfaceservice.insertProject(datas);
        } catch (Exception e) {

            maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
            maplist.put("returnStatus", "E");
            maplist.put("returnCode", "A0001-SMS");
            maplist.put("returnMsg", "调用失败");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            maplist.put("requstTime", df.format(new Date()));
            maplist.put("reponsTime", df.format(new Date()));
            maplist.put("attr1", null);
            maplist.put("attr2", null);
            maplist.put("attr3", null);

            maplistt.put("esbInfo", maplist);
            return maplistt;
        }
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }

    @Log("更新t_mm_staging数据")
    @ApiOperation(value = "更新t_mm_staging数据")
    @PostMapping(value = "/updataStaging")
    public Map updataStaging(@RequestBody Map datas) {
        Map maplist = new HashMap<>();
        Map maplistt = new HashMap<>();
        try {
            datainterfaceservice.insertStaging(datas);
        } catch (Exception e) {

            maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
            maplist.put("returnStatus", "E");
            maplist.put("returnCode", "A0001-SMS");
            maplist.put("returnMsg", "调用失败");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            maplist.put("requstTime", df.format(new Date()));
            maplist.put("reponsTime", df.format(new Date()));
            maplist.put("attr1", null);
            maplist.put("attr2", null);
            maplist.put("attr3", null);

            maplistt.put("esbInfo", maplist);
            return maplistt;
        }
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }

    @Log("更新t_mm_group数据")
    @ApiOperation(value = "更新t_mm_group数据")
    @PostMapping(value = "/updataGroup")
    public Map updataGroup(@RequestBody Map datas) {
        Map maplist = new HashMap<>();
        Map maplistt = new HashMap<>();
        try {
            datainterfaceservice.insertGroup(datas);
        } catch (Exception e) {

            maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
            maplist.put("returnStatus", "E");
            maplist.put("returnCode", "A0001-SMS");
            maplist.put("returnMsg", "调用失败");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            maplist.put("requstTime", df.format(new Date()));
            maplist.put("reponsTime", df.format(new Date()));
            maplist.put("attr1", null);
            maplist.put("attr2", null);
            maplist.put("attr3", null);

            maplistt.put("esbInfo", maplist);
            return maplistt;
        }
        maplist.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        maplist.put("returnStatus", "S");
        maplist.put("returnCode", "A0001-SMS");
        maplist.put("returnMsg", "调用成功");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        maplist.put("requstTime", df.format(new Date()));
        maplist.put("reponsTime", df.format(new Date()));
        maplist.put("attr1", null);
        maplist.put("attr2", null);
        maplist.put("attr3", null);
        maplistt.put("esbInfo", maplist);
        return maplistt;
    }

    @Log("定时任务,初始周数据")
    @ApiOperation(value = "定时任务.初始周数据")
    @PostMapping("/initZhoujihua")
    public void initZhoujihua() {
        service.weekMarketingPlanInitial();
    }


    @Log("定时任务,初始周数据")
    @ApiOperation(value = "定时任务.初始周数据")
    @PostMapping("/zhoujihua")
    //@Scheduled(cron = "0 0 18 * * ?")
    public void zhoujihua() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK3) == null) {
            //线程锁
            redisUtil.set(CACHE_LOCK3, true, EXPIRE_PERIOD);


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal_1 = Calendar.getInstance();//获取当前日期
            cal_1.add(Calendar.MONTH, 0);
            cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
            Map map = new HashMap();
            map.put("startTime", format.format(cal_1.getTime()));
            datainterfaceservice.intiSingAddData(map);
            //Nos数据
            caoPanDataService.getNosSigningAdd(map);
            //明源认购数据
            datainterfaceservice.intiOrderAddData(map);
            //明源来人量
            datainterfaceservice.insertVisllAdd(map);



            //保存日志表
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTime = df.format(new Date());
            SysLog sysLog1 = new SysLog();
            sysLog1.setStartTime(nowTime);
            sysLog1.setTaskName("签约数据");
            sysLog1.setNote("明源签约数据同步完成！！！");
            timeLogsDao.insertLogs(sysLog1);            //增量更新前一天nos数据
            /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            Date start = c.getTime();*/
            /*Map params = new HashMap();
            String startTime = format.format(start);
            params.put("startTime", startTime);
            //Nos数据
            caoPanDataService.getNosSigningAdd(params);*/
            //保存日志表
            sysLog1.setStartTime(nowTime);
            sysLog1.setTaskName("nos数据");
            sysLog1.setNote("nos数据同步完成！！！");
            timeLogsDao.insertLogs(sysLog1);
            //明源认购数据
            /*datainterfaceservice.intiOrderAddData(null);*/
            //明源来人量
            /*datainterfaceservice.insertVisllAdd(params);*/
            //保存日志表
            sysLog1.setStartTime(nowTime);
            sysLog1.setTaskName("来人量数据");
            sysLog1.setNote("来人量数据同步完成！！！");
            timeLogsDao.insertLogs(sysLog1);
            //周度汇总
            service.weekMarketingPlanInitial();
            //保存日志表
            sysLog1.setStartTime(nowTime);
            sysLog1.setTaskName("周计划数据");
            sysLog1.setNote("周计划初始化完成完成！！！");
            timeLogsDao.insertLogs(sysLog1);
            //调度明源数据做对比
            // flowService.weeklyForecast("");
        } else {
            log.info("周计划其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
        }
    }

    @PostMapping("/updateZJHData")
    // @Scheduled(cron = "0 0 17 ? * SUN")
    public ResultBody updateZJHData() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK11) == null) {
            redisUtil.set(CACHE_LOCK11, true, EXPIRE_PERIOD);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal_1 = Calendar.getInstance();//获取当前日期
            cal_1.add(Calendar.MONTH, 0);
            cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
            Map map = new HashMap();
            map.put("startTime", format.format(cal_1.getTime()));
            datainterfaceservice.intiSingAddData(map);
            //Nos数据
            caoPanDataService.getNosSigningAdd(map);
            //明源认购数据
            datainterfaceservice.intiOrderAddData(map);
            //明源来人量
            datainterfaceservice.insertVisllAdd(map);
            return ResultBody.success("更新成功！");
        } else {
            log.info("初始事业部表其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
            return ResultBody.success("初始事业部表其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：");
        }
    }


    @Log("初始mm_idm前缀的表,然后初始化事业表")
    @CessBody
    @ApiOperation(value = "初始mm_idm前缀的表,然后初始化事业表")
    @PostMapping("/initmmidm")
    //@Scheduled(cron = "0 0 1 * * ?")
    public void initmmidm() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK6) == null) {
            redisUtil.set(CACHE_LOCK6, true, EXPIRE_PERIOD);
            datainterfaceservice.initmmidm();
            datainterfaceservice.updataBusinsee();
        } else {
            log.info("初始事业部表其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
        }
    }

    @Log("初始明源")
    @CessBody
    @ApiOperation(value = "定时任务,每月初始明源")
    @PostMapping("/mingyue")
    public void mingyuan() {
        try {
            datainterfaceservice.mingyuan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Map params = new HashMap();


    @PostMapping("/addSign")
    public String addSignRequest(@RequestBody Map params) {
        this.params = params;
        return addSign();
    }

    @Log("增量同步明源签约数据")
    @CessBody
    @ApiOperation(value = "增量同步明源签约数据")
    // @Scheduled(cron = "0 0 1 * * ?")
    public String addSign() {
        String messages = "";
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK_ADDSIGN) == null) {
            redisUtil.set(CACHE_LOCK_ADDSIGN, true, EXPIRE_PERIOD);
            try {
                datainterfaceservice.intiSingAddData(params);
                messages = "成功";
            } catch (Exception e) {
                messages = "失败";
                e.printStackTrace();
            }
        } else {
            messages = "该定时仍在睡眠中，睡眠时间为" + EXPIRE_PERIOD + "s---请稍后稍后再试";
        }


        return messages;
    }


    @PostMapping("/addOrder")
    public String addOrderRequest(@RequestBody Map params) {
        this.params = params;
        return addOrder();
    }

    /*
     * 手动更新明源认购数据
     *
     * */

    @PostMapping("/synMyOrder")
    public ResultBody synMyOrder(@RequestBody Map params) {
        datainterfaceservice.intiOrderAddDataSD(params);
        return ResultBody.success("成功");
    }

    @Log("增量同步明源认购数据")
    @CessBody
    @ApiOperation(value = "增量同步明源认购数据")
    @Scheduled(cron = "0 30 1 * * ?")
    public String addOrder() {
        String messages = "";
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK_ADDSIGNORDER) == null) {
            redisUtil.set(CACHE_LOCK_ADDSIGNORDER, true, EXPIRE_PERIOD);
            try {
                datainterfaceservice.intiOrderAddData(params);
                messages = "成功";
            } catch (Exception e) {
                messages = "失败";
                e.printStackTrace();
            }
        } else {
            messages = "该定时仍在睡眠中，睡眠时间为" + EXPIRE_PERIOD + "s---请稍后稍后再试";
        }
        return messages;
    }


    /**
     * 定时修改佣金政策状态  暂时不用
     */

    // @Scheduled(cron = "0 50 06 * * ?")
    public void updatePolicyStatusByDate() {
        String messages = "";
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CM_STATUS) == null) {
            redisUtil.set(CM_STATUS, true, EXPIRE_PERIOD);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            String date = simpleDateFormat.format(new Date());
            cmPolicyApplyMapper.updatePolicyStatusByDate(date);
        } else {
            messages = "该定时仍在睡眠中，睡眠时间为" + EXPIRE_PERIOD + "s---请稍后稍后再试";
        }
    }

    @PostMapping("/sdUpdatePolicyStatusByDate")
    public ResultBody sdUpdatePolicyStatusByDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String date = simpleDateFormat.format(new Date());
        cmPolicyApplyMapper.updatePolicyStatusByDate(date);
        return ResultBody.success("执行成功！");
    }


    @Log("增量同步明源认购数据")
    @ApiOperation(value = "增量同步明源认购数据")
    @PostMapping("/addOrderByProject")
    public ResultBody addOrderByProject(@RequestBody Map map) {
        datainterfaceservice.intiOrderByProject(map);
        return ResultBody.success("同步明源认购数据成功！");
    }

    @Log("初始来人量")
    @ApiOperation(value = "定时任务", httpMethod = "GET")
    @GetMapping("/lairenlian")
    public void lairenlian(String startTime) {
        try {
            datainterfaceservice.initlairenliang(startTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Log("调用接口初始明源")
    @ApiOperation(value = "定时任务,每月初始明源", httpMethod = "GET")
    @GetMapping("/initmingyuan")
    @ResponseBody
    public void initmingyuan(String startTime, String endTime) {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK5) == null) {
            redisUtil.set(CACHE_LOCK5, true, EXPIRE_PERIOD);
            try {
                datainterfaceservice.initmingyuan(startTime, endTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Log("调用接口初始明源")
    @ApiOperation(value = "定时任务,每月初始明源", httpMethod = "GET")
    @GetMapping("/initSignByStart")
    @ResponseBody
    public void initSignByStart(String startTime, String endTime) {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK5) == null) {
            redisUtil.set(CACHE_LOCK5, true, EXPIRE_PERIOD);
            try {
                datainterfaceservice.initSignByStart(startTime, endTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Log("调用接口初始明源签约认购时间空数据")
    @ApiOperation(value = "调用接口初始明源签约认购时间空数据", httpMethod = "GET")
    @GetMapping("/initOrderAndSignNull")
    @ResponseBody
    public void initOrderAndSignNull() {
        try {
            datainterfaceservice.initOrderAndSignNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Log(",初始集团月度计划")
    @GetMapping(value = "/yuezhibiao")
    @ApiOperation(value = "每月初始月指标", httpMethod = "GET")
    @ResponseBody
    public VisolinkResultBody yuezhibiao(String time) {
        VisolinkResultBody bobys = new VisolinkResultBody();
        try {
            Map map = new HashMap();
            map.put("months", time);
            groupManageService.getBusiness(map);
        } catch (Exception e) {
            bobys.setCode(1);
            bobys.setMessages(e.getMessage());
        }
        return bobys;
    }

    @Log("定时任务,每月初始明源上年跟当前年认购签约数据")
    @CessBody
    @ApiOperation(value = "定时任务,每月1号6点后初始明源上年跟当前年认购签约数据")
    @PostMapping("/initMingYuanTwoYear")
    //@Scheduled(cron = "0 0 6 1 * ?")
    public void initMingYuanTwoYear() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK_MINGYUANTWOYEAR) == null) {
            redisUtil.set(CACHE_LOCK_MINGYUANTWOYEAR, true, EXPIRE_PERIOD);
            try {
                //增量更新前一天nos数据
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, -1);
                Date start = c.getTime();
                Map params = new HashMap();
                String startTime = format.format(start);
                params.put("startTime", startTime);
                //明源签约数据
                datainterfaceservice.intiSingAddData(new HashMap());
                //Nos数据
                caoPanDataService.getNosSigningAdd(params);
                //明源认购数据
                datainterfaceservice.intiOrderAddData(new HashMap());
                //明源来人量
                datainterfaceservice.insertVisllAdd(params);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    @Log("手动初始化月度计划报表数据")
    @CessBody
    @ApiOperation(value = "手动初始月度计划报表数据")
    @PostMapping("/initYueDu")
    public void initYueDu() {
        try {
            //增量更新前一天nos数据
            SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            Date start = c.getTime();
            Map params = new HashMap();
            String startTime = formatYMD.format(start);
            params.put("startTime", startTime);
            //初始化动态货值
            //gXCInterfaceservice.insertvaluedthz();全量
            gXCInterfaceservice.insertDynamicValue(null);//前两月null默认
            //初始化签约
            //gXCInterfaceservice.insertvalueqy();全量
            gXCInterfaceservice.insertSignPlan(null);//前两月null默认
            //供销存供货数据
            //gXCInterfaceservice.insertvaluegh();//全量
            gXCInterfaceservice.insertSupplyPlan(null);//前两月null默认
            //初始化关系事业部
            datainterfaceservice.initmmidm();
            datainterfaceservice.updataBusinsee();
            //明源签约数据
            datainterfaceservice.intiSingAddData(null);
            //Nos数据
            caoPanDataService.getNosSigningAdd(params);
            //明源认购数据
            datainterfaceservice.intiOrderAddData(null);
            //明源来人量
            datainterfaceservice.insertVisllAdd(params);
            //初始化全量签约月度表
            datainterfaceservice.intiSingDataAll();
            Map map = new HashMap();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            String year = format.format(new Date());
            year = year + "-01";
            map.put("months", year);
            //月度指标初始化
            groupManageService.getBusiness(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Log("初始化所有供货")
    @CessBody
    @ApiOperation(value = "初始化所有供货")
    @PostMapping("/initSupplyValue")
    public void initSupplyValue() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK12) == null) {
            redisUtil.set(CACHE_LOCK12, true, EXPIRE_PERIOD);
            try {
                newGxcByProjectService.getSupplyValue();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            log.info("初始化所有供货其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
        }
    }


    @Log("定时任务,每月1号12点初始化月度计划报表数据")
    @CessBody
    @ApiOperation(value = "定时任务,每月1号12点初始月度计划报表数据")
    @PostMapping("/jituan")
    //@Scheduled(cron = "0 0 12 1 * ?")
    public void jituan() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK2) == null) {
            redisUtil.set(CACHE_LOCK2, true, EXPIRE_PERIOD);
            try {
                //newGxcByProjectService.getSupplyValue();
                //初始化动态货值
                //gXCInterfaceservice.insertvaluedthz();全量
                gXCInterfaceservice.insertDynamicValue(null);//前两月null默认
                //初始化签约
                //gXCInterfaceservice.insertvalueqy();全量
                gXCInterfaceservice.insertSignPlan(null);//前两月null默认
                //供销存供货数据
                //gXCInterfaceservice.insertvaluegh();//全量
                gXCInterfaceservice.insertSupplyPlan(null);//前两月null默认
                gXCInterfaceservice.insertReportValue(null);
                //初始化关系事业部
                datainterfaceservice.initmmidm();
                datainterfaceservice.updataBusinsee();

                //初始化全量签约月度表
                datainterfaceservice.intiSingDataAll();
                Map map = new HashMap();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
                String year = format.format(new Date());
                year = year + "-01";
                map.put("months", year);
                //月度指标初始化
                groupManageService.getBusiness(map);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            log.info("月度指标其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
        }
    }

    /*已项目为纬度就行修改*/
    @Log("单项目获取最新供货")
    @ApiOperation(value = "单项目获取最新供货")
    @PostMapping("/jituanByproject")
    public ResultBody jituanByproject(@RequestBody Map map) {

        try {
            //初始化动态货值
            newGxcByProjectService.insertDynamicValue(map);//前两月null默认
            //初始化签约
            newGxcByProjectService.insertSignPlan(map);//前两月null默认
            //供销存供货数据
            newGxcByProjectService.insertSupplyPlan(map);//前两月null默认
            //供销存库存数据
            newGxcByProjectService.insertReportValue(map);
            //初始化关系事业部
            newGxcByProjectService.initmmidm(map.get("projectId").toString());
            newGxcByProjectService.updataBusinsee(map.get("projectId").toString());

            //初始化全量签约月度表
            int sign = dataAccessDao.getSignAll();
            //sign在数据库配置的
            if (sign == 1) {
                newGxcByProjectService.intiSingDataAll(map.get("projectId").toString());
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            String year = format.format(new Date());
            year = year + "-01";
            map.put("months", year);
            map.put("projectId", map.get("projectId").toString());
            //月度指标初始化
            newGxcByProjectService.getBusiness(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultBody.success("调用成功！");
    }

    @Log("初始化所有供货数据")
    @ApiOperation(value = "初始化所有供货数据")
    @PostMapping("/getSupplyValue")
    public ResultBody getSupplyValue() throws ParseException {
        //供销存库存数据
        //newGxcByProjectService.insertReportValueAll();
        return ResultBody.success(newGxcByProjectService.getSupplyValue());
    }


    @Log("集团确认后,把数据写入明源")
    @ApiOperation(value = "集团确认后,把数据写入明源,手工推送！")
    @PostMapping("/selectsignset")
    public void selectsignset(String time) {

        datainterfaceservice.selectsignset(time);
    }

    @Log("发送供销存esb接口")
    @CessBody
    @ApiOperation(value = "集团确认后,把数据写入供销存,手工推送！")
    @PostMapping("/sendesb")
    public ResultBody sendesb(@RequestBody Map<String, Object> params) {
        Map<Object, Object> resulMap = new HashMap<>();
        ResultBody<Object> resultBody = new ResultBody<>();
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();
        String version = nowDate.format(dt);
        try {
            //推送月份
            String months = params.get("months") + "";
            //区域ID
            List areaIds = (List) params.get("areaIds");
            if (areaIds == null || areaIds.size() == 0) {
                return resultBody;
            }
            for (Object areaId : areaIds) {
                //查询发送供销存的数据
                Map gxcParams = new HashMap();
                gxcParams.put("areaId", areaId);
                gxcParams.put("months", months);
                Map map = datainterfaceservice.selectSendGXC(gxcParams);
                //获取查询数据的list
                List<Map<String, String>> list = (List<Map<String, String>>) map.get("list");
                Integer integer = list.size();
                if (integer > 0) {
                    //发送供销存的数据
                    JSONObject jsonobj = toJsonObj(list);
                    long startTime = System.currentTimeMillis();   //获取开始时间
                    JSONObject json = HttpRequestUtil.httpPost(url, userId, password, jsonobj, false);  //发送数据
                    //获取响应的code码
                    if (json == null) {
                        resultBody.setMessages("ESB接口发生失败，json为空！！！！");
                        return resultBody;
                    }
                    String data = json.getString("esbInfo");
                    JSONObject jsondata = JSON.parseObject(data);
                    String token = jsondata.getString("returnStatus");
                    long endTime = System.currentTimeMillis(); //获取结束时间
                    //判断供销存响应是否成功
                    if (json != null && token.equals("S")) {//成功
                        String message1 = "本次初始化共发送" + integer + "条数据";
                        String message2 = "本次发送数据耗时" + (endTime - startTime) / 1000 + "s";
                        resulMap.put("message1", message1);
                        resulMap.put("message2", message2);
                        resultBody.setData(resulMap);
                        resultBody.setMessages("发送供销存数据成功!");
                        resultBody.setCode(200);
                        //保存日志
                        SysLog sysLog = new SysLog();
                        sysLog.setTaskName("发送供销存数据成功！");
                        sysLog.setStartTime(version);
                        sysLog.setNote(message1 + "--" + json.toString() + "参数:" + params);
                        timeLogsDao.insertLogs(sysLog);

                    } else {//失败
                        String message1 = "本次发送数据耗时" + (endTime - startTime) / 1000 + "s";
                        resulMap.put("message1", message1);
                        resultBody.setMessages("发送数据失败");
                        resultBody.setCode(-1);
                        resultBody.setData(resulMap);
                        //保存日志
                        SysLog sysLog = new SysLog();
                        sysLog.setTaskName("推送供销存数据失败，未查询到结果!");
                        sysLog.setStartTime(version);
                        sysLog.setNote(message1 + "参数:" + params);
                        timeLogsDao.insertLogs(sysLog);
                    }
                } else {
                    String message1 = "未查询到结果";
                    resulMap.put("message1", message1);
                    SysLog sysLog = new SysLog();
                    sysLog.setTaskName("推送供销存数据失败，未查询到结果!");
                    sysLog.setStartTime(version);
                    sysLog.setNote(message1 + "参数:" + params);
                    timeLogsDao.insertLogs(sysLog);
                    resultBody.setData(resulMap);
                    resultBody.setCode(0);
                }
                //每次推送数据修改区域状态
                Map areaMap = new HashMap();
                areaMap.put("planStatus", "3");
                areaMap.put("months", months);
                areaMap.put("businessId", areaId);
                areaMap.put("preparedByLevel", "2");
                groupManageDao.updatePlanStatus(areaMap);
            }
        } catch (Exception e) {
            SysLog sysLog = new SysLog();
            sysLog.setTaskName("推送供销存数据失败！");
            sysLog.setStartTime(version);
            sysLog.setNote(e.getMessage());
            timeLogsDao.insertLogs(sysLog);
        }
        return resultBody;
    }

    @Log("手动发送供销存esb接口")
    @CessBody
    @ApiOperation(value = "集团确认后,把数据写入供销存,手工推送！")
    @PostMapping("/sdSendGXC")
    public ResultBody sdSendGXC(@RequestBody Map<String, Object> params) {


        //查询发送供销存的数据
        //获取查询数据的list
        List<Map<String, String>> list = datainterfaceDao.getGXCData();
        Integer integer = list.size();
        if (integer > 0) {
            //发送供销存的数据
            JSONObject jsonobj = toJsonObj(list);
            long startTime = System.currentTimeMillis();   //获取开始时间
            JSONObject json = HttpRequestUtil.httpPost(url, userId, password, jsonobj, false);  //发送数据
            System.out.println("=======" + json.toJSONString());
            String data = json.getString("esbInfo");
            JSONObject jsondata = JSON.parseObject(data);
            String token = jsondata.getString("returnStatus");
            System.out.println("----------------" + token);
            long endTime = System.currentTimeMillis(); //获取结束时间

        }


        return null;
    }

    //封装发送供销存实体
    public JSONObject toJsonObj(List<Map<String, String>> list) {
        //日期格式化
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat smtnyr = new SimpleDateFormat("yyyy-MM-dd");
        //创建list，批量存放map
        List<Object> requestInfo = new ArrayList();
        //循环list，获取查询数据值，放入maps集合
        for (int i = 0; i < list.size(); i++) {
            Map<String, String> maps = new HashMap<>();
            //项目ID
            if (list.get(i).get("project_id") == null) {
                maps.put("projectID", "");
            } else {
                maps.put("projectID", list.get(i).get("project_id"));
            }
            //项目编码
            if (list.get(i).get("project_code") == null) {
                maps.put("projectCode", "");
            } else {
                maps.put("projectCode", list.get(i).get("project_code"));
            }
            //项目名称
            if (list.get(i).get("project_name") == null) {
                maps.put("projectName", "");
            } else {
                maps.put("projectName", list.get(i).get("project_name"));
            }
            //分期id
            if (list.get(i).get("stage_id") == null) {
                maps.put("stageId", "");
            } else {
                maps.put("stageId", list.get(i).get("stage_id"));
            }
            //分期名称
            if (list.get(i).get("stage_name") == null) {
                maps.put("stageName", "");
            } else {
                maps.put("stageName", list.get(i).get("stage_name"));
            }
            //组团id
            if (list.get(i).get("group_id") == null) {
                maps.put("groupId", "");
            } else {
                maps.put("groupId", list.get(i).get("group_id"));
            }
            //组团名称
            if (list.get(i).get("group_name") == null) {
                maps.put("groupName", "");
            } else {
                maps.put("groupName", list.get(i).get("group_name"));
            }
            //产品构成code "018.02.02.01"
            if (list.get(i).get("product_code_sale") == null) {
                maps.put("productCode", "");
            } else {
                maps.put("productCode", list.get(i).get("product_code_sale"));
            }
            //产品构成名称   "住宅-联排别墅-精装-可售-常规"
            if (list.get(i).get("product_name_info") == null) {
                maps.put("productName", "");
            } else {
                maps.put("productName", list.get(i).get("product_name_info"));
            }
            //面积段名称
            if (list.get(i).get("house_package_name") == null) {
                maps.put("areaName", "");
            } else {
                maps.put("areaName", list.get(i).get("house_package_name"));
            }
            //新增签约套数
            if (list.get(i).get("new_sign_set") == null) {
                maps.put("newTradeNumPlan", "");
            } else {
                maps.put("newTradeNumPlan", list.get(i).get("new_sign_set"));
            }
            //新增签约金额
            if (list.get(i).get("new_sign_funds") == null) {
                maps.put("newTradeAmountPlan", "");
            } else {
                maps.put("newTradeAmountPlan", list.get(i).get("new_sign_funds"));
            }
            //库存签约套数
            if (list.get(i).get("reserve_sign_set") == null) {
                maps.put("inventoryTradeNumPlan", "");
            } else {
                maps.put("inventoryTradeNumPlan", list.get(i).get("reserve_sign_set"));
            }
            //库存签约金额
            if (list.get(i).get("reserve_sign_funds") == null) {
                maps.put("inventoryTradeAmountPlan", "");
            } else {
                maps.put("inventoryTradeAmountPlan", list.get(i).get("reserve_sign_funds"));
            }
            //供货计划版本id
            if (list.get(i).get("version_id") == null) {
                maps.put("supplyPlanId", "");
            } else {
                maps.put("supplyPlanId", list.get(i).get("version_id"));
            }
            //签约计划编制日期
            if (list.get(i).get("months") == null) {
                maps.put("createDate", "");
            } else {
                maps.put("createDate", smtnyr.format(list.get(i).get("months")));
            }
            //是否车位
            if (list.get(i).get("is_parking") == null) {
                maps.put("isParking", "");
            } else {
                maps.put("isParking", list.get(i).get("is_parking"));
            }
            //创建map用于赋值
          /*
            maps.put("projectID", list.get(i).get("project_id"));//项目ID
            maps.put("projectCode", list.get(i).get("project_code"));//项目编码
            maps.put("projectName", list.get(i).get("project_name"));//项目名称
            maps.put("stageId", list.get(i).get("stage_id"));//分期id
            maps.put("stageName", list.get(i).get("stage_name"));//分期名称
            maps.put("groupId", list.get(i).get("group_id"));//组团id
            maps.put("groupName",list.get(i).get("group_name"));//组团名称
            maps.put("productId", list.get(i).get("pro_product_id"));//业态id
            maps.put("productName", list.get(i).get("pro_product_type"));//业态名称
            maps.put("buildId", list.get(i).get("building_id"));//楼栋id
            maps.put("buildName",list.get(i).get("building_name"));//楼栋名称
            maps.put("areaName", list.get(i).get("house_package_name"));//面积段名称
            maps.put("newTradeAmountPlan", list.get(i).get("new_sign_funds"));//新增签约
            maps.put("inventoryTradeAmountPlan", list.get(i).get("reserve_sign_funds"));//库存签约
             maps.put("supplyPlanId", 15);//供货计划版本id
            maps.put("supplyPlanDate",16);//计划供货日期
            maps.put("realSupplyDate", 17);//实际供货日期
            maps.put("roomNumByPlan", 18);//计划供货房源套数
            maps.put("parkingNumByPlan",19);//计划供货车位套数
            maps.put("roomNum", 20);//实际供货房源套数
            maps.put("parkingNum", 21);//实际供货车位套数
            maps.put("areaByPlan", 22);//计划供货面积
            maps.put("area",23);//实际供货面积
            maps.put("roomAmountByPlan", 24);//计划房源供货金额
            maps.put("amountContainParkingByPlan",25);//计划包含推售车位供货金额
            maps.put("amountWithoutParkingByPlan", 26);//计划不包含推售车位供货金额
            maps.put("roomAmount", 27);//实际房源供货金额
            maps.put("amountContainParking", 28);//实际包含推售车位供货金额
            maps.put("amountWithoutParking",29);//实际不包含推售车位供货金额
            maps.put("createDate", 30);//计划创建日期*/
            requestInfo.add(maps);
        }
        //json头 esbInfo
        JSONObject esbInfo = new JSONObject();
        esbInfo.put("instId", "test001");
        esbInfo.put("requestTime", smt.format(new Date()));
        esbInfo.put("attr1", "");
        esbInfo.put("attr2", "");
        esbInfo.put("attr3", "");
        //将封装好的数据放入json对象中
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("esbInfo", esbInfo);
        jsonobj.put("requestInfo", requestInfo);
        return jsonobj;
    }

    @Log("初始化签约行数据")
    @ApiOperation(value = "初始化签约行数据")
    @PostMapping("/initSingData")
    public ResultBody intiSingData(@RequestBody Map map) {
        return datainterfaceservice.intiSingData(map);
    }

    @Log("初始化全部签约行数据到月度表")
    @ApiOperation(value = "初始化全部签约行数据到月度表")
    @PostMapping("/initSingDataAll")
    public ResultBody initSingDataAll() {
        return datainterfaceservice.intiSingDataAll();
    }

    @Log("初始化全部签约行数据到月度表")
    @ApiOperation(value = "初始化全部签约行数据到月度表")
    @PostMapping("/intiSingDataMonth")
    public ResultBody intiSingDataMonth(@RequestBody Map map) {
        int startYear = Integer.parseInt(map.get("startYear") + "");
        int endYear = Integer.parseInt(map.get("endYear") + "");
        return datainterfaceservice.intiSingDataMonth(startYear, endYear);
    }

    @Log("初始化签约认购数据")
    @ApiOperation(value = "初始化签约认购数据")
    @PostMapping("/initMingYuanTwo")
    public ResultBody initMingYuanTwo() {
        return datainterfaceservice.initMingYuanTwo();
    }

    @Log("初始化认购全量数据")
    @ApiOperation(value = "初始化认购全量数据")
    @PostMapping("/initMingyuanOrder")
    public ResultBody initMingyuanOrder() {
        return datainterfaceservice.initMingyuanOrder();
    }

    @Log("获取每月自然周")
    @ApiOperation(value = "获取每月自然周")
    @PostMapping("/getMonthBiao")
    public ResultBody getMonthBiao(@RequestBody Map params) {
        ResultBody body = new ResultBody();
        body.setData(DateUtil.getWeek(params.get("thisTime") + ""));
        return body;
    }


    @Log("循环插入自然周")
    @ApiOperation(value = "循环插入自然周")
    @PostMapping("/insertMonthBiao")
    public ResultBody insertMonthBiao(@RequestBody Map params) {
        ResultBody body = new ResultBody();
        // Java8  LocalDate
        String thisTime = params.get("thisTime") + "";
        //设置时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        Calendar ca = Calendar.getInstance();
        //当前年
        int nowYear = Integer.parseInt(yearformat.format(ca.getTime()));
        //获得实体类
        int addYear = 2018;
        ca.set(Calendar.YEAR, addYear);
        //设置月份
        ca.set(Calendar.MONTH, 0);
        while (addYear <= nowYear) {
            //设置最后一天
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            //最后一天格式化
            String lastDay = format.format(ca.getTime());
            //累计年度
            addYear = Integer.parseInt(yearformat.format(ca.getTime()));
            if (addYear > nowYear) {
                return null;
            }
            int year = ca.get(Calendar.YEAR);
            // 获取月，这里需要需要月份的范围为0~11，因此获取月份的时候需要+1才是当前月份值
            int month = ca.get(Calendar.MONTH) + 1;
            String monthAdd = "";
            if (month < 10) {
                monthAdd = "0" + month;
            } else {
                monthAdd = month + "";
            }
            System.out.print(monthAdd);
            // Map thisDate=new HashMap();
            // thisDate.put("thisTime",);
            // getMonthBiao();
            ca.add(Calendar.MONTH, 1);
        }
        return body;
    }

    /**
     * 每天凌晨二点半自动同步主数据项目
     */

    @Log("同步主数据项目")
    @ApiOperation("同步主数据项目")
    @PostMapping("/synMasterDataProject")
    //@Scheduled(cron = "0 30 2 * * ?")
    public ResultBody synMasterDataProject() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK7) == null) {
            //线程锁
            redisUtil.set(CACHE_LOCK7, true, EXPIRE_PERIOD);
            managerservice.delPRoject();
            List<Map> list = managerservice.getWglProject(null);

            Map map = new HashMap(1);
            map.put("projectList", list);
            managerservice.addGlProject(map);
            datainterfaceservice.initProjectType();
            datainterfaceservice.delRepeatData();
            organizationService.synOrgFourLevel();
            return ResultBody.success("初始化成功！");
        } else {
            log.info("周计划其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
            return null;
        }
    }


    /**
     * 每天凌晨三点初始化项目标识
     */
    @Log("初始化项目标识")
    @ApiOperation("初始化项目标识")
    @PostMapping("/initProjectType")
    // @Scheduled(cron = "0 0 3 * * ?")
    public ResultBody initProjectType() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK8) == null) {
            //线程锁
            redisUtil.set(CACHE_LOCK8, true, EXPIRE_PERIOD);
            gXCInterfaceservice.insertSupplyPlan(null);
            datainterfaceservice.initProjectType();
            return ResultBody.success("初始化成功！");
        } else {
            log.info("周计划其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
            return null;
        }
    }

    @Log("全量拉取待结佣数据")
    @CessBody
    @ApiOperation(value = "全量拉取待结佣数据")
    @PostMapping("/initCommission")
    public ResultBody initCommission() {
        receiptServiceImpl.getMYhtfkapply();
        return ResultBody.success(commissionServiceImpl.initCommission());
    }

    @Log("定时任务,每天早上拉取待结佣数据（增量）")
    @CessBody
    @ApiOperation(value = "定时任务,每天早上拉取待结佣数据（增量）")
    @PostMapping("/incrementCommission")
    public ResultBody incrementCommission(String modifiedTime) {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(CACHE_LOCK10) == null) {
            /* 线程锁 */
            redisUtil.set(CACHE_LOCK10, true, EXPIRE_PERIOD);
            receiptServiceImpl.getMYhtfkapply();
            /* 获取上周时间 */
            if(modifiedTime == null || modifiedTime.equals("")){
                modifiedTime = cn.hutool.core.date.DateUtil.offsetDay(new Date(),-2).toString();
            }else{
                int i = Integer.parseInt(modifiedTime);
                modifiedTime = cn.hutool.core.date.DateUtil.offsetDay(new Date(),-i).toString();

            }
            return ResultBody.success(commissionServiceImpl.initCommission(modifiedTime));
        } else {
            log.info("周计划其他服务正在执行此项任务!!!!!!!!!!!!!,休眠：" + EXPIRE_PERIOD);
            return null;
        }
    }

}




