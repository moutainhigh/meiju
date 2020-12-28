package cn.visolink.firstplan.message.service;
import cn.visolink.exception.ResultBody;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/5/26 7:04 下午
 */
public interface MessageManagerService {

    /**
     * 查询模版列表
     * @param map
     * @return
     */
    public ResultBody queryTemplateList(Map map);


    /**
     * 添加消息模版
     */
    public ResultBody addMessageTemplate(Map map);
    /**
     * 添加消息模版-获取标签库
     */
    public ResultBody getLabelLibraryData(Map map);
    /**
     * 设置发送岗位
     */
    public ResultBody setPositionSending(Map map);

    /**
     * 设置发送岗位组-保存设置
     */
    public ResultBody setPositionSave(Map map);
    /**
     * 消息管理-添加标签
     */
    public ResultBody saveLabel(Map map);

    /**
     * 查询标签列表
     */
    public ResultBody queryLabelList(Map map);

    /**
     * 获取模版详细数据
     */
    public ResultBody getTemplateInfo(Map map);
    /**
     * 查询可选择业务模块数据
     */
    public ResultBody getTypeList(Map map);

    /**
     * 删除标签
     */
    public ResultBody deleteLabel(String id);
    /**
     * 根据id获取消息详情
     */
    public ResultBody getMessageInfoById(Map map);
    /**
     * 消息列表查询
     */
    public ResultBody queryMessageList(Map map);
    /**
     * 编辑消息
     */
    public ResultBody updateMessageInfo(Map map);
    /**
     * 查询消息详情
     */
    public ResultBody queryMessageInfo(Map map);
    /**
     * 移除消息
     */
    public ResultBody deleteMessageById(Map map);
    /**
     * 查询事业部/区域集团和群聊对应关系
     */
    public ResultBody queryBusinessGroupChat(Map map);

    /**
     * 删除事业部/区域集团和群聊对应关系
     */
    public ResultBody deleteBusinessGroupChat(Map map);
    /**
     * 添加群聊和区域集团对应关系
     */
    public ResultBody addBusinessGroupChat(Map map);
    /**
     * 查询群聊详情
     */
    public ResultBody queryBuinessGroupChatInfo(Map map);

    /**
     * 查询配置通用岗位下的所有人员
     */
    public ResultBody queryUserList(Map map);
    /**
     * 刷数据接口
     */
    public ResultBody initMessageSendUsers(Map map);
}


