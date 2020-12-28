package cn.visolink.salesmanage.fileimport.controller;

import cn.visolink.utils.Constant;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.fileimport.service.ImportService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@Api(tags = "导入")
@RequestMapping("/import")
public class ImportController {


    @Autowired
    ImportService importService;

    @Log("导入")
    @CessBody
    @ApiOperation(value = "集团导入")
    @ResponseBody
    @PostMapping(value = "/groupMonthlyPlanImport.action")
    public Map groupMonthlyPlanImport(MultipartFile file, String months){
        return importService.monthlyPlanImport(file,months, Constant.PREPARED_BY_UNIT_TYPE_GROUP);
    }

    @Log("区域导入")
    @CessBody
    @ResponseBody
    @ApiOperation(value = "区域导入")
    @PostMapping(value = "/regionMonthlyPlanImport.action")
    public Map regionMonthlyPlanImport(MultipartFile file,String months){
        return importService.monthlyPlanImport(file,months, Constant.PREPARED_BY_UNIT_TYPE_REGION);
    }

    @Log("项目导入")
    @CessBody
    @ResponseBody
    @ApiOperation(value = "项目导入")
    @PostMapping(value = "/projectMonthlyPlanImport.action")
    public Map projectMonthlyPlanImport(MultipartFile file,String month ){
        return importService.monthlyPlanImport(file,month, Constant.PREPARED_BY_UNIT_TYPE_PROJECT);
    }

    @Log("表三导入")
    @CessBody
    @ResponseBody
    @ApiOperation(value = "表三导入")
    @PostMapping(value = "/listThreePlanImport.action")
    public void listThreePlanImport(MultipartFile file,String month,String businessId){
        importService.listThreePlanImport(file, month,businessId);
    }
}