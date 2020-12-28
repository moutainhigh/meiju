package cn.visolink.salesmanage.homenotice.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.homenotice.service.HomeNoticeService;
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
@RequestMapping("/homeNotice")
public class HomeNoticeController {

    private final HomeNoticeService homeNoticeService;

    public HomeNoticeController(HomeNoticeService homeNoticeService) {
        this.homeNoticeService = homeNoticeService;
    }


    @Log("提示首页公告")
    @PostMapping(value = {"getHomeNotice"})
    @ApiOperation(value = "提示首页公告")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody getHomeNotice(@RequestBody Map<String, Object> map){
        try {
            return ResultUtil.success(homeNoticeService.getHomeNotice(map));
        }catch (Exception e){
            return ResultUtil.error(500,"数据异常！");
        }
    }

    @Log("添加已阅")
    @PostMapping(value = {"intoHomeNoticeRead"})
    @ApiOperation(value = "添加已阅")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody intoHomeNoticeRead(@RequestBody Map<String, Object> map){
        try {
            return ResultUtil.success(homeNoticeService.intoHomeNoticeRead(map));
        }catch (Exception e){
            return ResultUtil.error(500,"数据异常！");
        }
    }


    @Log("获取单个公告数据")
    @PostMapping(value = {"getApplyInfo"})
    @ApiOperation(value = "获取单个公告数据")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody getHomeNoticeInfo(@RequestBody Map<String, Object> map){
        try {
            return ResultUtil.success(homeNoticeService.getHomeNoticeInfo(map));
        }catch (Exception e){
            return ResultUtil.error(500,"数据异常！");
        }
    }

    @Log("查询首页公告")
    @PostMapping(value = {"selectHomeNotice"})
    @ApiOperation(value = "查询首页公告")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectHomeNotice(@RequestBody Map<String, Object> map){
        return homeNoticeService.selectHomeNotice(map);
    }

    @Log("保存首页公告")
    @PostMapping(value = {"initHomeNotice"})
    @ApiOperation(value = "保存首页公告")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initHomeNotice(HttpServletRequest request,@RequestBody Map<String, Object> map){
        if (homeNoticeService.initHomeNotice(request,map)==0) {
            return ResultUtil.error(500, "新增失败！");
        }
        return ResultUtil.success("保存成功！");
    }

    @Log("失效公告")
    @PostMapping(value = {"deleteHomeNotice"})
    @ApiOperation(value = "失效公告")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody deleteHomeNotice(@RequestBody Map<String, Object> map){
        if (homeNoticeService.deleteHomeNotice(map)<0) {
            return ResultUtil.error(500, "删除失败！");
        }
        return ResultUtil.success("删除成功！");
    }

    @Log("失效文件")
    @PostMapping(value = {"isDelFile"})
    @ApiOperation(value = "失效文件")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody isDelFile(@RequestBody Map<String, Object> map){
        if (homeNoticeService.isDelFile(map)<0) {
            return ResultUtil.error(500, "删除失败！");
        }
        return ResultUtil.success("删除成功！");
    }

}
