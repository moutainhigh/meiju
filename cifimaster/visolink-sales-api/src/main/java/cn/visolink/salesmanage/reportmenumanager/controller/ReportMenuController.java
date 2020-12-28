package cn.visolink.salesmanage.reportmenumanager.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.reportmenumanager.service.ReportMenuService;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api("报表类")
@RequestMapping("/Report")
public class ReportMenuController {

    @Autowired
    ReportMenuService reportService;

    @Log("插入一张报表")
    @CessBody
    @ApiOperation(value = "插入一张报表")
    @PostMapping("/insertReportMenu")
    public Integer insertReportMenu(@RequestBody Map<String, Object> map) {
       Integer result=   reportService.insertReportMenu(map);
        return  result;
    }

    @Log("更新一张报表")
    @CessBody
    @ApiOperation(value = "更新一张报表")
    @PostMapping("/updateReportMenu")
    public Integer updateReportMenu(@RequestBody Map<String, Object> map) {
        Integer result=  reportService.updateReportMenu(map);
        return result;
    }

    @Log("删除一张报表")
    //@CessBody
    @ApiOperation(value = "删除一张报表")
    @PostMapping("/deleteReportMenu")
    public VisolinkResultBody deleteReportMenu(@RequestBody Map<String, Object> map) {

        VisolinkResultBody resultBody=new VisolinkResultBody();

        Integer result=  reportService.deleteReportMenu(map);
        if(result==null){
            resultBody.setResult(result);
            resultBody.setCode(500);
            resultBody.setMessages("该菜单下尚有子菜单未删除！");
        }else {
            resultBody.setResult(result);

            resultBody.setMessages("删除成功！");
        }
        return  resultBody;

    }

    @Log("查找报表")
    @CessBody
    @ApiOperation(value = "查找报表")
    @PostMapping("/selectReportMenu")
    public PageInfo selectReportMenu(@RequestBody Map<String, Object> map) {
        PageInfo result= reportService.selectReportMenu(map);
        return result;
    }

}
