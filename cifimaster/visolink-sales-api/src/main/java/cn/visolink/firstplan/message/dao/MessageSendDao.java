package cn.visolink.firstplan.message.dao;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/6/1 3:21 下午
 */
public interface MessageSendDao {
    /**
     * 获取模版配置的发送人列表
     * template_id:模版id
     */
    List<Map> getSendUserList(Map map);
    /**
     * 获取指定消息详情
     */
    public Map getMessageInfo(String id);
    /**
     * 获取截止到当前时间未发送且设置为自动发送的消息列表
     */
    public List<Map> getSendMessageList(Map map);
    /**
     * 发送成功后将消息设置为发送成功状态
     */
    public void updateMessageSendStatus(String id);
    /**
     * 获取消息所属的模块类型
     */
    public String getMessageModelType(String id);

    /**
     * 记录消息发送的人员
     */
    public void recordMessageSendUser(Map map);
}
