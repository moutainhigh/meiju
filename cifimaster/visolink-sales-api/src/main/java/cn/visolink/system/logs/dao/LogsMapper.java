package cn.visolink.system.logs.dao;

import java.util.List;
import java.util.Map;

/**
 * @author：sjl
 * @date： 2019/10/24 10:15
 */
public interface LogsMapper {

    /**
     * 查询日志操作信息
     * @param paramMap
     * @return
     */
    List<Map> queryLogInfo(Map paramMap);
    String queryLogInfoCount(Map paramMap);
}
