package cn.visolink.exception;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/8/23 9:19 下午
 */
public class ResultUtil {

    /**
     * 成功返回
     * @param object
     * @return
     */
    public static ResultBody success(Object object) {
        ResultBody result = new ResultBody();
        result.setCode(200);
        result.setMessages("ok");
        result.setData(object);
        return result;
    }

    /**
     * 异常返回
     * @param code
     * @param msg
     * @return
     */
    public static ResultBody error(long code, String msg) {

        ResultBody result = new ResultBody();
        result.setCode(code);
        result.setMessages(msg);
        return result;
    }

    /**
     * 获取 错误map
     *
     * @param requstTime requstTime
     * @param returnMsg  returnMsg
     * @return return
     */
    public static Map<String, Object> getErrorMap(String requstTime, String returnMsg) {
        return getResultMap(requstTime, returnMsg, "E");
    }

    /**
     * 获取 成功map
     *
     * @param requstTime requstTime
     * @return return
     */
    public static Map<String, Object> getSuccessMap(String requstTime) {
        return getResultMap(requstTime, "调用成功", "S");
    }

    /**
     * 获取 结果map
     *
     * @param requstTime   requstTime
     * @param returnMsg    returnMsg
     * @param returnStatus returnStatus
     * @return return
     */
    public static Map<String, Object> getResultMap(String requstTime, String returnMsg, String returnStatus) {
        Map<String, Object> resultMap = new LinkedHashMap<>(2);
        Map<String, Object> errorMap = new LinkedHashMap<>(16);
        errorMap.put("instId", "RVNCTSEsZUASDGTEQ4658MYW5HH");
        errorMap.put("returnStatus", returnStatus);
        errorMap.put("returnCode", "A0001-SMS");
        errorMap.put("returnMsg", returnMsg);
        errorMap.put("requstTime", requstTime);
        errorMap.put("reponsTime", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss.SSS"));
        errorMap.put("attr1", null);
        errorMap.put("attr2", null);
        errorMap.put("attr3", null);

        resultMap.put("esbInfo", errorMap);
        return resultMap;
    }

}
