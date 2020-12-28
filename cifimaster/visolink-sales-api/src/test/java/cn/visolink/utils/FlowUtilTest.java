package cn.visolink.utils;

import cn.visolink.AppRun;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={AppRun.class})// 指定启动类
public class FlowUtilTest {
    @Autowired
    FlowUtil flowUtil;

    @Test
    public void getToken() {
//        FlowUtil client = new FlowUtil();
//        client.setOaHost("http://oadev.cifi.com.cn");
//        client.setSysCode("xsgl");
//        client.setSecretKey("xsgl");
        String token = flowUtil.getToken();
        System.out.println(token);
    }

    @Test
    public void saveFlow() {
    }

    @Test
    public void endFlow() {
    }

    @Test
    public void deleteFlow() {
    }

    @Test
    public void sendPostRequest() {
    }
}