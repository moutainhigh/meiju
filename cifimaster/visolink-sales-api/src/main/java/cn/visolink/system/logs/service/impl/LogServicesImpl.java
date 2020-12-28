package cn.visolink.system.logs.service.impl;

import cn.visolink.system.logs.dao.LogsMapper;
import cn.visolink.system.logs.service.LogServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author：sjl
 * @date： 2019/10/24 10:19
 */
@Service
public class LogServicesImpl implements LogServices {

    @Autowired
    private LogsMapper logsMapper;
    @Override
   public Map queryLogInfo(Map paramMap){
       Map resultMap = new HashMap<>();
        if (paramMap.get("date") != null && !paramMap.get("date").equals("")) {
            paramMap.put("startTime", paramMap.get("date").toString().substring(1, 11));
            paramMap.put("endTime", paramMap.get("date").toString().substring(27, 37));
        }
        int pageIndex = Integer.parseInt(paramMap.get("pageIndex") + "");
        int pageSize = Integer.parseInt(paramMap.get("pageSize") + "");
        int pageNum=(pageIndex-1)*pageSize;
        paramMap.put("pageIndex", pageNum);
        List<Map> list = logsMapper.queryLogInfo(paramMap);
        String total = logsMapper.queryLogInfoCount(paramMap);
        resultMap.put("list",list);
        resultMap.put("total",total);
        return  resultMap;
    }
}
