package cn.visolink.firstplan.receipt.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.firstplan.receipt.service.ReceiptService;
import cn.visolink.logs.aop.log.Log;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金付款单 前端控制器
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */

@RestController
@RequestMapping("/Receipt")
@Api(tags = "付款单数据接口")
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptServiceImpl;

    public ReceiptController(ReceiptService receiptServiceImpl) {
        this.receiptServiceImpl = receiptServiceImpl;
    }

    @Log("付款单数据查询")
    @ApiOperation(value = "付款单数据查询")
    @PostMapping("/selectReceipt")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectReceipt(@RequestBody Map<String, Object> map) {
        try {
            return receiptServiceImpl.selectReceipt(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }


    @Log("待付款单数据查询")
    @ApiOperation(value = "待付款单数据查询")
    @PostMapping("/selectWaitReceipt")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectWaitReceipt(@RequestBody Map<String, Object> map) {
        try {
            return receiptServiceImpl.selectWaitReceipt(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("佣金台账数据查询")
    @ApiOperation(value = "佣金台账数据查询")
    @PostMapping("/selectCommissionStanding")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectCommissionStanding(@RequestBody Map<String, Object> map) {
        try {
            return receiptServiceImpl.selectCommissionStanding(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }


    @Log("添加付款单")
    @ApiOperation(value = "添加付款单")
    @PostMapping("/initReceipt")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initReceipt(HttpServletRequest request,@RequestBody Map<String,Object> map) {
        try {
            map.put("userId",request.getHeader("userid"));
            map.put("jobId",request.getHeader("jobid"));
            map.put("jobOrgId",request.getHeader("joborgid"));
            map.put("orgId",request.getHeader("orgid"));
            return receiptServiceImpl.initReceipt(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("付款单明细数据查询")
    @ApiOperation(value = "付款单明细数据查询")
    @PostMapping("/selectReceiptDetail")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectReceiptDetail(@RequestBody Map<String, Object> map) {
        try {
            return receiptServiceImpl.selectReceiptDetail(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("添加付款单")
    @ApiOperation(value = "添加付款单")
    @PostMapping("/initReceiptDetail")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initReceiptDetail(HttpServletRequest request,@RequestBody Map map) {
        try {
            map.put("userId",request.getHeader("userid"));
            map.put("jobId",request.getHeader("jobid"));
            map.put("jobOrgId",request.getHeader("joborgid"));
            map.put("orgId",request.getHeader("orgid"));
            return receiptServiceImpl.initReceiptDetail(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }



    @Log("佣金台账数据导出")
    @ApiOperation(value = "佣金台账数据导出")
    @GetMapping("/ExcelCommissionStanding")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "id,id")
    })
    public void selectExcelCommissionStanding(HttpServletRequest request, HttpServletResponse response, String ids, String source_type_desc) {
        receiptServiceImpl.selectExcelCommissionStanding(request, response, ids,null,source_type_desc);
    }

    @Log("佣金台账数据导出")
    @ApiOperation(value = "佣金台账数据导出")
    @GetMapping("/AllExcelCommissionStanding")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rids", value = "id,id"),
            @ApiImplicitParam(name = "ids", value = "id,id"),
    })
    public void AllselectExcelCommissionStanding(HttpServletRequest request, HttpServletResponse response,
                                          String subscription_date_start,String subscription_date_end,
                                          String signing_date_start,String source_type_desc,
                                          String signing_date_end,String keyType,String value,
                                          String collection_proportion, String collection_proportion2,
                                          String commission_point, String commission_point2,
                                          String project_amount, String project_amount2,
                                          String project_status,String payment_status,
                                          String is_receipt,String orgId, String orgLevel
    ) {
        Map<String,Object> map =new HashMap<>();
        map.put("keyType",keyType);
        map.put("value",value);
        map.put("project_status",project_status);
        map.put("payment_status",payment_status);
        map.put("signing_date_start",signing_date_start);
        map.put("signing_date_end",signing_date_end);
        map.put("collection_proportion",collection_proportion);
        map.put("collection_proportion2",collection_proportion2);
        map.put("commission_point",commission_point);
        map.put("commission_point2",commission_point2);
        map.put("project_amount",project_amount);
        map.put("project_amount2",project_amount2);
        map.put("is_receipt",is_receipt);
        map.put("subscription_date_start",subscription_date_start);
        map.put("subscription_date_end",subscription_date_end);
        map.put("source_type_desc",source_type_desc);
        map.put("orgId",orgId);
        map.put("orgLevel",orgLevel);
        receiptServiceImpl.selectExcelCommissionStanding(request, response,null,map,source_type_desc);
    }

    @Log("付款单明细数据导出")
    @ApiOperation(value = "付款单明细数据导出")
    @GetMapping("/ExcelWaitReceipt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "id,id")
    })
    public void selectExcelWaitReceipt(HttpServletRequest request, HttpServletResponse response, String ids, String source_type_desc) {
        receiptServiceImpl.selectExcelWaitReceipt(request, response, ids,null,source_type_desc);
    }

    @Log("付款单明细数据导出")
    @ApiOperation(value = "付款单明细数据导出")
    @GetMapping("/AllExcelWaitReceipt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rids", value = "id,id"),
            @ApiImplicitParam(name = "ids", value = "id,id"),
    })
    public void AllselectExcelWaitReceipt(HttpServletRequest request, HttpServletResponse response,
                                          String subscription_date_start,String subscription_date_end,
                                          String signing_date_start,String source_type_desc,
                                          String signing_date_end,String keyType,String value,
                                          String collection_proportion, String collection_proportion2,
                                          String commission_point, String commission_point2,
                                          String project_amount, String project_amount2,
                                          String is_receipt,String orgId, String orgLevel
    ) {
        Map<String,Object> map =new HashMap<>(16);
        map.put("keyType",keyType);
        map.put("value",value);

        map.put("subscription_date_start",subscription_date_start);
        map.put("subscription_date_end",subscription_date_end);
        map.put("signing_date_start",signing_date_start);
        map.put("signing_date_end",signing_date_end);
        map.put("collection_proportion",collection_proportion);
        map.put("collection_proportion2",collection_proportion2);
        map.put("commission_point",commission_point);
        map.put("commission_point2",commission_point2);
        map.put("project_amount",project_amount);
        map.put("project_amount2",project_amount2);
        map.put("is_receipt",is_receipt);
        map.put("source_type_desc",source_type_desc);
        map.put("orgId",orgId);
        map.put("orgLevel",orgLevel);
        receiptServiceImpl.selectExcelWaitReceipt(request, response,null,map,source_type_desc);
    }


    @Log("付款单明细数据导出")
    @ApiOperation(value = "付款单明细数据导出")
    @GetMapping("/ReceiptDetailExcel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rids", value = "id,id"),
            @ApiImplicitParam(name = "ids", value = "id,id"),
    })
    public void exportExcelCommission(HttpServletRequest request, HttpServletResponse response, String rids, String ids, String source_type_desc) {
        receiptServiceImpl.selectExcelReceiptDetail(request, response, rids, ids,null,source_type_desc);
    }

    @Log("付款单明细数据导出")
    @ApiOperation(value = "付款单明细数据导出")
    @GetMapping("/AllReceiptDetailExcel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rids", value = "id,id"),
            @ApiImplicitParam(name = "ids", value = "id,id"),
    })
    public void AllReceiptDetailExcel(HttpServletRequest request, HttpServletResponse response,
                                      String receipt_id,String cityCompany, String projectName,String source_type_desc, String keyType,String value,
                                      String create_time_sta,String create_time_end, String payment_status, String project_id, String channel_name,
                                      String orgId, String orgLevel
    ) {
        Map<String,Object> map =new HashMap<>(16);
        map.put("receipt_id",receipt_id);
        map.put("cityCompany",cityCompany);
        map.put("projectName",projectName);
        map.put("source_type_desc",source_type_desc);
        map.put("keyType",keyType);
        map.put("value",value);
        map.put("create_time_sta",create_time_sta);
        map.put("create_time_end",create_time_end);
        map.put("payment_status",payment_status);
        map.put("project_id",project_id);
        map.put("channel_name",channel_name);
        map.put("orgId",orgId);
        map.put("orgLevel",orgLevel);
        receiptServiceImpl.selectExcelReceiptDetail(request, response, null, null,map,source_type_desc);
    }

    @Log("付款单明细数据导入")
    @CessBody
    @ApiOperation(value = "付款单明细数据导入")
    @ResponseBody
    @PostMapping(value = "/receiptDetailImport")
    public ResultBody receiptDetailImport(HttpServletRequest request,MultipartFile file, String months) {
        return receiptServiceImpl.receiptDetailImport(request.getHeader("username"),file, months);
    }

    @Log("付款单明细付款金额修改")
    @ApiOperation(value = "付款单明细付款金额修改")
    @PostMapping("/updateReceiptDetail")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody updateReceiptDetail(@RequestBody Map<String,Object> map,HttpServletRequest request) {
        String userId = request.getHeader("userid");
        map.put("username",userId);
        Integer num = receiptServiceImpl.updateReceiptDetail((List<Map<String, Object>>) map.get("list"),userId);
        ResultBody resultBody = new ResultBody<>();
        if (num > 0) {
            resultBody = receiptServiceImpl.updateReceiptAmount(map);
        } else {
            resultBody.setCode(-1);
            resultBody.setMessages("请填写付款申请金额！");
        }
        return resultBody;
    }


    @Log("付款审批状态修改")
    @ApiOperation(value = "付款审批状态修改")
    @PostMapping("/receiptAddApproval")
    @ApiImplicitParam(name = "map", value = "map")
    public Map receiptAddApproval(HttpServletRequest request,@RequestBody Map map) {
        String requstTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        try {
            // 参数校验
            if (CollUtil.isEmpty(map)) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，参数错误");
            }
            Map<String, Object> requestInfoMap = (Map<String, Object>) map.get("requestInfo");
            Map<String, Object> dataMap = (Map<String, Object>) requestInfoMap.get("data");

            // 参数校验
            if (CollUtil.isEmpty(dataMap)) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，参数错误");
            }
            if (dataMap.get("receiptCode") == null || StrUtil.isBlank(dataMap.get("receiptCode").toString())) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，付款单编码 必填");
            }
            if (dataMap.get("receiptId") == null || StrUtil.isBlank(dataMap.get("receiptId").toString())) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，付款单id 必填");
            }
            if (!StrUtil.equals("2", dataMap.get("paymentStatus").toString()) && !StrUtil.equals("3", dataMap.get("paymentStatus").toString()) && !StrUtil.equals("4", dataMap.get("paymentStatus").toString()) &&
                    !StrUtil.equals("5", dataMap.get("paymentStatus").toString()) ) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，付款单状态需为：2、3、4、5中的一种");
            }
            if (StrUtil.equals("4", dataMap.get("paymentStatus").toString())) {
                if(dataMap.get("paymentCode") == null || StrUtil.isBlank(dataMap.get("paymentCode").toString())){
                    return ResultUtil.getErrorMap(requstTime, "调用失败，付款单状态4时，付款申请编号必填");
                }else if(dataMap.get("paymentAmount") == null || StrUtil.isBlank(dataMap.get("paymentAmount").toString())){
                    return ResultUtil.getErrorMap(requstTime, "调用失败，付款单状态4时，付款申请金额必填");
                }else if(dataMap.get("approvalDate") == null || StrUtil.isBlank(dataMap.get("approvalDate").toString())){
                    return ResultUtil.getErrorMap(requstTime, "调用失败，付款单状态4时，付款申请时间必填");
                }
                try {
                    DateUtil.parse(dataMap.get("approvalDate").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResultUtil.getErrorMap(requstTime, "调用失败，付款申请时间格式有误。示例：2020-06-03 11:30:00");
                }
            }
            // 调用服务
            return receiptServiceImpl.receiptAddApproval(request,dataMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.getErrorMap(requstTime, "调用失败");
        }
    }

    @Log("付款单状态修改")
    @ApiOperation(value = "付款单状态修改")
    @PostMapping("/updatePaymentStatus")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody updatePaymentStatus(@RequestBody Map map) {
        try {
            return receiptServiceImpl.updatePaymentStatus(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }




    @Log("付款单生成验证")
    @ApiOperation(value = "付款单生成验证")
    @PostMapping("/getPaymentStatus")
    @ApiImplicitParam(name = "ids", value = "id,id")
    public ResultBody getPaymentStatus(@RequestBody Map map) {
        try {
            return receiptServiceImpl.getPaymentStatus(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("获取付款单明细上传文件查询")
    @ApiOperation(value = "获取付款单明细上传文件查询")
    @PostMapping("/getFileList")
    @ApiImplicitParam(name = "id", value = "id")
    public ResultBody getFileList(@RequestBody Map<String, Object> map) {
        try {
            return receiptServiceImpl.getFileList(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("获取付款单明细上传文件删除")
    @ApiOperation(value = "获取付款单明细上传文件删除")
    @PostMapping("/delFile")
    @ApiImplicitParam(name = "id", value = "id")
    public ResultBody delFile(@RequestBody Map map) {
        try {
            return receiptServiceImpl.delFile(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("付款单明细单条删除")
    @ApiOperation(value = "付款单明细单条删除")
    @PostMapping("/deleteReceiptDetail")
    @ApiImplicitParam(name = "id", value = "id")
    public ResultBody deleteReceiptDetail(@RequestBody Map map) {
        try {
            return receiptServiceImpl.deleteReceiptDetail(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("付款单条删除")
    @ApiOperation(value = "付款单条删除")
    @PostMapping("/deleteReceipt")
    @ApiImplicitParam(name = "id", value = "id")
    public ResultBody deleteReceipt(@RequestBody Map map) {
        try {
            return receiptServiceImpl.deleteReceipt(map);
        } catch (Exception e) {
            return ResultBody.success(false);
        }
    }

    @Log("执行付款单审批数据验证")
    @ApiOperation(value = "执行付款单审批数据验证")
    @PostMapping("/updateReceiptMyTrade")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody updateReceiptMyTrade(@RequestBody Map map) {
        /*付款审批更新明源数据*/
        receiptServiceImpl.updateReceiptMyTrade(map);
        return receiptServiceImpl.getMyStatus(map);
    }

    @Log("佣金付款单审批流数据")
    @ApiOperation(value = "佣金付款单审批流数据")
    @PostMapping("/selectInvoiceApplication")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody selectInvoiceApplication(@RequestBody Map map) {
        return receiptServiceImpl.selectInvoiceApplication(map);
    }



    @Log("佣金付款单审批流申请")
    @ApiOperation(value = "佣金付款单审批流申请")
    @PostMapping("/initInvoiceApplication")
    @ApiImplicitParam(name = "map", value = "map")
    public ResultBody initInvoiceApplication(@RequestBody Map map) {
        /*付款审批更新明源数据*/
        ResultBody  result = receiptServiceImpl.getMyStatus(map);
        boolean b = Boolean.parseBoolean(result.getData()+"");
        if(b){
            return receiptServiceImpl.initInvoiceApplication(map);
        }else{
            return result;
        }
    }


}
