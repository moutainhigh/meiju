package cn.visolink.salesmanage.fileexport.controller;

import cn.visolink.utils.Constant;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.fileexport.service.ExportService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@RestController
@Api(tags = "导出")
@RequestMapping("/Export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Log("集团Excel导出")
    @CessBody
    @ApiOperation(value = "集团Excel导出")
    @GetMapping(value = "/groupMonthlyPlanExport.action")
    public void groupMonthlyPlanExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) {
        exportService.monthlyPlanExport(request,response, month, Constant.PREPARED_BY_UNIT_TYPE_GROUP, businessId,Arrays.asList(Constant.PREPARED_BY_LEVEL_REGION,Constant.PREPARED_BY_LEVEL_PROJECT));
    }

    @Log("区域Excel导出")
    @CessBody
    @ApiOperation(value = "区域Excel导出")
    @GetMapping(value = "/regionMonthlyPlanExport.action")
    public void areaMonthlyPlanExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) {
        exportService.monthlyPlanExport(request,response, month, Constant.PREPARED_BY_UNIT_TYPE_REGION, businessId,Arrays.asList(Constant.PREPARED_BY_LEVEL_PROJECT));
    }

    @Log("项目Excel导出")
    @CessBody
    @GetMapping(value = "/projectMonthlyPlanExport.action")
    public void projectMonthlyPlanExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) {
        exportService.monthlyPlanProjectExport(request,response, month, Constant.PREPARED_BY_UNIT_TYPE_PROJECT, businessId,Arrays.asList(Constant.PREPARED_BY_LEVEL_DISTRIBUTION,Constant.PREPARED_BY_LEVEL_BATCH,Constant.PREPARED_BY_LEVEL_BLOCK,Constant.PREPARED_BY_LEVEL_FORMAT,Constant.PREPARED_BY_LEVEL_AREA_SEGMENT));
    }

    @Log("表三Excel导出")
    @CessBody
    @GetMapping(value = "/listThreeExport.action")
    public void listThreeExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) throws Exception {
        exportService.listThreeExport(request,response, month,businessId);
    }

    @Log("表四Excel导出")
    @CessBody
    @GetMapping(value = "/listFourExport.action")
    public void listFourExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) throws Exception {
        exportService.listFourExport(request,response, month,businessId);
    }


    @Log("集团Excel上报导出")
    @CessBody
    @ApiOperation(value = "集团Excel上报导出")
    @GetMapping(value = "/groupMonthlyUpExport.action")
    public void groupMonthlyUpExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) {
        exportService.monthlyPlanUpExport(request,response, month, Constant.PREPARED_BY_UNIT_TYPE_GROUP, businessId,Arrays.asList(Constant.PREPARED_BY_LEVEL_REGION,Constant.PREPARED_BY_LEVEL_PROJECT));
    }

    @Log("区域Excel上报导出")
    @CessBody
    @ApiOperation(value = "区域Excel上报导出")
    @GetMapping(value = "/regionMonthlyUpExport.action")
    public void regionMonthlyUpExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) {
        exportService.monthlyPlanUpExport(request,response, month, Constant.PREPARED_BY_UNIT_TYPE_REGION, businessId,Arrays.asList(Constant.PREPARED_BY_LEVEL_PROJECT));
    }


/*现在集团编制有改动，要求所有单元格放开编制，所以在这里单独做一个*/
    @Log("集团Excel编制导出")
    @CessBody
    @ApiOperation(value = "集团Excel编制导出")
    @GetMapping(value = "/groupMonthlyPlanWriteExport.action")
    public void groupMonthlyPlanWriteExport(HttpServletRequest request,HttpServletResponse response, String month, String businessId) {


        exportService.monthlyPlanExport(request,response, month,0, businessId,Arrays.asList(Constant.PREPARED_BY_LEVEL_REGION,Constant.PREPARED_BY_LEVEL_PROJECT));
    }

}
