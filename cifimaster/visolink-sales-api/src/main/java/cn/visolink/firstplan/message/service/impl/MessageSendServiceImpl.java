package cn.visolink.firstplan.message.service.impl;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.message.dao.MessageManagerDao;
import cn.visolink.firstplan.message.dao.MessageSendDao;
import cn.visolink.firstplan.message.pojo.ResultVO;
import cn.visolink.firstplan.message.service.MessageManagerService;
import cn.visolink.firstplan.message.service.MessageSendService;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.DingDingRobotUtil;
import cn.visolink.utils.DingDingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.tree.VoidDescriptor;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author sjl
 * @Created date 2020/5/26 9:47 下午
 * 消息发送服务
 */
@Service
@Transactional
public class MessageSendServiceImpl implements MessageSendService {

    @Value("${spring.profiles.active}")
    private String profiles;
    @Autowired
    private CifiMessagePush cifiMessagePush;
    @Autowired
    private MessageSendDao messageSendDao;
    @Autowired
    private MessageManagerDao messageManagerDao;

    //钉钉发送工作通知
    @Autowired
    private DingDingUtil dingDingUtil;

    @Autowired
    private DingDingRobotUtil dingDingRobotUtil;

    @Autowired
    private TimeLogsDao timeLogsDao;

    @Autowired
    private MessageManagerService messageManagerService;
    /**
     * 邮箱发送服务
     * @param map
     * @return
     */
    @Override
    public ResultBody emaillSend(Map map) {

       int pointsDataLimit = 20;//限制发送人数,每次最多20人
       String OfficeMail=map.get("OfficeMail")+"";
       if(OfficeMail!=null&&!"null".equals(OfficeMail)){
           OfficeMail=OfficeMail.substring(0,OfficeMail.lastIndexOf("|"));
           String[] split = OfficeMail.split("\\|");
           if(split!=null){
               List<String> userArray = Arrays.asList(split);
               List<String> userList = new ArrayList(userArray);
               //需要发送的人数
               Integer size = userList.size();
               if (pointsDataLimit < size) {
                   int part = size / pointsDataLimit;//分批数
                   System.out.println("共有 ： " + size + "条，！" + " 分为 ：" + (part+1) + "批发送");
                   for (int i = 0; i < part; i++) {
                       List<String> listPage = userList.subList(0, pointsDataLimit);
                       //执行发送
                       sendEmailMessage(map,listPage);
                       // 剔除
                       userList.subList(0, pointsDataLimit).clear();
                   }
                   if(!userList.isEmpty()){
                       //表示最后剩下的人数
                       //执行发送
                       sendEmailMessage(map,userList);
                   }
               }else{
                   //执行发送
                   sendEmailMessage(map,userList);
               }
           }
       }
        return  ResultBody.success(null);

    }

    /**
     * 执行消息发送
     * @param map 消息内容
     * @param emailList 人员
     * @return
     */
    public ResultBody  sendEmailMessage(Map map,List<String> emailList){
        String OfficeMail="";
        for (String email : emailList) {
            OfficeMail+=email+"|";
        }
        OfficeMail=OfficeMail.substring(0,OfficeMail.lastIndexOf("|"));
        //消息标题
        String title=map.get("message_title")+"";
        //消息内容
        String context=map.get("message_info")+"";
        if(!"".equals(OfficeMail)&&!"null".equals(OfficeMail)){
            ResultVO resultVO = cifiMessagePush.sendEmail(OfficeMail, title, context);
            if(resultVO!=null){
                //发送成功后将消息状态变更为已发送，更新发送时间
                String retMsg = resultVO.getRetMsg();
                if("success".equals(retMsg)){
                    String id=map.get("id")+"";
                    Map<Object, Object> sendMap = new HashMap<>();
                    //消息id
                    sendMap.put("message_id",id);
                    String usernames=map.get("userName")+"";
                    String[] splitUsername = usernames.split(",");
                    String employee_names=map.get("employee_name")+"";
                    String[] splitEname = employee_names.split(",");
                    for (int i=0;i<splitUsername.length;i++){
                        sendMap.put("username",splitUsername[i]);
                        sendMap.put("employee_name",splitEname[i]);
                        sendMap.put("send_type","邮箱");
                        sendMap.put("operator",map.get("operator"));
                        //记录本次发送人
                        messageSendDao.recordMessageSendUser(sendMap);
                    }
                    //更改消息状态
                    messageSendDao.updateMessageSendStatus(id);
                }
            }
            return  ResultBody.success(resultVO);
        }
        return null;
    }

    @Override
    public ResultBody qqEmailSend(Map map) {
        //存放日志
        Map logParams=new HashMap();
        try {
            String email=map.get("OfficeMail")+"";
            if("".equals(email)||"null".equals(email)){
                email="shaojialong@visolink.com";
            }
            String title=map.get("message_title")+"";
            if("".equals(title)||"null".equals(title)){
                title="测试发送";
            }
            String context=map.get("message_info")+"";
            Properties props=new Properties();
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host","smtp.163.com");// smtp服务器地址

            Session session = Session.getInstance(props);
            session.setDebug(true);

            Multipart mp = new MimeMultipart("related");
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(context,"text/html;charset=UTF-8"));

            mp.addBodyPart(bodyPart);

            Message msg = new MimeMessage(session);
            //邮箱标题
            msg.setSubject(title);
            msg.setContent(mp);

            msg.setFrom(new InternetAddress("13161922007@163.com"));//发件人邮箱(我的163邮箱)
            msg.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(email)); //收件人邮箱(我的QQ邮箱)
            msg.saveChanges();

            Transport transport = session.getTransport();
            transport.connect("13161922007@163.com","TLNOFJSYQPYWISOA");//发件人邮箱,授权码(可以在邮箱设置中获取到授权码的信息)

            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            String id=map.get("id")+"";
            Map<Object, Object> sendMap = new HashMap<>();
            //消息id
            sendMap.put("message_id",id);
            sendMap.put("username",map.get("userName"));
            sendMap.put("employee_name",map.get("employee_name"));
            sendMap.put("send_type","邮箱");
            sendMap.put("operator",map.get("operator"));
            //记录本次发送人
            messageSendDao.recordMessageSendUser(sendMap);
            //更改消息状态
            messageSendDao.updateMessageSendStatus(id);
            logParams.put("TaskName","消息发送成功");
            logParams.put("content","消息id:"+map.get("id"));
        }catch (Exception e){
            e.printStackTrace();
            logParams.put("TaskName","消息发送失败");
            logParams.put("content","消息id:"+map.get("id")+"异常信息:"+e.getMessage()+"失败原因:"+e.getCause());
        }finally {
            timeLogsDao.insertLog(logParams);
        }
        return ResultBody.success(logParams);
    }

    /**
     * 消息统一发送服务
     * @param map
     * @return
     */

    @Override
    public ResultBody SendMessageServer(Map map) {
        //存放日志
        Map logParams=new HashMap();
        try {
            //定义消息标题
            String message_title=null;
            //定义消息内容/正文
            String message_info=null;
            //是否发送钉钉 默认不发送
            boolean sendDingtalk=false;
            //是否发送邮箱 默认不发送
            boolean sendEmail=false;
            //封装手机号数组
            List<String> mobileList=new ArrayList<>();
            List<String> emailList=new ArrayList<>();
            //查询出所有截止到现在，未发送的消息
            //查询截止到当前时间未发送且设置为自动发送的消息
                List<Map> sendMessageList = messageSendDao.getSendMessageList(map);
                if(sendMessageList!=null){
                    for (Map messageMap : sendMessageList) {
                        String message_send_mode=messageMap.get("message_send_mode")+"";
                        if("1".equals(message_send_mode)){
                            sendDingtalk=true;
                        }else if("2".equals(message_send_mode)){
                            sendEmail=true;
                        }else if("3".equals(message_send_mode)){
                            sendEmail=true;
                            sendDingtalk=true;
                        }
                       // List<Map> sendUserList = messageSendDao.getSendUserList(messageMap);
                        List<Map> sendUserList=null;
                        List<Map> forUserList = messageManagerDao.getMessageForUserList(messageMap.get("id") + "");
                     /*   ResultBody resultBody = messageManagerService.queryUserList(messageMap);
                        if(resultBody.getCode()==200) {
                            sendUserList= (List<Map>) resultBody.getData();
                        }else{
                            return ResultBody.error(-1005,"发送时获取人员异常:"+resultBody.getMessages());
                        }*/
                        if(forUserList!=null&&forUserList.size()>0){
                            sendUserList=forUserList;
                        }else{
                            return ResultBody.error(-1005,"没有获取到待发送人员列表!");
                        }
                        String officeMails="";
                        String mobiles="";
                        String userNames="";
                        String employee_names="";
                        if(sendUserList!=null&&sendUserList.size()>0){
                            for (Map userMap : sendUserList) {
                                //获取发送人手机号
                                mobiles+=userMap.get("Mobile")+",";
                                //获取发送人邮箱
                                officeMails+=userMap.get("OfficeMail")+"|";
                                userNames+=userMap.get("userName")+",";
                                employee_names+=userMap.get("EmployeeName")+",";
                            }
                            //封装消息记录
                            messageMap.put("userName",userNames);
                            messageMap.put("Mobile",mobiles);
                            messageMap.put("OfficeMail",officeMails);
                            messageMap.put("employee_name",employee_names);
                            messageMap.put("operator","系统自动发送");
                            //循环发送邮箱
                            if(sendEmail){
                                emaillSend(messageMap);
                               /* if ("uat".equals(profiles) || "dev".equals(profiles)) {
                                    qqEmailSend(messageMap);
                                }else{

                                }*/
                            }
                            if(sendDingtalk){
                                dingDingUtil.sendDingTalkMessages(messageMap);
                              /*  if ("uat".equals(profiles) || "dev".equals(profiles)) {
                                    dingDingUtil.sendDingTalkMessages(messageMap);
                                }else{

                                }*/
                            }
                        }else{
                            logParams.put("TaskName","消息自动发送失败");
                            logParams.put("content","未找到可以发送的该项目岗位组成员!");
                            return ResultBody.error(-1005,"消息发送失败,未找到可以发送的该项目岗位组成员!");
                        }
                    }
                }
            //调用发送服务
            } catch (Exception e){
            logParams.put("TaskName","消息发送失败");
            logParams.put("content","失败信息:"+e.getMessage()+"失败原因:"+e.getCause());
            e.printStackTrace();
            return ResultBody.error(-1005,"消息发送失败!");
        }finally {
            //无论执行成功或者失败，都添加日志记录
            timeLogsDao.insertLog(logParams);
        }
        return ResultBody.success(null);
    }

    /**
     * 手动发送
     * @return
     */
    @Override
    public ResultBody manualSendMessageServer(Map maps, HttpServletRequest request) {
        //存放日志
        Map logParams=new HashMap();
        try {
            //封装手机号数组
            List<String> mobileList=new ArrayList<>();
            //是否发送钉钉 默认不发送
            boolean sendDingtalk=false;
            //是否发送邮箱 默认不发送
            boolean sendEmail=false;
            String sendTypeStr="";
            String message_send_mode=null;
            List<Map> sendMessageList = (List<Map>) maps.get("sendMessageList");
            if(sendMessageList!=null&&sendMessageList.size()>0){
                for (Map map : sendMessageList) {
                    //如果是手动调用
                    String id=map.get("id")+"";
                    List<String> sendTypeList= (List<String>) map.get("message_send_mode");
                    if(sendTypeList!=null&&sendTypeList.size()>0){
                        for (String sendType : sendTypeList) {
                            sendTypeStr+=sendType;
                        }
                    }else{
                        sendTypeStr="";
                    }
                    if("钉钉".equals(sendTypeStr)){
                        message_send_mode="1";
                    }else if("邮箱".equals(sendTypeStr)){
                        message_send_mode="2";
                    }else if("钉钉邮箱".equals(sendTypeStr)||"邮箱钉钉".equals(sendTypeStr)){
                        message_send_mode="3";
                    }
                    if(!"".equals(id)&&!"null".equals(id)){
                        //查询出指定的消息
                        Map messageInfo = messageSendDao.getMessageInfo(id);
                        if(messageInfo!=null){
                            if("".equals(sendTypeStr)){
                                message_send_mode=messageInfo.get("message_send_mode")+"";
                            }
                            if("1".equals(message_send_mode)){
                                sendDingtalk=true;
                            }else if("2".equals(message_send_mode)){
                                sendEmail=true;
                            }else if("3".equals(message_send_mode)){
                                sendDingtalk=true;
                                sendEmail=true;
                            }
                        }else{
                            return ResultBody.error(-1007,"未找到该消息!");
                        }

                        //获取选择的发送人列表
                        List<Map> userList = (List<Map>) map.get("sendUserCheckedList");
                       // userList.add
                        if(userList==null||userList.size()==0){
                            //根据模版id，查询出对应的岗位人员集
                            messageInfo.put("template_id",messageInfo.get("message_template_id"));
                            //userList= messageSendDao.getSendUserList(messageInfo);

                       /*     ResultBody resultBody = messageManagerService.queryUserList(messageInfo);
                            if(resultBody.getCode()==200) {
                                userList= (List<Map>) resultBody.getData();
                            }else{
                                return ResultBody.error(-1005,"发送时获取人员异常:"+resultBody.getMessages());
                            }*/
                            List<Map> forUserList = messageManagerDao.getMessageForUserList(id);
                            if(forUserList!=null&&forUserList.size()>0){
                                userList=forUserList;
                            }else{
                                return ResultBody.error(-1005,"未查询到可发送人员列表!");
                            }
                        }
                        String officeMails="";
                        String mobiles="";
                        String userNames="";
                        String employee_names="";
                        if(userList!=null&&userList.size()>0){
                            for (Map userMap : userList) {
                                //获取发送人手机号
                                mobiles+=userMap.get("Mobile")+",";
                                //获取发送人邮箱
                                officeMails+=userMap.get("OfficeMail")+"|";
                                userNames+=userMap.get("userName")+",";
                                employee_names+=userMap.get("EmployeeName")+",";
                            }
                            //封装消息记录
                            map.put("userName",userNames);
                            map.put("Mobile",mobiles);
                            map.put("OfficeMail",officeMails);
                            map.put("employee_name",employee_names);
                            map.put("operator","系统自动发送");
                            //循环发送邮箱
                            if(sendEmail){
                                emaillSend(map);
                               /* if ("uat".equals(profiles) || "dev".equals(profiles)) {
                                    qqEmailSend(messageMap);
                                }else{

                                }*/
                            }
                            if(sendDingtalk){
                                dingDingUtil.sendDingTalkMessages(map);
                              /*  if ("uat".equals(profiles) || "dev".equals(profiles)) {
                                    dingDingUtil.sendDingTalkMessages(messageMap);
                                }else{

                                }*/
                            }
                        }else{
                            return ResultBody.error(-1010,"消息发送失败,未找到可以发送的该项目岗位组成员!");
                        }

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logParams.put("TaskName","消息自动发送失败");
            logParams.put("content","未找到可以发送的该项目岗位组成员!");
            return ResultBody.error(-1005,"消息发送失败,未找到可以发送的该项目岗位组成员!");
        }
        return ResultBody.success(null);
    }


    /**
     * 钉钉发送工作通知服务
     * @param map
     * @return
     */
    @Override
    public ResultBody dingtalkSendWorkNotice(Map map) {
        dingDingUtil.sendDingTalkMessages(map);
        /*if(!"-1005".equals(token)){
            return  ResultBody.success(token);
        }else {
            return  ResultBody.error(-1005,token);
        }*/
        return null;
    }

}
