package cn.visolink.common;

import cn.visolink.config.ExtensionMethod;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.core.common.utils.RequestParamUtils;
import org.apache.wicket.core.dbhelper.api.FrameServiceApi;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author WCL
 * ajax请求控制器
 * 处理/FrameWeb/FrameService/Api请求
 **/
@Controller
@Slf4j
public class Api {

    @Resource(name = "frameServiceApi")
    private FrameServiceApi frameServiceApi;


    /**
     * Ajax请求控制器
     * @param funcid
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/FrameWeb/FrameService/Api/{funcid}"})
    @ResponseBody
    private ResultBody getStandardResult(
            @PathVariable("funcid") String funcid,
            HttpServletRequest request, HttpServletResponse response
            ) {
        ResultBody resultBody = getResultCommon(funcid, request, response);
        return resultBody;
    }

    /**
     * 案场请求控制器
     * @param funcid
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/FrameWeb/getSiteResult/Api/{funcid}"})
    @ResponseBody
    private ResultBody getSiteResult(
            @PathVariable("funcid") String funcid,
            HttpServletRequest request, HttpServletResponse response
    ) {
        return getResultCommon(funcid, request, response);
    }

    /**
     * 拓客 请求控制器
     * @param funcid
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/FrameWeb/getCustomerResult/Api/{funcid}"})
    @ResponseBody
    private ResultBody getCustomerResult(
            @PathVariable("funcid") String funcid,
            HttpServletRequest request, HttpServletResponse response
    ) {
        return getResultCommon(funcid, request, response);
    }

    private ResultBody getResultCommon(String funcid, HttpServletRequest request, HttpServletResponse response) {
        String param ="";
        try{
                /*contentType 为 application/json;charset=utf-8'取值方式*/
                try {
                    request.setCharacterEncoding("UTF-8");
                    ServletInputStream is = request.getInputStream();
                    param= IOUtils.toString(is, "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                    param = "";
                }
                if (("").equals(param)) {
                    /*contentType 为 application/x-www-form-urlencoded，取值方式*/
                    param = RequestParamUtils.getRequestJsonStringByMap(request);
                    Map<String, String[]> map = request. getParameterMap();
                }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ResultBody resultBody =null;
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        ExtensionMethod extensionMethod = (ExtensionMethod) wac.getBean(funcid);
        try {
            resultBody = extensionMethod.execute( param,request, response);
        } catch (BadRequestException bad){
            throw bad;
        }catch (Exception e) {
            e.printStackTrace();
            resultBody=new ResultBody();
            resultBody.setCode(-1);
            String errMsg = e.getMessage();
            if(errMsg == null || "".equals(errMsg)) {
                errMsg = "";
            }
            resultBody.setMessages(errMsg);
            Gson gson = new Gson();
        }
        return resultBody;
    }
}
