package cn.visolink.salesmanage.weeklymarketingplan.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.weeklymarketingplan.service.WeeklyMarketingService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


 /*
         * @author 孙林
        * @since 2019-10-15
        */
@RestController
@Api(tags = "营销周计划")
@RequestMapping("/weeklyMarketing")
public class WeeklyMarketingController {

    @Autowired
    WeeklyMarketingService service;

    @Log("初始化营销周计划详情表")
    @CessBody
    @ApiOperation(value = "初始化营销周计划详情表")
    @RequestMapping(value = "/weekMarketingPlanInitial",method = RequestMethod.GET)
    public  void weekMarketingPlanInitial() {
        service.weekMarketingPlanInitial();

    }

    @Log("跟新营销周计划详情表")
    // @CessBody
    @ApiOperation(value = "跟新营销周计划详情表")
    @RequestMapping(value = "/weekMarketingPlanUpdate",method = RequestMethod.POST)
    public  ResultBody weekMarketingPlanUpdate(@RequestBody Map map) {

        ResultBody resultBody=new ResultBody();

        Integer result= 0;
        if(map.get("0")!=null){
        Map<String,Object> parameters =(Map<String,Object>) map.get("0");
            result=  service.weekMarketingPlanUpdate(parameters);
        }else {
            result=  service.weekMarketingPlanUpdate(map);
        }

    if(result==null){
        resultBody.setMessages("因项目已被区域复核或区域已上报，现无法进行暂存或上报");
        resultBody.setCode(500);
    }else {
        resultBody.setData(result);
        resultBody.setCode(200);
    }

        return resultBody;

    }
        /*SALEMAN-724 */
        /*SALEMAN-725 */
     @Log("区域跟新营销周计划详情表")
     @CessBody
     @ApiOperation(value = "区域跟新营销周计划详情表")
     @RequestMapping(value = "/weekMarketingPlanAreaUpdate",method = RequestMethod.POST)
     public  Integer  weekMarketingPlanAreaUpdate(@RequestBody List<Map> map) {

         Integer result= 0;
         for(Map resultmap:map){

                 result += service.weekMarketingPlanUpdate(resultmap);
                 Map EffectiveMap = new HashMap();
                 EffectiveMap.put("how_week", resultmap.get("how_week"));
                 EffectiveMap.put("this_time", resultmap.get("this_time"));
                 EffectiveMap.put("area_id", resultmap.get("area_id"));
                 EffectiveMap.put("project_id", resultmap.get("project_id"));
                 EffectiveMap.put("is_effective", 1);
                 EffectiveMap.put("plan_status", 1);
                 EffectiveMap.put("type", resultmap.get("type"));
                 service.weekMarketingPlanEffective(EffectiveMap);
             }

         return result;

     }

     /*SALEMAN-699*/
     @Log("上报营销周计划详情表")
    // @CessBody
    @ApiOperation(value = "上报营销周计划详情表")
    @RequestMapping(value = "/weekMarketingPlanEffective",method = RequestMethod.POST)
    public ResultBody weekMarketingPlanEffective(@RequestBody Map map) {

         return service.weekMarketingPlanEffective(map);



    }

     @Log("项目查找营销周计划")
     @CessBody
     @ApiOperation(value = "项目查找营销周计划")
     @RequestMapping(value = "/ProjectSelectWeekly",method = RequestMethod.POST)
     public  List<Map> ProjectSelectWeekly(@RequestBody Map<String, Object> map) {
         List<Map> result=  service.ProjectSelectWeekly(map);
         return result;
     }
     @Log("区域查看周计划")
     @CessBody
     @ApiOperation(value = "区域查看周计划")
     @RequestMapping(value = "/regionSelectWeekly",method = RequestMethod.POST)
     public  List<Map> regionSelectWeekly(@RequestBody Map<String, Object> map) {
         List<Map> result=  service.regionSelectWeekly(map);
         return result;
     }

     @Log("集团查看周计划")
     @CessBody
     @ApiOperation(value = "集团查看周计划")
     @RequestMapping(value = "/groupSelectWeekly",method = RequestMethod.POST)
     public  List<Map> groupSelectWeekly(@RequestBody Map<String, Object> map) {
         List<Map> result=  service.groupSelectWeekly(map);
         return result;
     }
     /*SALEMAN-695*/
     @Log("营销周计划查看或编制")
     @CessBody
     @ApiOperation(value = "营销周计划查看或编制")
     @RequestMapping(value = "/ProjectExamineWeekly",method = RequestMethod.POST)
     public  List<Map> ProjectExamineWeekly(@RequestBody Map<String, Object> map) {
         List<Map> result=  service.ProjectExamineWeekly(map);
         return result;
     }

     @Log("营销周计划得到某个月的所有周")
     @CessBody
     @ApiOperation(value = "营销周计划得到某个月的所有周")
     @RequestMapping(value = "/weekMarketingWeekSelect",method = RequestMethod.POST)
     public  List<Map> weekMarketingWeekSelect(@RequestBody Map<String, Object> map) {
         List<Map> result=  service.weekMarketingWeekSelect(map);
         return result;
     }

     /*SALEMAN-723*/
     @Log("集团周报数据导出")
     @ApiOperation(value = "集团周报数据导出")
     @GetMapping(value = "/groupWeeklyDataExport")
     public void groupWeeklyDataExport(HttpServletRequest request, HttpServletResponse response, String this_time, int how_week, String projectId) {
         service.weeklyDataExport(request,response,this_time,how_week,1,projectId,null);
     }
     /*SALEMAN-723*/
     @Log("集团周报数据导出")
     @ApiOperation(value = "区域周报数据导出")
     @GetMapping(value = "/regionWeeklyDataExport")
     public void regionWeeklyDataExport(HttpServletRequest request, HttpServletResponse response, String this_time, int how_week, String projectId) {
         service.weeklyDataExport(request,response,this_time,how_week,2,projectId,null);
     }
    /*SALEMAN-723*/
     @Log("集团周报数据导出")
     @ApiOperation(value = "项目周报数据导出")
     @GetMapping(value = "/projectWeeklyDataExport")
     public void projectWeeklyDataExport(HttpServletRequest request, HttpServletResponse response, String this_time, int how_week, String projectId) {
         service.weeklyDataExport(request,response,this_time,how_week,3,projectId,null);
     }

     @Log("更新已阅状态")
     @CessBody
     @ApiOperation(value = "更新已阅状态")
     @PostMapping(value = "/updateCheckeds")
     public Map updateCheckeds(@RequestBody List<Map> listmap) {

         Map result=null;
         if(listmap.size()>1){
             listmap.get(1).put("areaId", listmap.get(1).get("area_id"));
             result = service.updateCheckeds(listmap.get(1));
         }else {
             listmap.get(0).put("projectId", listmap.get(0).get("project_id"));
             result = service.updateCheckeds(listmap.get(0));
         }

         return result;
     }
        /*SALEMAN-700,SALAMEN-688,salamen-781*/
     @Log("发起审批流并存储区域表单")
     //@CessBody
     @ApiOperation(value = "发起审批流并存储区域表单")
     @PostMapping(value = "/reportExcel")
     public ResultBody reportExcel(@RequestBody Map map,HttpServletRequest request, HttpServletResponse response){
         ResultBody resultBody=new ResultBody();

        String result=  service.reportExcel(request,response,map);
        if(result.equals("警告")){
            resultBody.setMessages("该流程已在审批中，无法重复提交");
            resultBody.setCode(-1);
        }else if(result.equals("已上报")){
            resultBody.setMessages("该项目已上报，无法重新发起或更改");
            resultBody.setCode(-1);
        }

        else {
            resultBody.setData(result);
            resultBody.setCode(200);
        }
            return resultBody;
    }
     }
