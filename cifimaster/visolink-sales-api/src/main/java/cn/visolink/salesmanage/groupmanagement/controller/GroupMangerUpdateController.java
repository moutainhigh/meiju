package cn.visolink.salesmanage.groupmanagement.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.businessmanager.service.BusinessManagerService;
import cn.visolink.salesmanage.datainterface.service.impl.DatainterfaceserviceImpl;
import cn.visolink.salesmanage.groupmanagement.service.UpdateMonthlyService;
import cn.visolink.salesmanage.monthdetail.service.MonthManagerService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
@Api("更新项目月度签约计划表")
public class GroupMangerUpdateController {

    @Autowired
    private UpdateMonthlyService service;

    @Autowired
    private BusinessManagerService Businessservice;

    @Autowired
    private MonthManagerService monthManagerService;

    /*
     * 修改表一暂存后的数据
     * */
    @Log("修改表一暂存后的数据")
    @CessBody
    @ApiOperation(value = "修改表一暂存后的数据")
    @PostMapping("/updateMonthlyPlan")

    public Integer updateMonthlyPlan(@RequestBody List<Map> listmap){
        //判断状态是否上报
        if(listmap.size()>0&&listmap.get(0).get("business_id")!=null&&listmap.get(0).get("business_id")!=""){
           String status = service.getProjectTableOneStatus(listmap.get(0).get("business_id")+"",listmap.get(0).get("months")+"");
           if("0".equals(status)){
            Integer result= service.updateMonthlyPlan(listmap);
            return result;
           }
            return 1;
        }
      return 1;
    }


    @Log("暂存，下达，上报 （修改状态）")
    @CessBody
    @ApiOperation(value = "暂存，下达，上报 （修改状态）")
    @PostMapping("/updatePlanEffective")
    public int updatePlanEffective(@RequestBody List<Map> listmap) {

                Map<String,Object> map= listmap.get(0);

        int result = service.updatePlanEffective(map);
        /*
        * 表一要是驳回，表二三四跟着驳回
        * */
        if(Integer.parseInt(map.get("plan_status").toString())==0){
            String projectId= map.get("business_id").toString();
            String months=map.get("months").toString();
             monthManagerService.weeklyPlanIsEffective(projectId,months,0);

        }
            /*
            * 上报后进行区域合计
            * */
                map.put("regionOrgId",map.get("region_org_id"));
                Businessservice.regionFundsUpdate(map);

        return result;
    }
}
