package cn.visolink.constant;

/**
 * 常用静态常量
 * @author WCL
 * @date 2018-12-26
 */
public class VisolinkConstant {

    public static final String RESET_PASS = "重置密码";

    public static final String RESET_MAIL = "重置邮箱";

    /**
     * 常用接口
     */
    public static class Url{
        public static final String SM_MS_URL = "https://sm.ms/api/upload";
    }

    /**
     * redis token 后缀
     */
    public static final String REDIS_TOKEN = "_token_";


    /**
     * 用户信息 后缀
     */
    public static final String REDIS_USER_INFO = ":info";

    /**
     * 签名key
     */
    public static final String SIGNING_KEY = "spring-security-@Jwt!&Secret^#";

    /**
     * redis 系统(来源)
     */
    public static final String REDIS_KEY = "salesmgt";

    public static final String TOKEN_KEY = "salemgtTokenKey";
}
