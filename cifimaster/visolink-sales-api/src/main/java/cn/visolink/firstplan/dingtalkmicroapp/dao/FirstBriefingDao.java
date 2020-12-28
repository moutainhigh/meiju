package cn.visolink.firstplan.dingtalkmicroapp.dao;

import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/8/27 10:49 上午
 */
public interface FirstBriefingDao {
    /**
     * 获取当前用户拥有的项目权限
     */
    public List<Map<String,String>> getUserProjectData(Map map);
    /**
     * 校验用户的功能权限
     */
    public List<Map> getUserFunctionPermission(Map map);

    /**
     * 校验用户的功能权限
     * bql 2020-09-29 优化
     *
     * @param map map
     * @return list
     */
    List<Map> getUserFunctionPermissionOptimiza(Map map);


}
