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
public class TicketEntry {

    private String ticketValue;
    private String ticketName;
    private String ticketType;
    private int timingVerifyTime;
    private long autoTimeout;
    public void setTicketValue(String ticketValue) {
         this.ticketValue = ticketValue;
     }
     public String getTicketValue() {
         return ticketValue;
     }

    public void setTicketName(String ticketName) {
         this.ticketName = ticketName;
     }
     public String getTicketName() {
         return ticketName;
     }

    public void setTicketType(String ticketType) {
         this.ticketType = ticketType;
     }
     public String getTicketType() {
         return ticketType;
     }

    public void setTimingVerifyTime(int timingVerifyTime) {
         this.timingVerifyTime = timingVerifyTime;
     }
     public int getTimingVerifyTime() {
         return timingVerifyTime;
     }

    public void setAutoTimeout(long autoTimeout) {
         this.autoTimeout = autoTimeout;
     }
     public long getAutoTimeout() {
         return autoTimeout;
     }

}