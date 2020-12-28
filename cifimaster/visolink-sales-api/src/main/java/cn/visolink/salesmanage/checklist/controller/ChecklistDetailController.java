package cn.visolink.salesmanage.checklist.controller;


import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.salesmanage.checklist.entity.ChecklistDetail;
import cn.visolink.salesmanage.checklist.service.ChecklistDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 佣金核算单明细 前端控制器
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@RestController
@RequestMapping("checklistDetail")
@Api(tags = "佣金核算单明细")
public class ChecklistDetailController {

    private final ChecklistDetailService checklistDetailService;

    @Autowired
    public ChecklistDetailController(ChecklistDetailService checklistDetailService) {
        this.checklistDetailService = checklistDetailService;
    }

    @Log("新增佣金核算单明细")
    @PostMapping(value = {"insertChecklistDetail"})
    @ApiOperation(value = "新增佣金核算单明细")
    public ResultBody insertChecklistDetail(ChecklistDetail checklistDetail) {
        return checklistDetailService.insertChecklistDetail(checklistDetail);
    }

    @Log("根据id，修改佣金核算单明细")
    @PostMapping(value = {"updateChecklistDetailById"})
    @ApiOperation(value = "根据id，修改佣金核算单明细")
    @ApiImplicitParams({
    })
    public ResultBody update(ChecklistDetail checklistDetail) {
        return checklistDetailService.updateChecklistDetailById(checklistDetail);
    }

    @Log("删除佣金核算单明细")
    @PostMapping(value = {"deleteChecklistDetail"})
    @ApiOperation(value = "删除佣金核算单明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "主键集合,多个用英文逗号拼接", required = true)
    })
    public ResultBody deleteChecklistDetail(@RequestParam("ids") String ids) {
        return checklistDetailService.deleteChecklistDetail(ids);
    }

    @Log("根据id，查询佣金核算单明细详情")
    @PostMapping(value = {"getChecklistDetailById"})
    @ApiOperation(value = "根据id，查询佣金核算单明细详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true)
    })
    public ResultBody getChecklistDetailById(String id) {
        return checklistDetailService.getChecklistDetailById(id);
    }

//    @Log("分页查询，佣金核算单明细")
//    @PostMapping(value = {"getChecklistDetailListPage"})
//    @ApiOperation(value = "分页查询，佣金核算单明细")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "map", value = "参数列表,例：{\"currentPage\":\"页码，从1开始，默认值：1\",\"pageSize\":\"每页条数，默认值：10\",\"checklistId\":\"核算单id\",\"keyWord\":\"关键字\"}", paramType = "body"),
//    })
//    public ResultBody getChecklistDetailListPage(@RequestBody Map<String, String> map) {
//        // 调用服务
//        return checklistDetailService.getChecklistDetailListPage(map);
//    }
}

