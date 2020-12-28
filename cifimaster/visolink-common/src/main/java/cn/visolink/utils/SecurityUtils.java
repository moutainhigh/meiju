package cn.visolink.utils;

import cn.hutool.json.JSONObject;
import io.cess.CessException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 获取当前登录的用户
 * @author WCL
 * @date 2019-01-17
 */
public class SecurityUtils {

    public static UserDetails getUserDetails() {
        UserDetails userDetails = null;
        try {
            userDetails = (UserDetails) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new CessException(-10_0010, "登录状态过期");
        }
        return userDetails;
    }

    /**
     * 获取系统用户名称
     * @return 系统用户名称
     */
    public static String getUsername(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("username", String.class);
    }

    /**
     * 获取系统用户id
     * @return 系统用户id
     */
    public static String getUserId(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("id", String.class);
    }
}
