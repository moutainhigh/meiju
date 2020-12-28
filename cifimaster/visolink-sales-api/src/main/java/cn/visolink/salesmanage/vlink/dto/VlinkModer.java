package cn.visolink.salesmanage.vlink.dto;

import cn.hutool.core.date.DateUtil;
import cn.visolink.utils.*;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *  薇链接口 模型
 *
 * @author bao
 * @date 2020-8-27
 */

@Data
@Slf4j
public class VlinkModer {

    private String url = "";
    /** 集团号 */
    private String groupId = "";

    /** 商户号 */
    private String appId = "";
    /** 营业执照公司名称 */
    private String appName = "";
    private String appKey = "";
    private String aesKey = "";
    private String entryptKey = "";
    private AESUtils aesUtils;

    /**
     * @param url
     * @param groupId
     * @param appId
     * @param appName
     * @param appKey
     */
    public VlinkModer(String url, String groupId, String appId, String appName, String appKey){
        this.url = url;
        this.groupId = groupId;
        this.appId = appId;
        this.appName = appName;
        this.appKey = appKey;
        this.aesKey = AESUtils.generateDesKey(128);//生成AES密钥
        try {
            this.entryptKey = RSAUtils.encryptByPrivate(aesKey, appKey);//私钥加密AES密钥
        } catch (Exception e) {
            e.printStackTrace();
        }
        aesUtils = new AESUtils(aesKey);
    }

    public BaseRequest getBaseRequest(String funCode, String timestamp){
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setReqId(timestamp+genRandom(3));
        baseRequest.setFunCode(funCode);
        baseRequest.setGroupId(groupId);
        baseRequest.setAppId(appId);
        baseRequest.setAppName(appName);
        return baseRequest;
    }






    public BaseResponse doPost(Object data, String funCode){
        BaseResponse response = new BaseResponse();
        String timestamp = DateUtil.format(new Date(),"YYYYMMddHHmmSSSSS");
        BaseRequest baseRequest = getBaseRequest(funCode, timestamp);
        baseRequest.setData(data);
        String reqJson = JSON.toJSONString(baseRequest);
        String body = "";
        try {
            body = aesUtils.encrypt(reqJson);//AES加密请求报文
        } catch (Exception e) {
            log.error("VlinkServer."+funCode+" 加密错误="+e.getMessage());
        }

        log.info("VlinkServer."+funCode+" 请求明文报文="+reqJson);

        Map<String, String> headerMap = getHeaderMap(timestamp, entryptKey);
        String result = HttpUtils.sendPost(url, body, headerMap);
        //JSONObject result = HttpRequestUtil.httpPost()
        log.info("VlinkServer."+funCode+" 返回报文="+result);
        Map<String, Object> respMap = JSONUtils.toMap(result);
        String code = String.valueOf(respMap.get("code"));
        String message = String.valueOf(respMap.get("message"));
        if(StringUtils.equals("00000", code)){
            //AES解密
            String resJson = "";
            try {
                resJson = aesUtils.decrypt(String.valueOf(respMap.get("sign")));
                log.info("VlinkServer."+funCode+" 返回明文报文="+resJson);
                response = JSONUtils.toObject(resJson, BaseResponse.class);
            } catch (Exception e) {
                log.error("VlinkServer."+funCode+" 解密错误="+e.getMessage());
            }
        }else{
            response.setCode(code);
            response.setMessage(message);
        }
        return response;
    }

    public static String genRandom(int pos){
        Random random = new Random();
        String result="";
        for(int i=0;i<pos;i++){
            result += random.nextInt(10);
        }
        return result;
    }

    private Map<String, String> getHeaderMap(String timestamp, String entryptKey) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("X-VLINK-GROUP-ID", groupId);
        headerMap.put("X-VLINK-APP-ID", appId);
        try {
            headerMap.put("X-VLINK-APP-NAME", URLEncoder.encode(appName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("url编码失败");
        }
        headerMap.put("X-VLINK-ENTRYPTKEY", entryptKey);
        headerMap.put("X-VLINK-TIMESTAMP", timestamp);
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        return headerMap;
    }


    /**
     * AES加密请求报文
     *
     * */
    public String getBodyEncrypt(String json) throws Exception {
        //AES加密请求报文
        return aesUtils.encrypt(json);
    }

    /**
     * AES解密
     *
     * */
    public String getBodyDecrypt(String json) throws Exception {
        //AES加密请求报文
        return aesUtils.decrypt(json);
    }

}
