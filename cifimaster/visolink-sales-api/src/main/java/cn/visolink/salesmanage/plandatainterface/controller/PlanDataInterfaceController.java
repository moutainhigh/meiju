package cn.visolink.salesmanage.plandatainterface.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.utils.HttpRequestUtil;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageService;
import cn.visolink.salesmanage.plandatainterface.service.PlanDataInterfaceservice;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.*;
import cn.visolink.salesmanage.datainterface.service.impl.*;


/**
 * <p>
 * 数据接口
 * </p>
 *
 * @author 李欢
 * @since 2019-11-18
 */
@RestController
@Api(tags = "数据接口")
@Slf4j
@RequestMapping("/plandatainterface")
    public class PlanDataInterfaceController {
        @Autowired
        PlanDataInterfaceservice datainterfaceservice;

    private static String MESSAGE_PUSH_PLAN1 = "MESSAGE_PUSH_PLAN1";
    private static int EXPIRE_PLAN =  (int) DateUtils.MILLIS_PER_MINUTE * 7 / 1000;


    @Autowired
    private RedisUtil redisUtil;

        @Log("更新mm_monthly_plan_index_detail_mingyuan数据")
        @ApiOperation(value = "更新mm_monthly_plan_index_detail_mingyuan数据")
        @PostMapping(value = "/updataMonthlyPlan")
        public
        Map updataMonthlyPlan(@RequestBody Map datas) {
            Map maplistt = new HashMap<>();
            maplistt =  datainterfaceservice.insertMonthPlan(datas);

            return maplistt;
        }



        @Log("更新mm_basic_trader_mingyuan数据")
        @ApiOperation(value = "更新mm_basic_trader_mingyuan数据")
        @PostMapping(value = "/updataBasic")
        public
        void updataBasic() {
            try {
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
            if (redisUtil.get(MESSAGE_PUSH_PLAN1) == null) {
                redisUtil.set(MESSAGE_PUSH_PLAN1, true, EXPIRE_PLAN);
                datainterfaceservice.insertBasic();
            }
        }

        //给出一个接口，如果提供了项目ID，则根据项目ID查询对应的周，月数据，如果不是则查询出所有的数据
        @Log("查询mm_monthly_plan_index_detail_mingyuan数据")
        @ApiOperation(value = "查询mm_monthly_plan_index_detail_mingyuan数据")
        @PostMapping(value = "/monthselect")
        public
        List<Map> reportMonthSelect(@RequestBody Map datas) {

            return datainterfaceservice.reportMonthSelect(datas);
        }


        //给出一个接口，如果提供了项目ID，则根据项目ID查询对应的周，月数据，如果不是则查询出所有的数据
        @Log("查询mm_monthly_plan_weekly_plan_mingyuan数据")
        @ApiOperation(value = "查询mm_monthly_plan_weekly_plan_mingyuan数据")
        @PostMapping(value = "/weekselect")
        public
        List<Map> reportWeekSelect(@RequestBody Map datas) {

            return datainterfaceservice.reportWeekSelect(datas);
        }







}




