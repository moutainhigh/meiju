package cn.visolink.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sjl
 * 过滤空数据，防止入库报错
 * @Created date 2020/7/30 4:42 下午
 */
public class FilterMapUtils {
    //过滤即将入库的参数数据
    public static Map filterMap(Map<String,Object> map){
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map.Entry<String,Object> entry:map.entrySet()){
            String value=entry.getValue()+"";
            if(!"".equals(value)&&!"null".equalsIgnoreCase(value)){
                resultMap.put(entry.getKey(),entry.getValue());
            }
        }
        return resultMap;
    }
}
