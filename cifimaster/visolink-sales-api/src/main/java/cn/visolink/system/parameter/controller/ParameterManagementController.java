package cn.visolink.system.parameter.controller;
import cn.hutool.core.map.MapUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.parameter.service.ParameterManagementService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理参数管理
 *
 * @author ligengying
 * @date 20190906
 */

@RestController
@Api(tags = "系统管理-参数管理")
@RequestMapping("param")
public class ParameterManagementController {

    @Autowired
    private ParameterManagementService parameterService;

    /**
     * 查询系统所有的参数
     *
     * @param
     * @return
     */
    @Log("查询所有的参数")
    @CessBody
    @ApiOperation(value = "查询系统所有的参数")
    @PostMapping("getSystemAllParams.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authCompanyId", value = "认证公司ID"),
            @ApiImplicitParam(name = "productId", value = "产品ID"),
            @ApiImplicitParam(name = "projectId", value = "项目ID")
    })
    public List<Map> getSystemAllParams(@RequestBody HashMap<String,String> reqMap) {

        List<Map> dictionaryList = parameterService.getSystemAllparams(reqMap);
        return dictionaryList;
    }

    /**
     * 系统新增参数
     *
     * @param reqMap
     * @return
     */
    @Log("系统新增参数")
    @CessBody
    @ApiOperation(value = "系统新增参数")
    @PostMapping("saveSystemParam.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父ID"),
            @ApiImplicitParam(name = "name", value = "参数名称"),
            @ApiImplicitParam(name = "code", value = "参数Code")
    })
    public int saveSystemParam(@RequestBody Map reqMap) {

        int number = parameterService.saveSystemParam(reqMap);
        return number;
    }

    /**
     * 系统修改参数
     *
     * @param reqMap
     * @return
     */
    @Log("系统修改参数")
    @CessBody
    @ApiOperation(value = "系统修改参数")
    @PostMapping("modifySystemParam.action")
    @ApiModelProperty(name = "reqMap", value = "请求参数")
    public int modifySystemParam(@RequestBody Map reqMap) {
        System.out.println(reqMap.get("ID"));
        int number = parameterService.modifySystemParam(reqMap);
        return number;
    }

    /**
     * 删除系统参数
     *
     * @param reqMap
     * @return
     */
    @Log("删除系统参数")
    @CessBody
    @ApiOperation(value = "删除系统参数")
    @PostMapping("removeSystemParam.action")
    @ApiModelProperty(name = "id", value = "参数ID")
    public int removeSystemParam(@RequestBody Map reqMap) {
        int number = parameterService.removeSystemParam(reqMap);
        return number;
    }

    /**
     * 查询子集参数（树形）
     *
     * @param id
     * @return
     */
    @Log("查询子集参数（树形）")
    @CessBody
    @ApiOperation(value = "查询子集参数（树形）")
    @GetMapping("getSystemTreeChildParams.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参数ID"),
            @ApiImplicitParam(name = "projectId", value = "项目ID")
    })
    public List<Map> getSystemTreeChildParams(@RequestParam String id, @RequestParam String projectId) {

        Map reqMap = MapUtil.newHashMap();
        reqMap.put("id", id);
        reqMap.put("projectId", projectId);
        return parameterService.getSystemTreeChildParams(reqMap);
    }

    /**
     * 查询子集参数（非树形）
     *
     * @param pid
     * @param projectId
     * @return
     */
    @Log("查询子集参数（非树形）")
    @CessBody
    @ApiOperation(value = "查询子集参数（非树形）")
    @GetMapping("getSystemChildParams.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父ID"),
            @ApiImplicitParam(name = "projectId", value = "项目ID"),
            @ApiImplicitParam(name = "pageSize",value ="每页显示数量"),
            @ApiImplicitParam(name="pageIndex",value ="当前页")
    })
    public Map getSystemChildParams(String pid, String projectId, String pageSize, String pageIndex) {

        Map reqMap = MapUtil.newHashMap();
        reqMap.put("pid", pid);
        reqMap.put("projectId", projectId);
        reqMap.put("pageSize", pageSize);
        reqMap.put("pageIndex", pageIndex);
        return parameterService.getSystemChildParams(reqMap);
    }

    /**
     * 启用/禁用参数
     *
     * @param id
     * @param status
     * @return
     */
    @Log("启用/禁用参数")
    @CessBody
    @ApiOperation(value = "启用/禁用参数")
    @GetMapping("modifySystemParamStatus.action")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参数ID"),
            @ApiImplicitParam(name = "status", value = "参数状态")
    })
    public int modifySystemParamStatus(@RequestParam String id, @RequestParam String status) {

        Map reqMap = MapUtil.newHashMap();
        reqMap.put("id", id);
        reqMap.put("status", status);
        return parameterService.modifySystemParamStatus(reqMap);
    }

    /**
     * 获取字典数据（定调价编制页面）
     */
    @Log("获取字典数据")
    @CessBody
    @ApiOperation(value = "获取字典数据")
    @PostMapping(value = "/getDicByCodeList")
    public ResultBody getDicByCodeList(@RequestBody Map map){
        ResultBody rs=new ResultBody();
        rs.setData(parameterService.getDicByCodeList(map));
        return rs;
    }
    /**
     * 获取字典数据（定调价编制页面）
     */
    @Log("获取字典数据树形数据")
    @CessBody
    @ApiOperation(value = "获取字典数据树形数据")
    @PostMapping(value = "/getDicByCodeLevelList")
    public ResultBody getDicByCodeLevelList(@RequestBody Map map){
        ResultBody rs=new ResultBody();
        List<Map> dicList=parameterService.getDicByCodeLevelList(map);
        rs.setData(dicList);
        return rs;
    }

}
