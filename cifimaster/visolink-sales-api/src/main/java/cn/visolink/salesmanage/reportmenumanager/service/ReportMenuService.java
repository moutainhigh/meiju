package cn.visolink.salesmanage.reportmenumanager.service;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface ReportMenuService {

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
    Integer deleteReportMenu(Map<String, Object> map);
    /*
     * 查找一张报表
     * */
    PageInfo selectReportMenu(Map map);
}
