package cn.visolink.salesmanage.checklist.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.checklist.entity.Checklist;
import cn.visolink.salesmanage.checklist.service.ChecklistService;
import io.swagger.annotations.*;
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
 * 佣金核算单 前端控制器
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@RestController
@RequestMapping("checklist")
@Api(tags = "佣金核算单")
public class ChecklistController {

    private final ChecklistService checklistService;

    @Autowired
    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @Log("新增佣金核算单")
    @PostMapping(value = {"insertChecklist"})
    @ApiOperation(value = "新增佣金核算单")
    public ResultBody insertChecklist(Checklist checklist) {
        return checklistService.insertChecklist(checklist);
    }

    @Log("根据id，修改佣金核算单")
    @PostMapping(value = {"updateChecklistById"})
    @ApiOperation(value = "根据id，修改佣金核算单")
    @ApiImplicitParams({
    })
    public ResultBody update(Checklist checklist) {
        return checklistService.updateChecklistById(checklist);
    }

    @Log("删除佣金核算单")
    @PostMapping(value = {"deleteChecklist"})
    @ApiOperation(value = "删除佣金核算单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"ids\":\"核算单ids\"}", paramType = "body"),
    })
    public ResultBody deleteChecklist(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        return checklistService.deleteChecklist(map, request);
    }

    @Log("根据id，查询佣金核算单详情")
    @PostMapping(value = {"getChecklistById"})
    @ApiOperation(value = "根据id，查询佣金核算单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true)
    })
    public ResultBody getChecklistById(String id) {
        return checklistService.getChecklistById(id);
    }

    @Log("创建正核算单")
    @PostMapping(value = {"createChecklist"})
    @ApiOperation(value = "创建正核算单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"mainDataProjectId\":\"主数据项目id\",\"division\":\"事业部\",\"cityCompany\":\"城市公司\",\"projectName\":\"项目名称\",\"commissionIds\":\"待结佣ids\",\"policyIds\":\"政策ids\",\"dealType\":\"成交类型\",\"checklistName\":\"核算单名称\"}", paramType = "body"),
    })
    public ResultBody createChecklist(@RequestBody Map<String, String> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.createChecklist(map, request);
    }

    @Log("分页查询，佣金核算单")
    @PostMapping(value = {"getChecklistListPage"})
    @ApiOperation(value = "分页查询，佣金核算单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"currentPage\":\"页码，从1开始，默认值：1\",\"pageSize\":\"每页条数，默认值：10\",\"keyWord\":\"关键字\",\"keyWordType\":\"关键字类型\",\"projectStatus\":\"立项状态\",\"isSettle\":\"是否结清：全部、是、否\",\"startTime\":\"开始时间\",\"endTime\":\"结束时间\",\"projectId\":\"项目id\",\"dealType\":\"成交类型\",\"checklistCode\":\"核算单编号\",\"channelName\":\"渠道名称\",\"isFather\":\"是否正核算单：0 负核算单、1 正核算单\",\"isAbnormal\":\"是否异常：全部、是、否\",\"orgLevel\":\"组织层级\",\"orgId\":\"组织id\",\"orderBy\":\"排序字段\",\"orderType\":\"排序类型\"}", paramType = "body"),
    })
    public ResultBody getChecklistListPage(@RequestBody Map<String, String> map) {
        // 调用服务
        return checklistService.getChecklistListPage(map);
    }

    @Log("分页查询，佣金核算单明细")
    @PostMapping(value = {"getChecklistDetailListPage"})
    @ApiOperation(value = "分页查询，佣金核算单明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"currentPage\":\"页码，从1开始，默认值：1\",\"pageSize\":\"每页条数，默认值：10\",\"checklistId\":\"核算单id\",\"keyWordType\":\"关键字类型\",\"keyWord\":\"关键字\",\"keyWordType2\":\"关键字类型2\",\"startKeyWord\":\"开始关键字2\",\"endKeyWord\":\"结束关键字2\",\"startSubscribeTime\":\"开始认购时间\",\"endSubscribeTime\":\"结束认购时间\",\"startSignTime\":\"开始签约时间\",\"endSignTime\":\"结束签约时间\",\"startProjectAmount\":\"开始立项金额\",\"endProjectAmount\":\"结束立项金额\",\"isSettle\":\"是否结清：全部、是、否\",\"orderBy\":\"排序字段\",\"orderType\":\"排序类型\"}", paramType = "body"),
    })
    public ResultBody getChecklistDetailListPage(@RequestBody Map<String, String> map) {
        // 调用服务
        return checklistService.getChecklistDetailListPage(map);
    }

    @Log("分页查询，佣金核算单-政策")
    @PostMapping(value = {"getChecklistPolicyListPage"})
    @ApiOperation(value = "分页查询，佣金核算单-政策")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"currentPage\":\"页码，从1开始，默认值：1\",\"pageSize\":\"每页条数，默认值：10\",\"checklistId\":\"核算单id\"}", paramType = "body"),
    })
    public ResultBody getChecklistPolicyListPage(@RequestBody Map<String, String> map) {
        // 调用服务
        return checklistService.getChecklistPolicyListPage(map);
    }

    @Log("关联核算单")
    @PostMapping(value = {"relatedChecklist"})
    @ApiOperation(value = "关联核算单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistIds\":\"核算单id\",\"commissionIds\":\"待结佣ids\"}", paramType = "body"),
    })
    public ResultBody relatedChecklist(@RequestBody Map<String, String> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.relatedChecklist(map, request);
    }

    @Log("移除核算单明细")
    @PostMapping(value = {"removeChecklistDetail"})
    @ApiOperation(value = "移除核算单明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistDetailId\":\"核算单明细id\"}", paramType = "body"),
    })
    public ResultBody removeChecklistDetail(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.removeChecklistDetail(map, request);
    }

    @Log("修改佣金金额")
    @PostMapping(value = {"updateProjectAmount"})
    @ApiOperation(value = "修改佣金立项金额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"id\":\"核算单明细id\",\"projectAmount\":\"佣金立项金额\",\"commissionPoint\":\"佣金点位\"}", paramType = "body"),
    })
    public ResultBody updateProjectAmount(@RequestBody Map<String, String> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.updateProjectAmount(map, request);
    }

    @Log("批量修改佣金立项金额")
    @PostMapping(value = {"updateBatchProjectAmount"})
    @ApiOperation(value = "批量修改佣金立项金额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "list", value = "参数列表,例：[{\"id\":\"核算单明细id\",\"projectAmount\":\"佣金立项金额\",\"commissionPoint\":\"佣金点位\"}]", paramType = "body"),
    })
    public ResultBody updateBatchProjectAmount(@RequestBody List<Map<String, String>> list, HttpServletRequest request) {
        // 调用服务
        return checklistService.updateBatchProjectAmount(list, request);
    }

    @Log("关联政策")
    @PostMapping(value = {"relatedPolicy"})
    @ApiOperation(value = "关联政策")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\",\"policyIds\":\"政策ids\"}", paramType = "body"),
    })
    public ResultBody relatedPolicy(@RequestBody Map<String, String> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.relatedPolicy(map, request);
    }

    @Log("移除政策")
    @PostMapping(value = {"removePolicy"})
    @ApiOperation(value = "移除政策")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\",\"policyIds\":\"政策ids\"}", paramType = "body"),
    })
    public ResultBody removePolicy(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.removePolicy(map, request);
    }

    @Log("导出核算单")
    @GetMapping(value = {"exportChecklist"})
    @ApiOperation(value = "导出核算单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "核算单id，多个用英文逗号拼接"),
    })
    public void exportChecklist(String ids, HttpServletRequest request, HttpServletResponse response) {
        // 调用服务
        checklistService.exportChecklist(ids, request, response);
    }

    @Log("导出核算单明细")
    @GetMapping(value = {"exportChecklistDetail"})
    @ApiOperation(value = "导出核算单明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "核算单明细id，多个用英文逗号拼接"),
    })
    public void exportChecklistDetail(String ids, HttpServletRequest request, HttpServletResponse response) {
        // 调用服务
        checklistService.exportChecklistDetail(ids, request, response);
    }

    @Log("导出全部")
    @GetMapping(value = {"exportAll"})
    @ApiOperation(value = "导出全部")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyWord", value = "关键字"),
            @ApiImplicitParam(name = "keyWordType", value = "关键字类型"),
            @ApiImplicitParam(name = "projectStatus", value = "立项状态"),
            @ApiImplicitParam(name = "isSettle", value = "是否结清：全部、是、否"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "projectId", value = "项目id"),
            @ApiImplicitParam(name = "dealType", value = "成交类型"),
            @ApiImplicitParam(name = "checklistCode", value = "核算单编号"),
            @ApiImplicitParam(name = "channelName", value = "渠道名称"),
            @ApiImplicitParam(name = "isFather", value = "是否正核算单：0 负核算单、1 正核算单"),
            @ApiImplicitParam(name = "isAbnormal", value = "是否异常：全部、是、否"),
            @ApiImplicitParam(name = "orgLevel", value = "组织层级"),
            @ApiImplicitParam(name = "orgId", value = "组织id"),
            @ApiImplicitParam(name = "orderBy", value = "排序字段"),
            @ApiImplicitParam(name = "orderType", value = "排序类型"),
    })
    public void exportAll(String keyWord, String keyWordType, String projectStatus, String isSettle, String startTime, String endTime,
                          String cityCompany, String projectId, String dealType, String checklistCode, String channelName, String isFather,
                          String isAbnormal, String orgLevel, String orgId, String orderBy, String orderType, HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, String> map = new HashMap<>(24);
        map.put("keyWord", keyWord);
        map.put("keyWordType", keyWordType);
        map.put("projectStatus", projectStatus);
        map.put("isSettle", isSettle);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("cityCompany", cityCompany);
        map.put("projectId", projectId);
        map.put("dealType", dealType);
        map.put("checklistCode", checklistCode);
        map.put("channelName", channelName);
        map.put("isFather", isFather);
        map.put("orgLevel", orgLevel);
        map.put("orgId", orgId);
        map.put("isAbnormal", isAbnormal);
        map.put("orderBy", orderBy);
        map.put("orderType", orderType);
        // 调用服务
        checklistService.exportAll(map, request, response);
    }

    @Log("导出全部明细")
    @GetMapping(value = {"exportAllDetail"})
    @ApiOperation(value = "导出全部明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checklistId", value = "核算单id"),
            @ApiImplicitParam(name = "keyWordType", value = "关键字类型"),
            @ApiImplicitParam(name = "keyWord", value = "关键字"),
            @ApiImplicitParam(name = "keyWordType2", value = "关键字类型2"),
            @ApiImplicitParam(name = "startKeyWord", value = "开始关键字2"),
            @ApiImplicitParam(name = "endKeyWord", value = "结束关键字2"),
            @ApiImplicitParam(name = "startSubscribeTime", value = "开始认购时间"),
            @ApiImplicitParam(name = "endSubscribeTime", value = "结束认购时间"),
            @ApiImplicitParam(name = "startSignTime", value = "开始签约时间"),
            @ApiImplicitParam(name = "endSignTime", value = "结束签约时间"),
            @ApiImplicitParam(name = "startProjectAmount", value = "开始立项金额"),
            @ApiImplicitParam(name = "endProjectAmount", value = "结束立项金额"),
            @ApiImplicitParam(name = "isSettle", value = "是否结清：全部、是、否"),
            @ApiImplicitParam(name = "orderBy", value = "排序字段"),
            @ApiImplicitParam(name = "orderType", value = "排序类型"),
    })
    public void exportAllDetail(String checklistId, String keyWordType, String keyWord, String keyWordType2, String startKeyWord, String endKeyWord,
                                String startSubscribeTime, String endSubscribeTime, String startSignTime, String endSignTime,
                                String startProjectAmount, String endProjectAmount, String isSettle, String orderBy, String orderType,
                                HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, String> map = new HashMap<>(26);
        map.put("checklistId", checklistId);
        map.put("keyWordType", keyWordType);
        map.put("keyWord", keyWord);
        map.put("keyWordType2", keyWordType2);
        map.put("startKeyWord", startKeyWord);
        map.put("endKeyWord", endKeyWord);
        map.put("startSubscribeTime", startSubscribeTime);
        map.put("endSubscribeTime", endSubscribeTime);
        map.put("startSignTime", startSignTime);
        map.put("endSignTime", endSignTime);
        map.put("startProjectAmount", startProjectAmount);
        map.put("endProjectAmount", endProjectAmount);
        map.put("isSettle", isSettle);
        map.put("orderBy", orderBy);
        map.put("orderType", orderType);
        // 调用服务
        checklistService.exportAllDetail(map, request, response);
    }

    @Log("导入核算单明细")
    @PostMapping(value = {"importChecklistDetail"})
    @ApiOperation(value = "导入核算单明细")
    public ResultBody importChecklistDetail(@ApiParam(value = "文件") MultipartFile file, @ApiParam(value = "成交类型") String dealType,
                                            @ApiParam(value = "计算类型") String calculationType) {
        // 调用服务
        return checklistService.importChecklistDetail(file, dealType, calculationType);
    }

    @Log("核算单审批")
    @PostMapping(value = {"checklistApprove"})
    @ApiOperation(value = "核算单审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\"}", paramType = "body"),
    })
    public ResultBody checklistApprove(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.checklistApprove(map, request);
    }

    @Log("核算单审批回调接口")
    @PostMapping(value = {"approvalCallbackInterface"})
    @ApiOperation(value = "核算单审批回调接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"requestInfo\":{\"data\":{\"checklistId\":\"核算单id\",\"projectStatus\":\"立项状态：3 已立项、4 立项通过、5 立项驳回、6 立项撤销\",\"projectCode\":\"立项编号\",\"projectTime\":\"立项时间，格式：2020-06-03 11:30:00\"}}}", paramType = "body"),
    })
    public Map approvalCallbackInterface(@RequestBody Map<String, Object> map) {
        String requstTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        try {
            // 参数校验
            if (CollUtil.isEmpty(map)) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，参数错误");
            }
            Map<String, Object> requestInfoMap = (Map) map.get("requestInfo");
            Map<String, Object> dataMap = (Map) requestInfoMap.get("data");

            // 参数校验
            if (dataMap.get("checklistId") == null || StrUtil.isBlank(dataMap.get("checklistId").toString())) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，核算单id 必填");
            }
            if (dataMap.get("projectStatus") == null || StrUtil.isBlank(dataMap.get("projectStatus").toString())) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，立项状态必填");
            }
            if (!StrUtil.equals("3", dataMap.get("projectStatus").toString())
                    && !StrUtil.equals("4", dataMap.get("projectStatus").toString())
                    && !StrUtil.equals("5", dataMap.get("projectStatus").toString())
                    && !StrUtil.equals("6", dataMap.get("projectStatus").toString())
                    && !StrUtil.equals("7", dataMap.get("projectStatus").toString())) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，立项状态需为：3、4、5、6、7中的一种");
            }
            if (StrUtil.equals("3", dataMap.get("projectStatus").toString()) && (dataMap.get("projectCode") == null || StrUtil.isBlank(dataMap.get("projectCode").toString()))) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，立项状态为3时，立项编号必填");
            }
            if (StrUtil.equals("3", dataMap.get("projectStatus").toString()) && (dataMap.get("projectTime") == null || StrUtil.isBlank(dataMap.get("projectTime").toString()))) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，立项状态为3时，立项时间必填");
            }
            try {
                DateUtil.parse(dataMap.get("projectTime").toString());
            } catch (Exception e) {
                e.printStackTrace();
                return ResultUtil.getErrorMap(requstTime, "调用失败，立项时间格式有误。示例：2020-06-03 11:30:00");
            }

            // 调用服务
            return checklistService.approvalCallbackInterface(dataMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.getErrorMap(requstTime, "调用失败");
        }
    }

    @Log("查询负核算单信息")
    @GetMapping(value = {"getNegativeChecklist"})
    @ApiOperation(value = "查询负核算单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checklistId", value = "核算单id"),
    })
    public ResultBody getNegativeChecklist(String checklistId) {
        // 调用服务
        return checklistService.getNegativeChecklist(checklistId);
    }

    @Log("创建负核算单")
    @PostMapping(value = {"createNegativeChecklist"})
    @ApiOperation(value = "创建负核算单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\",\"checklistDetailIds\":\"核算单明细ids\",\"checklistName\":\"核算单名称\"}", paramType = "body"),
    })
    public ResultBody createNegativeChecklist(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        // 调用服务
        return checklistService.createNegativeChecklist(map, request);
    }

    @Log("获取渠道名称")
    @PostMapping(value = {"getChannelName"})
    @ApiOperation(value = "获取渠道名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"orgLevel\":\"组织层级\",\"orgId\":\"组织id\"}", paramType = "body"),
    })
    public ResultBody getChannelName(@RequestBody Map<String, Object> map) {
        // 调用服务
        return checklistService.getChannelName(map);
    }

    @Log("核算单撤回")
    @PostMapping(value = {"checklistWithdraw"})
    @ApiOperation(value = "核算单撤回")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\"}", paramType = "body"),
    })
    public ResultBody checklistWithdraw(@RequestBody Map<String, Object> map) {
        // 调用服务
        return checklistService.checklistWithdraw(map);
    }

    @Log("核算单欠款校验")
    @PostMapping(value = {"checklistArrearCheck"})
    @ApiOperation(value = "核算单欠款校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\"}", paramType = "body"),
    })
    public ResultBody checklistArrearsCheck(@RequestBody Map<String, Object> map) {
        // 调用服务
        return checklistService.checklistArrearsCheck(map);
    }

    @Log("修改核算单名称")
    @PostMapping(value = {"updateChecklistName"})
    @ApiOperation(value = "修改核算单名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\",\"checklistName\":\"核算单名称\"}", paramType = "body"),
    })
    public ResultBody updateChecklistName(@RequestBody Map<String, String> map) {
        // 调用服务
        return checklistService.updateChecklistName(map);
    }

    @Log("修改结佣形式")
    @PostMapping(value = {"updateCommissionType"})
    @ApiOperation(value = "修改结佣形式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"checklistId\":\"核算单id\",\"commissionType\":\"结佣形式\"}", paramType = "body"),
    })
    public ResultBody updateCommissionType(@RequestBody Map<String, String> map) {
        // 调用服务
        return checklistService.updateCommissionType(map);
    }

    @Log("测试")
    @PostMapping(value = {"test"})
    @ApiOperation(value = "测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"number\":\"个数\"}", paramType = "body"),
    })
    public ResultBody test(@RequestBody Map<String, Object> map) {
        // 调用服务
        return checklistService.test(map);
    }
}
