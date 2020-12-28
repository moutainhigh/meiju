package cn.visolink.firstplan.message.dao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/5/26 9:55 下午
 */
@Mapper
public interface MessageManagerDao {
    /**
     * 获取模版列表
     * @param map
     * @return
     */
    List<Map> queryMessageTemplateList(Map map);
    /**
     * 获取模版总数量
     */
    String queryMessageTemplateListCount(Map map);
    /**
     * 获取已选择的通用岗位列表
     */
    List<Map> getSelectedJobList(Map map);
    /**
     * 获取已选择的通用岗位总数
     */
    String getSelectedJobListTotal(Map map);
    /**
     * 获取未选择的通用岗位列表-可选择
     */
    List<Map> getNotSelectedJobList(Map map);
    /**
     * 获取未选择的通用岗位列表-可选择总数
     */
    String getNotSelectedJobListTotal(Map map);
    /**
     * 删除当前消息模版关联的岗位组
     */
    void deleteSelectedComjob(String id);
    /**
     * 更新消息模版与岗位组的关联
     */
    void updateTemplateRelationcomjob(Map map);
    /**
     * 查询添加的标签名称是否已存在
     * @param label_name
     * @return
     */
    Map queryLabelName(@Param("label_name") String label_name,@Param("label_class_name") String label_class_name);
    /**
     * 添加标签
     */
    void saveLabel(Map map);
    /**
     * 查询标签列表
     */
    List<Map> queryLabelList(Map map);
    /**
     * 查询标签列表总数
     */
    String queryLabelListTotal(Map map);
    /**
     * 修改标签
     */
    void updateLabel(Map map);
    /**
     * 查询可以选择的业务归属
     */
    List<Map> getTemplateType(Map map);
    /**
     * 查询对应归属模块的标签库
     */
    List<Map> getLabelListByLabelType(String template_type);
    /**
     * 查询标签类别下面的标签列表
     */
    List<Map> getLabelListByClassName(Map map);
    /**
     * 添加模版
     */
    void saveTemplate(Map map);
    /**
     * 修改模版
     */
    void updateTemplate(Map map);
    /**
     * 查询模版实例明细详情
     */
    Map getTemplateInfo(String id);
    /**
     * 删除标签
     * @param id
     */
    void  deleteLabel(String id);
    /**
     * 根据id查询消息详情
     */
    public Map getMessageInfoById(String id);
    /**
     * 查询消息列表
     */
    public List<Map> queryMessageList(Map map);
    /**
     * 查询消息列表总数量
     */
    public String queryMessageListTotal(Map map);
    /**
     * 查询当前消息下即将发送的发送人
     */
    public List<Map> getMessageSendUserList(Map map);
    /**
     * 查询消息详情
     */
    public Map queryMessageInfo(String id);
    /**
     * 删除消息
     */
    public void deleteMessageById(String id);
    /**
     * 修改消息详情
     */
    public void updateMessageInfo(Map map);
    /**
     * 查询集团层级人员
     */
    public List<Map> getGroupUserList(Map map);
    /**
     * 查询群聊列表
     */
    public List<Map> queryGroupChatList(Map map);
    /**
     * 删除群聊
     */
    public void deleteBusinessGroupChat(Map map);
    /**
     * 添加群聊
     */
    public void addBuinessGroupChat(Map map);
    /**
     * 修改群聊
     */
    public void updateBusinessGroupChat(Map map);
    /**
     * 查询群聊详细信息
     */
    public Map queryBuinessGroupChatInfo(Map map);
    /**
     * 校验群聊信息
     */
    public Map checkBuinessGroup(Map map);
    /**
     * 查询通用岗位所属的层级
     */
    public List<Map> getCommonJobHrType(Map map);
    /**
     * 查询项目所属的组织
     */
    public Map getProjectBelongOrg(Map map);
    /**
     * 获取对应层级主岗的人员
     */
    public List<Map> getUserList(Map map);
    /**
     * 获取已发送的岗位人员
     */
    public List<Map> getSendedUserList(Map  map);
    /**
     * 根据json_id删除消息
     */
    public void deleteMessageByProjectId(String json_id);
    /**
     * 根据json_id查询消息
     */
    public Map  getMessageByProjectId(String json_id);
    /**
     * 插入待发送人员列表
     */
    public  void insertUserList(Map map);
    /**
     * 查询消息对应的人员列表
     */
    public List<Map> getMessageForUserList(String message_id);
}
