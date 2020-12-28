package cn.visolink.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @since 2019-11-20
 * @author lihuan
 */
public class HttpClientUtil {

    public static int time = 100;
    public static String doGet(String url, Map<String, Object> param) {
        HttpRequest request = HttpRequest.get(url);
        HttpResponse httpResponse = request.form(param).timeout(time * 1000).execute();

        String responseString = "";
        if(httpResponse.isOk()){
            responseString = httpResponse.body();
        }
        return responseString;
    }


    public static String doPost(String url, Map<String, Object> param) {
        HttpRequest request = HttpRequest.post(url);
        HttpResponse httpResponse = request.form(param).timeout(time * 1000).execute();

        String responseString = "";
        if(httpResponse.isOk()){
            responseString = httpResponse.body();
        }
        return responseString;
    }

    public static String doPostJson(String url, Map<String, Object> param) {
        return doPostJson(url,null,param);
    }
    public static String doPostJson(String url, Map<String, Object> header, Map<String, Object> param) {
        String json = JSON.toJSONString(param);
        System.out.println("json=" + json);
        return doPostJson(url,header,json);
    }
    public static String doPostJson(String url, Map<String, Object> header,String json) {
        HttpRequest request = HttpRequest.post(url);
        if(header != null && header.size() > 0){
            for(String key : header.keySet()){
                request.header(key,header.get(key).toString());
            }
        }
        HttpResponse httpResponse = request.body(json, "application/json").timeout(time * 1000).execute();

        String responseString = "";
        if(httpResponse.isOk()){
            responseString = httpResponse.body();
        }
        return responseString;
    }

    public static String doPostJson(String url, String json) {
        HttpRequest request = HttpRequest.post(url);
        HttpResponse httpResponse = request.body(json, "application/json").timeout(time * 1000).execute();

        String responseString = "";
        if(httpResponse.isOk()){
            responseString = httpResponse.body();
        }
        return responseString;
    }


}