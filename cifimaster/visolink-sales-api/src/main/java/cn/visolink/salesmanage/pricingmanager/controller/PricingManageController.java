package cn.visolink.salesmanage.pricingmanager.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.pricingmanager.service.PricingManagerService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "审批单")
@RequestMapping("/pricing")
public class PricingManageController {

    @Autowired
    PricingManagerService pricingManagerService;

    @Log("跟新审批单")
    @CessBody
    @ApiOperation(value = "跟新审批单")
    @RequestMapping(value = "/updatePricing",method = RequestMethod.POST)
    public Integer  updatePricing(@RequestBody Map<String,Object> map){
       Integer result=  pricingManagerService.updatePricing(map);
       return result;
    }

    @Log("编写审批单")
    @CessBody
    @ApiOperation(value = "编写审批单")
    @RequestMapping(value = "/insertPricing",method = RequestMethod.POST)
    public Integer  insertPricing(@RequestBody Map<String,Object> map){
        Integer result=  pricingManagerService.insertPricing(map);
        return result;
    }


    @Log("查找审批单")
    @CessBody
    @ApiOperation(value = "查找审批单")
    @RequestMapping(value = "/selectPricing",method = RequestMethod.POST)
    public List<Map> selectPricing(@RequestBody Map<String,Object> map){
        List<Map> result=  pricingManagerService.selectPricing(map);
        return result;
    }

}
