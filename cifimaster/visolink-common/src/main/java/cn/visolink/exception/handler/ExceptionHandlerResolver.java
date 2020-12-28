package cn.visolink.exception.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.conifg.PropertiesListenerConfig;
import com.google.common.collect.ImmutableMap;
import io.cess.CessException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/21 14:00
 * @Version 1.0
 **/
@Component
public class ExceptionHandlerResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) {
        response.reset();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
           Map<String, String> errorMessage = PropertiesListenerConfig.getAllProperty();
           OutputStream outputStream = response.getOutputStream();
           Map<Object, Object> immutableMap = MapUtil.newHashMap();

            if(e instanceof CessException){
                CessException  cessException = (CessException) e;
                immutableMap.put("code",cessException.getCode());
                immutableMap.put("messages",StrUtil.isEmpty(cessException.getMessage())?errorMessage
                                .get(String.valueOf(cessException.getCode())):cessException.getMessage());
                //outputStream.write(JSON.toJSONString(immutableMap).getBytes());
                outputStream.flush();
                outputStream.close();
                cessException.printStackTrace();
            }
            if(e instanceof BadRequestException){
                BadRequestException  badRequestException = (BadRequestException) e;
                immutableMap.put("code",badRequestException.getCode());
                immutableMap.put("messages", StrUtil.isEmpty(badRequestException.getMessage())?errorMessage
                                .get(badRequestException.getCode().toString()):badRequestException.getMessage());
                //outputStream.write(JSON.toJSONString(immutableMap).getBytes());
                outputStream.flush();
                outputStream.close();
                badRequestException.printStackTrace();
            }
            if (e instanceof AuthenticationException){
                AuthenticationException  authenticationException = (AuthenticationException) e;
                immutableMap.put("code","-10_0014");
                immutableMap.put("messages","无权限访问,请联系管理员!");
               // outputStream.write(JSON.toJSONString(immutableMap).getBytes());
                outputStream.flush();
                outputStream.close();
                authenticationException.printStackTrace();
            }

            else {
                throw e;
            }
        } catch (IOException es) {
            es.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
