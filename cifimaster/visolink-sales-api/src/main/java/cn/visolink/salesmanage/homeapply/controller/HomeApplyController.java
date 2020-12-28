package cn.visolink.salesmanage.homeapply.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.homeapply.service.HomeApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 夏威审批流Mapper
 * </p>
 *
 * @author bql
 * @since 2020-09-16
 */
@RestController
@Api(tags = "数据调整审批")
@Slf4j
@RequestMapping("/homeApply")
public class HomeApplyController {

    private final HomeApplyService homeApplyService;

    public HomeApplyController(HomeApplyService homeApplyService) {
        this.homeApplyService = homeApplyService;
    }

    @Log("获取申请人区域、部门、申请时间")
    @PostMapping(value = {"getApplyInfo"})
    @ApiOperation(value = "获取申请人区域、部门、申请时间")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody getApplyInfo(@RequestBody Map<String, Object> map){
        try {
            Map mm = homeApplyService.getApplyInfo(map);
            if(mm == null){
                return ResultUtil.error(500,"当前登陆人数据异常！");
            }else{
                return ResultUtil.success(mm);
            }
        }catch (Exception e){
            return ResultUtil.error(500,"数据异常！");
        }
    }

    @Log("查询首页申请")
    @PostMapping(value = {"selectHomeApply"})
    @ApiOperation(value = "查询首页申请")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectHomeApply(@RequestBody Map<String, Object> map){
        return homeApplyService.selectHomeApply(map);
    }

    @Log("保存首页申请")
    @PostMapping(value = {"initHomeApply"})
    @ApiOperation(value = "保存首页申请")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initHomeApply(HttpServletRequest request,@RequestBody Map<String, Object> map){
        if (homeApplyService.initHomeApply(request,map)==0) {
            return ResultUtil.error(500, "新增失败！");
        }
        return ResultUtil.success("保存成功！");
    }


    @Log("失效申请")
    @PostMapping(value = {"deleteHomeApply"})
    @ApiOperation(value = "失效申请")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody deleteHomeApply(@RequestBody Map<String, Object> map){
        if (homeApplyService.deleteHomeApply(map)<0) {
            return ResultUtil.error(500, "删除失败！");
        }
        return ResultUtil.success("删除成功！");
    }



    @Log("提交首页申请")
    @PostMapping(value = {"initHomeApplyFlow"})
    @ApiOperation(value = "保存首页申请")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initHomeApplyFlow(HttpServletRequest request,@RequestBody Map<String, Object> map){
        return homeApplyService.initHomeApplyFlow(request,map);
    }

}
