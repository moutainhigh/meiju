package cn.visolink.firstplan.skipnodeupload.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/28 3:32 下午
 */
@Mapper
public interface SkipNodeUploadFileDao {
    //查询已经上传的附件列表
    public List<Map> getRepairFileList(String id);
    /**
     * 查询有效附件
     */
    public void updateFileStatus(Map map);
    /**
     * 将附件状态更改为有效
     */
    public void updateFileStatusIseffective(String flow_id);
    /**
     * 获取节点名称
     */
    public String getNodeName(String plan_node_id);

    /**
     * 获取当前位于哪个节点页面
     */
    public Map getNodeDataByFlowId(String flow_id);
}
