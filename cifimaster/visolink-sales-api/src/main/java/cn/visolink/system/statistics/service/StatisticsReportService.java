package cn.visolink.system.statistics.service;

import cn.visolink.exception.ResultBody;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/8 10:48 上午
 */
public interface StatisticsReportService {

    ResultBody getStatisticsReportMenus(Map map);

    ResultBody addCommomUserReportMenus(Map map);

}
