package cn.visolink.system.statistics.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/8 10:46 上午
 */
@Mapper
public interface StatisticsReportMapper {

    List<Map> getStatisticsReportMenus(Map map);

    List<Map> getComomUserReportMenus(Map map);

    void addCommomUserReportMenus(Map map);
    Map getUserName(Map map);
    void insertCommomUserReportMenus(Map map);
    /**
     * 删除用户常用报表数据
     */
    void deleteUseReport(Map map);
}
