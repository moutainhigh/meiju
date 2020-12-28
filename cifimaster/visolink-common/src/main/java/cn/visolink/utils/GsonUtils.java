package cn.visolink.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

/**
 * Created by chencheng on 2017/8/17 11:26.
 * <br/>gson工具类
 * <br/>该方法对数值类型进行了判断，若存在int型数值，进行json转换后仍旧为int型而不是被转换成double
 * <br/>double类型的数据，若为1.0这种类型 会被转换为1
 */
public class GsonUtils {
    private GsonUtils(){}

    /**
     * 参数为true表示不会将int数值转换为double，false会将int值转换为double
     * @param bl 可选，默认为true
     * @return gson对象，该对象设置了prettyPrinting和disableHtmlEscaping以及数值类型转换
     */
    public static Gson create(boolean... bl){
        boolean is = false;
        if (bl!=null) {
            if (bl.length==0 || bl[0]) {
                is = true;
            }
        }
        boolean finalIs = is;
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
                .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (aDouble, type, jsonSerializationContext) -> {
                    if (aDouble == aDouble.longValue()){
                        if (finalIs){
                            return new JsonPrimitive(aDouble.longValue());
                        }else{
                            return new JsonPrimitive(aDouble);
                        }

                    }
                    return new JsonPrimitive(aDouble);
                }).create();
        return gson;
    }

    /**
     * 参数为true表示不会将int数值转换为double，false会将int值转换为double
     * @param bl 可选，默认为true
     * @return gsonBuilder对象，该对象设置了数值类型转换
     */
    public static GsonBuilder gsonBuilder(boolean... bl){
        boolean is = false;
        if (bl!=null) {
            if (bl.length==0 || bl[0]) {
                is = true;
            }
        }
        boolean finalIs = is;
        GsonBuilder gson = new GsonBuilder()
                .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (aDouble, type, jsonSerializationContext) -> {
                    if (aDouble == aDouble.longValue()){
                        if (finalIs){
                            return new JsonPrimitive(aDouble.longValue());
                        }else{
                            return new JsonPrimitive(aDouble);
                        }

                    }
                    return new JsonPrimitive(aDouble);
                });
        return gson;
    }

}
