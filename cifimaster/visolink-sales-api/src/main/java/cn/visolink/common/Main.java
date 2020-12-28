package cn.visolink.common;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.visolink.common.DataRequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.core.common.utils.RequestParamUtils;
import org.apache.wicket.core.dbhelper.api.FrameServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WCL
 * ajax请求控制器
 * 处理/FrameWeb/FrameService/Api请求
 **/
@RestController
@Slf4j
public class Main {
    @Autowired
    DataRequestService dataRequestService;

    @Resource(name = "frameServiceApi")
    private FrameServiceApi frameServiceApi;

    /**
     * Ajax请求控制器
     *
     * @param option
     * @param funcid
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/FrameWeb/FrameService/Main"})
    @ResponseBody
    private JSONObject getStandardResult(
            HttpServletRequest request, HttpServletResponse response
    ) {
        String param = "";
        String r = "";

        /**
         * 考虑Token解析验证
         */
        try {
            /*contentType 为 application/json;charset=utf-8'取值方式*/
            try {
                request.setCharacterEncoding("UTF-8");
                ServletInputStream is = request.getInputStream();
                param = IOUtils.toString(is, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
                param = "";
            }
            if (("").equals(param)) {
                /*contentType 为 application/x-www-form-urlencoded，取值方式*/
                param = RequestParamUtils.getRequestJsonStringByMap(request);
                Map<String, String[]> map = request.getParameterMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        r = frameServiceApi.getJsonByJsonData(param);
        JSONObject jsonObject = JSONUtil.parseObj(r);
        return jsonObject;
    }
}
