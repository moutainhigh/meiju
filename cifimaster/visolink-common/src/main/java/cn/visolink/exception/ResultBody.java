package cn.visolink.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/8 17:41
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultBody<T> implements Serializable {
    /**
     * 错误码
     */
    private long code=200;

    /** 提示信息. */
    private String messages="ok";

    /** 具体的内容. */
    private T data;

    public void setCode(long code) {
        this.code = code ;
    }

    public void setMessages(String messages){
        this.messages=messages;
    }

    public T getData(){
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功返回
     * @param object
     * @return
     */
    public static ResultBody success(Object object) {
        ResultBody result = new ResultBody();
        result.setCode(200);
        result.setMessages("ok");
        result.setData(object);
        return result;
    }

    /**
     * 异常返回
     * @param code
     * @param msg
     * @return
     */
    public static ResultBody error(Integer code, String msg) {
        ResultBody result = new ResultBody();
        result.setCode(code);
        result.setMessages(msg);
        return result;
    }
}
