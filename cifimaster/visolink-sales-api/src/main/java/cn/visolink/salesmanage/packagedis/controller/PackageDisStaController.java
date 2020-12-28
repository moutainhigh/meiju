package cn.visolink.salesmanage.packagedis.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.packagedis.service.PackageDiscontService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: sjl
 * @Date: 2019/11/11 0011 16:26
 * @Description:一揽子折扣（分期）
 */
@RestController
@RequestMapping("/package")
@Api("添加一揽子数据")
public class PackageDisStaController {
    @Autowired
    private PackageDiscontService packageDiscontService;

    @Log("查询登录人的项目")

    @ApiOperation(value = "查询登录人有权限的项目")
    @PostMapping("/getProjects")
    public List<Map> getPorjects(@RequestBody Map map) {
        try {
            return packageDiscontService.getProjects(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Log("查询项目的楼栋和液态")
    @ApiOperation(value = "查询项目的楼栋和液态")
    @PostMapping("/getBuildingAndFormatsData")
    public ResultBody getBuildingAndFormatsData(@RequestBody Map map) {
        return  packageDiscontService.getBuildingAndFormatsData(map);
    }



    @Log("添加一揽子折扣（折扣详情）数据")
    @CessBody
    @ApiOperation(value = "添加一揽子折扣（折扣详情）数据")
    @PostMapping("/insertPackageDis")
    public ResultBody insertPackageDis(@RequestBody Map map) {
        return packageDiscontService.insertPackageDis(map);
    }

    @Log("添加一揽子分期（分期详情）数据")
    @ApiOperation(value = "添加一揽子分期（分期详情）数据")
    @PostMapping("/insertPackageStages")
    public ResultBody insertPackageStages(@RequestBody Map map) {
        return packageDiscontService.insertPackageStages(map);

    }


    /**
     * 一揽子分期渲染页面
     */
    @Log("一揽子渲染页面")
    @ApiOperation(value = "一揽子渲染页面")
    @PostMapping("/packageApply")
    public ResultBody packageStagesDisApply(@RequestBody Map map){
        String flowCode = packageDiscontService.getFlowCode(map.get("baseId") + "");
        ResultBody<Object> resultBody = new ResultBody<>();
        //一揽子分期
        if("My_Package_Stage".equalsIgnoreCase(flowCode)){
            return packageDiscontService.packageStagesApply(map);
        }else if("My_Package_Dis".equalsIgnoreCase(flowCode)){
            return packageDiscontService.packageStagesDisApply(map);
        }else{
            Map<Object, Object> resultMap = new HashMap<>();
            resultMap.put("message","审批类型不存在!");
            resultBody.setData(resultMap);
            return resultBody;
        }
    }


    /**
     * 一揽子分期渲染页面
     */
    @Log("一揽子分期折扣列表")
    @CessBody
    @ApiOperation(value = "一揽子分期折扣列表")
    @PostMapping("/stagesSelect")
    public ResultBody stagesSelect(@RequestBody Map map){
        return packageDiscontService.stagesSelect(map);
    }

    /**
     * 获取流程详情数据
     */
    @Log("获取流程详情数据")
    @CessBody
    @ApiOperation(value="获取流程详情数据")
    @GetMapping("/getFlowDataInfo")
    public ResultBody getFlowDataInfo(@Param("jsonId") String jsonId){
        return  packageDiscontService.getFlowDataInfo(jsonId);
    }
    /**
     * 设置窗口期
     */
    @Log("获取流程详情数据")
    @CessBody
    @ApiOperation(value="获取流程详情数据")
    @PostMapping("/windowPhase")
    public ResultBody windowPhase(){
        return packageDiscontService.windowPhase();
    }


}

