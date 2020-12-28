package cn.visolink.utils;/**
 * Created by Administrator on 2017/7/4.
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * @author 陈鹏飞
 * @description http请求
 * @create 2017-07-04 16:41
 * When I wrote this, only God and I understood what I was doing Now, God
 * only knows
 **/
public class HttpRequestUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);    //日志记录

    /**
     * post请求
     *
     * @param url            url地址
     * @param jsonParam      参数
     * @param noNeedResponse 不需要返回结果 (true:返回null ,false:返回json结果)
     * @return
     */
    public static JSONObject httpPost(String url, JSONObject jsonParam, boolean noNeedResponse) {

        if (StringUtils.isEmpty(url)) {
            logger.error("post请求提交失败: url为null");
            return null;
        }
        //ceshizhuanyong
//        if (url.contains("192.168.110.71")){
//            url = url.replace("192.168.110.71:8083","192.168.100.163:8081");
//        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        method.setHeader("token",token);
        try {
            if (!StringUtils.isEmpty(jsonParam)) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity());
                    if (noNeedResponse) {
                        return null;
                    }
                    /**把json字符串转换成json对象**/
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                    logger.error("返回结果解析失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return jsonResult;
    }

    /**
     * post请求
     *
     * @param url            url地址
     * @param jsonParam      参数
     * @param noNeedResponse 不需要返回结果 (true:返回null ,false:返回json结果)
     * @return
     */
    public static JSONArray httpPost2(String url, JSONObject jsonParam, boolean noNeedResponse) {

        if (StringUtils.isEmpty(url)) {
            logger.error("post请求提交失败: url为null");
            return null;
        }

        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONArray jsonResult = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        HttpPost method = new HttpPost(url);
        method.setHeader("token",token);
        try {
            if (!StringUtils.isEmpty(jsonParam)) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity());
                    if (noNeedResponse) {
                        return null;
                    }
                    /**把json字符串转换成json对象**/
                    jsonResult = JSONObject.parseArray(str);
                } catch (Exception e) {
                    logger.error("返回结果解析失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return jsonResult;
    }


    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static String httpGet(String url, boolean noNeedResponse) {

        if (StringUtils.isEmpty(url)) {
            logger.error("get请求提交失败: url为null");
            return null;
        }
        //get请求返回结果
        JSONObject jsonResult = null;
        String strResult="";
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                strResult = EntityUtils.toString(response.getEntity());
                if (noNeedResponse) {
                    return null;
                }
                /**把json字符串转换成json对象**/
//                jsonResult = JSONObject.parseObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return strResult;
    }

    public static void main(String[] args) {
        httpGet("http://192.168.110.70:8234/bi?action=logout", true);
    }
}
