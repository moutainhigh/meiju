package cn.visolink.firstplan.message.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.service.MessageManagerService;
import cn.visolink.logs.aop.log.Log;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/5/26 7:21 下午
 */
@RestController
@Api(tags = "消息管理接口API")
@Slf4j
@RequestMapping("/messageManager")
public class MessageManagerController {

    @Autowired
    private MessageManagerService messageManagerService;


    @Log("消息管理_消息模版列表")
    @ApiOperation(value = "消息管理-消息模版列表数据查询")
    @PostMapping("/queryMessageTemplateList")
    public ResultBody queryTemplateList(@RequestBody Map paramMap) {
       return messageManagerService.queryTemplateList(paramMap);
    }

    @Log("消息管理_设置岗位组")
    @ApiOperation(value = "消息管理-设置岗位组")
    @PostMapping("/setPositionSending")
    public ResultBody setPositionSending(@RequestBody Map paramMap) {
        return messageManagerService.setPositionSending(paramMap);
    }
    @Log("消息管理_保存配置的岗位组")
    @ApiOperation(value = "消息管理-消息管理_保存配置的岗位组")
    @PostMapping("/setPositionSave")
    public ResultBody savePositionSending(@RequestBody Map paramMap) {
        return messageManagerService.setPositionSave(paramMap);
    }
    @Log("标签管理-查询标签列表")
    @ApiOperation(value = "标签管理_查询标签列表")
    @PostMapping("/queryLabelList")
    public ResultBody queryLabelList(@RequestBody Map paramMap) {
        return messageManagerService.queryLabelList(paramMap);
    }
    @Log("标签管理-添加/修改标签")
    @ApiOperation(value = "标签管理-添加/修改标签")
    @PostMapping("/saveLabel")
    public ResultBody saveLabel(@RequestBody Map paramMap) {
        return messageManagerService.saveLabel(paramMap);
    }
    @Log("标签管理-删除标签")
    @ApiOperation(value = "标签管理-删除标签")
    @PostMapping("/deleteLabel")
    public ResultBody deleteLabel(@RequestBody Map paramMap) {
        String id=paramMap.get("id")+"";
        return messageManagerService.deleteLabel(id);
    }


    @Log("标签管理-添加消息模版查询标签库")
    @ApiOperation(value = "标签管理-添加/修改标签")
    @PostMapping("/getLabelLibraryData")
    public ResultBody getLabelLibraryData(@RequestBody Map paramMap) {
        return messageManagerService.getLabelLibraryData(paramMap);
    }
    @Log("模版管理-更新模版")
    @ApiOperation(value = "模版管理-添加/修改模版")
    @PostMapping("/addMessageTemplate")
    public ResultBody addTemplate(@RequestBody Map paramMap, HttpServletRequest request) {
        //获取当前登陆的用户名称
        String username = request.getHeader("username");
        paramMap.put("username",username);
        return messageManagerService.addMessageTemplate(paramMap);
    }

    @Log("模版管理-查看模版详情")
    @ApiOperation(value = "模版管理-查看模版详情")
    @PostMapping("/getTemplateInfo")
    public ResultBody getTemplateInfo(@RequestBody Map paramMap) {
        return messageManagerService.getTemplateInfo(paramMap);
    }

    @Log("模版管理-查询可选模版业务模块")
    @ApiOperation(value = "模版管理-查询可选模版业务模块")
    @PostMapping("/getTypeList")
    public ResultBody getTypeList(@RequestBody Map paramMap) {
        return messageManagerService.getTypeList(paramMap);
    }
    @Log("预览消息详情")
    @ApiOperation(value = "预览消息详情")
    @PostMapping("/getMessageInfoById")
    public ResultBody getMessageInfoById(@RequestBody Map paramMap) {
        return messageManagerService.getMessageInfoById(paramMap);
    }
    @Log("查询消息列表")
    @ApiOperation(value = "查询消息列表")
    @PostMapping("/queryMessageList")
    public ResultBody queryMessageList(@RequestBody Map paramMap) {
        return messageManagerService.queryMessageList(paramMap);
    }
    @Log("查看消息详情")
    @ApiOperation(value = "查看消息详情")
    @GetMapping("/queryMessageInfo")
    public ResultBody queryMessageInfo(@Param("id") String id) {
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("id",id);
        return messageManagerService.queryMessageInfo(paramMap);
    }
    @Log("删除消息")
    @ApiOperation(value = "删除消息")
    @PostMapping("/deleteMessageById")
    public ResultBody queryMessageInfo(@RequestBody Map map) {
        return messageManagerService.deleteMessageById(map);
    }
    @Log("修改消息")
    @ApiOperation(value = "修改消息")
    @PostMapping("/updateMessageInfo")
    public ResultBody updateMessageInfo(@RequestBody Map map) {
        return messageManagerService.updateMessageInfo(map);
    }

    @Log("群聊管理-查询群聊列表")
    @ApiOperation(value = "群聊管理-查询群聊列表")
    @PostMapping("/queryBusinessGroupChat")
    public ResultBody queryBusinessGroupChat(@RequestBody Map map) {
        return messageManagerService.queryBusinessGroupChat(map);
    }

    @Log("群聊管理-删除群聊")
    @ApiOperation(value = "群聊管理-删除群聊")
    @PostMapping("/deleteBusinessGroupChat")
    public ResultBody deleteBusinessGroupChat(@RequestBody Map map) {
        return messageManagerService.deleteBusinessGroupChat(map);
    }
    @Log("群聊管理-查询详情")
    @ApiOperation(value = "群聊管理-查询详情")
    @PostMapping("/queryBuinessGroupChatInfo")
    public ResultBody queryBuinessGroupChatInfo(@RequestBody Map map) {
        return messageManagerService.queryBuinessGroupChatInfo(map);
    }
    @Log("群聊管理-更新群聊信息")
    @ApiOperation(value = "群聊管理-更新群聊信息")
    @PostMapping("/addBusinessGroupChat")
    public ResultBody addBusinessGroupChat(@RequestBody Map map) {
        return messageManagerService.addBusinessGroupChat(map);
    }
    @Log("获取对应消息即将发送的人员列表")
    @ApiOperation(value = "获取对应消息即将发送的人员列表")
    @PostMapping("/queryUserList")
    public ResultBody queryUserList(@RequestBody Map map) {
        return messageManagerService.queryUserList(map);
    }

    @Log("初始化消息待发送人员列表")
    @ApiOperation(value = "初始化消息待发送人员列表")
    @PostMapping("/initMessageSendUsers")
    public ResultBody initMessageSendUsers(@RequestBody Map map) {
        return messageManagerService.initMessageSendUsers(map);
    }


}
