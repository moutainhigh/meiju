package cn.visolink.firstplan.skipnodeupload.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.planmonitoring.service.PlanMontitorService;
import cn.visolink.firstplan.skipnodeupload.service.SkipNodeUploadFileService;
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
 * @Created date 2020/6/28 3:32 下午
 */
@RestController
@Api(tags = "略过节点补录附件")
@RequestMapping("/skipNodeUploadFile")
public class SkipNodeUploadFileController {

    @Autowired
    private SkipNodeUploadFileService skipNodeUploadFileService;

    @ApiOperation(value = "查询补录附件列表")
    @PostMapping("/getRepairFileList")
    public ResultBody getRepairFileList(@RequestBody Map map){
        return skipNodeUploadFileService.getRepairFileList(map);
    }

    @ApiOperation(value = "略过节点补录附件-保存/提交审批")
    @PostMapping("/saveRepairFile")
    public ResultBody saveRepairFile(@RequestBody Map map,HttpServletRequest request){
        return skipNodeUploadFileService.saveRepairFile(map,request);
    }

    @ApiOperation(value = "略过节点补录附件-保存/提交审批")
    @PostMapping("/applayCallback")
    public ResultBody applayCallback(@RequestBody Map map,HttpServletRequest request){
        return skipNodeUploadFileService.saveRepairFile(map,request);
    }
}
