package cn.visolink.salesmanage.caopandata.controller;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.caopandata.service.CaoPanDataService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/9 11:05 上午
 */
@RestController
@Api(tags = "初始化数据接口")
@Slf4j
@RequestMapping("/signInitedData")
public class CaoPanDataController {
    @Autowired
    private CaoPanDataService caoPanDataService;



    @Log("初始化签约信息数据")
    @CessBody
    @ApiOperation(value = "初始化签约信息数据")
    @PostMapping("/signData")
    public ResultBody initSignData() {
        try {
            ResultBody resultBody = caoPanDataService.getSigningData();
            return resultBody;
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setMessages("初始化数据失败");
            resultBody.setCode(-1);
            e.printStackTrace();
            return resultBody;
        }
    }


    @Log("初始化nos签约信息数据前两个月")
    @CessBody
    @ApiOperation(value = "初始化nos签约信息数据前两个月")
    @PostMapping("/initNosSignData")
    public ResultBody initNosSignData(@RequestBody Map params) {
        try {
            ResultBody resultBody = caoPanDataService.getNosSigningAdd(params);
            return resultBody;
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setMessages("初始化数据失败");
            resultBody.setCode(-1);
            e.printStackTrace();
            return resultBody;
        }
    }

    @Log("初始化非采购数据立项信息数据")
    @CessBody
    @ApiOperation(value = "初始化非采购数据立项信息数据")
    @PostMapping("/signCostData")
    public ResultBody signCostData() {
        try {
            ResultBody resultBody = caoPanDataService.initCostData();
            return resultBody;
        } catch (Exception e) {
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setMessages("初始化立项信息数据失败");
            resultBody.setCode(-1);
            e.printStackTrace();
            return resultBody;
        }
    }

}
