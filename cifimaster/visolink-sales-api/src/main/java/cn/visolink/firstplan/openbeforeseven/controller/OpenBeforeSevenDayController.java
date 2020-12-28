package cn.visolink.firstplan.openbeforeseven.controller;
import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.openbeforeseven.service.OpenBeforeSevenDayService;
import cn.visolink.firstplan.openbeforetwentyone.dao.OpenbeforetwentyoneDao;
import cn.visolink.logs.aop.log.Log;
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
 * @Created date 2020/3/9 12:26 下午
 */
@RestController
@Api(tags = "openBeforeSeven")
@RequestMapping("/openBeforeSeven")
public class OpenBeforeSevenDayController {
    @Autowired
    private OpenbeforetwentyoneDao openbeforetwentyoneDao;
    @Autowired
    private OpenBeforeSevenDayService openBeforeSevenDayService;

    @Log("")
    @ApiOperation(value = "")
    @PostMapping(value = "/updatePlanTime")
    public VisolinkResultBody updatePlanTime(@RequestBody Map map) {
        VisolinkResultBody<Object> visolinkResultBody = new VisolinkResultBody<>();
        try {
            openbeforetwentyoneDao.updatePlanTime(map);
            visolinkResultBody.setCode(200);
            return visolinkResultBody;
        } catch (Exception e) {
            visolinkResultBody.setCode(400);
            visolinkResultBody.setMessages("数据渲染失败");
            e.printStackTrace();
            visolinkResultBody.setResult(e);
            return visolinkResultBody;
        }
    }

    @Log("首开审批数据渲染")
    @ApiOperation(value = "首开审批数据渲染")
    @PostMapping(value = "viewOpenBeforeSevenDayOpenApplay")
    public VisolinkResultBody viewOpenBeforeSevenDayOpenApplay(@RequestBody Map map) {
        try {

            return openBeforeSevenDayService.viewOpenBeforeSevenDayOpenApplay(map);
        } catch (Exception e) {
            VisolinkResultBody resultBody = new VisolinkResultBody();
            e.printStackTrace();
            resultBody.setCode(500);
            resultBody.setMessages("数据渲染失败!");
            return resultBody;
        }
    }

    @Log("首开审批数据保存/提交审批")
    @ApiOperation(value = "首开审批数据保存/提交审批")
    @PostMapping(value = "/saveOpenBeforeSevenDayOpenApplay")
    public VisolinkResultBody saveOpenBeforeSevenDayOpenApplay(@RequestBody Map map, HttpServletRequest request) {
        try {
            String username=request.getHeader("username");
            map.put("username",username);
            return openBeforeSevenDayService.saveOpenBeforeSevenDayOpenApplay(map);
        } catch (Exception e) {
            VisolinkResultBody resultBody = new VisolinkResultBody();
            e.printStackTrace();
            resultBody.setCode(500);
            resultBody.setMessages("数据保存失败!");
            return resultBody;
        }
    }

    @Log("首开审批数据切换版本")
    @ApiOperation(value = "首开审批数据保存/提交审批")
    @PostMapping(value = "/switchVersionSevenDayOpenApplay")
    public VisolinkResultBody switchVersionSevenDayOpenApplay(@RequestBody Map map) {
        try {
            return openBeforeSevenDayService.switchVersion(map);
        } catch (Exception e) {
            VisolinkResultBody resultBody = new VisolinkResultBody();
            e.printStackTrace();
            resultBody.setCode(500);
            resultBody.setMessages("版本切换失败!");
            return resultBody;
        }
    }


    @Log("首开审批数据切换版本")
    @ApiOperation(value = "首开审批数据保存/提交审批")
    @PostMapping(value = "/applyAdoptTell")
    public VisolinkResultBody applyAdoptTellInterface(@RequestBody Map map){
        VisolinkResultBody resultBody = openBeforeSevenDayService.applyAdoptTellInterface(map);
        return  resultBody;
    }

    @Log("开盘审批数据导出")
    @ApiOperation(value = "开盘审批数据导出")
    @GetMapping(value = "/exportOpenApplayData")
    public ResultBody exportOpenApplayData(HttpServletRequest request, HttpServletResponse response){
        String id=request.getParameter("id");
        String node_level = request.getParameter("node_level");
        String projectName = request.getParameter("projectName");
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("node_level",node_level);
        paramMap.put("projectName",projectName);
        paramMap.put("id",id);
        ResultBody resultBody = openBeforeSevenDayService.exportOpenApplayData(paramMap,request,response);
        return  resultBody;
    }
}
