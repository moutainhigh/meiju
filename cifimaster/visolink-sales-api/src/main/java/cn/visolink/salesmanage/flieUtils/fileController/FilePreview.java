package cn.visolink.salesmanage.flieUtils.fileController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: DCS文档转换服务Java调用代码示例
 * @author LB
 * @date 20151014
 */
@RestController
@RequestMapping("/dcs.web")
@Api(tags = "文件预览")
public class FilePreview {

    @Value("${yongzhongurl}")
    private String serverurl;


    @ApiOperation(value = "文件预览")
    @PostMapping("/onlinefile")
    public  String sendPost(@RequestBody Map<String,String> param){
        String result="";
        List< NameValuePair> list = new ArrayList< NameValuePair>();
        String fullurl ="";
        //请求参数
        for(String key : param.keySet()) {
            list.add(new BasicNameValuePair(key, param.get(key)));
        }
        try{
            fullurl = EntityUtils.toString(new UrlEncodedFormEntity(list, Consts.UTF_8));
        }catch(Exception e){
            result ="下载失败";
            e.printStackTrace();
        }
        HttpPost httpget = new HttpPost(serverurl+"?"+fullurl);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse  response = httpclient.execute(httpget)){
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }else{
                result="下载失败";
            }
        } catch (Exception e) {
            result="下载失败";
            e.printStackTrace();
        } finally{
            httpget.releaseConnection();
        }
        return result;
    }

}

