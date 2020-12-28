package cn.visolink.firstplan.TaskLand.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.dao.TakeLandDao;
import cn.visolink.firstplan.TaskLand.form.ExcelGetLand;
import cn.visolink.firstplan.TaskLand.service.TakeLandService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.utils.CommUtilsUpdate;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author wyg
 */
@RestController
@Api(tags = "拿地模块")
@RequestMapping("/takeLand")
public class TakeLandController {

    @Autowired
    private TakeLandService takeLandService;

    @Autowired
    private TakeLandDao takeLandDao;


    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "三级联动查询项目接口")
    @PostMapping("/queryRegion")
    public Map queryRegion(@RequestBody Map map,HttpServletRequest servletRequest){
        map.put("userid",servletRequest.getHeader("userid"));
        String username=servletRequest.getHeader("username");
        Map resmenu = (Map) redisUtil.get("orgfpmenu"+username);
        if (resmenu!=null&&map.get("name").equals("")&&map.get("name")==""){
            return resmenu;
        }
        List<Map> mapRegion = takeLandService.queryRegion(map);
        Map menusMap = CommUtilsUpdate.buildTree(mapRegion);
        if(map.get("name").equals("")&&map.get("name")==""){
        //redisUtil.set("orgfpmenu"+username,menusMap);
        redisUtil.set("orgfpmenu"+username,menusMap,36000);
        }
        return menusMap;
    }

    @ApiOperation(value = "四级联动查询项目接口-佣金")
    @PostMapping("/queryCMRegion")
    public Map queryCMRegion(@RequestBody Map map,HttpServletRequest servletRequest){
        Map<Object, Object> resultMap = new HashMap<>();
        ResultBody<Object> resultBody = new ResultBody<>();
        map.put("userid",servletRequest.getHeader("userid"));
        String username=servletRequest.getHeader("username");
        Map resmenu = (Map) redisUtil.get("orgmenu"+username);
        if (resmenu!=null&&map.get("name").equals("")&&map.get("name")==""){
            System.out.println("走redis");
            return resmenu;
        }
        System.out.println("走数据库");
        String orgid = "";
        //查询出登录人的所有组织
        List<Map> orgList = authMapper.getOrgList(map.get("userid")+"");
        if(orgList==null||orgList.size()<=0){
            resultMap.put("code",-1904);
            resultMap.put("message","未查询到您的岗位所属组织,请先维护岗位组织!");
            return resultMap;
        }
        for (Map map1 : orgList) {
            orgid += "'" + map1.get("orgId") + "',";
        }

        String resOrgId = orgid.substring(0, orgid.length() - 1);
        map.put("resOrgId",resOrgId);
        List<Map> mapRegion = takeLandService.queryCMRegion(map);
        //System.out.println("组织ID"+orgId);
        Map menusMap = CommUtilsUpdate.buildTreeFour(mapRegion,orgList);
        if(map.get("name").equals("")&&map.get("name")=="") {
            //redisUtil.set("orgmenu" + username, menusMap);
            redisUtil.set("orgmenu"+username,menusMap,36000);
        }
        return menusMap;
    }

    /*
    * 根据组织iD获取所有子级
    * */
    @ApiOperation(value = "四级联动查询项目接口-佣金")
    @GetMapping("/queryOrgListByOrgId")
    public Map queryOrgListByOrgId(String orgId,String orgLevel){
        System.out.println(orgId);
        System.out.println(orgLevel);
        Map menusMap = new HashMap();
        if("2".equals(orgLevel)){
            List<Map> mapRegion = takeLandService.queryOrgListByOrgId(orgId);
             menusMap = CommUtilsUpdate.buildTree(mapRegion);
        }else if("3".equals(orgLevel)){
           List<Map> cityRegion = takeLandDao.queryOrgListByCityOrgId(orgId);
             menusMap = CommUtilsUpdate.buildTwoTree(cityRegion);
        }

        return menusMap;
    }
    @ApiOperation(value = "计划查询的相关操作")
    @PostMapping("/queryPlan")
    public ResultBody queryPlan(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        Map mapPro = takeLandService.queryPlan(map);
        resultBody.setData(mapPro);
        return resultBody;
    }

    @ApiOperation(value = "拿地后暂存")
    @PostMapping("/insertTakeLand")
    public VisolinkResultBody insertTakeLand( @RequestBody Map map, HttpServletRequest request){
        VisolinkResultBody resultBody = takeLandService.insertTakeLand(map,request);
        return resultBody;
    }

    @ApiOperation(value = "顶设一暂存")
    @PostMapping("/insertTopOne")
    public VisolinkResultBody insertTopOne( @RequestBody Map map,HttpServletRequest request){
        VisolinkResultBody resultBody = new VisolinkResultBody();
        try {
            resultBody = takeLandService.insertTopOne(map,request);
        }catch (Exception e){
            resultBody.setCode(-1);
        }
        return resultBody;
    }

    @ApiOperation(value = "查询监控页面所有的项目节点")
    @PostMapping("/selectPlanNode")
    public ResultBody selectPlanNode(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        List<Map> mapPlanNode = takeLandService.selectPlanNode(map);
        resultBody.setData(mapPlanNode);
        return resultBody;
    }

    @ApiOperation(value = "查询拿地后编制页面所有的信息")
    @PostMapping("/queryTakeLands")
    public ResultBody queryTakeLands(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        Map mapLand = takeLandService.queryTakeLands(map);
        resultBody.setData(mapLand);
        return resultBody;
    }

    @ApiOperation(value = "查询节点版本")
    @PostMapping("/selectNodeVersion")
    public ResultBody selectNodeVersion(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        List<Map> mapLand = takeLandService.selectNodeVersion(map);
        resultBody.setData(mapLand);
        return resultBody;
    }



    /***以下是提供给顶设一的接口************/
    @ApiOperation(value = "查询拿地后货值")
    @PostMapping("/queryValueStructure")
    public ResultBody queryValueStructure(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        Map mapLand = takeLandService.queryValueStructure(map);
        resultBody.setData(mapLand);
        return resultBody;
    }

    @ApiOperation(value = "查询拿地后房源")
    @PostMapping("/queryApartment")
    public ResultBody queryApartment(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        Map mapLand = takeLandService.queryApartment(map);
        resultBody.setData(mapLand);
        return resultBody;
    }

    @ApiOperation(value = "查询拿地后时间节点")
    @PostMapping("/queryTimeNode")
    public ResultBody queryTimeNode(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        List<Map> mapLand = takeLandService.queryTimeNode(map);
        resultBody.setData(mapLand);
        return resultBody;
    }

    /**********************************/

    @ApiOperation(value = "查询拿地后节点id")
    @PostMapping("/queryPlanNodeId")
    public ResultBody queryPlanNodeId(@RequestBody Map map){
        ResultBody resultBody = new ResultBody();
        List<Map> mapLand = takeLandService.queryPlanNodeId(map);
        resultBody.setData(mapLand);
        return resultBody;
    }

    @Log("附件查询")
    @ApiOperation(value = "附件查询")
    @PostMapping(value = "/queryfile")
    public VisolinkResultBody queryfile(@RequestBody Map params) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby.setResult(takeLandService.getFileLists(params.get("id")+""));
        } catch (Exception e) {
            resBoby.setCode(1);
            resBoby.setMessages("请求失败，请稍候再试");
        }
        return resBoby;
    }


    @Log("查询顶设一")
    @ApiOperation(value = "查询顶设一")
    @PostMapping(value = "/queryTopOne")
    public VisolinkResultBody queryTopOne(@RequestBody Map map) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            Map mapValue = takeLandService.queryTopOne(map);
            resBoby.setResult(mapValue);
        } catch (Exception e) {
            resBoby.setCode(-1);
            resBoby.setMessages("查询失败");
        }
        return resBoby;
    }

    @Log("查询顶设一")
    @ApiOperation(value = "查询顶设一")
    @PostMapping(value = "/takeLandSuccess")
    public VisolinkResultBody takeLandSuccess(@RequestBody Map map) {
        VisolinkResultBody resBoby=new VisolinkResultBody();
        try {
            resBoby = takeLandService.takeLandSuccess(map);
        } catch (Exception e) {
            resBoby.setCode(-1);
            resBoby.setMessages("查询失败");
        }
        return resBoby;
    }

    @Log("拿地后、顶设一数据导出")
    @ApiOperation(value = "导出")
    @GetMapping(value = "/export")
    public void export(HttpServletRequest request, HttpServletResponse response) {
          Map resultMap = new HashMap<>();
            try{
                Map excelGetLandMap = new HashMap();
                takeLandService.ExportGetLand(request,response,excelGetLandMap);
            }catch (Exception e){
                resultMap.put("code",-1);
                resultMap.put("message","失败");
            }

    }

}
