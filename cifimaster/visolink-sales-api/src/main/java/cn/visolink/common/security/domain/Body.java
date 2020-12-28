/**
  * Copyright 2019 bejson.com 
  */
package cn.visolink.common.security.domain;

import java.util.Map;

/**
 * Auto-generated: 2019-09-17 19:54:59
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Body {

    private String mfaSessionId;
    private String userId;
    private TicketEntry ticketEntry;
    private String identifierCode;
    private Map attributes;
    public void setMfaSessionId(String mfaSessionId) {
         this.mfaSessionId = mfaSessionId;
     }
     public String getMfaSessionId() {
         return mfaSessionId;
     }

    public void setUserId(String userId) {
         this.userId = userId;
     }
     public String getUserId() {
         return userId;
     }

    public void setTicketEntry(TicketEntry ticketEntry) {
         this.ticketEntry = ticketEntry;
     }
     public TicketEntry getTicketEntry() {
         return ticketEntry;
     }

    public void setIdentifierCode(String identifierCode) {
         this.identifierCode = identifierCode;
     }
     public String getIdentifierCode() {
         return identifierCode;
     }

    public void setAttributes(Map attributes) {
         this.attributes = attributes;
     }
     public Map getAttributes() {
         return attributes;
     }

}