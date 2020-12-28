package cn.visolink.salesmanage.monthdetail.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.businessmanager.dao.BusinessManagerDao;
import cn.visolink.salesmanage.businessmanager.service.BusinessManagerService;
import cn.visolink.salesmanage.groupmanagement.service.UpdateMonthlyService;
import cn.visolink.salesmanage.monthdetail.service.MonthManagerService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * ProjectClues前端控制器
 * </p>
 *
 * @author 孙林
 * @since 2019-9-17
 */
@RestController
@Api(tags = "表二表三表四")
@RequestMapping("/projectmanager")
public class MonthManagerController {

    @Autowired
    private MonthManagerService service;

    @Autowired
    private UpdateMonthlyService monthlyservice;

    @Autowired
    private BusinessManagerService Businessservice;

    @Autowired
    private BusinessManagerDao BusinessserDao;

    @Log("遍历月度计划明细到表二")
    @CessBody
    @ApiOperation(value = "遍历月度计划明细到表二")
    @RequestMapping(value = "/allMouthPlanSelect/{userId}/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public Map<String, Object> allMouthPlanSelect(@ApiParam(name = "userId", value = "用户ID")@PathVariable("userId")String userId,
            @ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                                          @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,
                                                          @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective) {
        Map<String, Object> result = service.allMouthPlanSelect(userId,projectId, months, isEffective);

        return result;
    }

    @Log("通过项目ID和月份来跟新表二信息（月度计划明细）")
    @CessBody
    @ApiOperation(value = "通过项目ID和月份来跟新表二信息（月度计划明细）")
    @RequestMapping(value = "/mouthPlanUpdate",method =RequestMethod.POST)
    public Integer mouthPlanUpdate(@RequestBody Map<String, Object> map) {
       Integer result= service.mouthPlanUpdate(map);
       return result;

    }

    @Log("通过项目ID和月份来设置表二（月度计划明细）是否生效状态")
    @CessBody
    @ApiOperation(value = "通过项目ID和月份来设置表二（月度计划明细）是否生效状态")
    @RequestMapping(value = "/mouthPlanEffective/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public Integer mouthPlanEffective(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                                          @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,
                                                          @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective) {

        /*
        * 表二提交已合并到表四提交里，该接口删不删待定
        * */
      //  Integer result=service.mouthPlanEffective(projectId, months, isEffective);
        return null;
    }


    @Log("通过项目ID和月份来设置表 四（月度计划明细）里面的风险")
    @CessBody
    @ApiOperation(value = "通过项目ID和月份来设置表 四（月度计划明细）里面的风险")
    @RequestMapping(value = "/mouthPlanUpdateRisk",method =RequestMethod.POST)
    public Integer mouthPlanUpdateRisk(@RequestBody Map<String, Object> map) {
          Integer result=service.mouthPlanUpdateRisk(map);
        return result;
    }


    @Log("将所有的招揽客人的渠道和渠道费用明细表示到前端去,若无数据则包括初始化")
    @CessBody
    @ApiOperation(value = "将所有的招揽客人的渠道和渠道费用明细表示到前端去,若无数据则包括初始化")
    @RequestMapping(value = "/allChannelDetail/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public List<Map> allChannelDetailSelect(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                                          @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,
                                                          @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective) {
        List<Map> result = service.allChannelDetailSelect(projectId, months, isEffective);

        return result;
    }




    @Log("通过项目ID和月份跟新渠道费用明细")
    @CessBody
    @ApiOperation(value = "通过项目ID和月份跟新渠道费用明细")
    @RequestMapping(value = "/updateChannelDetail",method =RequestMethod.POST)
    public Integer updateChannelDetail(@RequestBody List<Map> listmap) {
       Integer result= service.updateChannelDetail(listmap);
        return result;


    }


    @Log("通过项目ID和月份来更改渠道费用明细生效状态")
    //@CessBody
    @ApiOperation(value = "通过项目ID和月份来更改渠道费用明细生效状态")
    @RequestMapping(value = "/channelDetailEffective/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public VisolinkResultBody channelDetailEffective(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                                          @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,

                                                          @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective) {
    /*
    * 表三提交已合并到表四去，该接口删不删待定
    * */
     /*   String result = service.channelDetailEffective(projectId, months, isEffective);
        VisolinkResultBody resultBody=new VisolinkResultBody();
        if(result==null){
            resultBody.setMessages("上报成功");

        }else {
            resultBody.setMessages(result);
            resultBody.setCode(500);
        }*/

        return null;
    }



    @Log("查找表三下面柱状图的数据，每月matter金额和成交率")
    @CessBody
    @ApiOperation(value = "查找表三下面柱状图的数据，每月matter金额和成交率")
    @RequestMapping(value = "/columnSelect/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public List<Map> columnSelect(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                  @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,
                                  @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective){
        List<Map> result=service.columnSelect(projectId,months,isEffective);
                return result;
    }

    @Log("通过项目ID和月份来查找或初始化周计划")
    @CessBody
    @ApiOperation(value = "通过项目ID和月份来查找或初始化周计划")
    @RequestMapping(value = "/allWeeklyPlanSelect/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public List<Map> allWeeklyPlanSelect(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                                          @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,
                                                          @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective) {
          List<Map> result= service.allWeeklyPlanSelect(projectId, months, isEffective);
        return result;
    }


    @Log("通过项目ID和月份来决定表二表三表四生效状态")
    @CessBody
    @ApiOperation(value = "通过项目ID和月份来决定表二表三表四生效状态")
    @RequestMapping(value = "/weeklyPlanIsEffective/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public VisolinkResultBody weeklyPlanIsEffective(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                                          @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,
                                                          @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective) {
         String result= service.weeklyPlanIsEffective(projectId, months, isEffective);
        VisolinkResultBody resultBody=new VisolinkResultBody();

           Map map=new HashMap();
           map.put("business_id",projectId);
           map.put("months",months);
           if(isEffective==0){
               resultBody.setMessages("已驳回");
               /*
                * 驳回完成后需要修改表一状态
                * */
               map.put("plan_status",2);
               monthlyservice.updatePlanEffective(map);
           }
           if(isEffective==2){
               resultBody.setMessages("已审批完成");
               /*
               * 审批完成后需要修改表一状态
               * */
               map.put("plan_status",4);
               monthlyservice.updatePlanEffective(map);
           }
           if(isEffective==1){
               resultBody.setMessages("上报成功");
               /*
                * 上报完成后需要修改表一状态
                * */
               map.put("plan_status",3);
               monthlyservice.updatePlanEffective(map);
               resultBody.setResult(allWeeklyPlanSelect(projectId, months, isEffective));
           }
           /*修改区域合计*/


       String regionOrgId=BusinessserDao.selectOrgId(map);
        map.put("regionOrgId",regionOrgId);
        Businessservice.regionFundsUpdate(map);
        return resultBody;

    }
    @Log("暂存表四字段并校验")
    //@CessBody
    @ApiOperation(value = "暂存表四字段并校验")
    @RequestMapping(value = "/updateWeeklyPlan",method =RequestMethod.POST)
    public VisolinkResultBody  updateWeeklyPlan(@RequestBody  Map<String,Object> map){

        VisolinkResultBody resultBody=new VisolinkResultBody();

            Integer rep=0;

        List<Map> listmap=(ArrayList)map.get("data");

        Map<String,Object> riskMap=(Map)map.get("form");

        rep+=service.updateWeeklyPlan(listmap);
        /*
         * 表二的风险字段
         * */
        rep+=mouthPlanUpdateRisk(riskMap);

        if(map.get("upProject")!=null) {
            /*
             * 测验
             * */
            String result;
            Map<String, Object> stateMap = (Map) map.get("upProject");
            String projectId = stateMap.get("projectId").toString();
            String months = stateMap.get("months").toString();
            String userId = stateMap.get("userId").toString();
            Integer isEffective = Integer.parseInt(stateMap.get("is_effective").toString());

            result = service.weeklyPlanTestEffective(userId,projectId, months, isEffective);
            if (result != null) {
                resultBody.setMessages(result);
                resultBody.setCode(500);

            }
        }
        resultBody.setResult(rep);
       return resultBody;
    }



    @Log("求上一个月的周计划，在表四中和本月做对比")
    @CessBody
    @ApiOperation(value = "求上一个月的周计划，在表四中和本月做对比")
    @RequestMapping(value = "/frontselectWeeklyPlan/{projectId}/{months}/{isEffective}",method =RequestMethod.GET)
    public List<Map> frontselectWeeklyPlan(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId,
                                           @ApiParam(name = "months", value = "月份")@PathVariable("months") String months,
                                           @ApiParam(name = "isEffective", value = "启用状态，1为生效，0为未生效")@PathVariable("isEffective") Integer isEffective){
        List<Map> result= service.frontselectWeeklyPlan(projectId, months, isEffective);
        return result;
    }


    @Log("附件上传")
    @CessBody
    @ApiOperation(value = "附件上传")
    @RequestMapping(value = "/Upload",method =RequestMethod.POST)
    public  List<Map> UploadAttach(MultipartFile file,HttpServletRequest request) throws IOException {
        List<Map> result=  service.UploadAttach(file,request);
        return result;
    }

    @Log("附件传到前端")
    @CessBody
    @ApiOperation(value = "附件传到前端")
    @RequestMapping(value = "/selectAttach/{projectId}",method = RequestMethod.GET)
    public List<Map> selectAttach(@ApiParam(name = "projectId", value = "项目ID")@PathVariable("projectId")String projectId) {
        List<Map> result= service.selectAttach(projectId);
            return result;
    }

    @Log("附件的删除")
    @CessBody
    @ApiOperation(value = "附件的删除")
    @RequestMapping(value = "/deleteAttach/{fileID}/{IsDel}",method = RequestMethod.GET)
    public Integer deleteAttach(@ApiParam(name = "fileID",value = "附件ID") @PathVariable("fileID")String fileID,@ApiParam(name = "IsDel",value = "删除状态") @PathVariable("IsDel")Integer IsDel ){
        Integer result= service.deleteAttach(fileID,IsDel);
        return result;
    }



}
