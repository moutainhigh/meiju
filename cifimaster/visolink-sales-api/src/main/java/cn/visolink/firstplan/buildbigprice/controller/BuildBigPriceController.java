package cn.visolink.firstplan.buildbigprice.controller;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.buildbigprice.service.BuildBigPriceService;
import cn.visolink.firstplan.fpdesigntwo.service.DesignTwoIndexService;
import cn.visolink.firstplan.fpdesigntwo.service.impl.DesignTwoIndexServiceImpl;
import cn.visolink.logs.aop.log.Log;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/16 5:32 下午
 */
@RestController
@Api(tags = "buildBigPrice")
@RequestMapping("/buildBigPrice")
public class BuildBigPriceController {
    @Autowired
    private BuildBigPriceService buildBigPriceService;
    @Autowired
    private DesignTwoIndexService designTwoIndexService;
    @Autowired
    private DesignTwoIndexServiceImpl designTwoIndexServiceImpl;

    @Log("导出模版")
    @ApiOperation(value = "导出模版")
    @PostMapping(value = "/buildBigPriceExportTemplate")
    public ResultBody buildBigPriceExportTemplate(
            @RequestBody Map paramMaps,
             HttpServletRequest request, HttpServletResponse response
    ){

        return buildBigPriceService.exportExcelTemplate(request,response,paramMaps);
    }

    @Log("获取选择数据")
    @ApiOperation(value = "获取选择数据")
    @PostMapping(value = "/getProjectStages")
    public VisolinkResultBody getProjectStages(@RequestBody Map map){
        return buildBigPriceService.getProjectStages(map);
    }

    @Log("获取选择数据")
    @ApiOperation(value = "获取选择数据")
    @PostMapping(value = "/importExcelTemplate")
    public VisolinkResultBody getProjectStages(@RequestParam("file")MultipartFile multipartFile,@RequestParam("plan_id")String plan_id,@RequestParam("plan_node_id")String plan_node_id,@RequestParam("project_id")String project_id){
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("plan_id",plan_id);
        paramMap.put("plan_node_id",plan_node_id);
        paramMap.put("project_id",project_id);
        return buildBigPriceService.importExcelTemplate(multipartFile,paramMap);
    }

    @Log("渲染页面列表数据")
    @ApiOperation(value = "渲染页面列表数据")
    @PostMapping(value = "/viewBigPriceBuildData")
    public VisolinkResultBody viewBigBuildData(@RequestBody Map map,HttpServletRequest request){

        VisolinkResultBody resultBody = buildBigPriceService.viewBigBuildData(map,request);
        return resultBody;
    }
    @Log("提交楼栋大定价数据")
    @ApiOperation(value = "提交楼栋大定价数据")
    @PostMapping(value = "/submitBigPriceBuildData")
    public VisolinkResultBody submitBigPriceBuildData(@RequestBody Map map,HttpServletRequest request){
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        String plan_node_id=map.get("plan_node_id")+"";
        if("".equals(plan_node_id)||"null".equals(plan_node_id)){
            String plan_node_idNew = designTwoIndexService.forPlanNode(map, request);
            map.put("plan_node_id",plan_node_idNew);
        }
        designTwoIndexServiceImpl.updateLightStuat(map,request);
        response.setCode(200);
        response.setMessages("提交成功!");
       return  response;
    }
    @Log("保存楼栋大定价导入数据")
    @ApiOperation(value = "保存楼栋大定价导入数据")
    @PostMapping(value = "/saveBigPriceBuildData")
    public VisolinkResultBody saveBigPriceBuildData(@RequestBody Map map,HttpServletRequest request){
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        String plan_node_id=map.get("plan_node_id")+"";
        /*if("".equals(plan_node_id)||"null".equals(plan_node_id)){
            String plan_node_idNew = designTwoIndexService.forPlanNode(map, request);
            map.put("plan_node_id",plan_node_idNew);
        }*/
       // VisolinkResultBody resultBody = buildBigPriceService.updateBigPriceIsSave(map);
       // int code = resultBody.getCode();
       /* if(code==200){
            map.put("isUpdate",0);
            map.put("node_level",3);
            map.put("plan_node_id",plan_node_id);
            designTwoIndexServiceImpl.updateLightStuat(map,request);
        }*/
        response.setCode(200);
        response.setMessages("保存成功!");
        return  response;
    }
    @Log("移除数据空值")
    @ApiOperation(value = "移除数据空值")
    @PostMapping(value = "/removeNullVlue")
    public VisolinkResultBody removeNullVlue(@RequestBody Map map,HttpServletRequest request){
        Map map1 = buildBigPriceService.filterMap(map);
        VisolinkResultBody<Object> resultBody = new VisolinkResultBody<>();
        resultBody.setCode(200);
        resultBody.setMessages("提交成功!");
        resultBody.setResult(map1);
        return  resultBody;
    }
    @Log("楼栋大定价数据导出")
    @ApiOperation(value = "楼栋大定价数据导出")
    @GetMapping(value = "/exportBuildBigPriceData")
    public ResultBody exportBuildBigPriceData(HttpServletRequest request, HttpServletResponse response){
        return  buildBigPriceService.exportBuildBigPriceData(request,response);
    }
}
