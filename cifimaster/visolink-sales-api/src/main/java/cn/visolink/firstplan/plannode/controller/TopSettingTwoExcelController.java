package cn.visolink.firstplan.plannode.controller;

import cn.hutool.http.HttpRequest;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.plannode.service.TopSettingTwoExcelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * @author bao
 * @date 2020-04-20
 */

@RestController
@Api(tags = "顶设2数据导出Excel")
@RequestMapping("/TopSettingTwoExcel")
public class TopSettingTwoExcelController {

    private final TopSettingTwoExcelService topSettingTwoExcelService;

    public TopSettingTwoExcelController(TopSettingTwoExcelService topSettingTwoExcelService) {
        this.topSettingTwoExcelService = topSettingTwoExcelService;
    }


    @ApiOperation(value = "核心指标导出")
    @GetMapping("/IndicatorsExcel")
    @ApiImplicitParams ({
        @ApiImplicitParam(name = "planId", value="plan_id"),
        @ApiImplicitParam(name = "planNodeId", value="plan_node_id")
    })
    public void exportExcelIndicators(  HttpServletRequest request, HttpServletResponse response,String plan_id,String plan_node_id){
        Map map = new HashMap();
        map.put("plan_id",plan_id);
        map.put("plan_node_id",plan_node_id);
        topSettingTwoExcelService.exportExcelIndicators(request,response,map);
    }

    @ApiOperation(value = "全盘量价规划")
    @GetMapping("/VolumePricePlanningExcel")
    @ApiImplicitParams ({
            @ApiImplicitParam(name = "plan_id", value="plan_id"),
            @ApiImplicitParam(name = "plan_node_id", value="plan_node_id")
    })
    public void exportExcelVolumePricePlanning(  HttpServletRequest request, HttpServletResponse response,String plan_id,String plan_node_id){
        Map map = new HashMap();
        map.put("plan_id",plan_id);
        map.put("plan_node_id",plan_node_id);
        topSettingTwoExcelService.exportExcelVolumePricePlanning(request,response,map);
    }

    @ApiOperation(value = "首开前费用计划")
    @GetMapping("/CostPlanExcel")
    @ApiImplicitParam(name = "plan_node_id", value="plan_node_id")
    public void exportExcelCostPlan(  HttpServletRequest request, HttpServletResponse response,String plan_node_id){
        topSettingTwoExcelService.exportExcelCostPlan(request,response,plan_node_id);
    }

    @ApiOperation(value = "客储计划")
    @GetMapping("/CustomerSavingsPlanExcel")
    @ApiImplicitParams ({
            @ApiImplicitParam(name = "plan_id", value="plan_id"),
            @ApiImplicitParam(name = "plan_node_id", value="plan_node_id")
    })
    public void exportExcelCustomerSavingsPlan(  HttpServletRequest request, HttpServletResponse response,String plan_id,String plan_node_id){
        Map map = new HashMap();
        map.put("plan_id",plan_id);
        map.put("plan_node_id",plan_node_id);
        topSettingTwoExcelService.exportExcelCustomerSavingsPlan(request,response,map);
    }

}
