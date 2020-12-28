package cn.visolink.utils;

import java.io.Serializable;

/**
 * 请求报文体
 */
public class BaseRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String reqId;//请求序号，每次请求保持唯一，表明报文的唯一编号
	private String funCode;//接口方法编码
	private String groupId;//集团号，我司分配给集团的唯一编号。groupId、appId两个都填以appId为准
	private String appId;//商户号，我司分配给客户的唯一编号。集团模式appId、appName必填1个
	private String appName;//营业执照公司名称。集团模式appId、appName必填1个
	private Object data;//对应报文类型所要求的业务数据，详见各个接口请求参数要求

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
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
