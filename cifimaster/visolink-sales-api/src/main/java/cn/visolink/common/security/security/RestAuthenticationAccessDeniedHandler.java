package cn.visolink.common.security.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/20 17:43
 * @Version 1.0
 **/
@Component("RestAuthenticationAccessDeniedHandler")
public class RestAuthenticationAccessDeniedHandler  implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        //登陆状态下，权限不足执行该方法
        System.out.println("权限不足：" + e.getMessage());
        //if(e!=null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
       // }
        /*else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
        }*/

    }
}
