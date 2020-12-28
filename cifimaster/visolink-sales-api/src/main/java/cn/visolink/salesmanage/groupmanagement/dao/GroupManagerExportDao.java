package cn.visolink.salesmanage.groupmanagement.dao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author wjc
 * @date 2019--09-19
 *
 *
 *
 */
public interface GroupManagerExportDao {
    /**
     * 导出excel表
     * @param request
     * @param response
     * @return
     */
    Map<String,Object> indicatorDataExport(HttpServletRequest request, HttpServletResponse response, String months)  ;

}
