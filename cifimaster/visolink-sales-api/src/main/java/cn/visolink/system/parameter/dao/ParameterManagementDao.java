package cn.visolink.system.parameter.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ParameterManagementDao {

    /**
     * 查询系统的所有参数
     *
     * @param reqMap
     */
    List<Map> getSystemAllParams(HashMap<String, String> reqMap);

    /**
     * 新增系统参数
     *
     * @param map
     */
    int insertSystemParam(Map map);

    /**
     * 修改系统参数
     *
     * @param map
     * @return
     */
    int modifySystemParam(Map map);

    /**
     * 删除系统参数
     *
     * @param map
     * @return
     */
    int removeSystemParam(Map map);

    /**
     * 获取子集参数（树形）
     *
     * @param map
     * @return
     */
    List<Map> getSystemTreeChildParams(Map map);

    /**
     * 获取参数子级(非树形)
     *
     * @param map
     * @return
     */
    List<Map> getSystemChildParams(Map map);

    /**
     * 获取参数子级总记录数(非树形)
     *
     * @param map
     * @return
     */
    Map getSystemChildParamsCount(Map map);

    /*
        获取字典数据
    */
    List<Map> getDicByCodeList(Map map);

    /*
    获取字典数据
*/
    List<Map> getDicByCodeLevelList(Map map);
    /**
     * 查询参数Code是否已存在
     *
     * @param map
     * @return
     */
    Map getSystemParamCodeExists(Map map);

    /**
     * 启用/禁用参数
     *
     * @param map
     * @return
     */
    int modifySystemParamStatus(Map map);
}

