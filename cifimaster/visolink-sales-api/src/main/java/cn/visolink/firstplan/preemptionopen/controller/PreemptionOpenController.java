package cn.visolink.firstplan.preemptionopen.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.preemptionopen.service.PreemptionOpenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl 抢开申请
 * @Created date 2020/7/15 11:14 上午
 */
@RestController
@Api(tags = "抢开申请")
@RequestMapping("/PreemptionOpen")
public class PreemptionOpenController {
    @Autowired
    private PreemptionOpenService preemptionOpenService;
    @ApiOperation(value = "抢开申请渲染数据")
    @PostMapping(value = "/viewPreemptionOpenData")
    public ResultBody viewPreemptionOpenData(@RequestBody Map map){
        return preemptionOpenService.viewPreemptionOpenData(map);
    }

    @ApiOperation(value = "拆分新的周数据")
    @PostMapping(value = "/getWeekSpiltData")
    public ResultBody getWeekSpiltData(@RequestBody Map map){
        return preemptionOpenService.getWeekSpiltData(map);
    }

    @ApiOperation(value = "切换版本")
    @PostMapping(value = "/switchVersion")
    public ResultBody switchVersion(@RequestBody Map map){
        return preemptionOpenService.swicthVersion(map);
    }
    @ApiOperation(value = "保存/提交数据")
    @PostMapping(value = "/savePreemptionOpenData")
    public ResultBody savePreemptionOpenData(@RequestBody Map map, HttpServletRequest request){
        String username = request.getHeader("username");
        map.put("username",username);
        return preemptionOpenService.savePreemptionOpenData(map);
    }
    @ApiOperation(value = "快速审批")
    @PostMapping(value = "/approvedCallback")
    public ResultBody approvedCallback(@RequestBody Map map){
        return preemptionOpenService.approvedCallback(map);
    }

}
