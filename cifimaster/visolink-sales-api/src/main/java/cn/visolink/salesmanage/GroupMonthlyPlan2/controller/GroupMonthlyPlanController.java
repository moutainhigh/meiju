package cn.visolink.salesmanage.GroupMonthlyPlan2.controller;

import cn.visolink.logs.aop.log.Log;

import cn.visolink.salesmanage.GroupMonthlyPlan2.service.GroupMonthlyPlanService;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author wjc
 */
@RestController
@RequestMapping("/GroupMonthlyPlan")
@Api(tags = "导出")
public class GroupMonthlyPlanController {

    @Autowired
    private GroupMonthlyPlanService service ;
    @Log("初始化集团数据")
    @CessBody
    @ApiOperation(value = "初始化集团数据")
    @PostMapping("/GetMonthlyPlanByTheMonthCount")
    public int GetMonthlyPlanByTheMonthCount() {

        int result = service.GetMonthlyPlanByTheMonthCount("");
        return result;

    }



    @Log("初始化集团数据")
    @CessBody
    @ApiOperation(value = "初始化集团数据")
    @PostMapping("/SetMonthlyPlanInsert")
    public int SetMonthlyPlanInsert() {

        int result = service.SetMonthlyPlanInsert("");




        return result;




    }









}
