package cn.visolink.firstplan.firstopen.controller;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.firstopen.service.FirstOpenBroadcastService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/7/8 11:11 上午
 */
@RestController
@Api(tags = "firstOpenBroadcast")
@RequestMapping("/firstOpenBroadcast")
public class FirstOpenBroadcastController {

    @Autowired
    private FirstOpenBroadcastService firstOpenBroadcastService;
    @ApiOperation(value = "渲染数据")
    @PostMapping(value = "/viewOpenBroadcastData")
    public ResultBody viewOpenBroadcastData(@RequestBody Map map, HttpServletRequest request) {
        String username = request.getHeader("username");
        map.put("username",username);
        return firstOpenBroadcastService.viewOpenBroadcastData(map);
    }
    @ApiOperation(value = " 查询版本")
    @PostMapping(value = "/getBroadcasVersionData")
    public ResultBody getBroadcasVersionData(@RequestBody Map map) {
        return firstOpenBroadcastService.getBroadcasVersionData(map);
    }
    @ApiOperation(value = " 保存数据")
    @PostMapping(value = "/saveOpenBroadCastData")
    public ResultBody saveOpenBroadCastData(@RequestBody Map map,HttpServletRequest request) {
        String username = request.getHeader("username");
        map.put("username",username);
        return firstOpenBroadcastService.saveOpenBroadCastData(map);
    }
    @ApiOperation(value = " 导出excel")
    @GetMapping(value = "/exportExcelOpenBroadCastData")
    public ResultBody exportExcelOpenBroadCastData(HttpServletRequest request, HttpServletResponse response) {
        String plan_node_id=request.getParameter("plan_node_id");
        String project_id=request.getParameter("project_id");
        String plan_id=request.getParameter("plan_id");
        String projectName = request.getParameter("projectName");
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("projectName",projectName);
        paramMap.put("plan_node_id",plan_node_id);
        paramMap.put("plan_id",plan_id);
        paramMap.put("project_id",project_id);
        return firstOpenBroadcastService.exportExcelOpenBroadCastData(paramMap,request,response);
    }



}
