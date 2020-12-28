package cn.visolink.system.timelogs.bean;

import lombok.Data;

import java.util.Date;

/**
 * 日志类
 * @Auther: wang gang
 * @Date: 2019/10/6 10:40
 */
@Data
public class SysLog {

    private String TaskName;

    private String StartTime;

    private int ResultStatus;

    private String ExecutTime;

    private String Note;

}
