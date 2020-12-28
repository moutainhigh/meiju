package cn.visolink.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * [参数转换工具类]
 * <br/>
 * 注：此类中的gson解析进行了数据格式校验
 * 如果参数中的值存在int值,进行转换后仍然为int值而不是double值
 * 如果值为double, 若值为1.0这种类型，那么会被转换为1;若为1.1这种类型，转换后的值仍为1.1
 * 2017.8.14 chencheng
 */
public class ParamUtil {

    /**
     * 返回普通接口请求参数中的_param部分的map形式
     *
     * @param param
     * @return
     */
    public static Map<String, Object> getDataMapFromParam(String param) {

        Long startTime = System.currentTimeMillis();


        String r = "";

        String parajsonen = null;
        try {
            parajsonen = URLDecoder.decode(param, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Gson gson = new GsonBuilder()
        //        .setPrettyPrinting()
        //        .disableHtmlEscaping()
        //        .create();
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Double.class,
                        (JsonSerializer<Double>) (aDouble, type, jsonSerializationContext) -> {
                            if (aDouble == aDouble.longValue()) {
                                return new JsonPrimitive(aDouble.longValue());
                            }
                            return new JsonPrimitive(aDouble);
                        }).create();
        Map<String, Object> paramMap = gson.fromJson(parajsonen, HashMap.class);
        Map<String, Object> _dataMap = (Map<String, Object>) paramMap.get("_param");


        return _dataMap;
    }


    /**
     * 返回普通接口请求参数的map形式
     *
     * @param param
     * @return
     */
    public static Map<String, Object> getOrientialMapFromParam(String param) {

        Long startTime = System.currentTimeMillis();


        String r = "";

        String parajsonen = null;
        try {
            parajsonen = URLDecoder.decode(param, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//Gson gson = new GsonBuilder()
//	    .setPrettyPrinting()
//	    .disableHtmlEscaping()
//	    .create();
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Double.class,
                        (JsonSerializer<Double>) (aDouble, type, jsonSerializationContext) -> {
                            if (aDouble == aDouble.longValue()) {
                                return new JsonPrimitive(aDouble.longValue());
                            }
                            return new JsonPrimitive(aDouble);
                        }).create();
        Map<String, Object> paramMap = gson.fromJson(parajsonen, HashMap.class);


        return paramMap;
    }

    /**
     * 返回form表单或url中参数的json形式，此方法不考虑一键多值情况
     *
     * @param request
     * @return
     */
    public static String getJsonFromRequestParam(HttpServletRequest request) {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Double.class,
                        (JsonSerializer<Double>) (aDouble, type, jsonSerializationContext) -> {
                            if (aDouble == aDouble.longValue()) {
                                return new JsonPrimitive(aDouble.longValue());
                            }
                            return new JsonPrimitive(aDouble);
                        }).create();
        Map resultMap = new LinkedHashMap<>();
        Map requestParameterMap = request.getParameterMap();
        Set set = requestParameterMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String[] values = (String[]) entry.getValue();
            resultMap.put(key, values[0]);
        }

        return gson.toJson(resultMap);

    }


    /**
     * 返回form表单或url中参数的map形式，此方法不考虑一键多值情况
     *
     * @param request
     * @return
     */
    public static Map getMapFromRequestParam(HttpServletRequest request) {
        Map resultMap = new LinkedHashMap<>();
        Map requestParameterMap = request.getParameterMap();
        Set set = requestParameterMap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String[] values = (String[]) entry.getValue();
            resultMap.put(key, values[0]);
        }

        return resultMap;

    }
}
