package cn.visolink.salesmanage.flow.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wyg
 * @version 1.0
 * @date 2019/11/17 10:11 上午
 */
@Mapper
@Repository
public interface WorkflowDao {

    void updateStartworkflow(Map map);

    void insertStartworkflow(Map map);

    Map queryworkflow(@Param("BOID") String BOID);

    void insertMessage(@Param("message") String message);

    Map queryMmWeekMarketingPlan(@Param("id") String id);

    Map selectUrl(@Param("BOID") String BOID);

    Map selectBaseId(Map map);

    String selectJsonId(@Param("jsonId") String jsonId);

    void  insertCheckSale(List<Map<String, Object>> list);

    void insertWeelPlan(List<Map<String, Object>> list);

    //获取项目信息
    public List<Map> getProjectData(Map map);

   void updateFlowzdDateByJsonId(String json_id);

   //获取系统配置的流程Code
    Map getFlowCodeData(String flowCode);
    //获取主流程下的子流程
    Map getFlowChildCodeData(@Param("id") String id,@Param("orgName") String orgName);

    /**
     * 获取当前项目所属的组织
     */
    Map getProjectSubordinateOrg(String project_id);
    String selectPrimaryByFlowId(String flow_id);
    void updateFlowStatusById(Map map);

    void insertParamLog(String jsonStr);

    Map queryworkflowByBaseId(@Param("BOID") String BOID);
    /**
     * 获取流程id，珍珠链上线以前的
     */
    List<Map> getFlowId(Map map);
    /**
     * 根据flow_id查询flow_code
     */
    public String getFlowCodeByFlowId(String flow_id);
    /**
     * 查询最近半个小时发起的流程
     */
    public List<Map> getFlowNewData();
    /**
     * 查询OA最新的审批
     */
    public Map getOApushNewApplay(Map map);
    /**
     * 查询最近一次的下游系统推送记录
     */
    public Map getPushDownstreamSystemNew(Map map);
}
