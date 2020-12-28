package cn.visolink.firstplan.monitor.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.firstplan.monitor.service.MonitorService;
import cn.visolink.logs.aop.log.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Api(tags = "monitor")
@RequestMapping("/monitor")
public class MonitorController {


    @Autowired
    private MonitorService monitorService;
    @Log("监控详情页面")
    @ApiOperation(value = "监控详情")
    @PostMapping(value = "/queryMonitor")
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody queryMonitor(@RequestBody Map params) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        resBoby.setResult(monitorService.getPlanNodeInfo(params));
        return resBoby;
    }


    @Log("实时客储计划")
    @ApiOperation(value = "实时客储计划")
    @PostMapping(value = "/monitorRealNode")
    @Transactional(rollbackFor = Exception.class)
    public VisolinkResultBody monitorRealNode(@RequestBody Map params) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        resBoby.setResult(monitorService.queryMonitorNewNode(params));
        return resBoby;
    }

    @Log("实时客储计划")
    @ApiOperation(value = "实时客储计划")
    @PostMapping(value = "/selectMonitorPlanNode")
    public VisolinkResultBody selectMonitorPlanNode(@RequestBody Map params) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        resBoby.setResult(monitorService.selectMonitorPlanNode(params.get("plan_id")+""));
        return resBoby;
    }
}
