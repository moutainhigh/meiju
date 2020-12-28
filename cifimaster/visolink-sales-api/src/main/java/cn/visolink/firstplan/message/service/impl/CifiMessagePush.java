package cn.visolink.firstplan.message.service.impl;

import cn.visolink.firstplan.message.pojo.ResultVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class CifiMessagePush {
    //旭辉邮箱服务器地址
    @Value("${emailServer.messageHost}")
    private String messageHost;

    /**
     * 发送Email地址
     * https://service.cifi.com.cn/messaging/push/email/default
     */
    private final String emailUrl = "/messaging/push/email/blank";

    /**
     * 发送钉钉消息地址
     */
    private final String dingdingMsgUrl = "/messaging/push/ding_user/214438503";

    public String getMessageHost() {
        return messageHost;
    }

    public void setMessageHost(String messageHost) {
        this.messageHost = messageHost;
    }

    /**
     * 向指定收件人，发送邮件信息
     * @param emails 收件人列表，多个收件人，以|拆分
     * @param title 标题
     * @param content 邮件内容
     * @return
     */
    public ResultVO sendEmail(String emails, String title, String content){
        String variable = title  + " |" + content;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("destination", emails);
        params.add("variables", variable);

        return CifiMessagePush.sendPostRequest(messageHost + emailUrl,params);
    }
    /**
     * 向指定收件人，发送邮件信息，并返回发送结果说明，返回success表示成功
     * @param emails 收件人列表，多个收件人，以|拆分
     * @param title 标题
     * @param content 邮件内容
     * @return 发送结果说明，返回success表示成功
     */
    public String sendEmailAndGetResult(String emails, String title, String content){
        ResultVO resultVO = sendEmail(emails,title,content);
        return resultVO.getRetMsg();
    }

    public ResultVO sendDingDingMsg(String phones,String title,String markDownMsg){
        String variable = "**" +  title  + "**\n " + markDownMsg;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("destination",phones);
        params.add("variables", variable);

        return CifiMessagePush.sendPostRequest(messageHost + dingdingMsgUrl,params);
    }

    /**
     * 向目的URL发送post请求
     * @param url       目的url
     * @param params    发送的参数
     * @return  ResultVO
     */
    private static ResultVO sendPostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以html的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<ResultVO> response = client.exchange(url, method, requestEntity, ResultVO.class);
        ResultVO body = response.getBody();
        System.out.println(body);
        return response.getBody();
    }
}
