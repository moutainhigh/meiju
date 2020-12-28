package cn.visolink.system.parameter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数管理接口
 *
 * @author ligengying
 * @date 20190909
 */

public interface ParameterManagementService {

    /**
     * 获取所有的参数
     *
     * @param reqMap
     */
    List<Map> getSystemAllparams(HashMap<String, String> reqMap);

    /*
    获取字典数据
*/
    List<Map> getDicByCodeList(Map map);


    /*
    获取字典数据
*/
    List<Map> getDicByCodeLevelList(Map map);
    /**
     * 系统新增参数
     *
     * @param reqMap
     * @return
     */
    int saveSystemParam(Map reqMap);

    /**
     * 系统修改参数
     *
     * @param reqMap
     * @return
     */
    int modifySystemParam(Map reqMap);

    /**
     * 删除系统参数
     *
     * @param reqMap
     * @return
     */
    int removeSystemParam(Map reqMap);

    /**
     * 查询子集参数（树形）
     *
     * @param reqMap
     * @return
     */
    List<Map> getSystemTreeChildParams(Map reqMap);

    /**
     * 查询子集参数（非树形）
     *
     * @param reqMap
     * @return
     */
    Map getSystemChildParams(Map reqMap);

    /**
     * 启用/禁用参数
     *
     * @param reqMap
     * @return
     */
    int modifySystemParamStatus(Map reqMap);
}
