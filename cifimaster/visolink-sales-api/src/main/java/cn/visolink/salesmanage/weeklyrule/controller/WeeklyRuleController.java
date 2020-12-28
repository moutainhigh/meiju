package cn.visolink.salesmanage.weeklyrule.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.weeklymarketingplan.service.WeeklyMarketingService;
import cn.visolink.salesmanage.weeklyrule.service.WeeklyRuleService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Api(tags = "营销周计划")
@RequestMapping("/weeklyRule")
public class WeeklyRuleController {

    @Autowired
    WeeklyRuleService service;
        /*SALEMAN-703,SALEMAN-685*/
    @Log("规则表的查找")
    @CessBody
    @ApiOperation(value = "规则表的查找")
    @RequestMapping(value = "/WeeklyRuleSelect",method = RequestMethod.POST)
    public  ResultBody WeeklyRuleSelect(@RequestBody Map map){

       return service.WeeklyRuleSelect(map);
    }

    @Log("规则表的更新")
   // @CessBody
    @ApiOperation(value = "规则表的更新")
    @RequestMapping(value = "/WeeklyRuleUpdate",method = RequestMethod.POST)
    public  ResultBody WeeklyRuleUpdate(@RequestBody Map map){
        ResultBody resultBody=new ResultBody();
      Integer result= service.WeeklyRuleUpdate(map);
      if(result==-1){
          resultBody.setMessages("窗口开始时间或窗口结束时间未填写！");
          resultBody.setCode(500);
      }else {
          resultBody.setData(result);
          resultBody.setCode(200);
      }

        return resultBody;
    }

    @Log("规则表的删除")
   @CessBody
    @ApiOperation(value = "规则表的删除")
    @RequestMapping(value = "/WeeklyRuleDelete",method = RequestMethod.POST)
    public  Integer WeeklyRuleDelete(@RequestBody Map map){
        ResultBody resultBody=new ResultBody();
        Integer result= service.WeeklyRuleDelete(map);
        resultBody.setCode(200);
        resultBody.setMessages("删除成功");
        resultBody.setData(result);

        return result;
    }

    @Log("规则表的插入")
    // @CessBody
    @ApiOperation(value = "规则表的插入")
    @RequestMapping(value = "/WeeklyRuleInsert",method = RequestMethod.POST)
    public  ResultBody WeeklyRuleInsert(@RequestBody Map map){
        Integer result= service.WeeklyRuleInsert(map);
        ResultBody resultBody=new ResultBody();
        if(result==-1){
            resultBody.setMessages("窗口开始时间或窗口结束时间未填写！");
            resultBody.setCode(500);
        }else {
            resultBody.setData(result);
            resultBody.setCode(200);
        }

        return resultBody;
    }
}
