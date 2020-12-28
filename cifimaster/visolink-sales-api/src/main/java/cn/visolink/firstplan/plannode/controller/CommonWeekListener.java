package cn.visolink.firstplan.plannode.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.plannode.service.PlanNodeService;
import cn.visolink.logs.aop.log.Log;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bao
 * @date 2020-04-14
 */

@RestController
@Api(tags = "周上报数据生成")
@Slf4j
@RequestMapping("/CommonWeekPlan")
public class CommonWeekListener{

    private final PlanNodeService planNodeService;

    public CommonWeekListener(PlanNodeService planNodeService) {
        this.planNodeService = planNodeService;
    }

    @Log("定时任务,每月初始生成周上报")
    @CessBody
    @ApiOperation(value = "定时任务,每月1号0点生成周上报")
    @PostMapping("/initCommonWeekPlan")
    //@Scheduled(cron="0 0 0 1 * ?")
    public ResultBody initCommonWeekPlan() {
        try {
            planNodeService.insertCommonWeekPlan();
            System.out.println("周上报数据生成，执行任务成功");
            return ResultBody.success(true);
        }catch (Exception e){
            return ResultBody.success(false);
        }
    }

}
