package cn.visolink.utils;

import cn.hutool.http.HttpUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.dao.MessageSendDao;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/22 3:13 下午
 * 钉钉机器人发送工具类
 */
@Component
@Data
public class DingDingRobotUtil {
    //钉钉机器人地址
    @Value("${dingTalkRobot.url}")
    private String dingTalkRobotUrl;

    //钉钉群聊机器人密文
    @Value("${dingTalkRobot.secret}")
    private String secret;
    //查看详情跳转页面
    @Value("${DingDing.messageInfo}")
    private   String messageInfoUrl;

    @Value("${DingDing.speedLogoUrl}")
    private   String speedLogoUrl;

    @Value("${DingDing.warningLogoUrl}")
    private   String warningLogoUrl;

    //定调价预警图片
    @Value("${DingDing.pricIngLogoUrl}")
    private   String pricIngLogoUrl;


    @Autowired
    private MessageSendDao messageSendDao;

    public ResultBody dingTalkRobotSend(Map paramMap) {
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            System.out.println(sign);

            //是否通知所有人
            boolean isAtAll = true;
            //通知具体人的手机号码列表
            //List<String> mobileList = (List<String>) paramMap.get("mobileList");
            String message_title=paramMap.get("message_title")+"";
            String content=message_title;
            String message_id=paramMap.get("id")+"";
            //组装请求内容
            String reqStr = buildReqStr(content, isAtAll, null,message_id);

            //封装请求钉钉群聊机器人的地址
            String url=dingTalkRobotUrl+"&timestamp="+timestamp+"&sign="+sign;
            System.out.println("地址==="+url);
            //推送消息（http请求）
            String result = HttpUtil.post(url,reqStr);
            Map resultMap = JSON.parseObject(result, Map.class);
            if(resultMap!=null){
                String mesg=resultMap.get("errmsg")+"";
                if("ok".equalsIgnoreCase(mesg)){
                    resultMap.put("message","发送成功,请至群聊查看");
                    //发送成功将消息设置为已发送状态
                    String id=paramMap.get("id")+"";
                    messageSendDao.updateMessageSendStatus(id);
                    return ResultBody.success(resultMap);
                }
                System.out.println(mesg);
                return ResultBody.error(-1001,"群消息,发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1001,"群消息,发送失败");
        }
        return null;
    }
    private  String buildReqStr(String content,boolean isAtAll, List<String> mobileList,String message_id) {
        //消息内容
        Map<String, String> contentMap = Maps.newHashMap();
       /* List<String> atMobelList = new ArrayList<>();
        if(mobileList!=null&&mobileList.size()>0){
            for (String mobel : mobileList) {
                atMobelList.add("@"+mobel);
            }
        }*/
        contentMap.put("title",content);
        //文本中通知的手机号字符串
       /* String mobelStr="";
        if(atMobelList!=null&&atMobelList.size()>0){
            for (String mobel : atMobelList) {
                mobelStr+=mobel;
            }
        }*/
       // mobelStr.replaceAll("null","");
        //System.err.println(mobelStr);
       // System.err.println(atMobelList.toString());
        //查询当前即将发送的消息所属的预警类型
        String modelType = messageSendDao.getMessageModelType(message_id);
        String modelTypeArray1[]={"三大件延期提醒","九大节点延期提醒","首开前3月","首开前2月","首开前21天","首开前7天","首开当日播报"};
        List<String> asList = Arrays.asList(modelTypeArray1);
        String logoUrl="";
        if(asList.contains(modelType)){
            logoUrl=speedLogoUrl;
        }else if("定调价预警".equals(modelType)){
            logoUrl=pricIngLogoUrl;
        }else{
            logoUrl=warningLogoUrl;
        }
        contentMap.put("text","**"+content+"**"+"@所有人"+
                "!["+UUID.randomUUID().toString()+"]("+logoUrl+")");

        contentMap.put("singleTitle","查看详情");
        contentMap.put("singleURL",messageInfoUrl+"?id="+message_id);
        contentMap.put("hideAvatar","0");
        contentMap.put("btnOrientation","0");

        //通知人
        Map<String, Object> atMap = Maps.newHashMap();
        //1.是否通知所有人
        atMap.put("isAtAll", isAtAll);
        //2.通知具体人的手机号码列表
        //atMap.put("atMobiles", mobileList);

        Map<String, Object> reqMap = Maps.newHashMap();
        reqMap.put("msgtype", "actionCard");
        reqMap.put("actionCard",contentMap);
        reqMap.put("at", atMap);

        System.err.println(JSON.toJSONString(reqMap));
        return JSON.toJSONString(reqMap);
    }


}
