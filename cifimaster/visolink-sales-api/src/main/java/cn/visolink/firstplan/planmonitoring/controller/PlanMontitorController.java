package cn.visolink.firstplan.planmonitoring.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.firstplan.planmonitoring.service.PlanMontitorService;
import cn.visolink.utils.CommUtilsUpdate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/2/25 11:59 上午
 */
@RestController
@Api(tags = "首开计划监控")
@RequestMapping("/planMontitor")
public class PlanMontitorController {

    @Autowired
    private PlanMontitorService planMontitorService;
    //计划监控首页数据接口
    @ApiOperation(value = "计划监控首页数据接口")
    @PostMapping("/montitorIndex")
    public Map montitorIndex(@RequestBody Map map, HttpServletRequest request){
        Map mapData= planMontitorService.montitorIndex(map,request);
        return mapData;
    }
    //更改节点亮灯状态
    @ApiOperation(value = "定时更改节点亮灯")
    @PostMapping("/updateNodeStatusTiming")
    public void  updateNodeStatusTiming(){
        planMontitorService.updateNodeStatusTiming();

    }   //更改节点亮灯状态
    @ApiOperation(value = "获取区域列表")
    @PostMapping("/getIdmBuinessData")
    public VisolinkResultBody getIdmBuinessData(){
        return planMontitorService.getIdmBuinessData();

    }

    //定时刷节点亮灯
  // @Scheduled(cron = "0 0 0 * * ?")
    @ApiOperation(value = "定时更新项目关系表-用于项目合并")
    @PostMapping("/updateProjectRelationship")
    public void updateProjectRelationship(){
       planMontitorService.updateProjectRelationship();
    }
    //定时更新计划表的项目名称
    @ApiOperation(value = "定时更新计划表的项目名称")
    @PostMapping("/updatePlanProjectName")
    // @Scheduled(cron = "0 0 0 * * ?")
    public void updatePlanProjectName(){
        planMontitorService.updatePlanProjectName();
    }
    //定时更新计划表的当前节点
    @ApiOperation(value = "定时更新计划表的当前节点")
    @PostMapping("/updateSoonNode")
    // @Scheduled(cron = "0 0 0 * * ?")
    public void updateSoonNode(){
        planMontitorService.updateSoonNode();
    }
}
