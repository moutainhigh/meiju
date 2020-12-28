package cn.visolink.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.utils.flowpojo.FlowOpinion;
import cn.visolink.utils.flowpojo.FlowOpinionRes;
import cn.visolink.utils.flowpojo.FlowStateResult;
import cn.visolink.utils.flowpojo.SaveFlowRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class FlowUtil {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oa.host}")
    private String oaHost;

    @Value("${oa.oaPreviewUrl}")
    private String oaPreviewUrl;

    @Value("${oa.sysCode}")
    private String sysCode;

    @Value("${oa.secretKey}")
    private String secretKey;

    /**
     * 获取认证 token
     */
    private static final String getTokenUrl = "/uc/auth/v1/oauth";

    /**
     * 保存流程实例
     */
    private static final String saveFlowUrl = "/runtime/instance/v1/saveFlow";

    /**
     * 废弃流程实例
     */
    private static final String endFlowUrl = "/runtime/instance/v1/endFlow";

    /**
     * 删除流程实例
     */
    private static final String deleteFlowUrl = "/runtime/instance/v1/deleteFlow";

    /**
     * 查询流程实例的审批记录
     */
    private static final String flowOpinionsUrl = "/runtime/instance/v1/flowOpinions";

    /**
     * 查询流程实例的审批路径
     */
    private static final String flowPathUrl = "/runtime/instance/v1/flowPath";
    /**
     * 查询流程流程预览URL
     */
    private static final String flowPreviewUrl = "/front/#/preview/";

    /**
     * 查看历史审批记录
     */
    private static final String flowReviewUrl = "/front/#/reviewApproval/";

    /**
     * 获取认证 token
     * @return 获取的Token信息
     */
    public String getToken(){
//        String token = "";
        String token = redisUtil.get(VisolinkConstant.REDIS_KEY+".OAToken")+"";
        if(StrUtil.isNotBlank(token) && !"null".equals(token)){
            return token;
        }
        Map params = new HashMap();
        params.put("sysCode",sysCode);
        params.put("secretKey",secretKey);

        System.out.println(oaHost + getTokenUrl);
        String result = HttpClientUtil.doPostJson(oaHost + getTokenUrl,params);

        JSONObject jsonObject = JSON.parseObject(result);
        boolean state = (Boolean)jsonObject.get("state");
        if(state){
            //获取token成功
            token= jsonObject.get("token")+"";
            //他们的失效时间
            long expiresIn=Long.valueOf(jsonObject.get("expiresIn")+"");
            //保存token
            redisUtil.set(VisolinkConstant.REDIS_KEY+".OAToken",token,expiresIn-60000);
        }else{
            token =  "";
        }
        return token;
    }

    /**
     * 保存工作流
     * @param flowRequest 工作流信息
     * @return
     */
    public FlowStateResult saveFlow(SaveFlowRequest flowRequest){
        Map<String,Object>  header = new HashMap<>();
        String token = getToken();
        header.put("token",token);
        header.put("Authorization","Bearer "+token);
        //发起流程
        flowRequest.setSysCode(sysCode);
        String sysCode = flowRequest.getSysCode();
        if("".equals(sysCode)||"null".equals(sysCode)||null==sysCode){
            flowRequest.setSysCode("xsgl");
        }
        String json = JSON.toJSONString(flowRequest);
        System.err.println("推送OA参数:"+json);
        String result = HttpClientUtil.doPostJson(oaHost + saveFlowUrl,header,json);
        if(StrUtil.isNotBlank(result)){
            return JSON.parseObject(result,FlowStateResult.class);
        }
        return new FlowStateResult(false,"");
    }
    /**
     * 结束工作流
     * @param instanceId 实例ID
     * @return
     */
    public FlowStateResult endFlow(String instanceId){
        return updateFlowState(oaHost + endFlowUrl,instanceId);
    }

    /**
     * 删除工作流
     * @param instanceId 实例ID
     * @return
     */
    public FlowStateResult deleteFlow(String instanceId){
        return updateFlowState(oaHost + deleteFlowUrl,instanceId);
    }

    /**
     * 获取工作流审批信息
     * @param instanceId 实例ID
     * @return
     */
    public FlowOpinion flowOpinions(String instanceId){
        if(StrUtil.isEmpty(instanceId)){
            return null;
        }
        Map params = getRequestMap(instanceId);
        String token = getToken();
        Map<String,Object>  header = new HashMap<>();
        header.put("token",token);
        header.put("Authorization","Bearer "+token);
        String result = HttpClientUtil.doPostJson(oaHost + flowOpinionsUrl,header,params);

        if(StrUtil.isNotBlank(result)){
            return JSON.parseObject(result, FlowOpinion.class);
        }
        return null;
    }
    /**
     * 获取工作流审批信息
     * @param instanceId 实例ID
     * @return
     */
    public Map flowPaths(String instanceId){
        if(StrUtil.isEmpty(instanceId)){
            return null;
        }
        Map params = getRequestMap(instanceId);
        String token = getToken();
        Map<String,Object>  header = new HashMap<>();
        header.put("token",token);
        header.put("Authorization","Bearer "+token);
        String result = HttpClientUtil.doPostJson(oaHost + flowPathUrl,header,params);

        if(StrUtil.isNotBlank(result)){
            return JSON.parseObject(result, Map.class);
        }
        return null;
    }

    /**
     *  获取预览页面路径
     */
    public String getProviewUrl(String instId,String taskId){
        return  getProviewUrl(true,instId,taskId) ;
    }

    /**
     * 获取预览页面路径
     * @param isPc 是否PC
     * @param instId 流程ID
     * @param taskId 任务ID
     * @return
     */
    private String getProviewUrl(boolean isPc, String instId,String taskId){
        //Todo 考虑新增手机端
        Map mapRes = new HashMap();
        mapRes.put("proInstId",instId);
        if(StrUtil.isNotEmpty(taskId)){
            mapRes.put("taskId",taskId);
        }

        JSONObject json = new JSONObject(mapRes);
        //加密参数
        String previewUrl = oaPreviewUrl + flowPreviewUrl;
        try {
            previewUrl += EncryptUtils.getBase64(json.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return   previewUrl;
    }


    /**
     *  获取预览页面路径
     */
    public String getFlowReviewUrl(String instanceId){

        Map params = new HashMap();
        params.put("proInstId",instanceId);
        params.put("sysCode",sysCode);

        JSONObject json = new JSONObject(params);
        String urlparam = null;
        try {
            urlparam = EncryptUtils.getBase64(json.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return oaPreviewUrl + flowReviewUrl + urlparam;
    }

    /**
     * 根据URL获取流程处理结果
     * @param url 流程处理URL
     * @param instanceId 实例ID
     * @return
     */
    private FlowStateResult updateFlowState(String url, String instanceId){
        Map params = getRequestMap(instanceId);

        String token = getToken();
        Map<String,Object>  header = new HashMap<>();
        header.put("token",token);
        header.put("Authorization","Bearer "+token);

        String result = HttpClientUtil.doPostJson(url,header,params);

        if(StrUtil.isNotBlank(result)){
            return JSON.parseObject(result,FlowStateResult.class);
        }

        return new FlowStateResult(false,"");
    }

    /**
     * 获取请求实例信息的参数
     * @param instanceId
     * @return
     */
    private Map<String,String> getRequestMap(String instanceId){
        Map params = new HashMap();
        params.put("instanceId",instanceId);
        params.put("sysCode",sysCode);
        return params;
    }

    /**
     * 向目的URL发送post请求
     * @param url       目的url
     * @param params    发送的参数
     * @return  ResultVO
     */
    public static  Map sendPostRequest(String url, Map<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("token",params.get("token")+"");
        headers.add("Authorization",params.get("Authorization"));
        HttpMethod method = HttpMethod.POST;
        // 以json的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(params,headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<Map> response = client.exchange(url, method, requestEntity, Map.class);
        return response.getBody();
    }


}
