package cn.visolink.firstplan.message.pojo;

/**
 * 发送消息返回内容
 * @Author sjl
 * @param <T>
 */
public class ResultVO<T> {
    private Integer retCode;

    private String retMsg;

    private T retData;

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public T getRetData() {
        return retData;
    }

    public void setRetData(T retData) {
        this.retData = retData;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "retCode=" + retCode +
                ", retMsg='" + retMsg + '\'' +
                ", retData=" + retData +
                '}';
    }
}
