package cn.visolink.salesmanage.onlineretailersuse.controller;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.onlineretailersuse.service.OnlineretailersUseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/11/26 9:02 下午
 */
@RestController
@RequestMapping("/OnlineretailersUse")
@Api("电商使用政策申请")
public class OnlineretailersUseController {
    @Autowired
    private OnlineretailersUseService onlineretailersUseService;


    @Log("电商使用政策申请/政策申请列表查询")
    @ApiOperation(value = "电商使用政策申请/政策申请列表查询")
    @PostMapping("/getOnlineretailersUseApplayList")
    public ResultBody getOnlineretailersUseApplayList(@RequestBody Map map, HttpServletRequest request){
        /*货期请求头里的权限层级数据 bql 2020.07.20 */
        map.put("jobId",request.getHeader("jobid"));
        map.put("jobOrgId",request.getHeader("joborgid"));
        map.put("orgId",request.getHeader("orgid"));
        map.put("orgLevel",request.getHeader("orglevel"));
        map.put("username",request.getHeader("username"));
        return onlineretailersUseService.getOnlineretailersUseApplayList(map);
    }

    @Log("电商使用政策申请/政策保存/提交")
    @ApiOperation(value = "电商使用政策申请/政策保存/提交")
    @PostMapping("/saveOnlineretailer")
    public ResultBody saveOnlineretailer(@RequestBody Map map, HttpServletRequest request) {
        return onlineretailersUseService.saveOnlineretailersUseApplay(map, request);
    }

    @Log("电商使用政策申请数据详情查询/电商使用政策申请数据详情查询")
    @ApiOperation(value = "电商使用政策申请数据详情查询")
    @PostMapping("/queryOnOnlineretailersUseInfo")
    public ResultBody queryOnOnlineretailersUseInfo(@RequestBody Map map, HttpServletRequest request) {
        return onlineretailersUseService.queryOnOnlineretailersUseInfo(map, request);
    }

    @Log("推送明源政策数据/推送明源政策数据")
    @ApiOperation(value = "推送明源政策数据")
    @PostMapping("/synOnOnlineretailersUseData")
    public ResultBody synOnOnlineretailersUseData(@RequestBody Map map) {
        return onlineretailersUseService.synOnOnlineretailersUseData(map);
    }

    @Log("根据分期获取产品等数据/根据分期获取产品等数据")
    @ApiOperation(value = "根据分期获取产品等数据")
    @PostMapping("/getProductData")
    public ResultBody getProductData(@RequestBody Map map) {
        return onlineretailersUseService.getProductData(map);
    }

    @Log("删除政策数据/删除政策数据")
    @ApiOperation(value = "删除政策数据")
    @PostMapping("/deleteOnOnlineretailersUseData")
    public ResultBody deleteOnOnlineretailersUseData(@RequestBody Map map) {
        return onlineretailersUseService.deleteOnOnlineretailersUseData(map);
    }


}
