package cn.visolink.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * 获取 HttpServletRequest
 * @author WCL
 * @date 2018-11-24
 */
public class RequestHolder {

    public static HttpServletRequest getHttpServletRequest() {
        if(RequestContextHolder.getRequestAttributes() == null){
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }
}
