package cn.visolink.utils;

import java.io.Serializable;

/**
 * 响应报文体
 */
public class BaseResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;//响应码
	private String message;//响应信息
	private String reqId;//同请求报文一致
	private String funCode;//同请求报文一致
	private String groupId;//同请求报文一致
	private String appId;//同请求报文一致
	private String appName;//同请求报文一致
	private Object data;//回执的业务数据，详见各个接口响应参数

	public String getReqId() {
		return reqId;
	}
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	public String getFunCode() {
		return funCode;
	}
	public void setFunCode(String funCode) {
		this.funCode = funCode;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
