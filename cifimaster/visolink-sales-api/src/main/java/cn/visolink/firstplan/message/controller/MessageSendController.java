package cn.visolink.firstplan.message.controller;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.service.MessageSendService;
import cn.visolink.logs.aop.log.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/5/26 9:49 下午
 */
@RestController
@Api(tags = "消息发送服务接口API")
@Slf4j
@RequestMapping("/messageSend")
public class MessageSendController {
    @Autowired
    private MessageSendService messageSendService;

  /*  @Log("钉钉群机器人发送消息")
    @ApiOperation(value = "钉钉群机器人发送消息")
    @PostMapping("/dingTalkSend")
    public ResultBody dingTalkSend(@RequestBody Map paramMap) {
        return messageSendService.dingTalkSend(paramMap);
    }*/
    @Log("发送邮箱信息")
    @ApiOperation(value = "发送邮箱信息")
    @PostMapping("/emaillSend")
    public ResultBody emaillSend(@RequestBody Map paramMap) {
       return messageSendService.emaillSend(paramMap);
    }
    @Log("发送qq邮箱信息")
    @ApiOperation(value = "发送qq邮箱信息")
    @PostMapping("/QQEmailSend")
    public ResultBody qqEmailSend(@RequestBody Map paramMap)  {
        try {
            return messageSendService.qqEmailSend(paramMap);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1089,"邮箱发送失败:"+e.getCause());
        }
    }

    @Log("手动发送服务")
    @ApiOperation(value = "手动发送服务")
    @PostMapping("/manualSendMessageServer")
    public ResultBody manualSendMessageServer(@RequestBody Map paramMap, HttpServletRequest request)  {
        try {
            return messageSendService.manualSendMessageServer(paramMap,request);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1089,"信息发送失败:"+e.getCause());
        }
    }
    @Log("每十分钟自动发送服务")
    @ApiOperation(value = "每十分钟自动发送服务")
    @PostMapping("/sendMessageServer")
   // @Scheduled(cron = "0 0/30 * * * ?	")
    public ResultBody sendMessageServer()  {
            return messageSendService.SendMessageServer(null);
    }

    @Log("钉钉发送工作通知")
    @ApiOperation(value = "钉钉发送工作通知")
    @PostMapping("/dingtalkSendWorkNotice")
    public ResultBody dingtalkSendWorkNotice(@RequestBody Map map)  {
        return messageSendService.dingtalkSendWorkNotice(map);
    }

}
