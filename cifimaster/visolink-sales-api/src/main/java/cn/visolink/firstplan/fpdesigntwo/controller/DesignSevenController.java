package cn.visolink.firstplan.fpdesigntwo.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.fpdesigntwo.service.DesignSevenSevice;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.utils.Constant;
import cn.visolink.utils.UUID;
import com.alibaba.fastjson.JSON;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 * @author 孙林
 * @since 2020-2-17
 */
@RestController
@Api(tags = "首开前7天等模块")
@RequestMapping("/designmonths")
public class DesignSevenController {

    @Autowired
    private DesignSevenSevice designSevenSevice;

    @Autowired
    private DesignTwoIndexDao designTwoIndexDao;

    /*首开前7天数据，首开审批表数据*/
    @Log("首开前7天数据，首开审批表数据")
    @CessBody
    @ApiOperation(value = "首开前7天数据，首开审批表数据")
    @PostMapping(value = "/selectSevenIndex")
   public Map selectSevenDayIndex(@RequestBody Map map)
    {
        Map result=designSevenSevice.selectSevenDayIndex(map);
        return result;

    }

    /*跟新首开前7天数据，首开审批表数据*/
    @Log("跟新首开前7天数据，首开审批表数据")
    @CessBody
    @ApiOperation(value = "跟新首开前7天数据，首开审批表数据")
    @PostMapping(value = "/updateSevenIndex")
    public Integer  updateSevenDayIndex(@RequestBody Map map){
        /*若前端不传plan_node_id来说明要初始化，那么初始化的时候要传node_level  等级来*/
        Integer node_level=Integer.parseInt(map.get("node_level")+"");
        String node_name="";
                Integer days=0;
            switch (node_level){
                /*首开前三个月*/
                case 4:
                    days=90;
                    node_name="首开前3个月";
                    break;
                    /*首开前二个月*/
                case 5:
                    days=60;
                    node_name="首开前2个月";
                    break;
                    /*首开前21天*/
                case 6:
                    days=21;
                    node_name="首开前21天";
                    break;
                    /*首开前7天*/
                case 7:
                    days=7;
                    node_name="首开前7天";
                    break;
            }

        map.put("days",days);
        map.put("node_name",node_name);
        String planNodeId=  designSevenSevice.forPlanNode(map);
        map.put("plan_node_id",planNodeId);
        Integer result=designSevenSevice.updateSevenDayIndex(map);
        designTwoIndexDao.updateNodeName(map);
        return result;
    }

    /*查找客储计划周拆分和节点储客计划*/
    @Log("查找客储计划周拆分和节点储客计划")
   // @CessBody
    @ApiOperation(value = "查找客储计划周拆分和节点储客计划")
    @PostMapping(value = "/selectNodePlan")
    public VisolinkResultBody selectStorageNodePlan(@RequestBody Map map){
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            if (map.get("plan_id") == null || map.get("plan_id") == "") {
                Map map1 = designTwoIndexDao.selectPlanNodeId(map);
                map.put("plan_node_id", map1.get("plan_node_id"));
                map.put("plan_id", map1.get("plan_id"));
                map.put("node_level", map1.get("node_level"));
                map.put("operation","view");
            }
            Map result = designSevenSevice.selectStorageNodePlan(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        }catch(Exception e){
                resBoby.setCode(1);
                resBoby.setMessages("请求失败，请稍候再试");
            }
            return resBoby ;
    }


    /*更新客储计划周拆分和节点储客计划*/
    @Log("更新客储计划周拆分和节点储客计划")
    //@CessBody
    @ApiOperation(value = "更新客储计划周拆分和节点储客计划")
    @PostMapping(value = "/updateNodePlan")
   public VisolinkResultBody updateStorageNodePlan(@RequestBody Map map, HttpServletRequest request){
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
        /*若前端不传plan_node_id来说明要初始化，那么初始化的时候要传node_level  等级来*/
        List<Map> NodePlan= (ArrayList)map.get("NodePlan");
        List<Map> Week=(ArrayList)map.get("Week");
        NodePlan=iterator(NodePlan);
        Week=iterator(Week);
        map.put("NodePlan",NodePlan);
        map.put("Week",Week);
            System.out.println(JSON.toJSONString(Week));

        Integer node_level=Integer.parseInt(map.get("node_level")+"");
        String node_name="";
        Integer days=0;
        switch (node_level){
            /*首开前三个月*/
            case 4:
                days=90;
                node_name="首开前3个月";
                break;
            /*首开前二个月*/
            case 5:
                days=60;
                node_name="首开前2个月";
                break;
            /*首开前21天*/
            case 6:
                days=21;
                node_name="首开前21天";
                break;
            /*首开前7天*/
            case 7:
                days=7;
                node_name="首开前7天";
                break;
        }
        map.put("days",days);
        map.put("node_name",node_name);
        String planNodeId=  designSevenSevice.forPlanNode(map);
        map.put("plan_node_id",planNodeId);
         designSevenSevice.updateStorageNodePlan(map,request);
        designTwoIndexDao.updateNodeName(map);

        List<Map> result= designTwoIndexDao.selectStorageFlow(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        }catch(Exception e){
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("系统错误");
        }
        return resBoby ;
    }

    @Log("查找左上角的版本选择")
    //@CessBody
    @ApiOperation(value = "查找左上角的版本选择")
    @PostMapping(value = "/selectPlanNode")
    public VisolinkResultBody selectPlanNode(@RequestBody Map map) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
        List<Map> result=designTwoIndexDao.selectStorageFlow(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        }catch(Exception e){
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby ;
    }

    @Log("提交审批后调用的方法")
    @CessBody
    @ApiOperation(value = "提交审批后调用的方法")
    @PostMapping(value = "/forMtstart")
    public VisolinkResultBody update(@RequestBody Map map, HttpServletRequest request) {
        VisolinkResultBody<Object> visolinkResultBody = new VisolinkResultBody<>();
        String username = request.getHeader("username");
        String FlowCode= designTwoIndexDao.selectFlowCode(map.get("flow_id")+"");
        Map result=new HashMap();
        result.put("BSID","FP");
        result.put("BTID",FlowCode);
        result.put("codeBOID",map.get("flow_id"));
        result.put("bkUserID",username);
        result.put("loginKey", "");
        visolinkResultBody.setResult(result);
        return visolinkResultBody;
    }

    /*遍历数据使得里面的数据没有“”*/
    public List<Map> iterator(List<Map> listmap){
        /*
         * 遍历Map所有值，若传进来的数有空，则将它默认为0
         * */
        for (Map<String,Object> map : listmap) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String value = entry.getValue() + "";
                if ("".equals(value) ||"null".equalsIgnoreCase(value)) {
                    map.put(entry.getKey(),0);
                }
            }
        }
        return listmap;
    }


    /*现在集团编制有改动，要求所有单元格放开编制，所以在这里单独做一个*/
    @Log("开盘前3个月导出")
    @CessBody
    @ApiOperation(value = "开盘前3个月导出")
    @GetMapping(value = "/getMonthsExcel.action")
    public void groupMonthlyPlanWriteExport(HttpServletRequest request, HttpServletResponse response, String flowId) {
            designSevenSevice.monthlyExport(request,response,flowId);



   }
}
