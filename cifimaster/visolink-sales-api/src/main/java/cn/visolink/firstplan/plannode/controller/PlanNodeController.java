package cn.visolink.firstplan.plannode.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.plannode.service.PlanNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bao
 * @date 2020-04-14
 */

@RestController
@Api(tags = "计划节点控制")
@RequestMapping("/nodeContrl")
public class PlanNodeController {

    private final PlanNodeService planNodeService;

    public PlanNodeController(PlanNodeService planNodeService) {
        this.planNodeService = planNodeService;
    }

    @ApiOperation(value = "当前节点是否可操作")
    @PostMapping("/queryPlanNode")
    @ApiImplicitParam(name = "map", value="{\"projectId\":\"项目编码\",\"planNodeId\":\"项目编码\",\"node_level\":\"节点编码\"}")
    public ResultBody queryPlanNode(@RequestBody Map map){
        Map mapPro = new HashMap<>();
        try {
            mapPro = planNodeService.getPlanNodePower(map);
        }catch (Exception e){
            e.printStackTrace();
            mapPro.put("power",false);
            mapPro.put("read",false);
            mapPro.put("error","提示！数据异常！");
        }
        return ResultBody.success(mapPro);
    }

    @ApiOperation(value = "当前节点是否可操作")
    @PostMapping("/delPlanNode")
    @ApiImplicitParam(name = "map", value="{\"planNodeId\":\"项目编码\"}")
    public ResultBody delPlanNode(HttpServletRequest request, @RequestBody Map map){
        map.put("username",request.getHeader("userid"));
        return planNodeService.delPlanNodePower(map);
    }

}
