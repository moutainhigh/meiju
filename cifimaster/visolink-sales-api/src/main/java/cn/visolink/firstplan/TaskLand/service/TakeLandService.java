package cn.visolink.firstplan.TaskLand.service;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import cn.visolink.firstplan.TaskLand.form.ExcelGetLand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *
 *  * @author wyg
 *  * @since 2020-02-17
 */
public interface TakeLandService {

    /**
     * 查询区域名称
     * */
    public List<Map> queryRegion(Map map);
    /**
     * 查询佣金
     * */
    public List<Map> queryCMRegion(Map map);

    /**
    * 查询四级组织
    * */
    public List<Map> queryOrgListByOrgId(String orgId);
    /**
     * 查询区域名称
     * */
    public List<Map> queryCitys(Map map);

    /**
     *通过项目查询id
     * @param map
     * @return
     */
    public Map queryPlan(Map map);

    /**
     * 拿地数据暂存
     * */
    public VisolinkResultBody insertTakeLand(Map map, HttpServletRequest request);

    /**
     * 顶设一暂存
     * */
    public VisolinkResultBody insertTopOne(Map map,HttpServletRequest request);

    /**
     * 查询节点信息
     * */
    public List<Map> selectPlanNode(Map map);

    /**
     * 查询拿地信息
     * */
    public Map queryTakeLands(Map map);

    /**
     * 查询节点版本
     * */
    public List<Map> selectNodeVersion(Map map);

    /**
     *
     * 以下接口皆是查询拿地后数据
     * */
    public Map queryValueStructure(Map map);
    public Map queryApartment(Map map);
    public List<Map> queryTimeNode(Map map);
    public List<Map> querySalesTarget(Map map);
    public List<Map> queryCost(Map map);


    /**
     *查询拿地后的节点id
     * */
    List<Map> queryPlanNodeId(Map map);


    public List getFileLists(String id);

    /**
     * 查询顶设一数据
     * */
    Map queryTopOne(Map map);


    /**
     * 添加流程表
     * */
    VisolinkResultBody insertFlow(Map map);

    VisolinkResultBody takeLandSuccess(Map map);

    /**
     * 拿地后、顶设一数据导出
     * */
    void ExportGetLand(HttpServletRequest request, HttpServletResponse response, Map map);

}
