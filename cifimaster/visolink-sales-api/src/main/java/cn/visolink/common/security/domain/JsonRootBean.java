/**
  * Copyright 2019 bejson.com 
  */
package cn.visolink.common.security.domain;

/**
 * Auto-generated: 2019-09-17 19:54:59
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class JsonRootBean {

    private Boolean success;
    private String message;
    private String resultCode;
    private String cause;
    private Body body;
    public void setSuccess(Boolean success) {
         this.success = success;
     }
     public Boolean getSuccess() {
         return success;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setResultCode(String resultCode) {
         this.resultCode = resultCode;
     }
     public String getResultCode() {
         return resultCode;
     }

    public void setCause(String cause) {
         this.cause = cause;
     }
     public String getCause() {
         return cause;
     }

    public void setBody(Body body) {
         this.body = body;
     }
     public Body getBody() {
         return body;
     }

}