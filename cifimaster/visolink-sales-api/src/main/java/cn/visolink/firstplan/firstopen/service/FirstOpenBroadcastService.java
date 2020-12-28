package cn.visolink.firstplan.firstopen.service;

import cn.visolink.exception.ResultBody;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/7/8 11:10 上午
 */
public interface FirstOpenBroadcastService {
    ResultBody viewOpenBroadcastData(Map map);
    /**
     * 查询版本
     */
    ResultBody getBroadcasVersionData(Map map);

    /**
     * 保存
     */
    ResultBody saveOpenBroadCastData(Map map);

    /**
     * 导出
     */
    ResultBody exportExcelOpenBroadCastData(Map map, HttpServletRequest request, HttpServletResponse response);
}
