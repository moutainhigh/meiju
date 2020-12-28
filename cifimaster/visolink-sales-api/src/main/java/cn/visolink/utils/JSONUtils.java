package cn.visolink.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JSONUtils {

	private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static Map<String, Object> toMap(final String jsonBody) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        try {
            jsonMap = (Map<String, Object>) JSON.parseObject(jsonBody, (TypeReference)new TypeReference<TreeMap<String, Object>>() {}, new Feature[0]);
        }
        catch (Throwable t) {}
        return jsonMap;
    }

    public static <T> T toObject(final String jsonBody, final Class<T> jsonClazz) {
        if (StringUtils.isBlank((CharSequence)jsonBody)) {
            return null;
        }
        try {
            return (T) JSON.parseObject(jsonBody, (Class)jsonClazz);
        }
        catch (Throwable ignored) {
            logger.error("Json Object Convert To Failed,jsonString:" + jsonBody, ignored);
            return null;
        }
    }

    public static <T> List<T> toListObject(final String jsonBody, final Class<T> jsonClazz) {
        if (StringUtils.isBlank((CharSequence)jsonBody)) {
            return null;
        }
        try {
            return (List<T>) JSON.parseArray(jsonBody, (Class)jsonClazz);
        }
        catch (Throwable ex) {
            logger.error("Json Object Convert To Failed,jsonString:" + jsonBody, ex);
            return null;
        }
    }

    public static String ObjectTojson(final Object obj) {
        return JSON.toJSONString(obj);
    }

}
