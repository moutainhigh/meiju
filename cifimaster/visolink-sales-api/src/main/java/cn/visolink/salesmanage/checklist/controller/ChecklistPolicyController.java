package cn.visolink.salesmanage.checklist.controller;


import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.checklist.entity.ChecklistPolicy;
import cn.visolink.salesmanage.checklist.service.ChecklistPolicyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 佣金核算单-政策 前端控制器
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@RestController
@RequestMapping("checklistPolicy")
@Api(tags = "佣金核算单-政策")
public class ChecklistPolicyController {

    private final ChecklistPolicyService checklistPolicyService;

    @Autowired
    public ChecklistPolicyController(ChecklistPolicyService checklistPolicyService) {
        this.checklistPolicyService = checklistPolicyService;
    }

    @Log("新增佣金核算单-政策")
    @PostMapping(value = {"insertChecklistPolicy"})
    @ApiOperation(value = "新增佣金核算单-政策")
    public ResultBody insertChecklistPolicy(ChecklistPolicy checklistPolicy) {
        return checklistPolicyService.insertChecklistPolicy(checklistPolicy);
    }

    @Log("根据id，修改佣金核算单-政策")
    @PostMapping(value = {"updateChecklistPolicyById"})
    @ApiOperation(value = "根据id，修改佣金核算单-政策")
    @ApiImplicitParams({
    })
    public ResultBody update(ChecklistPolicy checklistPolicy) {
        return checklistPolicyService.updateChecklistPolicyById(checklistPolicy);
    }

    @Log("删除佣金核算单-政策")
    @PostMapping(value = {"deleteChecklistPolicy"})
    @ApiOperation(value = "删除佣金核算单-政策")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "主键集合,多个用英文逗号拼接", required = true)
    })
    public ResultBody deleteChecklistPolicy(@RequestParam("ids") String ids) {
        return checklistPolicyService.deleteChecklistPolicy(ids);
    }

    @Log("根据id，查询佣金核算单-政策详情")
    @PostMapping(value = {"getChecklistPolicyById"})
    @ApiOperation(value = "根据id，查询佣金核算单-政策详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true)
    })
    public ResultBody getChecklistPolicyById(String id) {
        return checklistPolicyService.getChecklistPolicyById(id);
    }

    @Log("分页查询，佣金核算单-政策")
    @PostMapping(value = {"getChecklistPolicyListPage"})
    @ApiOperation(value = "分页查询，佣金核算单-政策")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"id\":\"主键\"}", paramType = "body"),
    })
    public ResultBody getChecklistPolicyListPage(@RequestBody Map<String, String> map) {
        // 调用服务
        return checklistPolicyService.getChecklistPolicyListPage(map);
    }
}

