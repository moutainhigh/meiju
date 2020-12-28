package cn.visolink.salesmanage.signdata.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.signdata.service.SingDataService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/18 8:32 下午
 */

@RestController
@Api(tags = "审批单")
@RequestMapping("/sing")
public class SingDataController {

    @Autowired
    private SingDataService singDataService;


    @Log("获取月度/周度签约金额")
    @CessBody
    @ApiOperation(value = "获取月度/周度签约金额")
    @RequestMapping(value = "/getSingData",method = RequestMethod.POST)
    public Map getSingMoneyData(@RequestBody Map map){

        return singDataService.getSingMoneyData(map);
    }
}
