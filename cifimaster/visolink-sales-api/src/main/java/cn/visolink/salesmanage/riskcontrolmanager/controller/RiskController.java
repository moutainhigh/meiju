package cn.visolink.salesmanage.riskcontrolmanager.controller;

import cn.hutool.core.date.DateUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.riskcontrolmanager.dao.RiskContolDao;
import cn.visolink.salesmanage.riskcontrolmanager.service.RiskControlService;
import cn.visolink.utils.StringUtil;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@Api("风控系统")
@RequestMapping("/riskControl")
public class RiskController {

    private static String FENGKONG_PUSH_LOCK = "FENGKONG_PUSH_LOCK";
    private static int FENGKONG_PERIOD = (int) DateUtils.MILLIS_PER_MINUTE * 3 / 1000;

    @Autowired
    RiskControlService riskControlService;
    @Autowired
    RiskContolDao riskContolDao;
    @Autowired
    private RedisUtil redisUtil;

    /*SALEMAN-811,SALEMAN-827,SALEMAN-832,SALEMAN-842*/
    @Value("${createTimerisk.url}")
    private String createTimerisk;

    @Log("得到风控数据")
    @CessBody
    @ApiOperation(value = "得到风控数据")
    @PostMapping("/getData")
    public void getData(@RequestBody Map map) {
        riskControlService.getData(map);

    }


    @Log("得到风控数据，全量接口")
    @CessBody
    @ApiOperation(value = "得到风控数据，全量接口")
    @PostMapping("/getAllData")
    public void getAllData(@RequestBody Map map) {
        map.put("query", "全量");
        riskControlService.getData(map);

    }

    /*slaeman-1020*/
    @Log("定时初始化")
    @CessBody
    @ApiOperation(value = "定时初始化")
    @PostMapping("/initgetData")
    // @Scheduled(cron = "0 30 06 * * ?")
    public Boolean initgetData(Map map) {
        return riskControlService.getData(map);
    }

    @Log("查找风控概表")
    @CessBody
    @ApiOperation(value = "查找风控概表")
    @PostMapping("/getRiskData")
    public ResultBody selectRiskInfor(@RequestBody Map map) {
        return riskControlService.selectRiskInfor(map);


    }

    @Log("查找风控某个项目的细节表")
    @CessBody
    @ApiOperation(value = "查找风控某个项目的细节表")
    @PostMapping("/getRiskDetail")
    public ResultBody selectRiskInside(@RequestBody Map map) {
        /*前台有可能将值设置为字符串NULL*/
        if ((map.get("startTime") + "").equals("null")) {
            map.put("startTime", "");
        }
        if ((map.get("endTime") + "").equals("null")) {
            map.put("endTime", "");
        }
        return riskControlService.selectRiskInside(map);


    }

    @Log("单独初始化买方表")
    @CessBody
    @ApiOperation(value = "单独初始化买方表")
    @PostMapping("/setBuyer")
    public void setBuyer(@RequestBody Map map) {
        String queryStartDate = map.get("queryStartDate") + "";
        String queryEndDate = map.get("queryEndDate") + "";
        riskControlService.setBuyer(queryStartDate, queryEndDate);

    }

    @Log("查找事业部名字和ID")
    @CessBody
    @ApiOperation(value = "查找事业部名字和ID")
    @PostMapping("/findBusinessName")
    public List<Map> selectBusinessName() {

        List<Map> result = riskControlService.selectBusinessName();
        return result;
    }

    @Log("导出概述表")
    @CessBody
    @ApiOperation(value = "导出概述表")
    @GetMapping("/riskDataExport")
    public void riskDataExport(HttpServletRequest request, HttpServletResponse response, String startTime, String endTime, String business_unit_id, String channel, String userId, String byorder, String sorting) {
        /*前台有可能将值设置为字符串NULL*/
        if (startTime.equals("null")) {
            startTime="";
        }
        if (endTime.equals("null")) {
            endTime="";
        }
        Map map = new HashMap();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("business_unit_id", business_unit_id);
        map.put("channel", channel);
        map.put("userId", userId);
        map.put("byorder", byorder);
        map.put("sorting", sorting);
        riskControlService.riskDataExport(request, response, map, null);

    }

    @Log("导出风控详细表")
    @CessBody
    @ApiOperation(value = "导出风控详细表")
    @GetMapping("/riskDataInsideExport")
    public void riskDataInsideExport(HttpServletRequest request, HttpServletResponse response, String project_id, String startTime, String endTime, String choice, String channel, String business_unit_id, String userId) {
        Map map = new HashMap();
        /*前台有可能将值设置为字符串NULL*/
        if (startTime.equals("null")) {
            startTime = "";
        }
        if (endTime.equals("null")) {
            endTime = "";
        }
        map.put("project_id", project_id);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("choice", choice);
        map.put("channel", channel);
        map.put("business_unit_id", business_unit_id);
        map.put("userId", userId);

        riskControlService.riskDataExport(request, response, map, "inside");

    }


    @Log("仅仅更新概述表")
    @CessBody
    @ApiOperation(value = "仅仅更新概述表")
    @GetMapping("/risk")
    @Transactional(rollbackFor = Exception.class)
    public void riskDataInsideExpor() {
        riskContolDao.deleteRiskSurfaceBefore();
        riskContolDao.deleteOrderFkUpBefore();
        riskContolDao.insertOrderFkUp();
        riskContolDao.selectRiskSurface();
        /*更新风控概述表*/


    }


};
