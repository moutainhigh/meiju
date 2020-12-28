package cn.visolink.config;

import cn.visolink.exception.ResultBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 业务扩展类接口，所有业务的实现类都要实现execute方法。
 * @author WCL
 */
public interface ExtensionMethod {
    /**
     * Visolink 定义数据执行方法，所有拓展类都继承ExtensionMethod，并实现execute 方法
     * @param param
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    ResultBody execute(String param, HttpServletRequest request, HttpServletResponse response) throws Exception;


    Map login(String userName,String pwd ) throws Exception;

}
