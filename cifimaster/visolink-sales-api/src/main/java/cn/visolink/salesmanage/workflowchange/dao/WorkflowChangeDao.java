package cn.visolink.salesmanage.workflowchange.dao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 *
 *  * @author lihuan
 *  * @since 2019-11-12
 */
@Mapper
public interface WorkflowChangeDao {

    /**
     *签约后变更神批发起
     * @param map
     * @return
     */
    public void workflowSend(Map<String, Object> map);

    /**
     *签约后变更神批表单查询
     * @param map
     * @return
     */
    public List<Map> workflowSelect(Map<String, Object> map);


    /**
     *流程已存在更新批表单
     * @param map
     * @return
     */
    public void workflowUpdate(Map<String, Object> map);

    /**
     *流程记录解析后的参数
     * @param map
     * @return
     */
    public void workflowLogUpdate(Map<String, Object> map);

    /**
     *流程记录原始参数
     * @param param
     * @return
     */
    public void workflowParamUpdate(String param);

    /**
     * 查询流程的flow_code
     * */
    public Map queryFlowCode(@Param("json_id") String json_id);

    /**
     * 根据baseid查询flow_code
     */

    public Map queryFlowDateByBaseID(@Param("BOID") String boid, @Param("param1") String param1
            , @Param("param2") String param2
            , @Param("fileUrlOld") String fileUrlOld
            , @Param("fileUrlNew") String fileUrlNew);

    public Map queryworkflowByBaseId(@Param("BOID") String boid);

    //查询原code
    public Map getOldFlowCode(String flow_code);
    /**
     * 查询项目
     */
    public Map getTproject(String project_id);
    /**
     * 查询项目对应的区域集团id
     */
    public String getBuiness_id(String project_id);
    /**
     * 添加调用日志
     */
    public void addFlowResultInfo(Map paramMap);
}
