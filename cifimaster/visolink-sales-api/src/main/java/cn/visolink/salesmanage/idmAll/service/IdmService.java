package cn.visolink.salesmanage.idmAll.service;

import java.util.List;
import java.util.Map;

public interface IdmService {

    /**
     *查询idm岗位组
     *
     * @return
     */
    public List<Map> selectIdm(Map map);

    /**
     *添加idm岗位组
     *
     * @return
     */
    public Map addIdmCommonjobs(Map map);


    /**
     *定时刷新idm岗位组
     *
     * @return
     */
    public Map sfIdmCommonjobs();
}
