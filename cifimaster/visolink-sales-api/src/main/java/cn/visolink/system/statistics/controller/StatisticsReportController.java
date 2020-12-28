package cn.visolink.system.statistics.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.org.model.form.OrganizationForm;
import cn.visolink.system.statistics.service.StatisticsReportService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/8 11:02 上午
 */
@RestController
@Api(tags = "报表数据")
@RequestMapping("/report")
public class StatisticsReportController {

    @Autowired
    private StatisticsReportService statisticsReportService;

    /**
     * 获取授权报表数据
     *
     * @return 是否添加成功
     */
    @Log("获取授权报表数据")
    @CessBody
    @ApiOperation(value = "获取数据", notes = "获取授权报表数据")
    @PostMapping(value = "/getAuthorizationData")
    public ResultBody getAuthorizationData(@RequestBody Map map) {
        ResultBody statisticsReportMenus = statisticsReportService.getStatisticsReportMenus(map);
        return statisticsReportMenus;
    }

    /**
     * 获取授权报表数据
     *
     * @return 是否添加成功
     */
    @Log("添加常用报表")
    @CessBody
    @ApiOperation(value = "添加常用报表", notes = "添加常用报表")
    @PostMapping(value = "/addCommomUserReportMenus")
    public ResultBody addCommomUserReportMenus(@RequestBody Map map) {


        try {
            return statisticsReportService.addCommomUserReportMenus(map);
        }catch (Exception e){
            ResultBody<Object> resultBody = new ResultBody<>();
            resultBody.setCode(-1087);
            resultBody.setMessages("添加常用报表失败");
            return  resultBody;
        }

    }

}
