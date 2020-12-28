package cn.visolink.firstplan.fpdesigntwo.controller;


import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.buildbigprice.dao.BuildBigPriceDao;
import cn.visolink.firstplan.fpdesigntwo.dao.DesignTwoIndexDao;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.utils.DateUtil;
import cn.visolink.utils.UUID;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author 孙林
 * @since 2020-2-17
 */
@RestController
@Api(tags = "顶设2模块")
@RequestMapping("/designtwo")
public class DesignTwoIndexController {

    @Autowired
    private DesignTwoIndexService designTwoIndexService;

    @Autowired
    private DesignTwoIndexDao designTwoIndexDao;

    @Autowired
    private BuildBigPriceDao buildBigPriceDao;


    @Log("更新顶设2核心指标里的信息，顶设2-核心指标-量+利")
    //@CessBody
    @ApiOperation(value = "更新顶设2核心指标里的信息，顶设2-核心指标-量+利")
    @PostMapping(value = "/updateCodeIndex")
    public VisolinkResultBody updateCodeSelect(@RequestBody Map map, HttpServletRequest request) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {   /*清除掉数据里有“”的数据*/
            List<Map> listmap = new ArrayList<>();
            listmap.add(map);
            listmap = iterator(listmap);
            map = listmap.get(0);
            /*若前端不传plan_node_id来说明要初始化，那么初始化的时候要传node_level  等级来*/
            Map time = (Map) map.get("time");
            Map planMap = new HashMap();
            planMap.putAll(map);
            if (time != null) {
                String designTime = time.get("designtwo_time") + "";
                planMap.put("designtwo_time", designTime);
            }
            String planNodeId = designTwoIndexService.forPlanNode(planMap, request);
            map.put("plan_node_id", planNodeId);

            Integer result = designTwoIndexService.updateAllCodeIndex(map, request);


            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;

    }


    @Log("搜索顶设2核心指标里的信息，顶设2-核心指标-量+利")
    //@CessBody
    @ApiOperation(value = "搜索顶设2核心指标里的信息，顶设2-核心指标-量+利")
    @PostMapping(value = "/selectCodeIndex")
    public VisolinkResultBody indexCodeSelect(@RequestBody Map map) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            if (map.get("plan_id") == null || map.get("plan_id") == "") {
                Map map1 = designTwoIndexDao.selectPlanId(map);
                map.put("plan_id", map1.get("plan_id"));
            }
            Map result = designTwoIndexService.selectAllCodeIndex(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("顶设2全盘量价规划查找和大定价版本对标")
    //@CessBody
    @ApiOperation(value = "顶设2全盘量价规划查找和大定价版本对标")
    @PostMapping(value = "/selectAllPlan")
    public VisolinkResultBody selectAllPlan(@RequestBody Map map) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            if (map.get("plan_id") == null || map.get("plan_id") == "") {
                Map map1 = designTwoIndexDao.selectPlanId(map);
                map.put("plan_id", map1.get("plan_id"));
            }

            Map result = designTwoIndexService.selectAllPlan(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("顶设2全盘量价规划更新和大定价版本对标")
    // @CessBody
    @ApiOperation(value = "顶设2全盘量价规划更新和大定价版本对标")
    @PostMapping(value = "/updateAllPlan")
    public VisolinkResultBody updateAllPlan(@RequestBody Map listmap, HttpServletRequest request) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {

            List<Map> big = (ArrayList) listmap.get("big");
            big = iterator(big);
            List<Map> plan = (ArrayList) listmap.get("plan");
            plan = iterator(plan);
            listmap.put("big", big);
            listmap.put("plan", plan);

            /*若前端不传plan_node_id来说明要初始化，那么初始化的时候要传node_level  等级来*/
            String planNodeId = designTwoIndexService.forPlanNode(listmap, request);
            listmap.put("plan_node_id", planNodeId);
            List<Map> bigMap = (ArrayList) listmap.get("big");
            /*过滤掉小计*/
            List<Map> RealbigMap = new ArrayList<>();
            for (Map map1 : bigMap) {
                if (!(map1.get("product_type") + "").equals("小计")) {
                    RealbigMap.add(map1);
                }
            }
            listmap.put("big", RealbigMap);
            Integer result = designTwoIndexService.updateAllPlan(listmap, request);

            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    /*第二批次*/
    @Log("顶设2楼栋大定价")
    @CessBody
    @ApiOperation(value = "顶设2楼栋大定价")
    @PostMapping(value = "/selectBigPrice")
    public List<Map> selectBigPrice(@RequestBody Map map) {


        List<Map> result = designTwoIndexService.selectBigPrice(map);
        return result;
    }

    @Log("顶设2查找客储计划周拆分和节点储客计划")
    // @CessBody
    @ApiOperation(value = "顶设2查找客储计划周拆分和节点储客计划")
    @PostMapping(value = "/selectStorageNodePlan")
    public VisolinkResultBody selectStorageNodePlan(@RequestBody Map map) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            if (map.get("plan_id") == null || map.get("plan_id") == "") {
                Map map1 = designTwoIndexDao.selectPlanId(map);
                map.put("plan_id", map1.get("plan_id"));
            }
            Map result = designTwoIndexService.selectStorageNodePlan(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    @Log("顶设2更新客储计划周拆分和节点储客计划")
    //@CessBody
    @ApiOperation(value = "顶设2更新客储计划周拆分和节点储客计划")
    @PostMapping(value = "/updateStorageNodePlan")
    public VisolinkResultBody updateStorageNodePlan(@RequestBody Map map, HttpServletRequest request) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            List<Map> NodePlan = (ArrayList) map.get("NodePlan");
            List<Map> Week = (ArrayList) map.get("Week");
            NodePlan = iterator(NodePlan);
            Week = iterator(Week);
            map.put("NodePlan", NodePlan);
            map.put("Week", Week);

            map.put("node_level", 3);
            String planNodeId = designTwoIndexService.forPlanNode(map, request);
            map.put("plan_node_id", planNodeId);
            designTwoIndexService.updateStorageNodePlan(map, request);
            map.remove("flow_id");
            List<Map> result = designTwoIndexDao.selectStorageFlowTwo(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("查找顶设2左上角的版本选择")
    //@CessBody
    @ApiOperation(value = "查找顶设2左上角的版本选择")
    @PostMapping(value = "/selectPlanNode")
    public VisolinkResultBody selectPlanNode(@RequestBody Map map) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            if (map.get("plan_id") == null || map.get("plan_id") == "") {
                Map map1 = designTwoIndexDao.selectPlanId(map);
                map.put("plan_id", map1.get("plan_id"));
            }
            List<Map> result = designTwoIndexService.selectPlanNode(map);
            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    @Log("提交审批后调用的方法")
    @CessBody
    @ApiOperation(value = "提交审批后调用的方法")
    @PostMapping(value = "/forMtstart")
    public VisolinkResultBody update(@RequestBody Map map, HttpServletRequest request) {
        VisolinkResultBody<Object> visolinkResultBody = new VisolinkResultBody<>();
        String username = request.getHeader("username");
        String FlowCode = designTwoIndexDao.selectFlowCode(map.get("plan_node_id") + "");
        Map result = new HashMap();
        result.put("BSID", "FP");
        result.put("BTID", FlowCode);
        result.put("codeBOID", map.get("plan_node_id"));
        result.put("bkUserID", username);
        result.put("loginKey", "");
        visolinkResultBody.setResult(result);
        return visolinkResultBody;
    }

    @Log("审批接口")
    // @CessBody
    @ApiOperation(value = "审批接口")
    @PostMapping(value = "/forUpdateNode")
    public VisolinkResultBody forUpdateNode(@RequestBody Map map) {
        VisolinkResultBody resBoby = new VisolinkResultBody();

        try {
            String eventType = map.get("eventType") + "";
            String id = map.get("id") + "";
            String flowKey = map.get("flowKey") + "";
            String orgName = map.get("orgName") + "";
            String obj = "{\"orgName\":\"" + orgName + "\",\"flowKey\":\"" + flowKey + "\",\"instanceId\":\"" + id + "\",\"eventType\":\"" + eventType + "\",\"businesskey\":\"" + id + "\",\"sysCode\":\"xsgl\",\"bnsParameters\":null,\"backHandMode\":null}";
            HashMap hashMap = JSON.parseObject(obj, HashMap.class);
            designTwoIndexService.forUpdateNode(hashMap);
            resBoby.setResult(1);
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("首开前计划费用查询")
    @ApiOperation(value = "首开前计划费用查询")
    @PostMapping(value = "/queryOpenCost")
    public VisolinkResultBody queryOpenCost(@RequestBody Map params) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            String plan_node_id = params.get("plan_node_id") + "";
            String plan_id = params.get("plan_id") + "";
            if ("".equals(plan_node_id) || "null".equals(plan_node_id)) {
                //获取审批通过的最新版本数据
                String newplanNodeId = buildBigPriceDao.getNewPlanNodeData(plan_id);
                params.put("plan_node_id", newplanNodeId);
            }
            resBoby.setResult(designTwoIndexService.selectOpenCostByPlanNodeId(params.get("plan_node_id") + "", params.get("plan_id") + ""));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }

    @Log("首开前计划费用新增")
    @ApiOperation(value = "首开前计划费用新增")
    @PostMapping(value = "/insertOpenCost")
    public VisolinkResultBody insertOpenCost(@RequestBody Map params, HttpServletRequest request) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            String planNodeId = designTwoIndexService.forPlanNode(params, request);
            params.put("plan_node_id", planNodeId);
            designTwoIndexService.insertOrUpOpenCost(params);
        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    /*
    * 首开费用计划新增科目
    * */
    @Log("首开费用计划新增科目")
    @ApiOperation(value = "首开费用计划新增科目")
    @PostMapping("/insertSubject")
    public ResultBody insertSubject(@RequestBody Map map){
        try {
            Integer num = designTwoIndexDao.getSubjectName(map.get("two_subject_name")+"");
            if(num>0){
                return ResultBody.success("科目编码已存在，请重新输入！");
            }
            map.put("version",designTwoIndexDao.getMaxVersion());
            designTwoIndexDao.insertSubject(map);
            //动态添加字段
            designTwoIndexDao.addCommunCost(map);
        }catch(Exception e){
            e.printStackTrace();
        }
        return ResultBody.success("新增成功！");
    }

    /*
    * 首开费用科目查询
    * */
    @Log("首开费用科目查询")
    @ApiOperation(value = "首开费用科目查询")
    @GetMapping("/getSubject")
    public ResultBody getSubject(Integer pageSize,Integer pageNum){
        PageHelper.startPage(pageNum,pageSize);
        List<Map> list = designTwoIndexDao.getSubject();
        PageInfo<Map> pageInfo = new PageInfo<Map>(list);
        return ResultBody.success(pageInfo);
    }


    /*
     * 首开费用科目修改
     * */
    @Log("首开费用科目修改")
    @ApiOperation(value = "首开费用科目查询")
    @PostMapping("/updateSubject")
    public ResultBody updateSubject(@RequestBody Map map){
         designTwoIndexDao.updateSubject(map);
        return ResultBody.success("修改成功！");
    }
    @Log("快速审批")
    // @CessBody
    @ApiOperation(value = "快速审批")
    @PostMapping(value = "/fastUpdate")
    public VisolinkResultBody fastUpdate(@RequestBody Map map, HttpServletRequest request) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            Map ForMap = new HashMap();
            ForMap.put("eventType", 4);
            String node_level = map.get("node_level") + "";
            if (node_level.equals("3")) {
                ForMap.put("businesskey", map.get("plan_node_id"));
                ForMap.put("orgName", "fp_designtwo");
            } else if (node_level.equals("4")) {
                ForMap.put("businesskey", map.get("flow_id"));
                ForMap.put("orgName", "fp_open_three");
            } else {
                ForMap.put("businesskey", map.get("flow_id"));
                ForMap.put("orgName", "1");
            }
            String username = request.getHeader("username");
            ForMap.put("creator", username);
            designTwoIndexService.forUpdateNode(ForMap);
        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    @Log("顶设2货值结构部分")
    // @CessBody
    @ApiOperation(value = "顶设2货值结构部分")
    @PostMapping(value = "/getDesignTwoValue")
    public VisolinkResultBody getDesignTwoValue(@RequestBody Map map) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {
            if (map.get("plan_id") == null || map.get("plan_id") == "") {
                Map map1 = designTwoIndexDao.selectPlanId(map);
                map.put("plan_id", map1.get("plan_id"));
            }

            Map map1 = designTwoIndexService.getDesignTwoValue(map);
            resBoby.setResult(map1);
            resBoby.setCode(0);
        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    @Log("顶设2货值结构部分跟新")
    //@CessBody
    @ApiOperation(value = "顶设2货值结构部分跟新")
    @PostMapping(value = "/updateDesignTwoValue")
    public VisolinkResultBody updateDesignTwoValue(@RequestBody Map map, HttpServletRequest request) {
        VisolinkResultBody resBoby = new VisolinkResultBody();
        try {

            map.put("node_level", 3);
            String planNodeId = designTwoIndexService.forPlanNode(map, request);
            map.put("plan_node_id", planNodeId);
            Integer result = designTwoIndexService.updateDesignTwoValue(map, request);

            resBoby.setResult(result);
            resBoby.setCode(0);
        } catch (Exception e) {
            e.printStackTrace();
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    @Log("测试顶设2推倒创建版本")
    //@CessBody
    @ApiOperation(value = "测试顶设2推倒创建版本")
    @GetMapping(value = "/backDesignTwo")
    public void updateDesignTwoValue(String planId) {
        designTwoIndexService.backDesignTwo(planId);
    }


    /*遍历数据使得里面的数据没有“”*/
    public List<Map> iterator(List<Map> listmap) {
        /*
         * 遍历Map所有值，若传进来的数有空，则将它默认为0
         * */
        for (int i = 0; i < listmap.size(); i++) {
            Iterator iterable = listmap.get(i).entrySet().iterator();
            while (iterable.hasNext()) {
                Map.Entry entry_d = (Map.Entry) iterable.next();
                Object key = entry_d.getKey();
                Object value = entry_d.getValue();
                if (value == "") {
                    value = null;
                }
                listmap.get(i).put(key.toString(), value);
            }
        }
        return listmap;

    }

}
