package cn.visolink.common.security.security;

import cn.visolink.common.redis.service.RedisService;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.common.security.utils.JwtTokenUtil;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.handler.ExceptionHandlerResolver;
import cn.visolink.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author WCL
 */
@Slf4j
@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisService redisService;
    @Autowired
    ExceptionHandlerResolver exceptionHandlerResolver;


    private final JwtUserDetailsServiceImpl userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final String tokenHeader;

    public JwtAuthorizationTokenFilter(@Qualifier("jwtUserDetailsServiceImpl") JwtUserDetailsServiceImpl userDetailsService, JwtTokenUtil jwtTokenUtil, @Value("${jwt.header}") String tokenHeader) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenHeader = tokenHeader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String param= null;
        final String requestHeader = request.getHeader(this.tokenHeader);
        String isWhiteList  = request.getHeader("isWhiteList");
     /*   BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
        if(jsonObject!=null){
            param= jsonObject.toJSONString();
            System.out.println(param);
            if(param.contains("select")){
                throw new BadRequestException(1111,"参数异常，请求失败！");
            }
        }*/



        String username = null;
        String authToken = null;

        try {
            if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
                authToken = requestHeader.substring(7);
                /**
                 * 根据Token加载用户信息
                 */
                username = jwtTokenUtil.getUsernameFromToken(authToken);
//                String codeVal = this.redisService.getCodeVal(username);
//
//                if (Strings.isEmpty(codeVal)) {
//                    throw new BadRequestException(-10_0003,"您的账号暂未登录,请前往登录!");
//                }
//
//                if (!codeVal.toUpperCase().equals(authToken.toUpperCase())) {
//                    throw new BadRequestException(-10_0001, "您的账号已在其他地方登陆!");
//                }
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                try {
                // It is not compelling necessary to load the use details from the database. You could also store the information
                // in the token and read it from it. It's up to you ;)
              JwtUser userDetails = (JwtUser) this.userDetailsService.loadUserByUsername(username);

                // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
                // the database compellingly. Again it's up to you ;)
              if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
               }
//                } catch (BadRequestException badExceptio) {
//                    exceptionHandlerResovler.resolveException(request, response, null, badExceptio);
//                } catch (Exception e) {
//                    log.error(e.getMessage());
//                }
            }
            if(username!=null&&SecurityContextHolder.getContext().getAuthentication() != null){ SecurityUtils.getUserDetails();}

        } catch (BadRequestException badExceptio) {
            username = null;
            exceptionHandlerResolver.resolveException(request, response, null, badExceptio);
        } catch (ExpiredJwtException ex) {
            username = null;
            exceptionHandlerResolver.resolveException(request, response, null, new BadRequestException(-10_0013, "token已过期,请联系管理员！"));
        } catch (Exception e) {
            username = null;
            log.error(e.getMessage());
            exceptionHandlerResolver.resolveException(request, response, null,e);
        }
        if("true".equals(isWhiteList)){

        }else{
            if(username!=null){
                String user= (String) redisService.getObject( VisolinkConstant.TOKEN_KEY+"."+username);
                if(!username.equals(user)){
                    throw new BadRequestException(-10_0013, "登录状态失效！请重新登录！");
                }
            }
        }

        chain.doFilter(request, response);


    }
}
