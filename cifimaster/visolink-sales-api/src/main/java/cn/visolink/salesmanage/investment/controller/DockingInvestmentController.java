package cn.visolink.salesmanage.investment.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.investment.service.DockingInvestmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author yangjie
 * @date 2020-10-26
 */
@RestController
@RequestMapping("dockingInvestment")
@Api(tags = "对接投资系统")
public class DockingInvestmentController {

    private final DockingInvestmentService dockingInvestmentService;

    @Autowired
    public DockingInvestmentController(DockingInvestmentService dockingInvestmentService) {
        this.dockingInvestmentService = dockingInvestmentService;
    }

    @Log("获取上会版、拿地后数据（投资系统）")
    @PostMapping(value = {"getInvestmentSystemData"})
    @ApiOperation(value = "获取上会版、拿地后数据（投资系统）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"projectId\":\"项目id\"}", paramType = "body"),
    })
    public ResultBody getInvestmentSystemData(@RequestBody Map<String, Object> map) {
        return dockingInvestmentService.getInvestmentSystemData(map);
    }

}
