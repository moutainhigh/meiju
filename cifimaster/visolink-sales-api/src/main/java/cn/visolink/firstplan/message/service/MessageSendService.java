package cn.visolink.firstplan.message.service;

import cn.visolink.exception.ResultBody;

import javax.mail.NoSuchProviderException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/5/26 9:46 下午
 * 消息发送服务
 */
public interface MessageSendService {
    /**
     * 邮箱发送服务
     */
    public ResultBody emaillSend(Map map);
    /**
     * qq邮箱发送服务-自测
     */
    public ResultBody qqEmailSend(Map map);

    /**
     * 消息发送统一分配服务
     */
    public ResultBody SendMessageServer(Map map);
    /**
     * 手动发送
     */
    public ResultBody manualSendMessageServer(Map map, HttpServletRequest request);
    /**
     * 钉钉发送工作通知服务
     */
    public ResultBody dingtalkSendWorkNotice(Map map);

}
