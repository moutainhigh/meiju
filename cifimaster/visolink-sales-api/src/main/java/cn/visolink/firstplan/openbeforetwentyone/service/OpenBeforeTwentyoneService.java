package cn.visolink.firstplan.openbeforetwentyone.service;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.text.ParseException;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/6 2:01 下午
 */
public interface OpenBeforeTwentyoneService {
    //数据渲染
    VisolinkResultBody viewdelayOpenApplay(Map map) throws ParseException;
    //切换版本
    VisolinkResultBody switchVersion(Map  map);

    VisolinkResultBody getWeeklyResolution(Map map) throws ParseException;
    //保存/提交
    VisolinkResultBody saveelayOpenApplay(Map map);
    void updateOpen_timeData(Map map);

    ResultBody exportDelayOpenData(Map<Object,Object> map, HttpServletRequest request, HttpServletResponse response);
}
