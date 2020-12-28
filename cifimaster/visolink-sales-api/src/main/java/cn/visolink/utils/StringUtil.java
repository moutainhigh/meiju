package cn.visolink.utils;


/**
 * 字符串工具类
 */
public class StringUtil {
    /**
     * 检测字符串是否不为空(null,"","null")
     *
     * @param s
     * @return 不为空则返回true，否则返回false
     */
    public static boolean notEmpty(String s) {
        return s != null && !"".equals(s) && !"null".equals(s);
    }

    /**
     * 检测字符串是否为空(null,"","null")
     *
     * @param s
     * @return 为空则返回true，不否则返回false
     */
    public static boolean isEmpty(String s) {
        return s == null || "".equals(s.trim()) || "null".equals(s);
    }

    /**
     * 检测字符串是否为空(null,"null"),是则返回""
     *
     * @param s
     * @return
     */
    public static String nullToSapce(String s) {
        if(s == null || "null".equals(s)){
            return "";
        }
        return s;
    }

}
