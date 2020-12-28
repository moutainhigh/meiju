package cn.visolink.salesmanage.businessmanager.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;

import cn.visolink.salesmanage.businessmanager.service.BusinessManagerService;
import cn.visolink.salesmanage.datainterface.controller.DatainterfaceController;
import cn.visolink.salesmanage.datainterface.service.impl.DatainterfaceserviceImpl;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 孙林
 * @since 2019-9-17
 */
@RestController
@Api(tags = "区域指标详情")
@RequestMapping("/areadetail")
public class BusinessManagerController  {

    @Autowired
    private BusinessManagerService service;

    @Autowired
    DatainterfaceserviceImpl datainterfaceservice;

    @Autowired
    DatainterfaceController datainterfaceController;


    @Log("区域指标详情事业部信息")
    @CessBody
    @ApiOperation(value = "区域指标详情事业部信息")
     @RequestMapping(value = "/regionReportSelect/{regionOrgId}/{months}",method = RequestMethod.GET)
    public List<Map> regionReportSelect(@ApiParam(name = "regionOrgId", value = "事业部/区域ID")@PathVariable("regionOrgId")String regionOrgId,
                                      @ApiParam(name = "months", value = "月份")@PathVariable("months") String months
                                      ) {


       List<Map> result=service.regionReportSelect(regionOrgId,months);
         return result;
    }

     @Log("区域指标详情事业部信息合计")
    @CessBody
    @ApiOperation(value = "区域指标详情事业部信息合计")
     @RequestMapping(value = "/regionFundsSelect/{regionOrgId}/{months}",method = RequestMethod.GET)
    public List<Map> regionFundsSelect(@ApiParam(name = "regionOrgId", value = "事业部/区域ID")@PathVariable("regionOrgId")String regionOrgId,
                                      @ApiParam(name = "months", value = "月份")@PathVariable("months") String months) {
         List<Map> result=service.regionFundsSelect(regionOrgId,months);
         return result;
    }

     @Log("区域指标详情事业部信息合计修改")
    @CessBody
    @ApiOperation(value = "区域指标详情事业部信息合计修改")
     @RequestMapping(value = "/regionFundsUpdate",method = RequestMethod.POST)
    public Integer regionFundsUpdate(@RequestBody Map<String,Object>  map) {
         Integer result=service.regionFundsUpdate(map);
         return result;
    }

    @Log("区域指标详情事业部信息合计上报")
    //@CessBody
    @ApiOperation(value = "区域指标详情事业部信息合计上报")
    @RequestMapping(value = "/regionFundsEffective",method = RequestMethod.POST)
    public VisolinkResultBody regionFundsEffective(@RequestBody Map<String,Object>  map) {
        Integer result=service.regionFundsEffective(map);
        VisolinkResultBody resultBody=new VisolinkResultBody();
        if(result==null) {
            resultBody.setMessages("上报指标必须大于或等于下达指标！");
            resultBody.setCode(500);
        }else {
           if(map.get("planStatus").toString().equals("0")) {

               resultBody.setMessages("驳回成功！");
           }
            if(map.get("planStatus").toString().equals("2")) {
                resultBody.setMessages("上报成功！");
            }
        }
        /*
         * 区域上报后做一次集团汇总
         * */
        businessFundsUpdate(map);

        return resultBody;
    }


     @Log("事业部列表")
    @CessBody
    @ApiOperation(value = "事业部列表")
    @RequestMapping(value = "/businessDepartSelect/{months}",method = RequestMethod.GET)
    public List<Map> businessDepartSelect( @ApiParam(name = "months", value = "月份")@PathVariable("months") String months) {
         /*
          * 先算合计，再查找数据
          * */
         Map<String,Object> map=new HashMap<>();
         map.put("months",months);
         businessFundsUpdate(map);

         List<Map> result=service.businessDepartSelect(months);
         return result;
    }

     @Log("事业部 项目列表")
    @CessBody
    @ApiOperation(value = "事业部 项目列表")
     @RequestMapping(value = "/businessprojectSelect/{regionOrgId}/{months}",method = RequestMethod.GET)
    public List<Map> businessprojectSelect(@ApiParam(name = "regionOrgId", value = "事业部/区域ID")@PathVariable("regionOrgId")String regionOrgId,
                                      @ApiParam(name = "months", value = "月份")@PathVariable("months") String months) {

         List<Map> result=service.businessprojectSelect(regionOrgId,months);
         return result;

    }

     @Log("事业部列表合计")
    @CessBody
    @ApiOperation(value = "事业部列表合计")
     @RequestMapping(value = "/businessTotalSelect/{regionOrgId}/{months}",method = RequestMethod.GET)
    public List<Map> businessTotalSelect(@ApiParam(name = "regionOrgId", value = "事业部/区域ID")@PathVariable("regionOrgId")String regionOrgId,
                                      @ApiParam(name = "months", value = "月份")@PathVariable("months") String months) {
         List<Map> result=service.businessTotalSelect(regionOrgId,months);
         return result;
    }

    @Log("事业部列表合计修改提交")
    @CessBody
    @ApiOperation(value = "事业部列表合计修改提交")
    @RequestMapping(value = "/businessFundsUpdate",method = RequestMethod.POST)
    public Integer businessFundsUpdate(@RequestBody  Map<String,Object> map) {

        Integer result=service.businessFundsUpdate(map);
        return result;
    }

    @Log("集团确认")
    @ApiOperation(value = "集团确认")
    @CessBody
    @RequestMapping(value = "/businessFundsEffective",method = RequestMethod.POST)
    public Integer businessFundsEffective(@RequestBody  Map<String,Object> params){
        String months=params.get("months")+"";
        //修改集团状态
        Integer result=service.businessFundsEffective(months);
        //推送数据到供销存
        datainterfaceController.sendesb(params);

        //明源推送暂时不需要
        // datainterfaceservice.selectsignset(months);

        return  result;
    }

    @Log("区域指标详情集团批量驳回")
    //@CessBody
    @ApiOperation(value = "区域指标详情集团批量驳回")
    @RequestMapping(value = "/regionFundsAllUpdate",method = RequestMethod.POST)
    public VisolinkResultBody regionFundsEffective(@RequestBody List<Map> list  ) {
       Integer result=0;
            for(Map map1:  list){
                result+=  service.regionFundsEffective(map1);
                /*
                 * 区域上报后做一次集团汇总
                 * */
                businessFundsUpdate(map1);
            }

        VisolinkResultBody resultBody=new VisolinkResultBody();

        resultBody.setResult(result);

        return resultBody;
    }

}
