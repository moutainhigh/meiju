package cn.visolink.firstplan.commission.controller;

import cn.hutool.core.date.DateUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.commission.service.CommissionService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.utils.StringUtil;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bao
 * @date 2020-5-20
 */

@RestController
@Api(tags = "待结佣金数据接口")
@Slf4j
@RequestMapping("/Commission")
public class CommissionController {

    @Autowired
    private CommissionService commissionServiceImpl;


    @Log("佣金单项目数据获取")
    @ApiOperation(value = "佣金单项目数据获取")
    @PostMapping("/initCommissionByProject")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initCommissionByProject(@RequestBody Map map) {
        try {
            String modifiedTime = DateUtil.offsetDay(new Date(),-1).toString();
            return ResultBody.success(commissionServiceImpl.initCommission(modifiedTime,map.get("projectId")+""));
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }


    @Log("待结佣数据查询")
    @ApiOperation(value = "待结佣数据查询")
    @PostMapping("/selectCommission")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectCommission(@RequestBody Map<String, Object> map) {
        try {
            return commissionServiceImpl.selectCommission(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("不结佣备注修改")
    @ApiOperation(value = "不结佣备注修改")
    @PostMapping("/updateCommissionNo")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody updateCommissionNo(@RequestBody Map map) {
        return commissionServiceImpl.updateCommissionNo(map);
    }


    @Log("不结佣发放金额修改")
    @ApiOperation(value = "不结佣发放金额修改")
    @PostMapping("/updateCommissionNoPayment")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody updateCommissionNoPayment(@RequestBody Map map) {
        return commissionServiceImpl.updateCommissionNoPayment(map);
    }

    @Log("待结佣数据导出")
    @ApiOperation(value = "待结佣数据导出")
    @GetMapping("/CommissionExcel")
    @ApiImplicitParam(name = "map", value = "map")
    public void exportExcelCommission(HttpServletRequest request, HttpServletResponse response, String ids,String source_type_desc) {
        if (!StringUtil.isEmpty(ids)) {
            commissionServiceImpl.exportExcelCommission(request, response, ids,null,source_type_desc);
        }
    }


    @Log("待结佣数据导出")
    @ApiOperation(value = "待结佣数据导出")
    @GetMapping("/CommissionNoExcel")
    @ApiImplicitParam(name = "map", value = "map")
    public void CommissionNoExcel(HttpServletRequest request, HttpServletResponse response,
                                         String project_id,String source_type_desc,String keyType,String value,String transaction_status,
                                         String collection_proportion_start,String collection_proportion_end,String subscription_date_start,
                                         String subscription_date_end, String signing_date_start,String signing_date_end,String current_role,
                                         String gain_by,String ids,String orgId,String orgLevel
    ) {
        Map<String ,Object> map = new HashMap<>(16);
        map.put("ids",ids);
        map.put("project_id",project_id);
        map.put("source_type_desc",source_type_desc);
        map.put("keyType",keyType);
        map.put("value",value);
        map.put("transaction_status",transaction_status);
        map.put("gain_by",gain_by);
        map.put("collection_proportion_start",collection_proportion_start);
        map.put("collection_proportion_end",collection_proportion_end);
        map.put("subscription_date_start",subscription_date_start);
        map.put("subscription_date_end",subscription_date_end);
        map.put("signing_date_start",signing_date_start);
        map.put("signing_date_end",signing_date_end);
        map.put("current_role",current_role);
        map.put("orgId",orgId);
        map.put("orgLevel",orgLevel);
        commissionServiceImpl.exportExcelCommissionNo(request, response, map);
    }

    @Log("待结佣数据导出")
    @ApiOperation(value = "待结佣数据导出")
    @GetMapping("/AllCommissionExcel")
    @ApiImplicitParam(name = "map", value = "map")
    public void exportExcelAllCommission(HttpServletRequest request, HttpServletResponse response,
                                         String project_id,String source_type_desc,String keyType,String value,String transaction_status,
                                         String collection_proportion_start,String collection_proportion_end,String subscription_date_start,
                                         String subscription_date_end, String signing_date_start,String signing_date_end,String current_role,
                                         String orgId,String orgLevel
    ) {
        Map<String ,Object> map = new HashMap<>(16);
        map.put("project_id",project_id);
        map.put("source_type_desc",source_type_desc);
        map.put("keyType",keyType);
        map.put("value",value);
        map.put("transaction_status",transaction_status);
        map.put("collection_proportion_start",collection_proportion_start);
        map.put("collection_proportion_end",collection_proportion_end);
        map.put("subscription_date_start",subscription_date_start);
        map.put("subscription_date_end",subscription_date_end);
        map.put("signing_date_start",signing_date_start);
        map.put("signing_date_end",signing_date_end);
        map.put("current_role",current_role);
        map.put("orgId",orgId);
        map.put("orgLevel",orgLevel);
        commissionServiceImpl.exportExcelCommission(request, response, null,map,source_type_desc);
    }


    @Log("待结佣金数据导入")
    @CessBody
    @ApiOperation(value = "待结佣金数据导入")
    @ResponseBody
    @PostMapping(value = "/CommissionImport")
    public ResultBody commissionImport(MultipartFile file, String months) {
        return commissionServiceImpl.commissionImport(file, months);
    }

    @Log("终止结佣")
    @ApiOperation(value = "终止结佣")
    @PostMapping("/initCommissionNo")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initCommissionNo(HttpServletRequest request,@RequestBody Map map) {
        map.put("userId",request.getHeader("userid"));
        map.put("jobId",request.getHeader("jobid"));
        map.put("jobOrgId",request.getHeader("joborgid"));
        map.put("orgId",request.getHeader("orgid"));
        return commissionServiceImpl.initCommissionNo(map);
    }


    @Log("不结佣数据查询")
    @ApiOperation(value = "不结佣数据查询")
    @PostMapping("/selectCommissionNo")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectCommissionNo(@RequestBody Map<String, Object> map) {
        try {
            return commissionServiceImpl.selectCommissionNo(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("不结佣发放")
    @ApiOperation(value = "不结佣发放")
    @PostMapping("/updateGrant")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody updateGrant(@RequestBody Map map) {
        return commissionServiceImpl.updateGrant(map);
    }


    @Log("获取渠道名称")
    @ApiOperation(value = "获取渠道名称")
    @PostMapping("/getCurrentRole")
    public ResultBody getCurrentRole() {
        try {
            return commissionServiceImpl.getCurrentRole();
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("明源数据局部更新")
    @ApiOperation(value = "明源数据局部更新")
    @PostMapping("/updateMyTrade")
    public ResultBody updateMyTrade(@RequestBody List<String> list) {
        try {
            return commissionServiceImpl.updateMyTrade(list);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("查询业绩归属")
    @ApiOperation(value = "查询业绩归属")
    @PostMapping("/getGainBy")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody getGainBy(@RequestBody Map map) {
        try {
            return commissionServiceImpl.getGainBy(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }
}
