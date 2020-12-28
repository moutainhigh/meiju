package cn.visolink.common.security.config;

import cn.visolink.common.security.security.JwtAuthenticationEntryPoint;
import cn.visolink.common.security.security.JwtAuthorizationTokenFilter;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author WCL
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtUserDetailsServiceImpl jwtUserDetailsService;

    /**
     * 自定义基于JWT的安全过滤器
     */
    @Autowired
    JwtAuthorizationTokenFilter authenticationTokenFilter;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.auth.path}")
    private String loginPath;

    @Value("${jwt.backLogin.path}")
    private String backLogin;

    @Value("${jwt.dingLogin.path}")
    private String dingLogin;
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(passwordEncoderBean());
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

                // 禁用 CSRF
                .csrf().disable()

                // 授权异常
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // 不创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                // 过滤请求
                .authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/*.png",
                        "/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).anonymous()
                .antMatchers("/static/**").anonymous()
                .antMatchers("/img/**").anonymous()
                //健康检查
                .antMatchers("/health").anonymous()

                .antMatchers("/resultTest/**").anonymous()
                .antMatchers("/externaldrainage/**").anonymous()
                // 单点登录接口
                .antMatchers(HttpMethod.POST,"/auth/"+backLogin).anonymous()
                // 登录接口
                .antMatchers(HttpMethod.POST,"/auth/"+loginPath).anonymous()
                // 登录接口
                .antMatchers(HttpMethod.POST,"/auth/"+dingLogin).anonymous()
                // 验证码
                .antMatchers("/auth/vCode").permitAll()
                //版本更新
                .antMatchers("/version/versionUpdate").anonymous()
                // 忘记密码
                .antMatchers("/RevisePassWordServer").anonymous()
                //发送短信
                .antMatchers("/ShortMessageServer").anonymous()
                //退出登录
                .antMatchers("/auth/loginOut").anonymous()
                //退出登录
                .antMatchers("/auth/loginOutUserName").permitAll()

                // swagger start

                .antMatchers("/swagger-ui.html").anonymous()
                .antMatchers("/swagger-resources/**").anonymous()
                .antMatchers("/doc**").anonymous()
                .antMatchers("/v2/api-docs").anonymous()
                .antMatchers("/swagger**").anonymous()
                .antMatchers("/webjars/**").anonymous()
                .antMatchers("/*/api-docs").anonymous()
                // swagger end

                // 接口限流测试
                .antMatchers("/test/**").anonymous()
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()

                .antMatchers("/druid/**").anonymous()
                // 所有请求都需要认证
                .anyRequest().permitAll()
                // 防止iframe 造成跨域
                .and().headers().frameOptions().disable();
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
