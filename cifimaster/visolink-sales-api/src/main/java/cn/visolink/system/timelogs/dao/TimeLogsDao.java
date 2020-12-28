package cn.visolink.system.timelogs.dao;

import cn.visolink.system.timelogs.bean.SysLog;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface TimeLogsDao {

    /**
     * 插入日志
     * @param sysLog
     */
    void insertLogs(SysLog sysLog);


    /**
     * 插入日志
     * @param
     */
    void insertLog(Map params);

}
