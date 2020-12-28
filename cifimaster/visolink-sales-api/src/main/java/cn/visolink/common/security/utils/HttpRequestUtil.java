package cn.visolink.common.security.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.JsonObject;
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
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * @author wanggang
 * @description http请求
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
    public static JSONObject httpPost(String url,String userId,String password, JSONObject jsonParam, boolean noNeedResponse) {

        if (StringUtils.isEmpty(url)) {
            logger.error("post请求提交失败: url为null");
            return null;
        }
        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null!=userId && !"".equals(userId) && null!=password && !"".equals(password) ){
                String encoding = DatatypeConverter.printBase64Binary((userId+":"+password).getBytes("UTF-8"));

                method.setHeader("Authorization", "Basic " +encoding);
            }
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
            }else{
                logger.info("调用接口返回状态码为："+result.getStatusLine().getStatusCode());
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
    public static JSONObject httpPost2(String url, JSONObject jsonParam, boolean noNeedResponse) {

        if (StringUtils.isEmpty(url)) {
            logger.error("post请求提交失败: url为null");
            return null;
        }
        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONArray jsonResult = null;
        JSONObject josn = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        HttpPost method = new HttpPost(url);
        method.setHeader("token",token);
        try {
            if (!StringUtils.isEmpty(jsonParam)) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(JSONObject.toJSONString(jsonParam,SerializerFeature.DisableCircularReferenceDetect), "utf-8");
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
                   // jsonResult = JSONObject.parseArray(str);
                   // str = "{\n\"esbInfo\": {\n\"instId\": \"test001\",\n\"returnStatus\": \"S\",\n\"returnCode\": \"A014-SMS\",\n\"returnMsg\": \"调用成功\",\n\"requestTime\": \"2019-06-11 10:16:15\",\n\"responseTime\": \"2019-06-11 17:04:59\",\n\"attr1\": null,\n\"attr2\": null,\n\"attr3\": null\n}\n}";
                    josn =  JSONObject.parseObject(str);
                } catch (Exception e) {
                    logger.error("返回结果解析失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return josn;
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

    public static String doPost(String strUrl, String content) {
        String result = "";
        try {
            URL url = new URL(strUrl);
            //通过调用url.openConnection()来获得一个新的URLConnection对象，并且将其结果强制转换为HttpURLConnection.
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            //设置连接的超时值为30000毫秒，超时将抛出SocketTimeoutException异常
            urlConnection.setConnectTimeout(30000);
            //设置读取的超时值为30000毫秒，超时将抛出SocketTimeoutException异常
            urlConnection.setReadTimeout(30000);
            //将url连接用于输出，这样才能使用getOutputStream()。getOutputStream()返回的输出流用于传输数据
            urlConnection.setDoOutput(true);
            //设置通用请求属性为默认浏览器编码类型
            urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //getOutputStream()返回的输出流，用于写入参数数据。
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
            //此时将调用接口方法。getInputStream()返回的输入流可以读取返回的数据。
            InputStream inputStream = urlConnection.getInputStream();
            byte[] data = new byte[1024];
            StringBuilder sb = new StringBuilder();
            //inputStream每次就会将读取1024个byte到data中，当inputSteam中没有数据时，inputStream.read(data)值为-1
            while (inputStream.read(data) != -1) {
                String s = new String(data, Charset.forName("utf-8"));
                sb.append(s);
            }
            result = sb.toString();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

//    public static void main(String[] args) {
//        httpGet("http://192.168.110.12:8234/bi?action=logout", true);
//    }
}
