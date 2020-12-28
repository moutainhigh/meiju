package cn.visolink.salesmanage.reportmenumanager.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface ReportMenuDao {
        /*
        * 插入一张报表
        * */
     Integer insertReportMenu(Map<String,Object> map);
    /*
     * 跟新一张报表
     * */
    Integer updateReportMenu(Map<String,Object> map);
    /*
     * 删除一张报表
     * */
    Integer deleteReportMenu(Map<String,Object> map);
    /*
     * 查找报表
     * */
    List<Map> selectReportMenu(Map map);
    /*
     * 查找最高等级报表ID
     * */
    Map<String,Object> selectBestMenu();

    //获取最高级的id
    String getParentId();
}
