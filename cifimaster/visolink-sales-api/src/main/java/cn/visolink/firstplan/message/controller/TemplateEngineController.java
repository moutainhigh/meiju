package cn.visolink.firstplan.message.controller;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.service.PricingMessageService;
import cn.visolink.firstplan.message.service.TemplateEngineService;
import cn.visolink.logs.aop.log.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/8 9:42 上午
 */
@RestController
@Api(tags = "消息管理接口API")
@Slf4j
@RequestMapping("/messageGeneration")
public class TemplateEngineController {
    @Autowired
    private TemplateEngineService templateEngineService;
    @Autowired
    private PricingMessageService pricingMessageService;


    @Log("消息管理_首开计划消息生成")
    @ApiOperation(value = "消息管理_首开消息生成")
    @PostMapping("/firstPlanMessageGeneration")
    public ResultBody firstPlanMessage(@RequestBody Map paramMap) {
        return templateEngineService.firstPlanMessage(paramMap);
    }
    @Log("消息管理_三大件延期提醒消息生成")
    @ApiOperation(value = "消息管理_三大件延期提醒消息生成手动调用")
    @PostMapping("/threepiecesRemind")
    public ResultBody threepiecesRemind(@RequestBody Map map) {
        return templateEngineService.threepiecesRemind(map);
    }

    @Log("消息管理_三大件延期提醒消息生成")
    @ApiOperation(value = "消息管理_三大件延期提醒消息生成 （每天早上10点）")
    @PostMapping("/threepiecesRemindTimedTasks")
    //@Scheduled(cron = "0 0 10 * * ?")
    public ResultBody threepiecesRemindTimedTasks() {
        return templateEngineService.threepiecesRemind(null);
    }




    @Log("消息管理_客储偏差预警")
    @ApiOperation(value = "消息管理_客储偏差预警手动调用")
    @PostMapping("/customerStorageDeviationTimedTasks")
    //@Scheduled(cron = "0 0 9 * * ?")
    public ResultBody customerStorageDeviationTimedTasks() {
        return templateEngineService.customerStorageDeviation(null);
    }

    @Log("消息管理_客储偏差预警")
    @ApiOperation(value = "消息管理_客储偏差预警手动调用")
    @PostMapping("/customerStorageDeviation")
    //@Scheduled(cron = "0 0 9 * * ?")
    public ResultBody customerStorageDeviation(@RequestBody  Map map) {
        return templateEngineService.customerStorageDeviation(map);
    }

    @Log("消息管理_九大节点延期提醒")
    @ApiOperation(value = "消息管理_九大节点延期提醒（每天早上8点30）")
    @PostMapping("/sendNodeOverdueMes")
    //@Scheduled(cron = "0 30 8 * * ?")
    public ResultBody sendNodeOverdueMes(@RequestBody Map map) {
        return templateEngineService.sendNodeOverdueMes(map);
    }
    @Log("消息管理_九大节点延期提醒")
    @ApiOperation(value = "消息管理_九大节点延期提醒（每天早上8点30）")
    @PostMapping("/sendNodeOverdueMesTimedTasks")
    //@Scheduled(cron = "0 30 8 * * ?")
    public ResultBody sendNodeOverdueMesTimedTasks() {
        return templateEngineService.sendNodeOverdueMes(null);
    }


    @Log("消息管理_定调价消息提醒")
    @ApiOperation(value = "消息管理_定调价消息提醒")
    @PostMapping("/pricingMessageGen")
    //@Scheduled(cron = "0 30 8 * * ?")
    public ResultBody pricingMessageGen(@RequestBody Map map) {

        return pricingMessageService.pricingMessageGen(map.get("json_id")+"");
    }

    @Log("消息管理_首开简报")
    @ApiOperation(value = "消息管理_首开简报")
    @PostMapping("/firstBroadcastMessageGen")
    //@Scheduled(cron = "0 30 8 * * ?")
    public ResultBody firstBroadcastMessageGen(@RequestBody Map map) {
        return templateEngineService.firstBroadcastMessageGen(map);
    }





}
