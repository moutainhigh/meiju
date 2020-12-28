package cn.visolink.salesmanage.groupmanagement.controller;
import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.groupmanagement.dao.GroupManageDao;
import cn.visolink.salesmanage.groupmanagement.service.GroupManageService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author wjc
 */
@RestController
@RequestMapping("/group")
@Api("集团日期策划")

public class GroupManagementController {
    @Autowired
    private GroupManageService groupManageService;
    @Autowired
    private GroupManageDao groupManageDao;

    @Log("初始化集团数据")
    @CessBody
    @ApiOperation(value = "初始化集团数据")
    @PostMapping("/getMessage")
    public int getMessages(@RequestBody Map map) {
        int result = groupManageService.getBusiness(map);
        return result;
    }

    @Log("集团月度计划查询")
    @CessBody
    @ApiOperation(value = "集团月度计划查询")
    @RequestMapping(value = "/getGroupMonthPlans",method = RequestMethod.POST)
    public List<Map> getGroupMonthPlans(@RequestBody Map map) {
        List<Map> result = groupManageService.getGroupMonthPlan(map);
        return result;

    }

    @Log("查询集团月度计划的所有数据")
    @CessBody
    @ApiOperation(value = "查询集团月度计划的所有数据")
    @RequestMapping(value = "/getGroupAllMessages",method = RequestMethod.POST)
    public List<Map> getGroupAllMessages(@RequestBody Map map) {
        List<Map> result = groupManageService.getGroupAllMessage(map);
        return result;
    }

    @Log("查询集团下子级的数据")
    @CessBody
    @ApiOperation(value = "查询集团下子级的数据")
    @RequestMapping(value = "/getGroupChildMessages",method = RequestMethod.POST)
    public List<Map> getGroupChildMessages(@RequestBody Map map) {

        List<Map> result = groupManageService.getGroupChildMessage(map);
        return result;
    }

    @Log("暂存，下达，上报 （修改状态）")
    @CessBody
    @ApiOperation(value = "暂存，下达，上报 （修改状态）")
    @RequestMapping(value = "/updatePlanStatus",method = RequestMethod.POST )
    public int updatePlanStatus(@RequestBody Map map) {
        int result = groupManageService.updatePlanStatus(map);
        return result;
    }

    @Log("区域月度计划查询")
    @CessBody
    @ApiOperation(value = "区域月度计划查询")
    @RequestMapping(value = "/getRegionalMonthPlans",method = RequestMethod.POST)
    public List<Map> getRegionalMonthPlans(@RequestBody Map map) {
        List<Map> result = groupManageService.getRegionalMonthPlan(map);
        return result;

    }
    @Log("在区域里获取集团下达数据")
    @CessBody
    @ApiOperation(value = "在区域里获取集团下达数据")
    @RequestMapping(value = "/getGroupReleaseInRegional",method = RequestMethod.POST)
    public List<Map> getGroupReleaseInRegional(@RequestBody Map map) {
        List<Map> result = groupManageService.getGroupReleaseInRegional(map);
        return result;
    }

    @Log("初始化区域数据")
    @CessBody
    @ApiOperation(value = "初始化区域数据")
    @RequestMapping(value ="/getGroupMessages",method = RequestMethod.POST)
    public List<Map> getGroupMessages(@RequestBody Map map) {
        List<Map> result = groupManageService.getBusinessForRegional(map);
        return result;

    }
    @Log("获取初始化区域数据")
    @CessBody
    @ApiOperation(value = "获取初始化区域数据")
    @RequestMapping(value = "/getRegionalMessages",method = RequestMethod.POST)
    public List<Map> getRegionalMessages(@RequestBody Map map) {
        List<Map> result = groupManageService.getRegionalMessage(map);
        return result;

    }
    @Log("区域指标细化")
    @CessBody
    @ApiOperation(value = "区域指标细化")
    @RequestMapping(value = "/getRegionChildMessages",method = RequestMethod.POST)
    public List<Map> getRegionChildMessages(@RequestBody Map map) {
        List<Map> result = groupManageService.getRegionChildMessage(map);
        return result;

    }
    @Log("项目月度计划查询")
    @CessBody
    @ApiOperation(value = "项目月度计划查询")
    @RequestMapping(value = "/getProjectMonthPlans",method = RequestMethod.POST)
    public List<Map> getProjectMonthPlans(@RequestBody Map map) {
        List<Map> result = groupManageService.getProjectMonthPlan(map);


        return result;
    }
    @Log("从项目月度计划获取区域下达数据")
    @CessBody
    @ApiOperation(value = "从项目月度计划获取区域下达数据")
    @RequestMapping(value = "/getRegionalReleaseInProjects",method = RequestMethod.POST)
    public List<Map> getRegionalReleaseInProjects(@RequestBody Map map) {
        List<Map> result = groupManageService.getRegionalReleaseInProject(map);
        return result;
    }
    @Log("初始化项目数据")
    @CessBody
    @ApiOperation(value = "初始化项目数据")
    @RequestMapping(value = "/insertProjectMonthPlanIndex",method = RequestMethod.POST)
    public int insertProjectMonthPlanIndex(@RequestBody Map map) {
        int result= groupManageService.insertProjectMonthPlanIndex(map);
        return result;

    }

    @Log("获取初始化项目数据")
    @CessBody
    @ApiOperation(value = "获取初始化项目数据")
    @RequestMapping(value = "/getProjectMessages",method = RequestMethod.POST)
    public List<Map> getProjectMessages(@RequestBody Map map) {
        List<Map> result = groupManageService.getProjectMessage(map);
        List<Map> resultreal= groupManageDao.getGroupMonthPlanForProject(map);
        if(result!=null && result.size()>0){
            result.get(0).put("groupStatus",resultreal.get(0).get("plan_status"));
        }
        return result;

    }

    @Log("通过项目获取区域城市ID")
    @ApiOperation(value = "通过项目获取区域城市ID")
    @RequestMapping(value = "/getProjectAreaID",method = RequestMethod.POST)
    public VisolinkResultBody getProjectAreaID(@RequestBody Map params) {
        String projectId=params.get("projectId")+"";
        VisolinkResultBody boby=new VisolinkResultBody();
        Map result = groupManageService.getProjectAreaID(projectId);
        boby.setResult(result);
        return boby;
    }

    @Log("区域上报判断")
    @CessBody
    @ApiOperation(value = "区域上报判断")
    @RequestMapping(value = "/regionalReports",method = RequestMethod.POST)
    public int regionalReport(@RequestBody Map map) {
        int result = groupManageService.getRegionUnderProject(map);
        return result;
    }

    @Log("集团确认判断")
    @CessBody
    @ApiOperation(value = "集团确认判断")
    @RequestMapping(value = "/groupReports",method = RequestMethod.POST)
    public int groupJudge(@RequestBody Map map) {
        int result = groupManageService.getGroupUnderRegion(map);
        return result;
    }

    @Log("修改区域（库存，新增，合计）费用")
    @CessBody
    @ApiOperation(value = "修改区域（库存，新增，合计）费用")
    @RequestMapping(value = "/updateRegionFunds",method = RequestMethod.POST)
    public int updateRegionFunds(@RequestBody Map map) {
        int result = groupManageService.updateRegionFunds(map);
        return result;
    }

    @Log("修改项目（库存，新增，合计）费用")
    @CessBody
    @ApiOperation(value = "修改项目（库存，新增，合计）费用")
    @RequestMapping(value = "/updateProjectFunds",method = RequestMethod.POST)
    public int updateProjectFunds(@RequestBody Map map) {
        int result = groupManageService.updateRegionFunds(map);
        return result;
    }
}
