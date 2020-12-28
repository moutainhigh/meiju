package cn.visolink.firstplan.openbeforeseven.service;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/3/9 12:25 下午
 */
public interface OpenBeforeSevenDayService {
    VisolinkResultBody viewOpenBeforeSevenDayOpenApplay(Map map);
    VisolinkResultBody applyAdoptTellInterface(Map map);
    VisolinkResultBody saveOpenBeforeSevenDayOpenApplay(Map map);
    VisolinkResultBody switchVersion(Map map);
    ResultBody exportOpenApplayData(Map map,HttpServletRequest request, HttpServletResponse response);
}
