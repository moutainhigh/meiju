package cn.visolink.firstplan.message.service.impl;

import cn.visolink.utils.UUID;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author sjl
 * @Created date 2020/7/28 11:37 上午
 */
public class CifiMessagePushTest {
//    @Autowired
    CifiMessagePush cifiMessagePush;

    @Before
    public void setup(){
        cifiMessagePush = new CifiMessagePush();
        cifiMessagePush.setMessageHost("https://service-test.cifi.com.cn");
    }

    @Test
    public void testSendDingDingMsg(){
       // String phone = "18916907769";

        String phone = "13693242773";

        String title = "成都事业部成都东原印长江客储偏差预警提醒";
        String contentMsg = "[!["+ UUID.randomUUID().toString() +"](https://salesmgt.cifi.com.cn/netdata/logo/20200618220512-4829b840-2a5a-4d14-8f57-b308a1068ce8.png?n=%E9%92%89%E9%92%89%E6%8E%A8%E9%80%81_%E5%AE%9A%E8%B0%83%E4%BB%B7%E9%A2%84%E8%AD%A6%E7%AE%80%E6%8A%A5-3.png)]( http://salesmgt-uat.cifi.com.cn/dingTalkMessage.html?id=18298f4d-a5b1-44be-9131-408edf3faa47)  \n[查看详情]( http://salesmgt-uat.cifi.com.cn/dingTalkMessage.html?id=18298f4d-a5b1-44be-9131-408edf3faa47)";

        cifiMessagePush.sendDingDingMsg(phone,title,contentMsg);
    }
}