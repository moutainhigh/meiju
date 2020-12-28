package cn.visolink;

import cn.visolink.exception.conifg.PropertiesListener;
import cn.visolink.utils.SpringContextHolder;
import io.cess.core.Cess;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author WCL
 * @date 2018/11/15 9:20:19
 */
@EnableAsync
@Cess
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value ={"cn.visolink.message.dao","cn.visolink.system.**.dao","cn.visolink.**.dao"})
public class AppRun  extends SpringBootServletInitializer{

    public static void main(String[] args) {


        SpringApplication application = new SpringApplication(AppRun.class);
        // 注册监听器,读取异常编码配置文件
        application.addListeners(new PropertiesListener("code.properties"));
        application.run(args);
//        SpringApplication.run(AppRun.class, args);
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
    /**
     * 部署到tomcat需要添加如下代码
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AppRun.class);
    }


}
