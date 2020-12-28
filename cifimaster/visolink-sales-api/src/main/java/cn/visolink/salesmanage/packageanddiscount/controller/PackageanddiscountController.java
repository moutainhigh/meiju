package cn.visolink.salesmanage.packageanddiscount.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.packageanddiscount.service.PackageanddiscountService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/25 11:21 上午
 */
@RestController
@RequestMapping("/packageStage")
@Api("一揽子分期折扣数据")
public class PackageanddiscountController {

    @Autowired
    private PackageanddiscountService packageanddiscountService;


    @Log("一揽子分期/折扣渲染数据")
    @ApiOperation(value = "一揽子分期/折扣渲染数据")
    @PostMapping("/viewPackageDiscount")
    public VisolinkResultBody viewPackageDiscount(@RequestBody Map map, HttpServletRequest request){
        /*货期请求头里的权限层级数据 bql 2020.07.20 */
        map.put("jobId",request.getHeader("jobid"));
        map.put("jobOrgId",request.getHeader("joborgid"));
        map.put("orgId",request.getHeader("orgid"));
        map.put("orgLevel",request.getHeader("orglevel"));
        VisolinkResultBody resultBody = packageanddiscountService.viewPackageDiscount(map, request);
        return resultBody;
    }

    @Log("根据项目获取相应楼栋")
    @ApiOperation(value = "根据项目获取相应楼栋")
    @PostMapping("/getBuildDataByProjectId")
    public VisolinkResultBody getBuildDataByProjectId(@RequestBody Map map){
        return packageanddiscountService.getBuildDataByProjectId(map);
    }
    @Log("一揽子分期/折扣保存/提交")
    @ApiOperation(value = "保存/提交")
    @PostMapping("/savePackageDiscount")
    public VisolinkResultBody savePackageDiscount(@RequestBody Map map,HttpServletRequest request){
        map.put("job_id",request.getHeader("jobid"));
        map.put("job_org_id",request.getHeader("joborgid"));
        map.put("org_id",request.getHeader("orgid"));
        map.put("org_level",request.getHeader("orglevel"));
        return packageanddiscountService.savePackageDiscount(map,request);
    }
    @Log("一揽子分期/折扣申请列表")
    @ApiOperation(value = "一揽子分期/折扣申请列表")
    @PostMapping("/getApplayList")
    public VisolinkResultBody getApplayList(@RequestBody Map map,HttpServletRequest request){
        /*货期请求头里的权限层级数据 bql 2020.07.20 */
        map.put("jobId",request.getHeader("jobid"));
        map.put("jobOrgId",request.getHeader("joborgid"));
        map.put("orgId",request.getHeader("orgid"));
        map.put("orgLevel",request.getHeader("orglevel"));
        return packageanddiscountService.getApplayList(map,request);
    }
   @Log("一揽子分期/折扣向明源推送数据")
    @ApiOperation(value = "一揽子分期/折扣向明源推送数据")
    @PostMapping("/approvalPushDataForMy")
    public VisolinkResultBody approvalPushDataForMy(@RequestBody Map map){
        return packageanddiscountService.approvalPushDataForMy(map);
    }


}
