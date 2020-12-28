package cn.visolink.firstplan.openbeforetwentyone.controller;
import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.openbeforetwentyone.service.OpenBeforeTwentyoneService;
import cn.visolink.logs.aop.log.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.javassist.bytecode.stackmap.BasicBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/6 8:37 下午
 * 开盘前21天 延期申请
 *
 */

@RestController
@Api(tags = "openBeforeTwentyone")
@RequestMapping("/openBeforeTwentyone")
public class OpenBeforeTwentyoneController {
    @Autowired
    private OpenBeforeTwentyoneService openBeforeTwentyoneService;
    @Log("开盘前21天 延期申请渲染")
    @ApiOperation(value = "开盘前21天 延期申请渲染")
    @PostMapping(value = "/viewdelayOpenApplay")
    public VisolinkResultBody viewdelayOpenApplay(@RequestBody Map map){
        VisolinkResultBody<Object> visolinkResultBody = new VisolinkResultBody<>();
        try {
           return openBeforeTwentyoneService.viewdelayOpenApplay(map);
        }catch (Exception e){
            visolinkResultBody.setCode(400);
            visolinkResultBody.setMessages("数据渲染失败");
            e.printStackTrace();
            visolinkResultBody.setResult(e);
            return visolinkResultBody ;
        }
    }

    @Log("开盘前21天 延期开盘申请切换版本")
    @ApiOperation(value = "开盘前21天 延期开盘申请切换版本")
    @PostMapping(value = "/switchVersion")
    public  VisolinkResultBody switchVersion(@RequestBody Map map){
        VisolinkResultBody<Object> visolinkResultBody = new VisolinkResultBody<>();
        try {
             return  openBeforeTwentyoneService.switchVersion(map);
        } catch (Exception e){
            e.printStackTrace();
            visolinkResultBody.setMessages("版本切换失败!");
            return  visolinkResultBody;
        }
    }

    @Log("开盘前21天 保存/提交申请")
    @ApiOperation(value = "开盘前21天 保存/提交申请")
    @PostMapping(value = "/savelayOpenApplay")
    public VisolinkResultBody saveelayOpenApplay(@RequestBody Map map, HttpServletRequest request){
        String username=request.getHeader("username");
        map.put("username",username);
        VisolinkResultBody<Object> visolinkResultBody = new VisolinkResultBody<>();
        try {
            return  openBeforeTwentyoneService.saveelayOpenApplay(map);
        }catch (Exception e){
            e.printStackTrace();
            visolinkResultBody.setCode(500);
            visolinkResultBody.setMessages("数据保存失败!");
            return  visolinkResultBody;
        }
    }
    @Log("开盘前21天 获取周拆分数据")
    @ApiOperation(value = "获取周拆分数据")
    @PostMapping(value = "/getWeeklyResolution")
    public VisolinkResultBody getWeeklyResolution(@RequestBody Map map){
        try {
            return  openBeforeTwentyoneService.getWeeklyResolution(map);
        } catch (ParseException e) {
            e.printStackTrace();
            VisolinkResultBody<Object> response = new VisolinkResultBody<>();
            response.setMessages("周拆分获取失败");
            response.setCode(400);
            return  response;
        }
    }
    @Log("开盘前21天/7天，延期开盘数据导出")
    @ApiOperation(value = "开盘前21天/7天，延期开盘数据导出")
    @GetMapping(value = "/exportDelayOpenData")
    public ResultBody exportDelayOpenData(HttpServletRequest request, HttpServletResponse response
    ){

        String id=request.getParameter("id");
        String node_level = request.getParameter("node_level");
        String projectName = request.getParameter("projectName");
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("node_level",node_level);
        paramMap.put("projectName",projectName);
        paramMap.put("id",id);
        return  openBeforeTwentyoneService.exportDelayOpenData(paramMap,request,response);
    }
}
