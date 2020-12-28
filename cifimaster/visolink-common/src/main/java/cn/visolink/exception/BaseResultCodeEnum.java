package cn.visolink.exception;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/8/24 6:10 下午
 */
public class BaseResultCodeEnum {

    public static enum ExeStats{
        PARAM_EMPTY(-10_0000,"参数不能为空!");


        private ExeStats(Integer code,String msg){
            this.code=code;
            this.msg=msg;
        }
        private final Integer code;
        private final String msg;

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
