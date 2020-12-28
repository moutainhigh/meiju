package cn.visolink.utils;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.firstplan.message.dao.MessageSendDao;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author sjl
 * @Created date 2020/6/9 6:17 下午
 */
@Component
@Data
public class DingDingUtil {
    // 企业Id
    @Value("${DingDing.CorpId}")
    private  String corpId ;

    // 企业应用的凭证密钥
    @Value("${DingDing.appsecret}")
    private   String appsecret;

    // 钉钉获取token的接口地址
    @Value("${DingDing.accessTokenIp}")
    private   String accessTokenIp;

    // 企业应用应用授权码
    @Value("${DingDing.appkey}")
    private   String appkey;

    // 企业应用应用授权码
    @Value("${DingDing.userIdIp}")
    private   String userIdIp;

    // 企业应用应用授权码
    @Value("${DingDing.AgentId}")
    private   Long agentId;

    //查看详情跳转页面
    @Value("${DingDing.messageInfo}")
    private   String messageInfoUrl;

    @Value("${DingDing.speedLogoUrl}")
    private   String speedLogoUrl;

    @Value("${DingDing.warningLogoUrl}")
    private   String warningLogoUrl;

    @Value("${DingDing.firstBriefingLogoUrl}")
    private   String firstBriefingLogoUrl;
    //定调价预警图片
    @Value("${DingDing.pricIngLogoUrl}")
    private   String pricIngLogoUrl;
    //钉钉开盘简报地址
    @Value("${DingDing.firstBriefingMappUrl}")
    private   String firstBriefingMappUrl;

    @Autowired
    private MessageSendDao messageSendDao;

    @Autowired
    private RedisUtil redisUtil;
    public  String getToken(){
        try {
            //        String token = "";
            String token = redisUtil.get(VisolinkConstant.REDIS_KEY+".DingTalkToken")+"";
            if(StrUtil.isNotBlank(token) && !"null".equals(token)){
                return token;
            }
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("corpid",corpId);
            paramMap.put("corpsecret",appsecret);
            DefaultDingTalkClient client = new DefaultDingTalkClient(accessTokenIp);
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(appkey);
            request.setAppsecret(appsecret);
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            String errmsg = response.getErrmsg();
            if("ok".equals(errmsg)){
                String accessToken = response.getAccessToken();
                //返回token信息
                //保存token
                redisUtil.set(VisolinkConstant.REDIS_KEY+".DingTalkToken",accessToken,7200);
                //redisUtil.set("")
                return accessToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1005";
    }
   public String getUserInfo(String access_token,String mobile){
        try {
            //拼接用户id
            String userIds="";
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile?");
            OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
            String[] split = mobile.split(",");
            if(split!=null){
                for (String phone : split) {
                    request.setMobile(phone);
                    OapiUserGetByMobileResponse execute = client.execute(request, access_token);
                    String errmsg = execute.getErrmsg();
                    String userid = execute.getUserid();
                    if("ok".equals(errmsg)){
                        userIds+=userid+",";
                    }
                }
            }
            return userIds;
        }catch (Exception e){
            e.printStackTrace();
            return "-1008";
        }

   }

   public boolean sendDingTalkMessages(Map paramMap){
        try {
            //获取token
            String token = getToken();
            //获取要发送工作通知的手机号
            String mobile = paramMap.get("Mobile")+"";
            //获取用户id
            String userInfo = getUserInfo(token,mobile);
            //调用钉钉发送通知接口
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            if(!"".equals(userInfo)&&!"null".equals(userInfo)){
                userInfo=userInfo.substring(0,userInfo.lastIndexOf(","));
            }
            //指定用户群体（批量）
            request.setUseridList(userInfo);
            request.setAgentId(agentId);
            //是否通知所有人
            request.setToAllUser(false);
            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();


            String message_title=paramMap.get("message_title")+"";
            String message_info=paramMap.get("message_info")+"";
            msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
            msg.getActionCard().setTitle(message_title);
            //查询当前即将发送的消息所属的预警类型
            String modelType = messageSendDao.getMessageModelType(paramMap.get("id") + "");
            String modelTypeArray1[]={"首开前3月完成","首开前2月完成","首开前7天完成","首开前21天完成","首开当日播报","首开简报"};
            List<String> asList = Arrays.asList(modelTypeArray1);
            String logoUrl="";
            //详情跳转链接
            String infoLink=messageInfoUrl+"?id="+paramMap.get("id");

            //图片下方文字
            String text="";
            if(modelType.contains("逾期提醒")||modelType.contains("上传提醒")||modelType.contains("延期提醒")||modelType.contains("开放提醒")){
                logoUrl=speedLogoUrl;
            }else{
                if(asList.contains(modelType)){
                    logoUrl=speedLogoUrl;
                }else if("定调价预警提醒".equals(modelType)){
                    logoUrl=pricIngLogoUrl;
                }else if("开盘播报".equals(modelType)){
                    logoUrl=firstBriefingLogoUrl;
                    infoLink=firstBriefingMappUrl;
                    text=message_info;
                    if(text.contains("@")){
                        text=text.replaceAll("@", "\n\n");
                        System.err.println(text);
                    }

                }else {
                    logoUrl=warningLogoUrl;
                }
            }
            msg.getActionCard().setMarkdown("**"+message_title+"**\n\n!["+UUID.randomUUID().toString()+"]("+logoUrl+")"+text);
            msg.getActionCard().setSingleTitle("查看详情");
            System.err.println(logoUrl);
            msg.getActionCard().setSingleUrl(infoLink);
            msg.setMsgtype("action_card");
            request.setMsg(msg);
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request,token);

            if(response!=null){
                Long errcode = response.getErrcode();
                boolean success = response.isSuccess();
                if(0==errcode){
                    //发送成功将消息设置为已发送状态
                    String id=paramMap.get("id")+"";
                    Map<Object, Object> sendMap = new HashMap<>();
                    //消息id
                    String  userName = paramMap.get("userName")+"";
                    String employee_name=paramMap.get("employee_name")+"";
                    String[] split = userName.split(",");
                    String[] splitemployeename = employee_name.split(",");
                    for (int i=0;i<split.length;i++){
                        sendMap.put("message_id",id);
                        sendMap.put("username",split[i]);
                        sendMap.put("employee_name",splitemployeename[i]);
                        sendMap.put("send_type","钉钉");
                        sendMap.put("operator",paramMap.get("operator"));
                        //记录本条消息发送人
                        messageSendDao.recordMessageSendUser(sendMap);
                    }

                    //更改消息状态
                    messageSendDao.updateMessageSendStatus(id);
                }
                if(success){
                    return success;
                }else{
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
   }


}
