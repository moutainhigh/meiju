package cn.visolink.firstplan.dataAccess.service;

import java.util.List;
import java.util.Map;

public
interface DataAccessService {
    /**
     * 新增t_mm_project
     */
    int insertPanoramaProject(Map params);

    int insertReport(List list);


    int updateReport(List list);

    int insertCard(List list);

    int delGuestStorage(Map params);

    int delGuestStorageByProject(Map params);

    int delGuestStorageAll();

    Map sendNodeReport(Map parmas);

    Map sendNodeWarn(Map parmas);
    List<Map> queryLsTable(Map map);

}
