package cn.visolink.report;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletResponse;
@RestController
@Api(tags = "Message")
@RequestMapping("/report")
public class ReportController {

    @RequestMapping("/redirectMy")
    public void forwardMyUrl(String env)throws Exception{
        String myurl = "http://sales-test.cifi.com.cn:9060";
        if("prod".equals(env)){
            myurl = "http://sales.cifi.com.cn:9060";
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.sendRedirect(myurl);
    }
}
