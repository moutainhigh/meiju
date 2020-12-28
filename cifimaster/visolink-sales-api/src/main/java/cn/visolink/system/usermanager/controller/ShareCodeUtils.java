package cn.visolink.system.usermanager.controller;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * @author wjc
 * @date 2019/09/12
 */
public class ShareCodeUtils {
    @Autowired

    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_XML = "text/xml";
    /**
     * 自定义进制(0,1没有加入,容易与o,l混淆)
     */
    private static final char[] r = new char[]{'q', 'w', 'e', '8', 'a', 's', '2', 'd', 'z', 'x', '9', 'c', '7', 'p', '5', 'i', 'k', '3', 'm', 'j', 'u', 'f', 'r', '4', 'v', 'y', 'l', 't', 'n', '6', 'b', 'g', 'h'};
    /**
     * (不能与自定义进制有重复)
     */
    private static final char b = 'o';
    /**
     * 序列最小长度
     */
    private static final int s = 8;
    /**
     * 进制长度
     */
    private static final int binLen = r.length;
    public static String toSerialCode(long id) {
        char[] buf = new char[32];
        int charPos = 32;

        while ((id / binLen) > 0) {
            int ind = (int) (id % binLen);
            buf[--charPos] = r[ind];
            id /= binLen;
        }
        buf[--charPos] = r[(int) (id % binLen)];
        String str = new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if (str.length() < s) {
            StringBuilder sb = new StringBuilder();
            sb.append(b);
            Random rnd = new Random();
            for (int i = 1; i < s - str.length(); i++) {
                sb.append(r[rnd.nextInt(binLen)]);
            }
            str += sb.toString();
        }
        return str;
    }
    private static String execute(CloseableHttpClient httpClient, HttpUriRequest httpGetOrPost) {
        String res = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGetOrPost);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity, CHARSET_UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("短信发送失败");
        } finally {
          /*  doResponseClose(response);*/
        }
        return res;
    }
    /**
     * post json数据
     * @param url
     * @param jsonStr
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String postJson(String url, String jsonStr) throws UnsupportedEncodingException {
        String res = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = null;
            if (null != jsonStr) {
                stringEntity = new StringEntity(jsonStr.toString(), CHARSET_UTF_8);
            }
            httpPost.setHeader("Content-Type", CONTENT_TYPE_JSON);
            httpPost.setEntity(stringEntity);
            res = execute(httpClient, httpPost);
        } finally {
           /* doHttpClientClose(httpClient);*/
        }
        return res;
    }

}
