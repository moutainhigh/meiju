package cn.visolink.firstplan.dingtalkmicroapp.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.dingtalkmicroapp.service.FirstBriefingService;
import cn.visolink.logs.aop.log.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sjl 钉钉h5微应用-首开简报
 * @Created date 2020/8/27 10:50 上午
 */
@RestController
@Api(tags = "firstBriefing")
@RequestMapping("/firstBriefing")
public class FirstBriefingController {
    @Autowired
    private FirstBriefingService firstBriefingService;

    @ApiOperation(value = "钉钉微应用开盘播报数据查询")
    @GetMapping(value = "/getFirstBriefingList")
    public ResultBody getFirstBriefingList(HttpServletRequest request) {
        String project_id = request.getParameter("project_id");
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("project_id",project_id);
        return firstBriefingService.getFirstBriefingList(paramMap,request);
    }
    @ApiOperation(value = "钉钉微应用开盘播报单个批次查询")
    @GetMapping(value = "/getFirstBriefingInfo")
    public ResultBody getFirstBriefingInfo(HttpServletRequest request) {
        String batch_id = request.getParameter("batch_id");
        HashMap<Object, Object> paramMap = new HashMap<>();
        paramMap.put("batch_id",batch_id);
        return firstBriefingService.getFirstBriefingInfo(paramMap);
    }
    @ApiOperation(value = "定时发送开盘播报钉钉消息")
    @PostMapping(value = "/firstBriefingMessage")
    public ResultBody firstBriefingMessage() {
        return firstBriefingService.firstBriefingMessage();
    }

    @ApiOperation(value = "定时重新推送OA的审批结果")
    @PostMapping(value = "/flowComparisonPush")
    public ResultBody flowComparisonPush() {
        firstBriefingService.flowComparisonPush();
        return null;
    }

}
